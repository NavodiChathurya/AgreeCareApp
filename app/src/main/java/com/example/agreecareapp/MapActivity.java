package com.example.agreecareapp;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import com.google.firebase.database.*;

import java.io.IOException;
import java.util.*;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    private EditText searchBar;
    private Button btnSearch, btnShops, btnFarms, btnAll;
    private TextView arrowBack;
    private LatLng lastSearchedLatLng = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // Initialize views
        searchBar = findViewById(R.id.searchBar);
        btnSearch = findViewById(R.id.btnSearch);
        arrowBack = findViewById(R.id.arrow_back);
        btnShops = findViewById(R.id.btnShops);
        btnFarms = findViewById(R.id.btnFarms);
        btnAll = findViewById(R.id.btnAll);

        // Back arrow click
        arrowBack.setOnClickListener(v -> {
            startActivity(new Intent(this, SensorLocationActivity.class));
            finish();
        });

        // Setup map fragment
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) mapFragment.getMapAsync(this);

        // Search button
        btnSearch.setOnClickListener(v -> {
            String location = searchBar.getText().toString();
            if (!location.isEmpty()) {
                searchLocation(location);
            } else {
                Toast.makeText(this, "Please enter a location", Toast.LENGTH_SHORT).show();
            }
        });

        // Filter buttons
        btnShops.setOnClickListener(v -> {
            String location = searchBar.getText().toString();
            if (!location.isEmpty()) {
                loadLocationsFromFirebase("shop", location);
            } else {
                Toast.makeText(this, "Please search a location first", Toast.LENGTH_SHORT).show();
            }
        });

        btnFarms.setOnClickListener(v -> {
            String location = searchBar.getText().toString();
            if (!location.isEmpty()) {
                loadLocationsFromFirebase("farm", location);
            } else {
                Toast.makeText(this, "Please search a location first", Toast.LENGTH_SHORT).show();
            }
        });

        btnAll.setOnClickListener(v -> {
            String location = searchBar.getText().toString();
            if (!location.isEmpty()) {
                loadLocationsFromFirebase("all", location);
            } else {
                Toast.makeText(this, "Please search a location first", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            showUserLocation();  // ✅ Using safe listener
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private void showUserLocation() {
        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null && mMap != null) {
                LatLng userLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.addMarker(new MarkerOptions()
                        .position(userLatLng)
                        .title("You Are Here")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 13));
            }
        });
    }

    private void searchLocation(String locationName) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addressList = geocoder.getFromLocationName(locationName, 1);
            if (addressList != null && !addressList.isEmpty()) {
                Address address = addressList.get(0);
                lastSearchedLatLng = new LatLng(address.getLatitude(), address.getLongitude());

                mMap.clear();
                mMap.addMarker(new MarkerOptions()
                        .position(lastSearchedLatLng)
                        .title(locationName + " Center"));

                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lastSearchedLatLng, 13));
            } else {
                Toast.makeText(this, "Location not found", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Geocoder error", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadLocationsFromFirebase(String filterType, String cityFilter) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("locations");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mMap.clear();
                List<LocationItem> matchedItems = new ArrayList<>();

                for (DataSnapshot child : snapshot.getChildren()) {
                    LocationItem item = child.getValue(LocationItem.class);

                    if (item != null &&
                            (filterType.equals("all") || item.type.equalsIgnoreCase(filterType)) &&
                            (cityFilter == null || (item.city != null && item.city.equalsIgnoreCase(cityFilter)))) {

                        LatLng position = new LatLng(item.latitude, item.longitude);
                        float color = item.type.equals("shop") ?
                                BitmapDescriptorFactory.HUE_ORANGE : BitmapDescriptorFactory.HUE_GREEN;

                        mMap.addMarker(new MarkerOptions()
                                .position(position)
                                .title(item.name)
                                .icon(BitmapDescriptorFactory.defaultMarker(color)));

                        matchedItems.add(item);
                    }
                }

                if (!matchedItems.isEmpty()) {
                    showPopupList(matchedItems, filterType);
                } else {
                    Toast.makeText(MapActivity.this, "No " + filterType + "s found in " + cityFilter, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MapActivity.this, "Failed to load map data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showPopupList(List<LocationItem> items, String type) {
        StringBuilder message = new StringBuilder();
        for (LocationItem item : items) {
            message.append("📍 ").append(item.name)
                    .append(" (").append(item.type).append(")")
                    .append("\nCity: ").append(item.city != null ? item.city : "Unknown")
                    .append("\n\n");
        }

        new AlertDialog.Builder(MapActivity.this)
                .setTitle("Nearby " + (type.equals("all") ? "Locations" : type + "s"))
                .setMessage(message.toString())
                .setPositiveButton("OK", null)
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE &&
                grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            showUserLocation();  // ✅ Called again after permission granted
        }
    }
}

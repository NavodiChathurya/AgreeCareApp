package com.example.agreecareapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CropManagementActivity extends AppCompatActivity {

    private static final String TAG = "CropManagement";

    private RecyclerView cropRecyclerView;
    private CropAdapter cropAdapter;
    private final List<Crop> cropList = new ArrayList<>();
    private final List<Crop> fullCropList = new ArrayList<>();
    private EditText searchBar;
    private Spinner sortSpinner;
    private Button btnAddCrop;
    private Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_management);

        CropDataManager.init(this);

        // UI Bindings
        cropRecyclerView = findViewById(R.id.recyclerViewCrops);
        searchBar = findViewById(R.id.searchBar);
        sortSpinner = findViewById(R.id.sortSpinner);
        btnAddCrop = findViewById(R.id.btnAddCrop);
        btnBack = findViewById(R.id.btnBack);

        // RecyclerView setup
        cropRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        cropRecyclerView.setNestedScrollingEnabled(true); // ✅ Important for scroll
        cropAdapter = new CropAdapter(cropList);
        cropRecyclerView.setAdapter(cropAdapter);

        // Sort Spinner
        String[] sortOptions = {"Name", "Status", "Date Added", "Priority"};
        ArrayAdapter<String> sortAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                sortOptions
        );
        sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortSpinner.setAdapter(sortAdapter);

        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sortCrops(sortOptions[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Search Filter
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterCrops(s.toString());
            }
        });

        // Add Crop
        if (btnAddCrop != null) {
            btnAddCrop.setOnClickListener(v -> {
                startActivity(new Intent(CropManagementActivity.this, AddCropActivity.class));
            });
        }

        // Back to Dashboard
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> {
                startActivity(new Intent(CropManagementActivity.this, Dashboard.class));
                finish();
            });
        }

        // Load initial data
        loadCrops();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCrops();
    }

    private void loadCrops() {
        fullCropList.clear();
        cropList.clear();

        List<Crop> cropsFromManager = CropDataManager.getCrops();
        if (cropsFromManager != null) {
            fullCropList.addAll(cropsFromManager);
        } else {
            Log.e(TAG, "CropDataManager returned null crops");
        }

        // 🔧 (Optional) Fake test data for scroll testing
        for (int i = 1; i <= 30; i++) {
            Crop fakeCrop = new Crop("Crop " + i, "Growing", "Test crop " + i, System.currentTimeMillis());
            fullCropList.add(fakeCrop);
        }

        String selectedSort = sortSpinner.getSelectedItem() != null ? sortSpinner.getSelectedItem().toString() : "Name";
        sortCrops(selectedSort);
        cropAdapter.notifyDataSetChanged();
        Log.d(TAG, "Loaded crops: " + fullCropList.size());
    }

    private void filterCrops(String query) {
        cropList.clear();
        if (query == null || query.trim().isEmpty()) {
            cropList.addAll(fullCropList);
        } else {
            String lowerQuery = query.toLowerCase();
            for (Crop crop : fullCropList) {
                boolean matches = false;
                if (crop.name != null && crop.name.toLowerCase().contains(lowerQuery)) {
                    matches = true;
                } else if (crop.message != null && crop.message.toLowerCase().contains(lowerQuery)) {
                    matches = true;
                }
                if (matches) {
                    cropList.add(crop);
                }
            }
        }
        cropAdapter.notifyDataSetChanged();
        Log.d(TAG, "Filtered crops: " + cropList.size() + " for query: " + query);
    }

    private void sortCrops(String criteria) {
        switch (criteria) {
            case "Name":
                Collections.sort(fullCropList, Comparator.comparing(c -> c.name != null ? c.name.toLowerCase() : ""));
                break;
            case "Status":
                Collections.sort(fullCropList, (c1, c2) -> {
                    String s1 = c1.status != null ? c1.status.toLowerCase() : "";
                    String s2 = c2.status != null ? c2.status.toLowerCase() : "";
                    if ("Harvested".equals(s1)) return -1;
                    if ("Harvested".equals(s2)) return 1;
                    if ("Growing".equals(s1)) return -1;
                    if ("Growing".equals(s2)) return 1;
                    return s1.compareTo(s2);
                });
                break;
            case "Date Added":
                Collections.sort(fullCropList, (c1, c2) -> Long.compare(c2.timestamp, c1.timestamp));
                break;
            case "Priority":
                Collections.sort(fullCropList, (c1, c2) -> {
                    int p1 = getPriority(c1.status);
                    int p2 = getPriority(c2.status);
                    return Integer.compare(p2, p1);
                });
                break;
        }
        filterCrops(searchBar.getText().toString());
    }

    private int getPriority(String status) {
        if ("Urgent".equals(status)) return 3;
        if ("High".equals(status)) return 2;
        if ("Medium".equals(status)) return 1;
        return 0;
    }
}

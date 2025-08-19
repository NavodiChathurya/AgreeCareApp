package com.example.agreecareapp;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ViewSavedDataActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DataAdapter adapter;
    private List<FieldData> fieldDataList;
    private Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_saved_data);

        // Enable back button in action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Saved Field Data");
        }

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        fieldDataList = new ArrayList<>();
        adapter = new DataAdapter(fieldDataList);
        recyclerView.setAdapter(adapter);

        // Initialize Back Button
        backButton = findViewById(R.id.back_button);
        if (backButton == null) {
            Toast.makeText(this, "Error loading back button", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Set click listener for Back Button
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed(); // Navigate back to previous activity
            }
        });

        // Simulate saved data (replace with actual data retrieval)
        loadSampleData();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed(); // Navigate back when back icon is clicked
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadSampleData() {
        // Simulate data saved from ViewDataActivity
        fieldDataList.add(new FieldData("10.5", "Tomatoes", "2025-09-15", "Medium", "Loamy", "Sunny weather noted"));
        fieldDataList.add(new FieldData("15.0", "Corn", "2025-10-01", "High", "Sandy", "Needs monitoring"));
        adapter.notifyDataSetChanged();
    }

    // Simple data class to hold field details
    public static class FieldData {
        String area, cropType, harvestDate, irrigation, soilType, notes;

        FieldData(String area, String cropType, String harvestDate, String irrigation, String soilType, String notes) {
            this.area = area;
            this.cropType = cropType;
            this.harvestDate = harvestDate;
            this.irrigation = irrigation;
            this.soilType = soilType;
            this.notes = notes;
        }
    }

    // Adapter for RecyclerView
    public static class DataAdapter extends RecyclerView.Adapter<DataAdapter.ViewHolder> {
        private List<FieldData> dataList;

        DataAdapter(List<FieldData> dataList) {
            this.dataList = dataList;
        }

        @Override
        public ViewHolder onCreateViewHolder(android.view.ViewGroup parent, int viewType) {
            android.view.View view = android.view.LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_field_data, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            FieldData data = dataList.get(position);
            holder.areaText.setText("Area: " + data.area + " acres");
            holder.cropText.setText("Crop: " + data.cropType);
            holder.harvestText.setText("Harvest Date: " + data.harvestDate);
            holder.irrigationText.setText("Irrigation: " + data.irrigation);
            holder.soilText.setText("Soil: " + data.soilType);
            holder.notesText.setText("Notes: " + data.notes);
        }

        @Override
        public int getItemCount() {
            return dataList.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            android.widget.TextView areaText, cropText, harvestText, irrigationText, soilText, notesText;

            ViewHolder(android.view.View itemView) {
                super(itemView);
                areaText = itemView.findViewById(R.id.text_area);
                cropText = itemView.findViewById(R.id.text_crop);
                harvestText = itemView.findViewById(R.id.text_harvest);
                irrigationText = itemView.findViewById(R.id.text_irrigation);
                soilText = itemView.findViewById(R.id.text_soil);
                notesText = itemView.findViewById(R.id.text_notes);
            }
        }
    }
}
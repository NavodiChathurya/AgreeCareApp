package com.example.agreecareapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CropAdapter extends RecyclerView.Adapter<CropAdapter.CropViewHolder> {

    private final List<Crop> cropList;

    public CropAdapter(List<Crop> cropList) {
        this.cropList = cropList;
    }

    @NonNull
    @Override
    public CropViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_crop, parent, false);
        return new CropViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CropViewHolder holder, int position) {
        Crop crop = cropList.get(position);

        // Bind crop data safely
        holder.textName.setText(crop.getName() != null ? crop.getName() : "Unnamed Crop");
        holder.textStatus.setText(crop.getStatus() != null ? crop.getStatus() : "Unknown Status");
        holder.textDate.setText(crop.getDate() != null ? crop.getDate() : "Unknown Date");
        holder.textMessage.setText(crop.getMessage() != null ? crop.getMessage() : "No Message");

        // Handle delete
        holder.btnDelete.setOnClickListener(v -> {
            if (crop.getKey() != null) {
                CropDataManager.removeCrop(crop.getKey());
                cropList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, cropList.size());
                Toast.makeText(v.getContext(), "Crop deleted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(v.getContext(), "Error: Crop key not found", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return cropList != null ? cropList.size() : 0;
    }

    // ViewHolder
    static class CropViewHolder extends RecyclerView.ViewHolder {
        TextView textName, textStatus, textDate, textMessage;
        Button btnDelete;

        public CropViewHolder(@NonNull View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.textCropName);
            textStatus = itemView.findViewById(R.id.textCropStatus);
            textDate = itemView.findViewById(R.id.textCropDate);
            textMessage = itemView.findViewById(R.id.textCropMessage);
            btnDelete = itemView.findViewById(R.id.btnDeleteCrop);
        }
    }
}

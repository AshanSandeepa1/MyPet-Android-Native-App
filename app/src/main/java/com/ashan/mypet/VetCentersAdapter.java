package com.ashan.mypet;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class VetCentersAdapter extends RecyclerView.Adapter<VetCentersAdapter.ViewHolder> {

    private List<VetCenter> vetCenterList;
    private Context context;
    private OnItemClickListener listener;

    // Define the interface for item click
    public interface OnItemClickListener {
        void onItemClick(VetCenter vetCenter);  // Pass the full VetCenter object
    }

    public VetCentersAdapter(List<VetCenter> vetCenterList, OnItemClickListener listener) {
        this.vetCenterList = vetCenterList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_vet_center, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        VetCenter vetCenter = vetCenterList.get(position);
        holder.name.setText(vetCenter.getName());
        holder.district.setText(vetCenter.getDistrict());

        // Use the listener to pass the full VetCenter object to the bottom sheet
        holder.itemView.setOnClickListener(v -> listener.onItemClick(vetCenter));  // Pass full VetCenter object
    }

    @Override
    public int getItemCount() {
        return vetCenterList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, district;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tvName);
            district = itemView.findViewById(R.id.tvDistrict);
        }
    }
}

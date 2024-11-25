package com.ashan.mypet;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;

public class HealthRecordAdapter extends RecyclerView.Adapter<HealthRecordAdapter.HealthRecordViewHolder> {

    private final List<HealthRecord> healthRecordList;
    private final OnHealthRecordClickListener onHealthRecordClickListener;

    // Constructor
    public HealthRecordAdapter(List<HealthRecord> healthRecordList, OnHealthRecordClickListener listener) {
        this.healthRecordList = healthRecordList;
        this.onHealthRecordClickListener = listener;
    }

    @NonNull
    @Override
    public HealthRecordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_health_record, parent, false);
        return new HealthRecordViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull HealthRecordViewHolder holder, int position) {
        HealthRecord record = healthRecordList.get(position);

        holder.recordTitle.setText(record.getTitle());
        holder.recordType.setText(record.getType());

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String formattedDate = sdf.format(record.getDate().toDate());
        holder.recordDate.setText(formattedDate);

        // Click listeners
        holder.itemView.setOnClickListener(v -> onHealthRecordClickListener.onHealthRecordClick(record));
        holder.deleteIcon.setOnClickListener(v -> onHealthRecordClickListener.onHealthRecordDelete(record));
    }

    @Override
    public int getItemCount() {
        return healthRecordList.size();
    }

    public static class HealthRecordViewHolder extends RecyclerView.ViewHolder {
        TextView recordTitle, recordType, recordDate;
        ImageView deleteIcon;

        public HealthRecordViewHolder(View itemView) {
            super(itemView);
            recordTitle = itemView.findViewById(R.id.recordType);
            recordType = itemView.findViewById(R.id.recordDescription);
            recordDate = itemView.findViewById(R.id.recordDate);
            deleteIcon = itemView.findViewById(R.id.btnDeleteRecord);
        }
    }

    // Functional interface for item click handling
    public interface OnHealthRecordClickListener {
        void onHealthRecordClick(HealthRecord healthRecord);
        void onHealthRecordDelete(HealthRecord healthRecord);
    }
}

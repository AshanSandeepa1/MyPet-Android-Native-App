package com.ashan.mypet;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class VetCenterBottomSheet extends BottomSheetDialogFragment {

    private VetCenter vetCenter;

    public VetCenterBottomSheet(VetCenter vetCenter) {
        this.vetCenter = vetCenter;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_vet_center, container, false);

        TextView tvName = view.findViewById(R.id.tvDetailName);
        TextView tvAddress = view.findViewById(R.id.tvDetailAddress);
        TextView tvDoctor = view.findViewById(R.id.tvDetailDoctor);
        TextView tvAvailability = view.findViewById(R.id.tvDetailAvailability);
        TextView tvRating = view.findViewById(R.id.tvDetailRating);
        Button btnOpenInMaps = view.findViewById(R.id.btnOpenInMaps);

        tvName.setText(vetCenter.getName());
        tvAddress.setText(vetCenter.getAddress());
        tvDoctor.setText(vetCenter.getDrName() + " (" + vetCenter.getDrSpecialization() + ")");
        tvAvailability.setText(vetCenter.getDrAvailability());
        tvRating.setText("Rating: " + vetCenter.getRating() + " ★");

        btnOpenInMaps.setOnClickListener(v -> {
            Uri gmmIntentUri = Uri.parse(vetCenter.getMap()); // Make sure the map URL is correct
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps"); // Ensure it's Google Maps
            try {
                startActivity(mapIntent);
            } catch (ActivityNotFoundException e) {
                // Fallback if Google Maps is not installed
                e.printStackTrace();
                // Optionally, you can notify the user that the app is missing
            }
        });


        return view;
    }
}

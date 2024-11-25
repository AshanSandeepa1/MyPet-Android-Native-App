package com.ashan.mypet;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

public class MedicalFragment extends Fragment {

    public MedicalFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_medical, container, false);

        // Initialize the cards
        CardView healthRecordsCard = view.findViewById(R.id.healthRecordsCard);
        CardView vetCentersCard = view.findViewById(R.id.vetCentersCard);

        // Set click listeners for cards
        healthRecordsCard.setOnClickListener(v -> openHealthRecordsActivity());
        vetCentersCard.setOnClickListener(v -> openVetCentersActivity());

        return view;
    }

    private void openHealthRecordsActivity() {
        Intent intent = new Intent(getActivity(), HealthRecordsActivity.class);
        startActivity(intent);
    }

    private void openVetCentersActivity() {
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
    }
}

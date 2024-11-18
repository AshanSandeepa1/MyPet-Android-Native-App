package com.ashan.mypet;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.cardview.widget.CardView;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

public class HomeFragment extends Fragment {

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        // Find the CardView by ID
        CardView healthPlusCard = rootView.findViewById(R.id.health_plus_card);
        CardView myPetCard = rootView.findViewById(R.id.mypet_card);
        // Find and set click listeners for images
        ImageView healthRecordsImage = rootView.findViewById(R.id.health_records_image);
        ImageView vetCentersImage = rootView.findViewById(R.id.vet_centers_image);


        myPetCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to start the AddPetActivity
                Intent intent = new Intent(getActivity(), AddpetActivity.class);
                startActivity(intent);
            }
        });

        healthRecordsImage.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), LoginActivity.class);
            startActivity(intent);
        });

        vetCentersImage.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), RegisterActivity.class);
            startActivity(intent);
        });

        return rootView;
    }
}

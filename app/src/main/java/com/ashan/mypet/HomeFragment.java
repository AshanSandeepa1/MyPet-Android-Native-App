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
import androidx.navigation.Navigation;
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

        // Find the CardViews by ID
        CardView healthPlusCard = rootView.findViewById(R.id.health_plus_card);
        CardView myPetCard = rootView.findViewById(R.id.mypet_card);
        CardView petStoreCard = rootView.findViewById(R.id.petStore_card);

        // Find and set click listeners for images
        ImageView healthRecordsImage = rootView.findViewById(R.id.health_records_image);
        ImageView vetCentersImage = rootView.findViewById(R.id.vet_centers_image);

        CardView tipsCard = rootView.findViewById(R.id.tips_card);

        // OnClickListener for 'My Pet' Card
        myPetCard.setOnClickListener(v -> {
            // Create an Intent to start the AddPetActivity
            Intent intent = new Intent(getActivity(), AddpetActivity.class);
            startActivity(intent);
        });

        // OnClickListener for health records image
        healthRecordsImage.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), HealthRecordsActivity.class);
            startActivity(intent);
        });

        // OnClickListener for vet centers image
        vetCentersImage.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), VetCentersActivity.class);
            startActivity(intent);
        });

        // OnClickListener for tips card
        tipsCard.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), TipsTricksActivity.class);
            startActivity(intent);
        });

        // OnClickListener for Pet Store Card
       // petStoreCard.setOnClickListener(v -> {
             //Use the Navigation Component to navigate to BuySellFragment
      //      NavController navController = NavHostFragment.findNavController(HomeFragment.this);
      //      navController.navigate(R.id.action_homeFragment_to_buySellFragment);
      //  });

        return rootView;
    }
}

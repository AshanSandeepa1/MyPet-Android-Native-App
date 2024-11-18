package com.ashan.mypet;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

public class BuySellFragment extends Fragment {

    public BuySellFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_buy_sell, container, false);

        // Initialize the button
        Button btnAddPet = view.findViewById(R.id.btnAddPet);

        // Set an OnClickListener to the button
        btnAddPet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start AddPetActivity when the button is clicked
                Intent intent = new Intent(getActivity(), AddpetActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }
}

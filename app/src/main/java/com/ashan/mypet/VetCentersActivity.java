package com.ashan.mypet;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class VetCentersActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private RecyclerView recyclerViewVetCenters;
    private VetCentersAdapter adapter;
    private Spinner spinnerDistrict;
    private List<VetCenter> vetCenterList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vet_centers);

        // Transparent status bar
        Window window = getWindow();
        window.setStatusBarColor(android.graphics.Color.TRANSPARENT);
        window.getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        setContentView(R.layout.activity_vet_centers);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize Views
        recyclerViewVetCenters = findViewById(R.id.recyclerViewVetCenters);
        spinnerDistrict = findViewById(R.id.spinnerDistrict);

        // Set up RecyclerView
        recyclerViewVetCenters.setLayoutManager(new LinearLayoutManager(this));
        adapter = new VetCentersAdapter(vetCenterList, vetCenter -> {
            // Pass the full VetCenter object to the BottomSheet
            VetCenterBottomSheet bottomSheet = new VetCenterBottomSheet(vetCenter);
            bottomSheet.show(getSupportFragmentManager(), "VetCenterBottomSheet");
        });
        recyclerViewVetCenters.setAdapter(adapter);

        // Load Districts into Spinner
        loadDistricts();

        // Load All Vet Centers
        loadVetCenters(null);

        // Handle District Selection
        spinnerDistrict.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedDistrict = parent.getItemAtPosition(position).toString();
                if (selectedDistrict.equals("All")) {
                    loadVetCenters(null);
                } else {
                    loadVetCenters(selectedDistrict);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    private void loadDistricts() {
        List<String> districts = new ArrayList<>();
        districts.add("All");
        districts.add("Colombo");
        districts.add("Galle");
        districts.add("Kandy");
        districts.add("Jaffna");
        // Add more districts as needed

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, districts);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDistrict.setAdapter(adapter);
    }

    private void loadVetCenters(String districtFilter) {
        db.collection("VetCenters")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    vetCenterList.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        VetCenter vetCenter = doc.toObject(VetCenter.class);
                        if (districtFilter == null || vetCenter.getDistrict().equals(districtFilter)) {
                            vetCenterList.add(vetCenter);
                        }
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error loading data", Toast.LENGTH_SHORT).show());
    }
}

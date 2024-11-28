package com.ashan.mypet;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment implements PetAdapter.OnPetClickListener {

    private TextView profileNameTextView, profileEmailTextView;
    private ImageView profilePictureImageView;
    private Button editProfileButton, addNewPetButton, logoutButton, changePasswordButton;
    private RecyclerView petRecyclerView;
    private PetAdapter petAdapter;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private List<Pet> petList;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize Firebase components
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();

        // Initialize UI elements
        profileNameTextView = view.findViewById(R.id.profile_name_textview);
        profileEmailTextView = view.findViewById(R.id.profile_email_textview);
        profilePictureImageView = view.findViewById(R.id.profile_picture_imageview);
        editProfileButton = view.findViewById(R.id.edit_profile_button);
        addNewPetButton = view.findViewById(R.id.add_new_pet_button);
        logoutButton = view.findViewById(R.id.logout_button);
        changePasswordButton = view.findViewById(R.id.change_password_button);
        petRecyclerView = view.findViewById(R.id.pet_recycler_view);

        // Set up RecyclerView for pet list
        petRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        petList = new ArrayList<>();
        petAdapter = new PetAdapter(petList, this);
        petRecyclerView.setAdapter(petAdapter);

        // Load user data
        loadUserData();

        // Load user pets
        loadUserPets();

        // Set up button listeners
        editProfileButton.setOnClickListener(v -> {
            // Open Edit Profile activity
            Intent intent = new Intent(getContext(), AddpetActivity.class);
            startActivity(intent);
        });

        addNewPetButton.setOnClickListener(v -> {
            // Open Add New Pet activity
            Intent intent = new Intent(getContext(), AddpetActivity.class);
            startActivity(intent);
        });

        logoutButton.setOnClickListener(v -> {
            // Log out from Firebase
            mAuth.signOut();
            Intent intent = new Intent(getContext(), LoginActivity.class);
            startActivity(intent);
            getActivity().finish();
        });

        changePasswordButton.setOnClickListener(v -> {
            // Allow the user to change their password (send reset email)
            if (currentUser != null) {
                mAuth.sendPasswordResetEmail(currentUser.getEmail())
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(getContext(), "Password reset email sent.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getContext(), "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        return view;
    }

    private void loadUserData() {
        String userId = FirebaseAuth.getInstance().getUid();

        if (userId == null) {
            Toast.makeText(getContext(), "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        // Fetch user data from the "Users" collection where document ID is the same as userId
        db.collection("users").document(userId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // DocumentSnapshot for the user
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            // Retrieve user details from the Firestore document
                            String displayName = document.getString("username");
                            String email = document.getString("email");
                            String profilePictureUrl = document.getString("profilePicture");

                            // Set the retrieved data in the UI components
                            profileNameTextView.setText(displayName);
                            profileEmailTextView.setText(email);

                            // Load profile picture using Glide (if available)
                            if (profilePictureUrl != null && !profilePictureUrl.isEmpty()) {
                                Glide.with(getContext())
                                        .load(profilePictureUrl)
                                        .into(profilePictureImageView);
                            }
                        } else {
                            Toast.makeText(getContext(), "No such user document", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "Error loading user data", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void loadUserPets() {
        String userId = FirebaseAuth.getInstance().getUid();

        if (userId == null) {
            Toast.makeText(getContext(), "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        // Query to fetch pets from Firestore
        db.collection("users").document(userId).collection("pets")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        petList.clear(); // Clear existing list
                        for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                            Pet pet = document.toObject(Pet.class);
                            petList.add(pet);
                        }
                        petAdapter.notifyDataSetChanged(); // Update the adapter
                    } else {
                        Toast.makeText(getContext(), "Error loading pets", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onPetClick(Pet pet) {
        // Handle pet item click, maybe open pet details screen
        Intent intent = new Intent(getContext(), AddpetActivity.class);
        intent.putExtra("petId", pet.getPId());  // Pass the petId for details
        startActivity(intent);
    }
}

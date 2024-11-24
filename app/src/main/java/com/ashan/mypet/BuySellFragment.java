package com.ashan.mypet;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BuySellFragment extends Fragment {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView emptyText;
    private StoreAdapter storeAdapter;
    private List<PetItem> itemList;
    private FirebaseFirestore db;
    private Spinner categorySpinner;
    private List<String> categoryList;
    private ImageButton toggleButton;
    private boolean isGridView = true; // Track the current view type
    private SearchView searchBar;

    public BuySellFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_buy_sell, container, false);

        // Initialize UI components
        recyclerView = view.findViewById(R.id.recycler_view);
        progressBar = view.findViewById(R.id.progress_bar);
        emptyText = view.findViewById(R.id.empty_text);
        categorySpinner = view.findViewById(R.id.category_spinner);
        toggleButton = view.findViewById(R.id.toggle_button);
        searchBar = view.findViewById(R.id.search_bar);

        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize lists
        itemList = new ArrayList<>();
        categoryList = new ArrayList<>();

        // Set up RecyclerView with grid layout as default
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        storeAdapter = new StoreAdapter(itemList, getContext(), this::showItemDetails);
        recyclerView.setAdapter(storeAdapter);

        // Load categories and items from Firestore
        loadCategoriesFromFirestore();
        loadItemsFromFirestore();

        // Toggle between grid and list views
        toggleButton.setOnClickListener(v -> toggleRecyclerViewLayout());

        // Set up SearchView
        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterItemsBySearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterItemsBySearch(newText);
                return true;
            }
        });

        return view;
    }

    private void toggleRecyclerViewLayout() {
        if (isGridView) {
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            toggleButton.setImageResource(android.R.drawable.ic_menu_sort_by_size); // Icon for list view
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
            toggleButton.setImageResource(android.R.drawable.ic_menu_gallery); // Icon for grid view
        }
        isGridView = !isGridView;
    }

    private void loadCategoriesFromFirestore() {
        db.collection("pet_store_items")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    Set<String> categoriesSet = new HashSet<>();
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        String category = document.getString("category");
                        if (category != null) categoriesSet.add(category);
                    }
                    categoryList.clear();
                    categoryList.add("All");
                    categoryList.addAll(categoriesSet);

                    // Set up category spinner
                    ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, categoryList);
                    categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    categorySpinner.setAdapter(categoryAdapter);

                    categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            filterItemsByCategory(categoryList.get(position));
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                            // No action needed
                        }
                    });
                })
                .addOnFailureListener(e -> {
                    Log.e("FirestoreError", "Error loading categories: " + e.getMessage());
                    Toast.makeText(getContext(), "Failed to load categories.", Toast.LENGTH_SHORT).show();
                });
    }

    private void loadItemsFromFirestore() {
        showLoadingState(true);
        db.collection("pet_store_items")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    itemList.clear();
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        try {
                            PetItem item = document.toObject(PetItem.class);
                            itemList.add(item);
                        } catch (Exception e) {
                            Log.e("FirestoreError", "Error mapping document: " + e.getMessage());
                        }
                    }
                    showLoadingState(false);
                    updateRecyclerView();
                })
                .addOnFailureListener(e -> {
                    Log.e("FirestoreError", "Error loading items: " + e.getMessage());
                    showLoadingState(false);
                    emptyText.setText("Failed to load items.");
                });
    }

    private void filterItemsByCategory(String category) {
        if ("All".equals(category)) {
            storeAdapter.updateList(itemList); // Show all items
            emptyText.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            return;
        }

        List<PetItem> filteredList = new ArrayList<>();
        for (PetItem item : itemList) {
            if (category.equals(item.getCategory())) {
                filteredList.add(item);
            }
        }

        storeAdapter.updateList(filteredList);

        if (filteredList.isEmpty()) {
            emptyText.setVisibility(View.VISIBLE);
            emptyText.setText("No items in this category.");
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyText.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void filterItemsBySearch(String query) {
        if (query == null || query.isEmpty()) {
            storeAdapter.updateList(itemList); // Show all items when search is empty
            emptyText.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            return;
        }

        List<PetItem> filteredList = new ArrayList<>();
        for (PetItem item : itemList) {
            if (item.getName().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(item);
            }
        }

        storeAdapter.updateList(filteredList);

        if (filteredList.isEmpty()) {
            emptyText.setVisibility(View.VISIBLE);
            emptyText.setText("No items match your search.");
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyText.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void updateRecyclerView() {
        if (itemList.isEmpty()) {
            emptyText.setVisibility(View.VISIBLE);
            emptyText.setText("No items available.");
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyText.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            storeAdapter.notifyDataSetChanged();
        }
    }

    private void showLoadingState(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(isLoading ? View.GONE : View.VISIBLE);
        emptyText.setVisibility(View.GONE);
    }

    private void showItemDetails(PetItem item) {
        ItemDetailsBottomSheet bottomSheet = ItemDetailsBottomSheet.newInstance(item);
        bottomSheet.show(getChildFragmentManager(), "item_details");
    }
}

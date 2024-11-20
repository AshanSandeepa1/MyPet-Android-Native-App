package com.ashan.mypet;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class BuySellFragment extends Fragment {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView emptyText;
    private StoreAdapter storeAdapter;
    private List<PetItem> itemList;
    private FirebaseFirestore db;

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

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2)); // 2-column grid layout

        itemList = new ArrayList<>();
        storeAdapter = new StoreAdapter(itemList, getContext(), this::showItemDetails);
        recyclerView.setAdapter(storeAdapter);

        db = FirebaseFirestore.getInstance();


        loadItemsFromFirestore();

        return view;
    }

    private void loadItemsFromFirestore() {
        db.collection("pet_store_items") // Reference to your Firestore collection
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    itemList.clear();  // Clear the list before adding new items
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        PetItem item = document.toObject(PetItem.class); // Map Firestore document to PetItem
                        itemList.add(item);  // Add to the list
                    }
                    storeAdapter.notifyDataSetChanged();  // Notify adapter about the data change
                })
                .addOnFailureListener(e -> {
                    // Handle Firestore errors here
                });
    }


    private void showItemDetails(PetItem item) {
        ItemDetailsBottomSheet bottomSheet = ItemDetailsBottomSheet.newInstance(item);
        bottomSheet.show(getChildFragmentManager(), "item_details");
    }
}


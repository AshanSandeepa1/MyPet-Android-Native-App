package com.ashan.mypet;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.bumptech.glide.Glide;

public class ItemDetailsBottomSheet extends BottomSheetDialogFragment {

    private static final String ARG_ITEM_NAME = "item_name";
    private static final String ARG_ITEM_PRICE = "item_price";
    private static final String ARG_ITEM_DESCRIPTION = "item_description";
    private static final String ARG_ITEM_IMAGE_URL = "item_image_url";

    public static ItemDetailsBottomSheet newInstance(PetItem item) {
        ItemDetailsBottomSheet fragment = new ItemDetailsBottomSheet();
        Bundle args = new Bundle();
        args.putString(ARG_ITEM_NAME, item.getName());
        args.putString(ARG_ITEM_PRICE, String.valueOf(item.getPrice()));
        args.putString(ARG_ITEM_DESCRIPTION, item.getDescription());
        args.putString(ARG_ITEM_IMAGE_URL, item.getImage_url());
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_item_details, container, false);

        // Retrieve arguments
        Bundle args = getArguments();
        if (args == null) {
            dismiss(); // Close the bottom sheet if no data is passed
            return view;
        }

        String itemName = args.getString(ARG_ITEM_NAME);
        String itemPrice = args.getString(ARG_ITEM_PRICE);
        String itemDescription = args.getString(ARG_ITEM_DESCRIPTION);
        String itemImageUrl = args.getString(ARG_ITEM_IMAGE_URL);

        // Bind data to views
        TextView itemNameTextView = view.findViewById(R.id.item_detail_name);
        TextView itemPriceTextView = view.findViewById(R.id.item_detail_price);
        TextView itemDescriptionTextView = view.findViewById(R.id.item_detail_description);
        ImageView itemImageView = view.findViewById(R.id.item_detail_image);

        itemNameTextView.setText(itemName);
        itemPriceTextView.setText("$" + itemPrice);
        itemDescriptionTextView.setText(itemDescription);

        // Load the image using Glide
        Glide.with(requireContext())
                .load(itemImageUrl)
                .placeholder(R.drawable.ic_pet_food) // Use a default placeholder image
                .error(R.drawable.error_image) // Use an error image if loading fails
                .into(itemImageView);

        return view;
    }
}

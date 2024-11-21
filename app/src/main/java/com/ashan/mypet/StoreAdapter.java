package com.ashan.mypet;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class StoreAdapter extends RecyclerView.Adapter<StoreAdapter.ViewHolder> {

    private List<PetItem> items; // List of items to display
    private final Context context;
    private final ItemClickListener clickListener;

    // Interface for handling item clicks
    public interface ItemClickListener {
        void onItemClick(PetItem item);
    }

    // Constructor to initialize the adapter
    public StoreAdapter(List<PetItem> items, Context context, ItemClickListener clickListener) {
        this.items = items;
        this.context = context;
        this.clickListener = clickListener;
    }

    // Method to dynamically update the data and refresh the UI
    public void updateList(List<PetItem> newList) {
        this.items = newList;
        notifyDataSetChanged(); // Notify RecyclerView about the data change
    }

    // Create and return a ViewHolder for each item
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item layout (item_store.xml)
        View view = LayoutInflater.from(context).inflate(R.layout.item_store, parent, false);
        return new ViewHolder(view);
    }

    // Bind data to the ViewHolder
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Get the current PetItem
        PetItem item = items.get(position);

        // Set the name and price
        holder.name.setText(item.getName());
        holder.price.setText("Rs." + item.getPrice());

        // Check if image URL is valid and not empty
        String imageUrl = item.getImage_url();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            // Load the image using Glide
            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.error_image) // Placeholder while loading
                    .error(R.drawable.error_image) // Error image if loading fails
                    .into(holder.image);
        } else {
            // Set a default image if the URL is null or empty
            holder.image.setImageResource(R.drawable.error_image);
        }

        // Set item click listener on the whole item view
        holder.itemView.setOnClickListener(v -> clickListener.onItemClick(item));
    }

    // Return the number of items in the list
    @Override
    public int getItemCount() {
        return items.size();
    }

    // ViewHolder class to hold the views for each item
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image; // ImageView for the item image
        TextView name, price; // TextViews for name and price

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Initialize the views
            image = itemView.findViewById(R.id.item_image);
            name = itemView.findViewById(R.id.item_name);
            price = itemView.findViewById(R.id.item_price);
        }
    }
}

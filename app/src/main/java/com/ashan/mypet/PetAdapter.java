package com.ashan.mypet;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class PetAdapter extends RecyclerView.Adapter<PetAdapter.PetViewHolder> {

    private List<Pet> petList;
    private OnPetClickListener listener;

    public PetAdapter(List<Pet> petList, OnPetClickListener listener) {
        this.petList = petList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pet, parent, false);
        return new PetViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PetViewHolder holder, int position) {
        Pet pet = petList.get(position);
        holder.petNameTextView.setText(pet.getName());
        holder.petTypeTextView.setText(pet.getSpecies());

        // Load pet image using Glide
        Glide.with(holder.itemView.getContext())
                .load(pet.getImage())  // Ensure pet.getImage() returns a valid image URL
                .into(holder.petImageView);

        holder.itemView.setOnClickListener(v -> listener.onPetClick(pet));
    }

    @Override
    public int getItemCount() {
        return petList.size();
    }

    public static class PetViewHolder extends RecyclerView.ViewHolder {
        TextView petNameTextView, petTypeTextView;
        ImageView petImageView;

        public PetViewHolder(View itemView) {
            super(itemView);
            petNameTextView = itemView.findViewById(R.id.petName);
            petTypeTextView = itemView.findViewById(R.id.petType);
            petImageView = itemView.findViewById(R.id.petImage);  // Assuming you have an ImageView in your layout
        }
    }

    public interface OnPetClickListener {
        void onPetClick(Pet pet);
    }
}

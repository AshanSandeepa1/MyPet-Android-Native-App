package com.ashan.mypet;

import com.google.firebase.firestore.DocumentId;

public class PetItem {

    @DocumentId
    private String id;
    private String name;
    private double price;
    private String description;
    private String category;
    private String image_url;
    private long timestamp;
    private float rating;

    // Empty constructor for Firestore
    @SuppressWarnings("unused")
    public PetItem() {}

    // Parameterized constructor
    @SuppressWarnings("unused")
    public PetItem(String name, double price, String description, String category, String image_url, long timestamp, float rating) {
        this.name = name;
        this.price = price;
        this.description = description;
        this.category = category;
        this.image_url = image_url;
        this.timestamp = timestamp;
        this.rating = rating;
    }

    // Getter and setter methods for each field
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }
}

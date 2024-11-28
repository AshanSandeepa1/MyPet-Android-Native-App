package com.ashan.mypet;

import com.google.firebase.firestore.PropertyName;

public class VetCenter {

    private String name;
    private String address;
    private String district;
    private String drName;
    private String drSpecialization;
    private String drAvailability;
    private String email;
    private String phone;
    private String map;
    private double rating;

    public VetCenter() {
        // Default constructor required for Firestore
    }

    public VetCenter(String name, String address, String district, String drName, String drSpecialization,
                     String drAvailability, String email, String phone, String map, double rating) {
        this.name = name;
        this.address = address;
        this.district = district;
        this.drName = drName;
        this.drSpecialization = drSpecialization;
        this.drAvailability = drAvailability;
        this.email = email;
        this.phone = phone;
        this.map = map;
        this.rating = rating;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getDistrict() {
        return district;
    }

    public String getDrName() {
        return drName;
    }

    public String getDrSpecialization() {
        return drSpecialization;
    }

    public String getDrAvailability() {
        return drAvailability;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getMap() {
        return map;
    }

    public double getRating() {
        return rating;
    }
}

package com.ashan.mypet;

import com.google.firebase.Timestamp;

public class Pet {
    private String pId;  // petId
    private String name;
    private String species;
    private int age;  // assuming age is an integer
    private String image;
    private boolean vaccinated;
    private boolean neutered;
    private String gender;  // Add gender field
    private Timestamp dob;  // Change dob to Timestamp

    // No-argument constructor
    public Pet() {}

    // Constructor with all fields
    public Pet(String pId, String name, String species, int age, String image, boolean vaccinated, boolean neutered, String gender, Timestamp dob) {
        this.pId = pId;
        this.name = name;
        this.species = species;
        this.age = age;
        this.image = image;
        this.vaccinated = vaccinated;
        this.neutered = neutered;
        this.gender = gender;
        this.dob = dob;
    }

    // Getters and setters
    public String getPId() {
        return pId;
    }

    public void setPId(String pId) {
        this.pId = pId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public boolean isVaccinated() {
        return vaccinated;
    }

    public void setVaccinated(boolean vaccinated) {
        this.vaccinated = vaccinated;
    }

    public boolean isNeutered() {
        return neutered;
    }

    public void setNeutered(boolean neutered) {
        this.neutered = neutered;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Timestamp getDob() {
        return dob;
    }

    public void setDob(Timestamp dob) {
        this.dob = dob;
    }

    // Convert Timestamp to String (if needed)
    public String getDobAsString() {
        if (dob != null) {
            return dob.toDate().toString();  // or format it using SimpleDateFormat if preferred
        }
        return null;
    }
}

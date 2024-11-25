package com.ashan.mypet;

import com.google.firebase.Timestamp;

public class HealthRecord {
    private String recordId;     // Firestore document ID
    private String type;         // Type of the health record (e.g., Vaccination)
    private String title;        // Title of the health record (e.g., Rabies Vaccine)
    private Timestamp date;      // Timestamp of the record
    private String vetName;      // Name of the vet
    private String notes;        // Additional notes about the health record
    private String image;        // Image related to the record (e.g., vaccine certificate)
    private String status;       // Status of the record (e.g., Completed)
    private Timestamp createdAt; // Timestamp when the record was created

    // Default constructor (required for Firestore)
    public HealthRecord() {}

    // Constructor with parameters
    public HealthRecord(String type, String title, Timestamp date, String vetName, String notes, String image, String status, Timestamp createdAt) {
        this.type = type;
        this.title = title;
        this.date = date;
        this.vetName = vetName;
        this.notes = notes;
        this.image = image;
        this.status = status;
        this.createdAt = createdAt;
    }

    // Getter and setter for recordId (Firestore document ID)
    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    // Getter and setter for other fields
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public String getVetName() {
        return vetName;
    }

    public void setVetName(String vetName) {
        this.vetName = vetName;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}

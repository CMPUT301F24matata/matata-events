package com.example.matata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Organizer extends User {
    private String organizerID;
    private Facility facility;

    public Organizer(String name, String email, String phoneNumber, String organizerID) {
        super(name, email, phoneNumber);
        this.organizerID = organizerID;
    }

    // Method to create a facility
    public void createFacility(String name, String description) {
        facility = new Facility(name, description);
    }

    // Method to update facility details
    public void updateFacility(String name, String description) {
        if (facility != null) {
            facility.setName(name);
            facility.setDescription(description);
        } else {
            System.out.println("No facility created yet.");
        }
    }

    public Facility getFacility() {
        return facility;
    }


    public String getOrganizerID() {
        return organizerID;
    }
    public void setOrganizerID(String organizerID) {
        this.organizerID = organizerID;
    }
}



package com.example.matata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Organizer {

    private List<Facility> myFacilities = new ArrayList<>();

    private String name;

    public Organizer (String name) {
        this.name = name;
    }

    public String getName () {
        return this.name;
    }

    public void setName (String name) {
        this.name = name;
    }

    private Facility getFacilityByName (String facilityName) {
        Facility target = null;
        for (Facility facility : myFacilities) {
            if (facility.getName().equals(facilityName)) {
                target = facility;
            }
        }

        return target;
    }

    public List<Facility> getFacilities (Administrator administrator) {
        return myFacilities;
    }

    public void createFacility (String name, String description) {
        Facility target = getFacilityByName(name);
        if (target == null) {
            myFacilities.add(new Facility(name, description));
        } else {
            System.out.println("Facility name already exist");
        }
    }

    public void deleteFacility (Facility facility) {
        if (!myFacilities.remove(facility)) {
            System.out.println("Facility not found");
        }
    }

    public void updateFacility (Facility facility, String newName, String newDescription) {
        if (myFacilities.contains(facility)){
            facility.setName(newName, this);
            facility.setDescription(newDescription, this);
        } else {
            System.out.println("Facility not found");
        }
    }


}



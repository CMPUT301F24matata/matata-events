package com.example.matata;

public class Facility {
    private String name;
    private String description;

    public Facility(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name, Organizer organizer) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description, Organizer organizer) {
        this.description = description;
    }
}
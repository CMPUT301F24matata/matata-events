package com.example.matata;

import java.io.Serializable;

public class Event implements Serializable {
    private String title;
    private String date;
    private String time;
    private String location;
    private String description;

    // Constructor
    public Event(String title, String date, String time, String location, String description) {
        this.title = title;
        this.date = date;
        this.time = time;
        this.location = location;
        this.description = description;
    }

    /**
     * Getters for the Event class
     * @return
     */
    public String getTitle() {
        return title;
    }
    public String getDate() {
        return date;
    }
    public String getTime() {
        return time;
    }
    public String getLocation() {
        return location;
    }
    public String getDescription() {
        return description;
    }

    /**
     * Setters for the Event class
     * @param title
     */
    public void setTitle(String title) {
        this.title = title;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public void setTime(String time) {
        this.time = time;
    }
    public void setLocation(String location) {
        this.location = location;
    }
    public void setDescription(String description) {
        this.description = description;
    }
}

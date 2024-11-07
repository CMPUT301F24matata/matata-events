package com.example.matata;

import java.io.Serializable;

public class Event implements Serializable {
    private String title;
    private String date;
    private int capacity;
    private String time;
    private String location;
    private String description;
    private String Eventid;
    private String Organizerid;
    private int waitlistLimit;

    // Constructor
    public Event(String title, String date, String time, String location, String description, int capacity,String Eventid, String Organizerid, int weightlistSize) {
        this.title = title;
        this.Eventid=Eventid;
        this.date = date;
        this.time = time;
        this.capacity=capacity;
        this.location = location;
        this.description = description;
        this.Organizerid = Organizerid;
        this.waitlistLimit = weightlistSize;
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
    public String getEventid() {
        return Eventid;
    }
    public int getCapacity() {return capacity;}
    public String getLocation() {
        return location;
    }
    public String getDescription() {
        return description;
    }
    public String getOrganizerid() {return Organizerid;}
    public int getWaitlistLimit() {return waitlistLimit;}

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
    public void setCapacity(int capacity){this.capacity=capacity;}
    public void setTime(String time) {
        this.time = time;
    }
    public void setLocation(String location) {
        this.location = location;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setEventid(String eventid) {
        Eventid = eventid;
    }
    public void setOrganizerid(String organizerid) {Organizerid = organizerid;}
    public void setWaitlistLimit(int waitlistLimit) {this.waitlistLimit = waitlistLimit;}
}

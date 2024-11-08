package com.example.matata;

import java.io.Serializable;

/**
 * Event class represents an event with details such as title, date, time, location,
 * description, capacity, event ID, organizer ID, and a waitlist limit. This class
 * implements Serializable to allow instances to be passed between activities.
 *
 * Outstanding issues: Field names are inconsistent in capitalization (e.g., Eventid
 * and Organizerid) which could lead to confusion. Additionally, no validation is
 * provided for inputs (e.g., capacity cannot be negative).
 */
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

    /**
     * Constructs an Event object with the specified details.
     *
     * @param title the title of the event
     * @param date the date of the event
     * @param time the time of the event
     * @param location the location where the event will be held
     * @param description a brief description of the event
     * @param capacity the maximum number of attendees for the event
     * @param Eventid the unique identifier for the event
     * @param Organizerid the unique identifier for the event organizer
     * @param weightlistSize the maximum number of people allowed on the waitlist
     */
    public Event(String title, String date, String time, String location, String description, int capacity, String Eventid, String Organizerid, int weightlistSize) {
        this.title = title;
        this.Eventid = Eventid;
        this.date = date;
        this.time = time;
        this.capacity = capacity;
        this.location = location;
        this.description = description;
        this.Organizerid = Organizerid;
        this.waitlistLimit = weightlistSize;
    }

    /**
     * Gets the title of the event.
     *
     * @return the title of the event
     */
    public String getTitle() {
        return title;
    }

    /**
     * Gets the date of the event.
     *
     * @return the date of the event
     */
    public String getDate() {
        return date;
    }

    /**
     * Gets the time of the event.
     *
     * @return the time of the event
     */
    public String getTime() {
        return time;
    }

    /**
     * Gets the unique identifier for the event.
     *
     * @return the event ID
     */
    public String getEventid() {
        return Eventid;
    }

    /**
     * Gets the capacity of the event.
     *
     * @return the maximum number of attendees allowed
     */
    public int getCapacity() {
        return capacity;
    }

    /**
     * Gets the location of the event.
     *
     * @return the location of the event
     */
    public String getLocation() {
        return location;
    }

    /**
     * Gets the description of the event.
     *
     * @return a brief description of the event
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets the organizer ID for the event.
     *
     * @return the organizer's unique identifier
     */
    public String getOrganizerid() {
        return Organizerid;
    }

    /**
     * Gets the waitlist limit for the event.
     *
     * @return the maximum number of people allowed on the waitlist
     */
    public int getWaitlistLimit() {
        return waitlistLimit;
    }

    /**
     * Sets the title of the event.
     *
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Sets the date of the event.
     *
     * @param date the date to set
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * Sets the capacity of the event.
     *
     * @param capacity the capacity to set
     */
    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    /**
     * Sets the time of the event.
     *
     * @param time the time to set
     */
    public void setTime(String time) {
        this.time = time;
    }

    /**
     * Sets the location of the event.
     *
     * @param location the location to set
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * Sets the description of the event.
     *
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Sets the unique identifier for the event.
     *
     * @param eventid the event ID to set
     */
    public void setEventid(String eventid) {
        Eventid = eventid;
    }

    /**
     * Sets the unique identifier for the organizer.
     *
     * @param organizerid the organizer ID to set
     */
    public void setOrganizerid(String organizerid) {
        Organizerid = organizerid;
    }

    /**
     * Sets the waitlist limit for the event.
     *
     * @param waitlistLimit the maximum number of people allowed on the waitlist
     */
    public void setWaitlistLimit(int waitlistLimit) {
        this.waitlistLimit = waitlistLimit;
    }
}

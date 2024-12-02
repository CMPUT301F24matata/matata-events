package com.example.matata;

import java.io.Serializable;

/**
 * The {@code Event} class represents an event with various attributes such as title, date, time,
 * location, description, capacity, unique identifiers for the event and its organizer, waitlist limit,
 * and a geolocation requirement flag. This class implements {@link Serializable} to allow instances
 * to be passed between activities in Android applications.
 *
 * <h2>Key Features:</h2>
 * <ul>
 *     <li>Stores essential event details such as title, date, time, location, and description.</li>
 *     <li>Includes unique identifiers for the event and the organizer for database integration.</li>
 *     <li>Implements getter and setter methods for all fields, allowing for easy manipulation of event data.</li>
 *     <li>Supports serialization for seamless transfer of event objects between Android activities.</li>
 * </ul>
 *
 * <h2>Usage:</h2>
 * <pre>
 * // Creating an event object
 * Event event = new Event(
 *     "Music Concert",
 *     "12/25/2024",
 *     "19:00",
 *     "Central Park",
 *     "An evening of live music and entertainment.",
 *     500,
 *     "event123",
 *     "organizer456",
 *     50,
 *     true
 * );
 *
 * // Accessing event details
 * String title = event.getTitle();
 * int capacity = event.getCapacity();
 * </pre>
 *
 * <h2>Limitations:</h2>
 * <ul>
 *     <li>No validation is enforced on fields, such as ensuring that capacity is non-negative.</li>
 *     <li>Field names are inconsistently capitalized (e.g., {@code Eventid} and {@code Organizerid}).</li>
 *     <li>The class does not include methods for formatting or displaying event details.</li>
 * </ul>
 */
public class Event implements Serializable {

    /**
     * The title of the event.
     */
    private String title;

    /**
     * The date of the event.
     */
    private String date;

    /**
     * The maximum capacity of attendees for the event.
     */
    private int capacity;

    /**
     * The time the event is scheduled to start.
     */
    private String time;

    /**
     * The location where the event will be held.
     */
    private String location;

    /**
     * A brief description of the event.
     */
    private String description;

    /**
     * The unique identifier for the event.
     */
    private String Eventid;

    /**
     * The unique identifier for the organizer of the event.
     */
    private String Organizerid;

    /**
     * The maximum number of people allowed on the waitlist for the event.
     */
    private int waitlistLimit;

    /**
     * Whether the event requires geolocation
     */
    private Boolean geoRequirement;


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
    public Event(String title, String date, String time, String location, String description, int capacity, String Eventid, String Organizerid, int weightlistSize, boolean geoRequirement) {
        this.title = title;
        this.Eventid = Eventid;
        this.date = date;
        this.time = time;
        this.capacity = capacity;
        this.location = location;
        this.description = description;

        this.Organizerid = Organizerid;
        this.waitlistLimit = weightlistSize;
        this.geoRequirement = geoRequirement;
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
     * Gets the geolocation requirement for the event.
     *
     * @return whether the event require geolocation
     */
    public boolean getGeoRequirement() {
        return geoRequirement;
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

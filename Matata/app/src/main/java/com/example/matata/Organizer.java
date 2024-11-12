package com.example.matata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Organizer class represents an organizer user with the ability to manage a facility.
 * This class extends the User class by adding an organizer ID and methods for creating and updating
 * a facility. Each Organizer can create or update a single Facility, which stores details
 * about the facility they manage.
 *
 * Outstanding issues: The class currently supports only one Facility per Organizer.
 * Additional logic may be required if an organizer should manage multiple facilities.
 */
public class Organizer extends User {

    /**
     * The unique identifier for the organizer of the event.
     */
    private String organizerID;

    /**
     * The Facility object containing details about the facility associated with the event.
     */
    private Facility facility;


    /**
     * Constructs an Organizer with the specified details.
     *
     * @param name        the name of the organizer
     * @param email       the email address of the organizer
     * @param phoneNumber the phone number of the organizer
     * @param organizerID the unique identifier for the organizer
     */
    public Organizer(String name, String email, String phoneNumber, String organizerID) {
        super(name, email, phoneNumber);
        this.organizerID = organizerID;
    }

    /**
     * Creates a new Facility with the given name and description and assigns it to this Organizer.
     *
     * @param name        the name of the facility
     * @param description a brief description of the facility
     */
    public void createFacility(String name, String description) {
        facility = new Facility(name, description);
    }

    /**
     * Updates the details of the existing Facility if one exists. If no facility has been created,
     * it outputs a message indicating that no facility exists to update.
     *
     * @param name        the new name of the facility
     * @param description the new description of the facility
     */
    public void updateFacility(String name, String description) {
        if (facility != null) {
            facility.setName(name);
            facility.setDescription(description);
        } else {
            System.out.println("No facility created yet.");
        }
    }

    /**
     * Gets the Facility managed by this Organizer.
     *
     * @return the Facility object managed by this Organizer, or null if none exists
     */
    public Facility getFacility() {
        return facility;
    }

    /**
     * Gets the unique organizer ID of this Organizer.
     *
     * @return the organizer ID
     */
    public String getOrganizerID() {
        return organizerID;
    }

    /**
     * Sets the unique organizer ID for this Organizer.
     *
     * @param organizerID the new organizer ID to set
     */
    public void setOrganizerID(String organizerID) {
        this.organizerID = organizerID;
    }
}

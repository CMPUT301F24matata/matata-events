package com.example.matata;

/**
 * Entrant class represents an individual entrant with personal information including
 * name, phone number, and email. This class provides constructors to initialize an entrant's
 * details, along with getters and setters for each field.
 *
 * Outstanding issues: This class does not include validation for name, phone number,
 * or email, so any class utilizing Entrant should ensure these fields are valid if necessary.
 */
public class Entrant {

    /**
     * String representing the name of the user.
     */
    private String name;

    /**
     * String representing the phone number of the user.
     */
    private String phoneNumber;

    /**
     * String representing the email address of the user.
     */
    private String email;


    /**
     * Constructs an Entrant object with the specified name, phone number, and email.
     *
     * @param name the name of the entrant
     * @param phoneNumber the phone number of the entrant
     * @param email the email address of the entrant
     */
    public Entrant(String name, String phoneNumber, String email) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }

    /**
     * Gets the name of the entrant.
     *
     * @return the name of the entrant
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the entrant.
     *
     * @param name the name to set for the entrant
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the phone number of the entrant.
     *
     * @return the phone number of the entrant
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Sets the phone number of the entrant.
     *
     * @param phoneNumber the phone number to set for the entrant
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * Gets the email address of the entrant.
     *
     * @return the email address of the entrant
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email address of the entrant.
     *
     * @param email the email address to set for the entrant
     */
    public void setEmail(String email) {
        this.email = email;
    }
}

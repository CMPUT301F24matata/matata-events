package com.example.matata;

/**
 * The User class serves as a superclass for user-related roles, such as Entrant and Organizer.
 * It holds common attributes like name, email, and phone number, which are shared across all user types.
 * This class provides basic getters and setters, as well as a method to display user information.
 *
 * Outstanding issues: None identified; however, this class could potentially include additional
 * common functionality for future subclasses if needed.
 */
public class User {

    /** The name of the user */
    private String name;

    /** The email address of the user */
    private String email;

    /** The phone number of the user */
    private String phoneNumber;

    /**
     * Constructs a User instance with the specified name, email, and phone number.
     *
     * @param name        the name of the user
     * @param email       the email address of the user
     * @param phoneNumber the phone number of the user
     */
    public User(String name, String email, String phoneNumber) {
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    /**
     * Gets the name of the user.
     *
     * @return the user's name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the user.
     *
     * @param name the name to set for the user
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the email address of the user.
     *
     * @return the user's email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email address of the user.
     *
     * @param email the email address to set for the user
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the phone number of the user.
     *
     * @return the user's phone number
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Sets the phone number of the user.
     *
     * @param phoneNumber the phone number to set for the user
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * Displays the user's information (name, email, and phone number) in the console.
     */
    public void displayUserInfo() {
        System.out.println("Name: " + name);
        System.out.println("Email: " + email);
        System.out.println("Phone: " + phoneNumber);
    }
}

package com.example.matata;

/**
 * The {@code Entrant} class represents an individual participant in an event or activity.
 * It encapsulates the personal information of an entrant, including their name, phone number,
 * and email address. This class provides constructors for initialization and includes
 * getter and setter methods for accessing and modifying the entrant's details.
 * <p>
 * <h2>Key Features:</h2>
 * <ul>
 *     <li>Encapsulation of personal details (name, phone number, email).</li>
 *     <li>Easy access and modification of entrant data through getters and setters.</li>
 *     <li>Flexible construction to initialize entrant data upon creation.</li>
 * </ul>
 *
 * <h2>Usage:</h2>
 * <pre>
 * // Create an entrant
 * Entrant entrant = new Entrant("John Doe", "123-456-7890", "john.doe@example.com");
 *
 * // Access entrant details
 * String name = entrant.getName();
 * String phone = entrant.getPhoneNumber();
 * String email = entrant.getEmail();
 *
 * // Modify entrant details
 * entrant.setName("Jane Doe");
 * entrant.setPhoneNumber("987-654-3210");
 * entrant.setEmail("jane.doe@example.com");
 * </pre>
 *
 * <h2>Limitations:</h2>
 * <ul>
 *     <li>No validation for the format of name, phone number, or email address.</li>
 *     <li>It is recommended to implement validation logic in classes utilizing {@code Entrant}.</li>
 * </ul>
 *
 * <h2>Thread Safety:</h2>
 * This class is not thread-safe. If multiple threads access or modify instances of this class concurrently,
 * external synchronization is required.
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

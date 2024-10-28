package com.example.matata;

import java.lang.reflect.Constructor;

public class Entrant {
    private String name;
    private String phoneNumber;
    private String email;

    /**
     * Constructors for an Entrant (personal info)
     * @param name
     * @param phoneNumber
     * @param email
     */
    public Entrant(String name, String phoneNumber, String email) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }

    /**
     * Setters and Getters
     * @return
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

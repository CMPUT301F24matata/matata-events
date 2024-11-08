package com.example.matata;

/**
 * Facility class represents a facility with a name and description. This class provides
 * methods to set and retrieve these details, which can be used to display facility information
 * within the application.
 *
 * Outstanding issues: The class does not include validation for name or description, so
 * any class utilizing Facility should ensure these fields are valid if necessary.
 */
public class Facility {

    private String name;
    private String description;

    /**
     * Constructs a Facility object with the specified name and description.
     *
     * @param name the name of the facility
     * @param description a brief description of the facility
     */
    public Facility(String name, String description) {
        this.name = name;
        this.description = description;
    }

    /**
     * Gets the name of the facility.
     *
     * @return the facility name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the facility.
     *
     * @param name the name to set for the facility
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the description of the facility.
     *
     * @return the facility description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the facility.
     *
     * @param description the description to set for the facility
     */
    public void setDescription(String description) {
        this.description = description;
    }
}

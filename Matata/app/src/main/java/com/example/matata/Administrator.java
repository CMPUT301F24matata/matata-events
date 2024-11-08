package com.example.matata;

/**
 * The Administrator class represents an administrator user within the Matata application.
 * It contains basic information about the administrator, specifically their name.
 *
 * Outstanding issues:
 * - Expand functionality to include additional administrator attributes, such as contact details.
 * - Consider implementing additional methods for handling administrator-specific operations.
 */
public class Administrator {

    private String name;

    /**
     * Constructs an Administrator with the specified name.
     *
     * @param name the name of the administrator
     */
    public Administrator(String name) {
        this.name = name;
    }

    /**
     * Returns the name of the administrator.
     *
     * @return the name of the administrator
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the administrator.
     *
     * @param name the new name of the administrator
     */
    public void setName(String name) {
        this.name = name;
    }
}

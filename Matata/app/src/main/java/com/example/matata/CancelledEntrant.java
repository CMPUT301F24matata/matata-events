package com.example.matata;

/**
 * Represents a cancelled entrant with their name and reason for cancellation.
 */
public class CancelledEntrant {
    /**
     * Name of the cancelled entrant.
     */
    private String name;

    /**
     * Reason for the cancellation.
     */
    private String reason;

    /**
     * Constructs a new {@code CancelledEntrant} with the specified name and cancellation reason.
     *
     * @param name   The name of the entrant.
     * @param reason The reason for the cancellation.
     */
    public CancelledEntrant(String name, String reason) {
        this.name = name;
        this.reason = reason;
    }

    /**
     * Gets the name of the cancelled entrant.
     *
     * @return The name of the entrant.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the reason for the cancellation.
     *
     * @return The cancellation reason.
     */
    public String getReason() {
        return reason;
    }
}

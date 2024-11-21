package com.example.matata;

public class CancelledEntrant {
    private String name;
    private String reason;

    public CancelledEntrant(String name, String reason) {
        this.name = name;
        this.reason = reason;
    }

    public String getName() {
        return name;
    }

    public String getReason() {
        return reason;
    }
}


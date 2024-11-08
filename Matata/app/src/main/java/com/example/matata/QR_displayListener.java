package com.example.matata;

import java.sql.Date;
import java.sql.Time;

/**
 * QR_displayListener is an interface for handling data read from a QR code.
 * Implementations of this interface should define the behavior for processing
 * event details such as title, description, poster ID, date, and time when a QR code is scanned.
 *
 * Outstanding issues: This interface currently uses java.sql.Date and java.sql.Time types,
 * which may not be ideal for Android development. Consider using java.util.Date or other
 * Android-compatible date/time types if further compatibility issues arise.
 */
public interface QR_displayListener {

    /**
     * Called when data is read from a QR code.
     *
     * @param Title       the title of the event
     * @param description the description of the event
     * @param Poster_id   the ID of the poster associated with the event
     * @param date        the date of the event
     * @param time        the time of the event
     */
    void onDataRead(String Title, String description, String Poster_id, Date date, Time time);
}

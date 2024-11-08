package com.example.matata;

/**
 * TimePickerListener is an interface that provides a callback for when a time is selected
 * from a TimePickerFragment. Classes that implement this interface can define specific actions
 * to be taken when a time is chosen, such as updating a UI element or saving the time data.
 *
 * Outstanding issues: This interface assumes that time is selected in 24-hour format,
 * as it uses hour and minute integers as parameters.
 */
public interface TimePickerListener {

    /**
     * Called when a time is selected from the TimePicker.
     *
     * @param hourOfDay the selected hour of the day (in 24-hour format)
     * @param minute    the selected minute
     */
    void onTimeSelected(int hourOfDay, int minute);
}

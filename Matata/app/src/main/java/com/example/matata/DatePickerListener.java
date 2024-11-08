package com.example.matata;

/**
 * DatePickerListener is an interface that provides a callback for handling date selections
 * in the DatePickerFragment. Classes that implement this interface must define the
 * onDateSelected method to receive the selected date and take any necessary actions.
 *
 * Outstanding issues: This interface does not include validation or checks, so any
 * class implementing it should handle null values or invalid date inputs if needed.
 */
public interface DatePickerListener {

    /**
     * Called when a date is selected in the DatePickerFragment.
     *
     * @param year  the selected year
     * @param month the selected month (0-based, i.e., January is 0)
     * @param date  the selected day of the month
     */
    void onDateSelected(int year, int month, int date);
}

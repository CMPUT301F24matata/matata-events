package com.example.matata;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

/**
 * DatePickerFragment is a DialogFragment that displays a date picker dialog to allow users
 * to select a date. Once a date is selected, it communicates the chosen date back to the
 * calling activity, which should implement the DatePickerListener interface provided
 * in a different class file.
 *
 * Outstanding issues: The host activity must implement DatePickerListener
 * to handle the selected date. No checks are in place to handle cases where the
 * activity does not implement this interface, potentially causing runtime issues.
 */
public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    /**
     * Initializes and returns the date picker dialog, setting it to the current date.
     *
     * @param savedInstanceState the saved instance state with data about the dialog instance (if any)
     * @return a DatePickerDialog initialized to the current date
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int date = c.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(getActivity(), this, year, month, date);
    }

    /**
     * Attaches the fragment to the context. This method is part of the fragment lifecycle.
     *
     * @param context the context to attach the fragment to, typically the host activity
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    /**
     * Callback method that is triggered when the user selects a date in the date picker.
     * Sends the selected date to the host activity if it implements the DatePickerListener interface.
     *
     * @param datePicker the DatePicker view used to select the date
     * @param year the selected year
     * @param month the selected month (0-based, i.e., January is 0)
     * @param date the selected day of the month
     */
    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int date) {
        if (getActivity() instanceof DatePickerListener) {
            ((DatePickerListener) getActivity()).onDateSelected(year, month, date);
        }
    }
}

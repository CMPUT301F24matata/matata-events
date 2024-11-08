package com.example.matata;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

/**
 * TimePickerFragment displays a time picker dialog that allows users to select a time.
 * Once a time is selected, it communicates the chosen time back to the hosting activity
 * through the TimePickerListener interface.
 *
 * Outstanding issues: None identified; however, it assumes that the hosting activity
 * implements TimePickerListener. Additional error handling may be required if this is not the case.
 */
public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    /**
     * Creates the time picker dialog with the current time as the default selection.
     *
     * @param savedInstanceState if the fragment is being re-created from a previous saved state
     * @return a new instance of TimePickerDialog set to the current time
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    /**
     * Called when the fragment is attached to the activity.
     *
     * @param context the context of the calling activity
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    /**
     * Called when the user selects a time in the time picker.
     * This method passes the selected hour and minute to the hosting activity
     * if it implements the TimePickerListener interface.
     *
     * @param timePicker the TimePicker view associated with this listener
     * @param hourOfDay  the selected hour
     * @param minute     the selected minute
     */
    @Override
    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
        if (getActivity() instanceof TimePickerListener) {
            ((TimePickerListener) getActivity()).onTimeSelected(hourOfDay, minute);
        }
    }
}

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
 * The {@code DatePickerFragment} class represents a dialog fragment that displays a date picker dialog.
 * <p>
 * This class allows users to select a date from a calendar-style picker. The selected date is
 * communicated back to the host activity, which is expected to implement the {@code DatePickerListener}
 * interface. This makes it suitable for scenarios requiring date input, such as event scheduling
 * or setting reminders.
 * <p>
 * The class is designed to be lightweight and reusable. It sets the default date to the current
 * system date when initialized.
 * <p>
 * <strong>Key Features:</strong>
 * <ul>
 *     <li>Displays a customizable date picker dialog.</li>
 *     <li>Handles date selection through a callback interface.</li>
 *     <li>Ensures default initialization to the current date.</li>
 * </ul>
 *
 * <h2>Usage:</h2>
 * <pre>
 * public class MainActivity extends AppCompatActivity implements DatePickerListener {
 *     ...
 *     void showDatePicker() {
 *         DialogFragment datePickerFragment = new DatePickerFragment();
 *         datePickerFragment.show(getSupportFragmentManager(), "datePicker");
 *     }
 *
 *     {@literal @Override}
 *     public void onDateSelected(int year, int month, int date) {
 *         // Handle the selected date
 *     }
 * }
 * </pre>
 *
 * <h2>Outstanding Issues:</h2>
 * <ul>
 *     <li>No validation is performed to ensure the host activity implements the {@code DatePickerListener} interface,
 *     which could lead to a runtime exception.</li>
 *     <li>The selected date is directly passed to the host activity without additional formatting or validation.</li>
 * </ul>
 */
public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    /**
     * Initializes and returns a {@link DatePickerDialog} set to the current date.
     * <p>
     * This method retrieves the current system date using {@link Calendar} and uses it to set
     * the initial values for the date picker.
     *
     * @param savedInstanceState A {@link Bundle} containing the saved state of the dialog fragment, if any.
     * @return A {@link DatePickerDialog} instance initialized with the current date.
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
     * Attaches the fragment to a context.
     * <p>
     * This method is part of the fragment lifecycle and is invoked when the fragment is associated with
     * a host activity or another context. It can be overridden to perform additional setup operations,
     * such as verifying the host context.
     *
     * @param context The {@link Context} to which the fragment is being attached, typically the host activity.
     * @throws IllegalStateException If the host activity does not implement {@link DatePickerListener}.
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    /**
     * Callback method invoked when the user selects a date in the date picker.
     * <p>
     * This method passes the selected year, month, and day of the month to the host activity through the
     * {@link DatePickerListener#onDateSelected(int, int, int)} callback. The month value is zero-based,
     * meaning January is represented as 0, February as 1, and so on.
     * <p>
     * Example:
     * <pre>
     * {@literal @Override}
     * public void onDateSelected(int year, int month, int date) {
     *     String selectedDate = String.format("%02d/%02d/%04d", date, month + 1, year);
     *     textView.setText(selectedDate);
     * }
     * </pre>
     *
     * @param datePicker The {@link DatePicker} view used to select the date.
     * @param year       The year selected by the user.
     * @param month      The zero-based month selected by the user.
     * @param date       The day of the month selected by the user.
     */
    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int date) {
        if (getActivity() instanceof DatePickerListener) {
            ((DatePickerListener) getActivity()).onDateSelected(year, month, date);
        }
    }
}

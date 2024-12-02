package com.example.matata;

/**
 * The {@code DatePickerListener} interface defines a callback mechanism for handling date selections
 * made in the {@link DatePickerFragment}. This interface is intended to be implemented by activities
 * or other components that need to process the date selected by the user in a date picker dialog.
 * <p>
 * By implementing this interface, a class can handle the user's date selection and take appropriate
 * actions, such as displaying the selected date in the UI or storing it for further use.
 * <p>
 * <strong>Key Features:</strong>
 * <ul>
 *     <li>Provides a simple and reusable mechanism for handling date selections.</li>
 *     <li>Ensures separation of concerns by delegating the responsibility of processing the selected date
 *     to the implementing class.</li>
 * </ul>
 *
 * <h2>Usage:</h2>
 * <pre>
 * public class MainActivity extends AppCompatActivity implements DatePickerListener {
 *
 *     // Show the DatePickerFragment
 *     void showDatePicker() {
 *         DatePickerFragment datePickerFragment = new DatePickerFragment();
 *         datePickerFragment.show(getSupportFragmentManager(), "datePicker");
 *     }
 *
 *     {@literal @Override}
 *     public void onDateSelected(int year, int month, int date) {
 *         // Handle the selected date, e.g., display it in a TextView
 *         String formattedDate = String.format("%02d/%02d/%04d", date, month + 1, year);
 *         textView.setText(formattedDate);
 *     }
 * }
 * </pre>
 *
 * <h2>Outstanding Issues:</h2>
 * <ul>
 *     <li>This interface does not include validation or error handling. Implementing classes should
 *     ensure that selected dates are valid and handle null or out-of-range values if necessary.</li>
 *     <li>No default implementation is provided, so each class must fully implement the callback method.</li>
 * </ul>
 */
public interface DatePickerListener {

    /**
     * Called when a date is selected in the {@link DatePickerFragment}.
     * <p>
     * This method provides the selected year, month, and day of the month to the implementing class.
     * The month parameter is zero-based, meaning January is represented as 0, February as 1, and so on.
     * The implementing class is responsible for processing the selected date as needed.
     * <p>
     * Example:
     * <pre>
     * {@literal @Override}
     * public void onDateSelected(int year, int month, int date) {
     *     String formattedDate = String.format("%02d/%02d/%04d", date, month + 1, year);
     *     textView.setText(formattedDate);
     * }
     * </pre>
     *
     * @param year  The year selected by the user (e.g., 2024).
     * @param month The zero-based month selected by the user (e.g., 0 for January, 11 for December).
     * @param date  The day of the month selected by the user (e.g., 1-31, depending on the month and year).
     */
    void onDateSelected(int year, int month, int date);
}

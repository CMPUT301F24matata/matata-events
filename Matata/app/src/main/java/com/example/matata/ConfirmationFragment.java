package com.example.matata;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

/**
 * The {@code ConfirmationFragment} class represents a confirmation dialog for the user.
 * <p>
 * This dialog is displayed when the user attempts to leave an activity without saving changes, specifically
 * when working with event creation or modification screens. The dialog prompts the user with the following message:
 * <blockquote>
 * "Your event hasn't been saved yet. Are you sure you want to go back?"
 * </blockquote>
 * The dialog provides two options:
 * <ul>
 *     <li>Yes: Terminates the current activity, discarding any unsaved changes.</li>
 *     <li>No: Dismisses the dialog and returns the user to the current screen.</li>
 * </ul>
 * <p>
 * The dialog is useful for preventing accidental data loss due to navigation away from the current screen.
 *
 * <h2>Potential Future Enhancements:</h2>
 * <ul>
 *     <li>Provide an option to save changes before leaving the activity.</li>
 *     <li>Explicitly handle unsaved data and allow recovery after the user decides to leave.</li>
 *     <li>Customize the dialog's message and behavior based on the context of the invoking activity.</li>
 * </ul>
 */
public class ConfirmationFragment extends DialogFragment {

    /**
     * Called to create the dialog box for confirming the user's decision to leave without saving changes.
     * <p>
     * The dialog contains:
     * <ul>
     *     <li>A message: "Your event hasn't been saved yet. Are you sure you want to go back?"</li>
     *     <li>A "Yes" button: Closes the current activity when clicked.</li>
     *     <li>A "No" button: Dismisses the dialog without any action.</li>
     * </ul>
     *
     * @param savedInstanceState A {@link Bundle} containing the saved state of the dialog fragment, if any.
     * @return An {@link AlertDialog} instance representing the confirmation dialog.
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Build the alert dialog with a message and two options: "Yes" and "No".
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(requireActivity());
        dialogBuilder.setMessage("Your event hasn't been saved yet. Are you sure you want to go back?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    /**
                     * Handles the "Yes" button click event.
                     * <p>
                     * When the user selects "Yes," the current activity is terminated using {@link androidx.fragment.app.FragmentActivity#finish()}.
                     * This discards any unsaved changes and navigates the user back to the previous screen.
                     *
                     * @param dialogInterface The interface for interacting with the dialog.
                     * @param i An identifier for the button that was clicked.
                     */
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        requireActivity().finish(); // Ends the activity, discarding any unsaved changes
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    /**
                     * Handles the "No" button click event.
                     * <p>
                     * When the user selects "No," the dialog is dismissed, allowing the user to remain on the current screen
                     * and continue editing or interacting with the activity.
                     *
                     * @param dialogInterface The interface for interacting with the dialog.
                     * @param i An identifier for the button that was clicked.
                     */
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss(); // Closes the dialog box without further action
                    }
                });
        return dialogBuilder.create(); // Return the fully built dialog
    }
}

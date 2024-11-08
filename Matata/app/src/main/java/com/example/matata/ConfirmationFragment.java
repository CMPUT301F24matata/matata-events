package com.example.matata;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

/**
 * ConfirmationFragment class is a DialogFragment that provides a dialog prompt to the user,
 * asking if they want to discard their unsaved changes and go back. This dialog includes
 * "Yes" and "No" options. Selecting "Yes" will close the current activity, while "No" will
 * simply dismiss the dialog.
 *
 * Outstanding issues: This dialog only closes the activity on "Yes". Consider adding logic
 * to explicitly handle any unsaved data if required by the application's flow in the future.
 */
public class ConfirmationFragment extends DialogFragment {

    /**
     * Creates and returns the dialog box to confirm the user's decision to discard unsaved changes.
     *
     * @param savedInstanceState the saved instance state containing data about the dialog instance (if any)
     * @return the AlertDialog created with "Yes" and "No" buttons for user confirmation
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Build the alert dialog with a message and two options: "Yes" and "No".
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(requireActivity());
        dialogBuilder.setMessage("Your event hasn't been saved yet. Are you sure you want to go back?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    /**
                     * Handles the "Yes" button click event, closing the activity when the button is pressed.
                     *
                     * @param dialogInterface the dialog interface
                     * @param i the identifier of the button pressed
                     */
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        requireActivity().finish(); // Ends the activity, discarding any unsaved changes
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    /**
                     * Handles the "No" button click event, dismissing the dialog when the button is pressed.
                     *
                     * @param dialogInterface the dialog interface
                     * @param i the identifier of the button pressed
                     */
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss(); // Closes the dialog box without further action
                    }
                });
        return dialogBuilder.create(); // Return the fully built dialog
    }
}

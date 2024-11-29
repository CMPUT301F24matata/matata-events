package com.example.matata;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.util.List;

/**
 * ClearPendingListFragment class is a DialogFragment that provides a dialog prompt to the user, asking if they want to
 * cancel all entrants who haven't accepted the invitation yet. This class creates a dialog with "Yes" and "No" options.
 * Selecting "Yes" will close the current activity, and selecting "No" will dismiss the dialog.
 *
 * Outstanding issues: The action for "Yes" currently only finishes the activity. Consider adding functionality to actually
 * clear pending invitations if this is required in future development.
 */
public class ClearPendingListFragment extends DialogFragment {


    /**
     * Creates and returns the dialog box to confirm the user's action to cancel pending entrants.
     *
     * @param savedInstanceState the saved instance state containing data about the dialog instance (if any)
     * @return the AlertDialog created with "Yes" and "No" buttons for user confirmation
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Build the alert dialog with a message and two options: "Yes" and "No".
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(requireActivity());
        dialogBuilder.setMessage("Do you want to cancel all entrants that haven't accepted the invitation yet?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    /**
                     * Handles the "Yes" button click event, closing the activity when the button is pressed.
                     *
                     * @param dialogInterface the dialog interface
                     * @param i the identifier of the button pressed
                     */
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        requireActivity().finish(); // Ends the activity
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

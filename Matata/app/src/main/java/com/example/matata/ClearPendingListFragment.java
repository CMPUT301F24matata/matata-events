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
 * The {@code ClearPendingListFragment} class is a {@link DialogFragment} that displays a confirmation dialog to the user.
 * <p>
 * This dialog prompts the user with the following message:
 * <blockquote>
 * "Do you want to cancel all entrants that haven't accepted the invitation yet?"
 * </blockquote>
 * It provides two options:
 * <ul>
 *     <li>Yes: Terminates the current activity.</li>
 *     <li>No: Dismisses the dialog without performing any action.</li>
 * </ul>
 * <p>
 * This class can be extended in the future to include functionality for clearing pending entrants or performing
 * other actions associated with the "Yes" button.
 */
public class ClearPendingListFragment extends DialogFragment {


    /**
     * Called to create the dialog box that prompts the user for confirmation.
     * <p>
     * The dialog contains:
     * <ul>
     *     <li>A message: "Do you want to cancel all entrants that haven't accepted the invitation yet?"</li>
     *     <li>A "Yes" button: Closes the activity when clicked.</li>
     *     <li>A "No" button: Dismisses the dialog without further action.</li>
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
        dialogBuilder.setMessage("Do you want to cancel all entrants that haven't accepted the invitation yet?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    /**
                     * Handles the "Yes" button click event.
                     * <p>
                     * When the user selects "Yes," the current activity is terminated using {@link androidx.fragment.app.FragmentActivity#finish()}.
                     *
                     * @param dialogInterface The interface for interacting with the dialog.
                     * @param i An identifier for the button that was clicked.
                     */
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        requireActivity().finish(); // Ends the activity
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    /**
                     * Handles the "No" button click event.
                     * <p>
                     * When the user selects "No," the dialog is dismissed without any further action.
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

package com.example.matata;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

/**
 * The {@code FrozenDialogFragment} class represents a dialog fragment that informs the user
 * about their account's frozen status. This dialog provides an option to exit the application.
 *
 * <h2>Features:</h2>
 * <ul>
 *     <li>Displays an alert dialog with a title and message explaining the account's frozen status.</li>
 *     <li>Includes a single action button to exit the application.</li>
 * </ul>
 *
 * <h2>Use Case:</h2>
 * This dialog is typically displayed when the user's account is restricted or suspended,
 * prompting them to contact an administrator for further assistance.
 */
public class FrozenDialogFragment extends DialogFragment {

    /**
     * Called to create the dialog's user interface.
     * Constructs an alert dialog with a title, message, and an "Exit App" button.
     *
     * <h3>Dialog Structure:</h3>
     * <ul>
     *     <li><b>Title:</b> "Account Frozen"</li>
     *     <li><b>Message:</b> Provides information about the frozen status and instructions to contact the admin.</li>
     *     <li><b>Action Button:</b> Allows the user to exit the app completely.</li>
     * </ul>
     *
     * @param savedInstanceState A {@code Bundle} containing the dialog's previously saved state, if any.
     * @return The constructed dialog to be displayed to the user.
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Account Frozen")
                .setMessage("Your account is frozen. Please contact the admin for assistance.")
                .setPositiveButton("Exit App", (dialog, which) -> {
                    requireActivity().finishAffinity();
                });

        return builder.create();
    }
}

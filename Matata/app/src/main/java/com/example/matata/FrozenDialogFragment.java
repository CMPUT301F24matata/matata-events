package com.example.matata;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

/**
 * FrozenDialogFragment displays a dialog informing the user that their account is frozen.
 * The dialog provides an option for the user to exit the application.
 */
public class FrozenDialogFragment extends DialogFragment {

    /**
     * Called to create the dialog's user interface.
     * Displays an alert dialog with a message about the account's frozen status and a button to exit the app.
     *
     * @param savedInstanceState Bundle containing the dialog's previously saved state, if any.
     * @return The dialog to be displayed.
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

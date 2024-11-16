package com.example.matata;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

public class FrozenDialogFragment extends DialogFragment {

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

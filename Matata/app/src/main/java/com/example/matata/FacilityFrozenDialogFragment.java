package com.example.matata;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * FacilityFrozenDialogFragment displays a dialog to notify the user that their facility is frozen.
 * The user is provided with an option to reset the facility's information.
 * This dialog integrates with Firebase Firestore to update facility data.
 */
public class FacilityFrozenDialogFragment extends DialogFragment {

    /**
     * Instance of FirebaseFirestore for database operations.
     */
    private FirebaseFirestore db;

    /**
     * Called to initialize and inflate the dialog's view.
     * Sets up the UI components, such as the reset facility button and informational text.
     *
     * @param inflater           The LayoutInflater used to inflate the dialog's layout.
     * @param container          The parent container for the dialog's view, if applicable.
     * @param savedInstanceState Bundle containing the dialog's previously saved state, if any.
     * @return The root view of the dialog's layout.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_facility_frozen, container, false);

        Button createFacilityButton = view.findViewById(R.id.create_facility_button);
        Button exitButton = view.findViewById(R.id.exit_button);
        TextView contact_admin_text = view.findViewById(R.id.contact_admin_text);

        contact_admin_text.setText("Please contact the admin for further assistance to unfreeze your facility.");

        String facilityId = Settings.Secure.getString(requireContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        createFacilityButton.setOnClickListener(v -> {
            DocumentReference facilityRef = db.collection("FACILITY_PROFILES").document(facilityId);
            facilityRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    facilityRef.update("name", "");
                    facilityRef.update("address", "");
                    facilityRef.update("email", "");
                    facilityRef.update("notifications", false);
                    facilityRef.update("profileUri", "");
                    facilityRef.update("capacity", "");
                    facilityRef.update("contact", "");
                    facilityRef.update("owner", "");
                }
            });
            dismiss();
        });

        exitButton.setOnClickListener(v -> {
            if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                getParentFragmentManager().popBackStack();
            } else {
                requireActivity().finish();
            }
        });

        return view;
    }

    /**
     * Called when the dialog is starting.
     * Ensures that the dialog is not cancellable by the user, either by tapping outside the dialog
     * or using the back button.
     */
    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null) {
            getDialog().setCanceledOnTouchOutside(false);
            getDialog().setCancelable(false);
        }
    }
}

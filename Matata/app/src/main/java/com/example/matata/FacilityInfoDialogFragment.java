// FacilityInfoDialogFragment.java
package com.example.matata;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

/**
 * FacilityInfoDialogFragment displays facility information in a dialog fragment.
 * It provides details such as name, address, capacity, contact, email, owner,
 * and notifications status.
 */
public class FacilityInfoDialogFragment extends DialogFragment {

    /**
     * Creates a new instance of FacilityInfoDialogFragment, passing in the required
     * facility information as arguments.
     *
     * @param name               The name of the facility.
     * @param address            The address of the facility.
     * @param capacity           The capacity of the facility.
     * @param contact            Contact information for the facility.
     * @param email              Email address for the facility.
     * @param owner              The name of the facility owner.
     * @param notificationsEnabled Boolean indicating if notifications are enabled for the facility.
     * @return A new instance of FacilityInfoDialogFragment with the provided data.
     */
    public static FacilityInfoDialogFragment newInstance(String name, String address, String capacity,
                                                         String contact, String email, String owner,
                                                         boolean notificationsEnabled) {
        FacilityInfoDialogFragment fragment = new FacilityInfoDialogFragment();

        // Pass data to the fragment
        Bundle args = new Bundle();
        args.putString("name", name);
        args.putString("address", address);
        args.putString("capacity", capacity);
        args.putString("contact", contact);
        args.putString("email", email);
        args.putString("owner", owner);
        args.putBoolean("notificationsEnabled", notificationsEnabled);
        fragment.setArguments(args);

        return fragment;
    }

    /**
     * Inflates the fragment's view with facility information.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate views.
     * @param container          The parent view that this fragment's UI should attach to.
     * @param savedInstanceState A Bundle with the fragment's saved state, if any.
     * @return The View for the fragment's UI.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_facility_info, container, false);

        TextView facilityName = view.findViewById(R.id.facilityName);
        TextView facilityAddress = view.findViewById(R.id.facilityAddress);
        TextView facilityCapacity = view.findViewById(R.id.facilityCapacity);
        TextView facilityContact = view.findViewById(R.id.facilityContact);
        TextView facilityEmail = view.findViewById(R.id.facilityEmail);
        TextView facilityOwner = view.findViewById(R.id.facilityOwner);
        TextView notificationsStatus = view.findViewById(R.id.notificationsStatus);

        if (getArguments() != null) {
            facilityName.setText(getArguments().getString("name"));
            facilityAddress.setText(getArguments().getString("address"));
            facilityCapacity.setText(getArguments().getString("capacity"));
            facilityContact.setText(getArguments().getString("contact"));
            facilityEmail.setText(getArguments().getString("email"));
            facilityOwner.setText(getArguments().getString("owner"));
            notificationsStatus.setText(getArguments().getBoolean("notificationsEnabled") ? "Enabled" : "Disabled");
        }

        view.findViewById(R.id.confirmButton).setOnClickListener(v -> dismiss());

        return view;
    }

    /**
     * Creates the dialog for the fragment, allowing dismissal when clicking outside.
     *
     * @param savedInstanceState A Bundle with the fragment's saved state, if any.
     * @return The created Dialog instance.
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setCanceledOnTouchOutside(true); // Allows clicking outside to dismiss
        return dialog;
    }
}

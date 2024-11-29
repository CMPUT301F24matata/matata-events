package com.example.matata;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

public class NotificationDialogFragment extends DialogFragment {

    private Spinner groupSpinner;
    private Button sendNotificationButton;
    private String[] groups = {"Waitlist", "Pending", "Accepted", "Rejected"};
    private ArrayAdapter<String> adapter;
    private String uid;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Create an AlertDialog Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Set a custom layout for the dialog
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_notification_dialog, null);

        // Set up the Spinner
        groupSpinner = view.findViewById(R.id.group_spinner);
        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, groups);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        groupSpinner.setAdapter(adapter);

        // Get the unique ID from the intent
        if (getArguments() != null) {
            uid = getArguments().getString("uid");
        }

        // Set up the Send Notification button
        sendNotificationButton = view.findViewById(R.id.send_notification_button);
        sendNotificationButton.setOnClickListener(v -> {

            // Handle sending notification logic
            String selectedGroup = groupSpinner.getSelectedItem().toString();
            Notifications notifications = new Notifications();
            if (selectedGroup.equals("Waitlist")) {
                notifications.sendNotification(this.getActivity(), "Waitlist-" + uid, "Waitlist Notification", "You have a new waitlist entry");
            } else if (selectedGroup.equals("Pending")) {
                // Handle pending notification logic
                String fillerText = "Filler text, I'm working on this";
            }

            // Send notification based on selected group
            Toast.makeText(getActivity(), "Sending notification to: " + selectedGroup, Toast.LENGTH_SHORT).show();
            dismiss(); // Close the dialog
        });

        // Set the dialog view and title
        builder.setView(view)
                .setTitle("Notification Manager");

        // Return the created dialog
        return builder.create();
    }

    // Ensure that the background activity remains visible
    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            Window window = dialog.getWindow();
            if (window != null) {
                window.setDimAmount(0.4f); // Adjust background dim level
            }
        }
    }
}
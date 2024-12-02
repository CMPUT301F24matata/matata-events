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
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

public class NotificationDialogFragment extends DialogFragment {

    private Spinner groupSpinner;
    private Button sendNotificationButton;
    private String[] groups = {"Waitlist", "Pending", "Accepted", "Rejected"};
    private String[] adminGroups = {"All"};
    private ArrayAdapter<String> adapter;
    private String uid;
    private String admin;
    private EditText title;
    private EditText message;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Create an AlertDialog Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Set a custom layout for the dialog
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_notification_dialog, null);

        // Get the unique ID from the intent
        if (getArguments() != null) {
            admin = getArguments().getString("admin");
            if ("true".equals(admin)) {
                uid = getArguments().getString("uid");
            } else {
                uid = null;
            }
        }

        // Set up the Spinner
        if (admin.equals("false")) {
            groupSpinner = view.findViewById(R.id.group_spinner);
            adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, groups);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            groupSpinner.setAdapter(adapter);
        } else {
            groupSpinner = view.findViewById(R.id.group_spinner);
            adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, adminGroups);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            groupSpinner.setAdapter(adapter);
        }

        title = view.findViewById(R.id.title_EditText);
        message = view.findViewById(R.id.message_EditText);

        // Set up the Send Notification button
        sendNotificationButton = view.findViewById(R.id.send_notification_button);
        sendNotificationButton.setOnClickListener(v -> {
            String titleString = title.getText().toString().trim();
            String messageString = message.getText().toString();

            if (titleString.isEmpty() || messageString.isEmpty()) {
                Toast.makeText(getActivity(), "Please complete all fields", Toast.LENGTH_SHORT).show();
            } else {
                // Handle sending notification logic
                String selectedGroup = groupSpinner.getSelectedItem().toString();
                Notifications notifications = new Notifications();
                if (selectedGroup.equals("Waitlist")) {
                    notifications.sendNotification(this.getActivity(), "Waitlist-" + uid, titleString, messageString);
                } else if (selectedGroup.equals("Pending")) {
                    notifications.sendNotification(this.getActivity(), "Pending-" + uid, titleString, messageString);
                } else if (selectedGroup.equals("Accepted")) {
                    notifications.sendNotification(this.getActivity(), "Accepted-" + uid, titleString, messageString);
                } else if (selectedGroup.equals("Rejected")) {
                    notifications.sendNotification(this.getActivity(), "Rejected-" + uid, titleString, messageString);
                } else {
                    notifications.sendNotification(this.getActivity(), "All", titleString, messageString);
                }

                // Send notification based on selected group
                Toast.makeText(getActivity(), "Sending notification to: " + selectedGroup, Toast.LENGTH_SHORT).show();
                dismiss(); // Close the dialog
            }
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
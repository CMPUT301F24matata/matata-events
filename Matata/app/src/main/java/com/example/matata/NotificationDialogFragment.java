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

/**
 * The {@code NotificationDialogFragment} class represents a dialog fragment that allows
 * users (both admins and organizers) to send notifications to specific user groups
 * associated with an event. The dialog provides input fields for the notification title
 * and message, along with a dropdown for selecting the recipient group.
 *
 * <h2>Features:</h2>
 * <ul>
 *     <li>Displays a dialog with fields for entering notification details.</li>
 *     <li>Supports group-based notifications (e.g., "Waitlist," "Pending," "Accepted," "Rejected").</li>
 *     <li>Admin users have the option to send notifications to all users.</li>
 *     <li>Validates user input to ensure all fields are completed before sending the notification.</li>
 * </ul>
 *
 * <h2>Class Responsibilities:</h2>
 * <ul>
 *     <li>Handles UI elements such as input fields, dropdown selection, and a button for sending notifications.</li>
 *     <li>Communicates with the {@link Notifications} class to send the notification to Firebase topics.</li>
 *     <li>Dismisses the dialog upon successfully sending a notification.</li>
 * </ul>
 *
 * <h2>Dependencies:</h2>
 * This class requires:
 * <ul>
 *     <li>A layout file named {@code fragment_notification_dialog} with appropriate UI elements.</li>
 *     <li>The {@link Notifications} class for sending notifications to Firebase Cloud Messaging (FCM) topics.</li>
 * </ul>
 */
public class NotificationDialogFragment extends DialogFragment {

    /**
     * Dropdown spinner for selecting the recipient group.
     */
    private Spinner groupSpinner;

    /**
     * Button for sending the notification.
     */
    private Button sendNotificationButton;

    /**
     * Array of recipient groups available for non-admin users.
     */
    private String[] groups = {"Waitlist", "Pending", "Accepted", "Rejected"};

    /**
     * Array of recipient groups available for admin users.
     */
    private String[] adminGroups = {"All"};

    /**
     * Adapter for populating the {@link Spinner} with group options.
     */
    private ArrayAdapter<String> adapter;

    /**
     * Unique identifier for the event.
     */
    private String uid;

    /**
     * Indicates whether the user is an admin ("true") or not ("false").
     */
    private String admin;

    /**
     * Input field for entering the notification title.
     */
    private EditText title;

    /**
     * Input field for entering the notification message.
     */
    private EditText message;

    /**
     * Called to create the dialog interface.
     * Initializes UI components, validates user input, and handles notification sending logic.
     *
     * @param savedInstanceState Bundle containing the dialog's previously saved state, if any.
     * @return A dialog instance displaying the notification management interface.
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Create an AlertDialog Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Set a custom layout for the dialog
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_notification_dialog, null);

        // Get the unique ID from the intent
        if (getArguments() != null) {
            uid = getArguments().getString("uid");
            admin = getArguments().getString("admin");
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
                Log.d("sending notification", "group: " + selectedGroup);
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
                    Log.d("sending notification", "sent to all");
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

    /**
     * Called when the dialog is starting.
     * Ensures that the background activity remains visible with a dimmed background effect.
     */
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
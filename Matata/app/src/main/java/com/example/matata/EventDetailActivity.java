package com.example.matata;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.google.android.gms.tasks.OnCompleteListener;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;

import java.util.List;
import java.util.Map;

/**
 * The {@code EventDetailActivity} class provides detailed information about a selected event
 * and allows users to join or withdraw from the event's waitlist. Users can also view a list
 * of cancelled entrants. This activity interacts with Firebase Firestore for retrieving and
 * updating event-related data.
 *
 * <h2>Features:</h2>
 * <ul>
 *     <li>Displays event details including title, date, time, location, and description.</li>
 *     <li>Provides functionality to join or withdraw from an event's waitlist.</li>
 *     <li>Manages user subscriptions to Firebase Cloud Messaging (FCM) topics for notifications.</li>
 *     <li>Fetches and displays a list of cancelled entrants for the event.</li>
 * </ul>
 *
 * <h2>Usage:</h2>
 * <pre>
 * // Start EventDetailActivity with intent data
 * Intent intent = new Intent(context, EventDetailActivity.class);
 * intent.putExtra("title", "Sample Event");
 * intent.putExtra("date", "2024-12-25");
 * intent.putExtra("time", "19:00");
 * intent.putExtra("location", "Central Park");
 * intent.putExtra("description", "An evening of live music.");
 * context.startActivity(intent);
 * </pre>
 *
 * <h2>Outstanding Issues:</h2>
 * <ul>
 *     <li>The class assumes that the {@code Event_id} is valid and already exists in Firestore.</li>
 *     <li>No error handling for scenarios where the event or user profile data is missing or corrupted.</li>
 *     <li>Limited customization of dialog messages and actions.</li>
 * </ul>
 */
public class EventDetailActivity extends AppCompatActivity {

    /**
     * Instance of FirebaseFirestore used for database operations.
     */
    private FirebaseFirestore db;

    /**
     * The unique identifier for the event. Default value is "sample_event_id".
     */
    private String Event_id = "sample_event_id";

    /**
     * The unique identifier for the user.
     */
    private String USER_ID;

    /**
     * RecyclerView for displaying a list of cancelled entrants.
     */
    private RecyclerView recyclerView;

    /**
     * Button to fetch and display cancelled entrants.
     */
    private Button viewCancelledButton;

    /**
     * Initializes the EventDetailActivity, sets up views, and retrieves event details from intent.
     *
     * @param savedInstanceState if the activity is being re-initialized, this contains the data it most recently supplied
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_event_details);

        db = FirebaseFirestore.getInstance();
        USER_ID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        // Retrieve views
        TextView titleTextView = findViewById(R.id.ViewEventTitle);
        TextView dateTextView = findViewById(R.id.ViewEventDate);
        TextView timeTextView = findViewById(R.id.ViewEventTime);
        TextView locationTextView = findViewById(R.id.ViewEventLoc);
        TextView descriptionTextView = findViewById(R.id.ViewEventDesc);
        ImageView posterImageView = findViewById(R.id.poster_pic_Display);
        ImageView backButton = findViewById(R.id.go_back_view_event);
        Button joinWaitlistButton = findViewById(R.id.join_waitlist_button);


        // Retrieve data from Intent
        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        String date = intent.getStringExtra("date");
        String time = intent.getStringExtra("time");
        String location = intent.getStringExtra("location");
        String description = intent.getStringExtra("description");

        // Set data to views
        titleTextView.setText(title);
        descriptionTextView.setText(description);
        dateTextView.setText(date);
        timeTextView.setText(time);
        locationTextView.setText(location);

        // Back button listener to close the activity
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();  // Closes EventDetailActivity and returns to previous activity
            }
        });

        DocumentReference eventRef = db.collection("EVENT_PROFILES").document(Event_id);
        DocumentReference entrantRef = db.collection("USER_PROFILES").document(USER_ID);

        // Check if user is already on the waitlist and update button text accordingly
        eventRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot eventSnapshot) {
                if (eventSnapshot.exists()) {
                    List<DocumentReference> waitlist = (List<DocumentReference>) eventSnapshot.get("waitlist");
                    if (waitlist != null && waitlist.contains(entrantRef)) {
                        joinWaitlistButton.setText("Withdraw");
                    } else {
                        joinWaitlistButton.setText("Join Waitlist");
                    }
                }
            }
        });

        // Join or withdraw from waitlist when button is clicked
        joinWaitlistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (joinWaitlistButton.getText().toString().equals("Withdraw")) {
                    withdrawDialog();
                } else {
                    eventRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    int limit = document.getLong("WaitlistLimit").intValue();
                                    List<DocumentReference> waitlist = (List<DocumentReference>) document.get("waitlist");
                                    if (limit == -1 || waitlist.size() < limit) {
                                        confirmationDialog();
                                    } else {
                                        Toast.makeText(EventDetailActivity.this, "Waitlist Full", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        }
                    });
                }

            }

            /**
             * Displays a dialog to confirm the user wants to withdraw from the waitlist.
             */
            private void withdrawDialog() {
                AlertDialog.Builder builder = new AlertDialog.Builder(EventDetailActivity.this);
                builder.setCancelable(true);
                builder.setMessage("Confirm to withdraw from waitlist");
                builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        exitWaitlist();
                    }
                });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }


            /**
             * Removes the user from the waitlist in Firestore.
             */
            private void exitWaitlist() {
                eventRef.update("waitlist", FieldValue.arrayRemove(entrantRef))
                        .addOnSuccessListener(aVoid -> {
                            Log.d("Firebase", "Entrant removed from waitlist successfully");
                            joinWaitlistButton.setText("Join Waitlist");

                            // Unsubscribe from topic
                            unsubscribeFromTopic(Event_id);
                        })
                        .addOnFailureListener(e -> Log.e("Firebase", "Error removing entrant from waitlist", e));
            }

            /**
             * Displays a confirmation dialog for joining the waitlist.
             */
            private void confirmationDialog() {
                AlertDialog.Builder builder = new AlertDialog.Builder(EventDetailActivity.this);
                builder.setCancelable(true);
                builder.setMessage("Confirm to join waitlist");
                builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Uncomment the following line to enable waitlist addition
                        addToWaitList();

                        Log.d("Subcribe", "Subscribe method called");
                        // Subscribe to topic
                        subscribeToTopic(Event_id);
                    }
                });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }

            /**
             * Adds the user to the waitlist in Firestore if space is available.
             */
            private void addToWaitList() {
                db.runTransaction((Transaction.Function<Void>) transaction -> {
                    DocumentSnapshot eventSnapshot = transaction.get(eventRef);
                    List<DocumentReference> waitlist = (List<DocumentReference>) eventSnapshot.get("waitlist");

                    if (waitlist == null) {
                        waitlist = new ArrayList<>();
                    }
                    waitlist.add(entrantRef);
                    transaction.update(eventRef, "waitlist", waitlist);
                    return null;
                }).addOnSuccessListener(aVoid -> {
                    Log.d("Added", "Entrant added to waitlist successfully!!!");
                    joinWaitlistButton.setText("Withdraw");

                }).addOnFailureListener(e -> Log.e("Firebase", "Error adding entrant to waitlist", e));
            }
        });
    }

    /**
     * Subscribes the user to a specific topic.
     *
     * @param topic   The topic to subscribe to (e.g., event waitlist ID).
     */
    private void subscribeToTopic(String topic) {
        Log.d("Subcribe", "Subscribe method called");
        FirebaseMessaging.getInstance().subscribeToTopic(topic)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("Subscribe", "Subscribed to topic: " + topic);
                    } else {
                        Log.e("Failure", "Failed to subscribe to topic: " + topic, task.getException());
                    }
                });
    }

    /**
     * Unsubscribes the user from a specific topic.
     *
     * @param topic   The topic to unsubscribe from (e.g., event waitlist ID).
     */
    private void unsubscribeFromTopic(String topic) {
        Log.d("Unsubcribe", "Unsubscribe method called");
        FirebaseMessaging.getInstance().unsubscribeFromTopic(topic)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("Unsubscribe", "Unsubscribed from topic: " + topic);
                    } else {
                        Log.e("Failure", "Failed to unsubscribe from topic: " + topic, task.getException());
                    }
                });
    }
}

package com.example.matata;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class ManageAllEventsActivity extends AppCompatActivity {

    private LinearLayout eventDetails;
    private ImageView btnToggleDetails;
    private boolean isDropdownVisible = false;
    private TextView btnViewEvent, btnFreezeEvent, btnDeleteEvent;
    private ImageView btnBack;
    private boolean isEventFrozen = false;
    private FirebaseFirestore db;
    private LinearLayout eventsContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manage_all_events);

        db = FirebaseFirestore.getInstance();

        eventsContainer = findViewById(R.id.eventsContainer);
        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> finish());

        fetchFromFirestore();

    }

    private void fetchFromFirestore() {
        eventsContainer.removeAllViews();

        db.collection("EVENT_PROFILES")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        for (QueryDocumentSnapshot document : querySnapshot) {
                            String eventTitle = document.getString("Title");
                            String eventId = document.getId();
                            String orgId = document.getString("OrganizerID");
                            String orgName = document.getString("OrganiserName");
                            String eventCreationDate = document.getString("CreationDate");
                            String eventStatus = document.getString("Status");

                            if (eventTitle != null)  {
                                addEventItem(eventTitle, eventId, orgName, eventCreationDate, eventStatus, orgId);
                            }
                        }
                    } else {
                        showToast("Failed to load events");
                    }
                });
    }

    private void addEventItem(String title, String eventId, String organizerName, String creationDate, String status, String orgId) {
        // Inflate the event_item layout
        View eventView = getLayoutInflater().inflate(R.layout.event_item, eventsContainer, false);

        TextView eventTitle = eventView.findViewById(R.id.event_title);
        TextView eventStatus = eventView.findViewById(R.id.event_status);
        TextView organizerNameView = eventView.findViewById(R.id.organizer_name);
        TextView creationDateView = eventView.findViewById(R.id.event_creation_date);
        LinearLayout eventDetails = eventView.findViewById(R.id.event_details);
        ImageView toggleButton = eventView.findViewById(R.id.add_event);

        Log.d("ManageAllEvents", "event_details found: " + (eventDetails != null));

        // Set data
        eventTitle.setText(title);
        organizerNameView.setText(organizerName);
        creationDateView.setText(creationDate);

        if (status != null && status.equalsIgnoreCase("active")) {
            eventStatus.setText("Active");
            eventStatus.setBackgroundResource(R.drawable.status_badge_background);
        } else {
            eventStatus.setText("Inactive");
            eventStatus.setBackgroundResource(R.drawable.status_badge_background2);
        }

        // Initially hide event details
        eventDetails.setVisibility(View.GONE);
        toggleButton.setImageResource(R.drawable.ic_add); // Set initial icon to '+'

        // Toggle the visibility of event details when the '+' or '-' button is clicked
        toggleButton.setOnClickListener(view -> {
            if (eventDetails.getVisibility() == View.VISIBLE) {
                eventDetails.setVisibility(View.GONE);
                toggleButton.setImageResource(R.drawable.ic_add); // Change icon back to '+'
            } else {
                eventDetails.setVisibility(View.VISIBLE);
                toggleButton.setImageResource(R.drawable.ic_remove); // Change icon to '-'
            }
        });

        // Handle "View Event" button click
        TextView btnViewEvent = eventView.findViewById(R.id.btn_view_event);
        btnViewEvent.setOnClickListener(v -> {
            Intent intent = new Intent(ManageAllEventsActivity.this, ViewEvent.class);
            intent.putExtra("EVENT_ID", eventId);
            intent.putExtra("IS_ADMIN_VIEW", true);
            intent.putExtra("ORG_ID", orgId);
            startActivity(intent);
        });

        // Add the populated eventView to the eventsContainer
        eventsContainer.addView(eventView);
    }


    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void toggleDropdown() {
        if (isDropdownVisible) {
            // Hide the dropdown and change icon to +
            eventDetails.setVisibility(View.GONE);
            btnToggleDetails.setImageResource(R.drawable.ic_add); // Set + icon
        } else {
            // Show the dropdown and change icon to -
            eventDetails.setVisibility(View.VISIBLE);
            btnToggleDetails.setImageResource(R.drawable.ic_remove); // Set - icon
        }
        isDropdownVisible = !isDropdownVisible;
    }


}

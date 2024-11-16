package com.example.matata;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class ManageAllEventsActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private LinearLayout eventsContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manage_all_events);

        db = FirebaseFirestore.getInstance();

        eventsContainer = findViewById(R.id.eventsContainer);
        ImageView btnBack = findViewById(R.id.btnBack);
        ImageView iconDashboard = findViewById(R.id.icon_dashboard);
        ImageView iconUsers = findViewById(R.id.icon_users);
        ImageView iconReports = findViewById(R.id.icon_reports);
        ImageView iconNotifications = findViewById(R.id.icon_notifications);
        ImageView iconSettings = findViewById(R.id.icon_settings);

        btnBack.setOnClickListener(v -> finish());

        iconDashboard.setOnClickListener(v -> {
            Intent intent = new Intent(ManageAllEventsActivity.this, AdminView.class);
            startActivity(intent);
        });

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
                        showToast();
                    }
                });
    }

    private void addEventItem(String title, String eventId, String organizerName, String creationDate, String status, String orgId) {

        View eventView = getLayoutInflater().inflate(R.layout.event_item, eventsContainer, false);

        TextView eventTitle = eventView.findViewById(R.id.event_title);
        TextView eventStatus = eventView.findViewById(R.id.event_status);
        TextView organizerNameView = eventView.findViewById(R.id.organizer_name);
        TextView creationDateView = eventView.findViewById(R.id.event_creation_date);
        LinearLayout eventDetails = eventView.findViewById(R.id.event_details);
        ImageView toggleButton = eventView.findViewById(R.id.add_event);
        TextView btnFreezeEvent = eventView.findViewById(R.id.btn_freeze_event);
        TextView btnViewEvent = eventView.findViewById(R.id.btn_view_event);
        TextView btnDeleteEvent = eventView.findViewById(R.id.btn_delete_event);
        TextView acceptedCount = eventView.findViewById(R.id.accepted_count);
        TextView pendingCount = eventView.findViewById(R.id.pending_count);
        TextView waitlistCount = eventView.findViewById(R.id.waitlist_count);
        TextView rejectedCount = eventView.findViewById(R.id.rejected_count);

        DocumentReference eventRef = db.collection("EVENT_PROFILES").document(eventId);

        eventRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Object acceptedField = documentSnapshot.get("accepted");
                if (acceptedField instanceof List<?>) {
                    List<?> acceptedList = (List<?>) acceptedField;
                    acceptedCount.setText(String.valueOf(acceptedList.size()));
                } else {
                    acceptedCount.setText("0");
                    Log.e("StatsActivity", "'accepted' field is not a List");
                }

                Object pendingField = documentSnapshot.get("pending");
                if (pendingField instanceof List<?>) {
                    List<?> pendingList = (List<?>) pendingField;
                    pendingCount.setText(String.valueOf(pendingList.size()));
                } else {
                    pendingCount.setText("0");
                    Log.e("StatsActivity", "'pending' field is not a List");
                }

                Object rejectedField = documentSnapshot.get("rejected");
                if (rejectedField instanceof List<?>) {
                    List<?> rejectedList = (List<?>) rejectedField;
                    rejectedCount.setText(String.valueOf(rejectedList.size()));
                } else {
                    rejectedCount.setText("0");
                    Log.e("StatsActivity", "'rejected' field is not a List");
                }

                Object waitlistField = documentSnapshot.get("waitlist");
                if (waitlistField instanceof List<?>) {
                    List<?> waitlistList = (List<?>) waitlistField;
                    waitlistCount.setText(String.valueOf(waitlistList.size()));
                } else {
                    waitlistCount.setText("0");
                    Log.e("StatsActivity", "'waitlist' field is not a List");
                }

            } else {
                Log.e("StatsActivity", "Document does not exist");
                acceptedCount.setText("0");
            }
        }).addOnFailureListener(e -> {
            Log.e("StatsActivity", "Failed to fetch accepted count: " + e.getMessage());
            acceptedCount.setText("0");
        });

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

        eventDetails.setVisibility(View.GONE);
        toggleButton.setImageResource(R.drawable.ic_add);

        toggleButton.setOnClickListener(view -> {
            if (eventDetails.getVisibility() == View.VISIBLE) {
                eventDetails.setVisibility(View.GONE);
                toggleButton.setImageResource(R.drawable.ic_add);
            } else {
                eventDetails.setVisibility(View.VISIBLE);
                toggleButton.setImageResource(R.drawable.ic_remove);
            }
        });

        btnViewEvent.setOnClickListener(v -> {
            Intent intent = new Intent(ManageAllEventsActivity.this, ViewEvent.class);
            intent.putExtra("EVENT_ID", eventId);
            intent.putExtra("IS_ADMIN_VIEW", true);
            intent.putExtra("ORG_ID", orgId);
            startActivity(intent);
        });

        btnFreezeEvent.setOnClickListener(v -> {
            if (eventStatus.getText().toString().equalsIgnoreCase("Active")) {
                eventStatus.setText("Inactive");
                eventStatus.setBackgroundResource(R.drawable.status_badge_background2);
                btnFreezeEvent.setText("Frozen");
                updateEventStatusInDatabase(eventId, "Inactive");
            } else {
                eventStatus.setText("Active");
                eventStatus.setBackgroundResource(R.drawable.status_badge_background);
                btnFreezeEvent.setText("Freeze");
                updateEventStatusInDatabase(eventId, "Active");
            }
        });

        btnDeleteEvent.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Confirm Deletion")
                    .setMessage("Are you sure you want to delete this event?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        deleteEventFromDatabase(eventId, orgId);
                        eventsContainer.removeView(eventView);
                    })
                    .setNegativeButton("No", null)
                    .show();
        });

        eventsContainer.addView(eventView);
    }

    private void deleteEventFromDatabase(String eventId, String orgId) {
        db.collection("EVENT_PROFILES").document(eventId)
                .delete()
                .addOnSuccessListener(aVoid -> Log.d("ManageAllEvents", "Event deleted successfully"))
                .addOnFailureListener(e -> Log.e("ManageAllEvents", "Error deleting event", e));

        db.collection("ORGANIZER_PROFILES").document(orgId)
                .update("organizedEvents", FieldValue.arrayRemove(eventId))
                .addOnSuccessListener(aVoid -> Log.d("ManageAllEvents", "Event reference removed from organizer profile"))
                .addOnFailureListener(e -> Log.e("ManageAllEvents", "Error removing event reference", e));
    }

    private void updateEventStatusInDatabase(String eventId, String newStatus) {
        db.collection("EVENT_PROFILES").document(eventId)
                .update("Status", newStatus)
                .addOnSuccessListener(aVoid -> Log.d("ManageAllEvents", "Event status updated successfully"))
                .addOnFailureListener(e -> Log.e("ManageAllEvents", "Failed to update event status", e));
    }

    private void showToast() {
        Toast.makeText(this, "Failed to load events", Toast.LENGTH_SHORT).show();
    }

}
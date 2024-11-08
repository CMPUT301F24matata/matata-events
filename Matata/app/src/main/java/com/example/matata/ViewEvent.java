package com.example.matata;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Transaction;

import java.util.ArrayList;
import java.util.List;

/**
 * The ViewEvent class represents the activity to view details of a specific event.
 * It allows users to join or withdraw from a waitlist, respond to invitations, and view event details.
 * The activity also includes options for event organizers to manage entrant status.
 * Note: Geolocation consent is required to join the waitlist.
 */
public class ViewEvent extends AppCompatActivity {

    // Event details encoded in Base64
    private String argbase64;

    // Base64 string for event poster image
    private String posterBase64;

    // UI elements
    private ImageView goBack;
    private ImageView poster;
    private TextView title;
    TextView capacity;
    TextView desc;
    TextView time;
    TextView date;
    TextView location;

    // Firestore instance
    private FirebaseFirestore db;

    // Floating action button for showing QR code
    private FloatingActionButton showQR;

    // Button to join waitlist
    Button waitlistBtn;

    // Unique device ID for identifying the user
    private String USER_ID;

    // Firestore references to event and entrant documents
    private DocumentReference eventRef;
    private DocumentReference entrantRef;

    // Button for draw event management
    private Button drawBtn;

    // Unique identifier for the event
    private String uid;


    /**
     * Called when the activity is first created.
     * Sets up initial configurations, initializes Firebase and UI elements,
     * and loads the event details based on the unique event ID.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down then this Bundle contains the data it most
     *                           recently supplied in onSaveInstanceState.
     *                           Otherwise, it is null.
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_event_details);

        db = FirebaseFirestore.getInstance();

        // Initialize UI elements
        goBack = findViewById(R.id.go_back_view_event);
        title = findViewById(R.id.ViewEventTitle);
        capacity = findViewById(R.id.ViewEventCapacity);
        desc = findViewById(R.id.ViewEventDesc);
        time = findViewById(R.id.ViewEventTime);
        date = findViewById(R.id.ViewEventDate);
        location = findViewById(R.id.ViewEventLoc);
        poster = findViewById(R.id.poster_pic_Display);
        showQR = findViewById(R.id.show_QR);
        waitlistBtn = findViewById(R.id.join_waitlist_button);
        drawBtn = findViewById(R.id.draw_button);

        USER_ID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        Intent intent = getIntent();
        uid = intent.getStringExtra("Unique_id");
        Log.wtf(TAG, uid);

        eventRef = db.collection("EVENT_PROFILES").document(uid);
        entrantRef = db.collection("USER_PROFILES").document(USER_ID);

        goBack.setOnClickListener(v -> finish());

        showQR.setOnClickListener(view -> {
            QR_displayFragment qrDisplayFragment = QR_displayFragment.newInstance(argbase64);
            qrDisplayFragment.show(getSupportFragmentManager(), "Show QR");
        });

        drawBtn.setOnClickListener(view -> {
            Intent intent1 = new Intent(view.getContext(), EventDraw.class);
            intent1.putExtra("Unique_id", uid);
            view.getContext().startActivity(intent1);
        });

        loadEventDetails(uid);
        refreshEntrantStatus();
    }

    /**
     * Refreshes the entrant status whenever the activity resumes.
     */
    @Override
    protected void onResume() {
        super.onResume();
        refreshEntrantStatus();
    }

    /**
     * Retrieves and updates the user's current status in the event (e.g., pending, accepted, waitlist).
     */
    public void refreshEntrantStatus() {
        eventRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    List<DocumentReference> waitlist = (List<DocumentReference>) document.get("waitlist");
                    List<DocumentReference> pending = (List<DocumentReference>) document.get("pending");
                    List<DocumentReference> accepted = (List<DocumentReference>) document.get("accepted");

                    String organizerId = document.getString("OrganizerID");

                    updateButtonVisibility(organizerId);
                    updateWaitlistButtonText(waitlist, pending, accepted);
                }
            } else {
                Log.e("Firebase", "Error fetching event details: ", task.getException());
            }
        });
        waitlistBtn.setOnClickListener(v -> {
            String joinBtnText = waitlistBtn.getText().toString();
            if (joinBtnText.equals("Pending")) {
                showInvitationDialog();
            } else if (joinBtnText.equals("Withdraw")) {
                withdrawDialog();
            } else if (joinBtnText.equals("Join Waitlist")) {
                joinWaitlist();
            }
        });
    }

    /**
     * Updates the text of waitlistBtn based on the user staus.
     */
    void updateWaitlistButtonText(List<DocumentReference> waitlist, List<DocumentReference> pending, List<DocumentReference> accepted) {
        if (pending != null && pending.contains(entrantRef)) {
            waitlistBtn.setText("Pending");
        } else if (accepted != null && accepted.contains(entrantRef)) {
            waitlistBtn.setText("Accepted");
        } else if (waitlist != null && waitlist.contains(entrantRef)) {
            waitlistBtn.setText("Withdraw");
        } else {
            waitlistBtn.setText("Join Waitlist");
        }
    }

    /**
     * Updates the visibility of buttons based on whether user is the organizer.
     */
    void updateButtonVisibility(String organizerId) {
        if (organizerId == null || !organizerId.equals(USER_ID)) {
            drawBtn.setVisibility(View.INVISIBLE);
        }else {
            waitlistBtn.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Displays a dialog for accepting or declining an invitation.
     */
    void showInvitationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ViewEvent.this);
        builder.setTitle("Invitation");
        builder.setMessage("Do you want to accept or decline the invitation?");

        builder.setPositiveButton("Accept", (dialog, which) -> {
            Intent intent = new Intent(ViewEvent.this, SignUpSheet.class);
            intent.putExtra("Unique_id", uid);
            startActivity(intent);
            waitlistBtn.setText("Accepted");
            Toast.makeText(ViewEvent.this, "Invitation Accepted", Toast.LENGTH_SHORT).show();
            addToAccepted();
        });

        builder.setNegativeButton("Decline", (dialog, which) -> {
            eventRef.update("pending", FieldValue.arrayRemove(entrantRef))
                    .addOnSuccessListener(aVoid -> {
                        Log.d("Firebase", "User declined invitation, removed from pending list");
                        waitlistBtn.setText("Join Waitlist");
                        resampleEntrant();
                        Toast.makeText(ViewEvent.this, "Invitation Declined", Toast.LENGTH_SHORT).show();
                    }).addOnFailureListener(e -> Log.e("Firebase", "Error declining invitation", e));
            addToRejected();
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Adds the user to the rejected list in Firestore.
     */
    private void addToRejected() {
        db.runTransaction((Transaction.Function<Void>) transaction -> {
            DocumentSnapshot eventSnapshot = transaction.get(eventRef);
            List<DocumentReference> rejected = (List<DocumentReference>) eventSnapshot.get("rejected");
            if (rejected == null) {
                rejected = new ArrayList<>();
            }
            rejected.add(entrantRef);
            transaction.update(eventRef, "rejected", rejected);
            return null;
        }).addOnSuccessListener(aVoid -> {
            Log.d("Firebase", "Entrant added to rejected list successfully");
            eventRef.update("pending", FieldValue.arrayRemove(entrantRef));
        }).addOnFailureListener(e -> Log.e("Firebase", "Error adding entrant to rejected list", e));
    }

    /**
     * Adds the user to the accepted list in Firestore.
     */
    private void addToAccepted() {
        db.runTransaction((Transaction.Function<Void>) transaction -> {
            DocumentSnapshot eventSnapshot = transaction.get(eventRef);
            List<DocumentReference> accepted = (List<DocumentReference>) eventSnapshot.get("accepted");
            if (accepted == null) {
                accepted = new ArrayList<>();
            }
            accepted.add(entrantRef);
            transaction.update(eventRef, "accepted", accepted);
            return null;
        }).addOnSuccessListener(aVoid -> {
            Log.d("Firebase", "Entrant added to accepted list successfully");
            eventRef.update("pending", FieldValue.arrayRemove(entrantRef));
        }).addOnFailureListener(e -> Log.e("Firebase", "Error adding entrant to accepted list", e));
    }

    /**
     * Adds the user to the waitlist after verifying capacity and user's consent for geolocation access.
     */
    private void joinWaitlist() {
        eventRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                DocumentSnapshot document = task.getResult();
                Long limitValue = document.getLong("WaitlistLimit");
                if (limitValue != null) {
                    int limit = limitValue.intValue();
                    List<DocumentReference> waitlist = (List<DocumentReference>) document.get("waitlist");
                    if (limit == -1 || (waitlist != null && waitlist.size() < limit)) {
                        confirmationDialog();
                    } else {
                        runOnUiThread(() -> Toast.makeText(ViewEvent.this, "Waitlist Full", Toast.LENGTH_SHORT).show());
                    }
                } else {
                    Log.e(TAG, "Error: WaitlistLimit field missing");
                }
            } else {
                Log.e(TAG, "No such document or error fetching document");
            }
        });
    }

    /**
     * Displays a confirmation dialog before joining the waitlist.
     */
    private void confirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ViewEvent.this);
        builder.setCancelable(true);
        builder.setMessage("Confirm to join waitlist");
        builder.setPositiveButton("Confirm", (dialog, which) -> showGeolocationWarning());
        builder.setNegativeButton(android.R.string.cancel, (dialog, which) -> {});
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Displays a warning dialog to request user's consent for geolocation access.
     */
    private void showGeolocationWarning() {
        AlertDialog.Builder geoBuilder = new AlertDialog.Builder(ViewEvent.this);
        geoBuilder.setTitle("Geolocation Required");
        geoBuilder.setMessage("To join the waitlist for this event, we need access to your location. Do you agree to share your location data?");
        geoBuilder.setPositiveButton("Accept", (dialog, which) -> addToWaitList());
        geoBuilder.setNegativeButton("Decline", (dialog, which) -> Toast.makeText(ViewEvent.this, "You must accept geolocation to join the waitlist", Toast.LENGTH_SHORT).show());
        AlertDialog geoDialog = geoBuilder.create();
        geoDialog.show();
    }

    /**
     * Adds the user to the waitlist in Firestore.
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
            Log.d("Firebase", "Entrant added to waitlist successfully");
            waitlistBtn.setText("Withdraw");
            eventRef.update("rejected", FieldValue.arrayRemove(entrantRef));
        }).addOnFailureListener(e -> Log.e("Firebase", "Error adding entrant to waitlist", e));
    }

    /**
     * Displays a dialog for confirming withdrawal from the waitlist.
     */
    private void withdrawDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ViewEvent.this);
        builder.setCancelable(true);
        builder.setMessage("Confirm to withdraw from waitlist");
        builder.setPositiveButton("Confirm", (dialog, which) -> exitWaitlist());
        builder.setNegativeButton(android.R.string.cancel, (dialog, which) -> {});
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Removes the user from the waitlist in Firestore.
     */
    private void exitWaitlist() {
        eventRef.update("waitlist", FieldValue.arrayRemove(entrantRef))
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firebase", "Entrant withdrew from waitlist successfully");
                    waitlistBtn.setText("Join Waitlist");
                }).addOnFailureListener(e -> Log.e("Firebase", "Error deleting entrant from waitlist", e));
    }

    /**
     * Loads event details from Firestore and sets them in the respective UI elements.
     *
     * @param uid The unique identifier for the event.
     */
    public void loadEventDetails(String uid) {
        DocumentReference doc = db.collection("EVENT_PROFILES").document(uid);
        doc.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String Title = documentSnapshot.getString("Title");
                Long Capacity = documentSnapshot.getLong("Capacity");
                String Description = documentSnapshot.getString("Description");
                String Time = documentSnapshot.getString("Time");
                String Date = documentSnapshot.getString("Date");
                String Location = documentSnapshot.getString("Location");

                argbase64 = documentSnapshot.getString("bitmap");
                String ImageUri = documentSnapshot.getString("Poster");
                if (!ImageUri.isEmpty()) {
                    Glide.with(this).load(ImageUri).into(poster);
                }

                Bitmap QR = decodeBase64toBmp(argbase64);

                title.setText(Title != null ? Title : "");
                capacity.setText(String.valueOf(Capacity));
                desc.setText(Description != null ? Description : "");
                time.setText(Time != null ? Time : "");
                date.setText(Date != null ? Date : "");
                location.setText(Location != null ? Location : "");
            } else {
                Toast.makeText(ViewEvent.this, "No event found", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> Toast.makeText(ViewEvent.this, "Failed to load event", Toast.LENGTH_SHORT).show());
    }

    /**
     * Decodes a Base64 encoded string to a Bitmap image.
     *
     * @param bmp64 The Base64 encoded string.
     * @return The decoded Bitmap image.
     */
    public Bitmap decodeBase64toBmp(String bmp64) {
        try {
            byte[] decodedBytes = Base64.decode(bmp64, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        } catch (IllegalArgumentException e) {
            return null;  // Return null if decoding fails
        }
    }

    /**
     * Resamples the entrant from the waitlist to the pending list when a user declines an invitation.
     */
    private void resampleEntrant() {
        db.runTransaction((Transaction.Function<Void>) transaction -> {
            DocumentSnapshot snapshot = transaction.get(eventRef);
            List<DocumentReference> waitlist = (List<DocumentReference>) snapshot.get("waitlist");
            List<DocumentReference> pending = (List<DocumentReference>) snapshot.get("pending");

            if (waitlist != null && !waitlist.isEmpty()) {
                DocumentReference newEntrant = waitlist.get(0);
                if (pending == null) pending = new ArrayList<>();
                pending.add(newEntrant);

                transaction.update(eventRef, "pending", pending);
                transaction.update(eventRef, "waitlist", FieldValue.arrayRemove(newEntrant));

                Log.d("Firebase", "New entrant added to pending list: " + newEntrant.getId());
            }
            return null;
        }).addOnSuccessListener(aVoid -> {
            Log.d("Firebase", "Successfully resampled a new entrant from waitlist");
            Toast.makeText(ViewEvent.this, "A new entrant has been selected!", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> Log.e("Firebase", "Error resampling a new entrant", e));
    }
}

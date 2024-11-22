package com.example.matata;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AlertDialogLayout;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Transaction;

import java.util.ArrayList;
import java.util.List;

/**
 * The `ViewEvent` class represents the activity for viewing detailed information about an event.
 * It provides functionality for attendees to join or withdraw from the event's waitlist, accept or decline invitations,
 * and view event details such as title, description, time, date, location, and capacity.
 * Organizers can manage event settings, including editing, deleting the event, or performing a draw.
 */
public class ViewEvent extends AppCompatActivity {

    /**
     * Event details encoded as a Base64 string.
     */
    private String argbase64;

    /**
     * Base64 encoded string representing the event poster image.
     */
    private String posterBase64;

    // UI Elements

    /**
     * ImageView for navigating back from the event details screen.
     */
    private ImageView goBack;

    /**
     * ImageView for displaying the event poster image.
     */
    private ImageView poster;

    /**
     * TextView for displaying the event title.
     */
    private TextView title;

    /**
     * TextView for displaying the event capacity.
     */
    private TextView capacity;

    /**
     * TextView for displaying the event description.
     */
    private TextView desc;

    /**
     * TextView for displaying the event time.
     */
    private TextView time;

    /**
     * TextView for displaying the event date.
     */
    private TextView date;

    /**
     * TextView for displaying the event location.
     */
    private TextView location;

    /**
     * FloatingActionButton for displaying the QR code of the event.
     */
    private FloatingActionButton showQR;

    /**
     * Button for joining or withdrawing from the event waitlist.
     */
    private Button waitlistBtn;

    /**
     * Layout button for managing event draws.
     */
    private LinearLayout drawBtn;

    /**
     * Layout button for editing event details.
     */
    private LinearLayout editEvent;

    /**
     * Layout button for deleting the event.
     */
    private LinearLayout delEvent;

    /**
     * Unique identifier for the user.
     */
    private String USER_ID;

    /**
     * Firestore instance used for database operations.
     */
    private FirebaseFirestore db;

    /**
     * Firestore reference for the event document.
     */
    private DocumentReference eventRef;

    /**
     * Firestore reference for the user document.
     */
    private DocumentReference entrantRef;

    /**
     * Unique identifier for the event.
     */
    private String uid;

    /**
     * Boolean indicating whether geolocation consent is required to join the event's waitlist.
     */
    private boolean geoRequirement;

    /**
     * Boolean indicating whether the view is accessed by an administrator.
     */
    private boolean admin_view;

    /**
     * Initializes the activity, sets up Firebase, UI components, and loads event details.
     *
     * @param savedInstanceState If the activity is being re-initialized after being shut down, this contains the data it most recently supplied.
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
        editEvent= findViewById(R.id.EditEvent);
        delEvent=findViewById(R.id.DeleteEvent);

        Intent intent = getIntent();
        admin_view = intent.getBooleanExtra("IS_ADMIN_VIEW", false);
        if (admin_view) {
            uid = intent.getStringExtra("EVENT_ID");
            USER_ID = intent.getStringExtra("ORG_ID");
        } else {
            uid = intent.getStringExtra("Unique_id");
            USER_ID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        }

        eventRef = db.collection("EVENT_PROFILES").document(uid);
        entrantRef = db.collection("USER_PROFILES").document(USER_ID);


        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    String ParentName=intent.getStringExtra("Parent");
                    if (ParentName!=null && ParentName.equals("AddEvent")) {
                        Intent intent = new Intent(ViewEvent.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }

                }catch (Exception e){
                    throw e;
                }
                finish();




            }
        });

        showQR.setOnClickListener(view -> {
            QR_displayFragment qrDisplayFragment = QR_displayFragment.newInstance(argbase64);
            qrDisplayFragment.show(getSupportFragmentManager(), "Show QR");
        });

        drawBtn.setOnClickListener(view -> {
            Intent intent1 = new Intent(view.getContext(), EventDraw.class);
            intent1.putExtra("Unique_id", uid);
            view.getContext().startActivity(intent1);
        });

        editEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(view.getContext(),EditEvent.class);
                intent.putExtra("Unique_id",uid);
                view.getContext().startActivity(intent);
            }
        });

        delEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder=new AlertDialog.Builder(ViewEvent.this);
                builder.setMessage("Are you sure you want to delete this event?")
                        .setCancelable(true)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                eventRef.delete()
                                            .addOnSuccessListener(aVoid->{
                                                Toast.makeText(ViewEvent.this,"Event Deleted",Toast.LENGTH_SHORT).show();
                                                Intent intent_main=new Intent(ViewEvent.this,MainActivity.class);
                                                startActivity(intent_main);
                                                finish();
                                            })
                                            .addOnFailureListener(e->{
                                                Toast.makeText(ViewEvent.this,"Failed to delete",Toast.LENGTH_SHORT).show();
                                            });
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });

                AlertDialog alert=builder.create();
                alert.show();

            }
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
            editEvent.setVisibility(View.INVISIBLE);
            delEvent.setVisibility(View.INVISIBLE);
        }else {
            waitlistBtn.setVisibility(View.INVISIBLE);
            editEvent.setVisibility(View.VISIBLE);
            delEvent.setVisibility(View.VISIBLE);
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
        builder.setPositiveButton("Confirm", (dialog, which) -> {
            loadEventDetails(uid);
            new Handler().postDelayed(() -> {
                if (geoRequirement) {
                    showGeolocationWarning();
                } else {
                    addToWaitList();
                }
            }, 300);
        });
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

            // Subscribe to topic for the user
            Notifications notifications = new Notifications();
            notifications.subscribeToTopic("Waitlist-" + uid);
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

                    // Unsubscribe from topic
                    Notifications notifications = new Notifications();
                    notifications.unsubscribeFromTopic("Waitlist-" + uid);
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
                Boolean GeoRequirement = documentSnapshot.getBoolean("GeoRequirement");

                argbase64 = documentSnapshot.getString("bitmap");
                try{
                    String ImageUri = documentSnapshot.getString("Poster");
                    assert ImageUri != null;
                    if (!ImageUri.isEmpty()) {
                        Glide.with(this).load(ImageUri).into(poster);
                    }
                    else{
                        Glide.with(this).load(R.drawable.ic_upload).into(poster);
                    }
                }catch(Exception e){
                    throw e;
                }

                Bitmap QR = decodeBase64toBmp(argbase64);

                title.setText(Title != null ? Title : "");
                capacity.setText(String.valueOf(Capacity));
                desc.setText(Description != null ? Description : "");
                time.setText(Time != null ? Time : "");
                date.setText(Date != null ? Date : "");
                location.setText(Location != null ? Location : "");
                geoRequirement = Boolean.TRUE.equals(GeoRequirement);
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

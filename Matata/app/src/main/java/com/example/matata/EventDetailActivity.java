package com.example.matata;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventDetailActivity extends AppCompatActivity {
    // sample user to add to waitlist
    private FirebaseFirestore db;
    private String Event_id = "sample_event_id";
    //private static final String USER_ID = "unique_user_id";
    private String USER_ID;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_event_details);
        // get db
        db = FirebaseFirestore.getInstance();
        USER_ID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        // Get views
        TextView titleTextView = findViewById(R.id.ViewEventTitle);
        TextView dateTextView = findViewById(R.id.ViewEventDate);
        TextView timeTextView = findViewById(R.id.ViewEventTime);
        TextView locationTextView = findViewById(R.id.ViewEventLoc);
        TextView descriptionTextView = findViewById(R.id.ViewEventDesc);
        ImageView posterImageView = findViewById(R.id.poster_pic_Display);
        ImageView backButton = findViewById(R.id.go_back_view_event);
        Button joinWaitlistButton = findViewById(R.id.join_waitlist_button);

        // Get data from Intent
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

        // Back button listener
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();  // Closes EventDetailActivity and returns to MainActivity
            }
        });

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference eventRef = db.collection("EVENT_PROFILES").document(Event_id);
        DocumentReference entrantRef = db.collection("USER_PROFILES").document(USER_ID);

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

        // join waitlist
        joinWaitlistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (joinWaitlistButton.getText().toString().equals("Withdraw")) {
                    // Show dialog to confirm exiting the waitlist
                    withdrawDialog();
                } else {
                    eventRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    // Retrieve the 'limit' field
                                    int limit = document.getLong("WaitlistLimit").intValue();
                                    List<DocumentReference> waitlist = (List<DocumentReference>) document.get("waitlist");
                                    if (limit == -1 || waitlist.size() < limit) {
                                        confirmationDialog(); // Show confirmation dialog for registration
                                    } else {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(EventDetailActivity.this, "Waitlist Full", Toast.LENGTH_SHORT).show();
                                                // Optionally, handle the over-limit case
                                            }
                                        });                                                }
                                } else {
                                    System.out.println("No such document!");
                                }
                            }
                        }
                    });
                }
            }

            private void withdrawDialog() {
                AlertDialog.Builder builder = new AlertDialog.Builder(EventDetailActivity.this);
                builder.setCancelable(true);
                builder.setMessage("Confirm to withdraw from waitlist");
                builder.setPositiveButton("Confirm",
                        new DialogInterface.OnClickListener() {
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

            private void exitWaitlist() {
                eventRef.update("waitlist", FieldValue.arrayRemove(entrantRef))
                        .addOnSuccessListener(aVoid -> {
                            Log.d("Firebase", "Entrant added to waitlist successfully");
                            joinWaitlistButton.setText("Join Waitlist");
                        }).addOnFailureListener(e -> {
                            Log.e("Firebase", "Error adding entrant to waitlist", e);
                        });
            }

            private void confirmationDialog() {
                AlertDialog.Builder builder = new AlertDialog.Builder(EventDetailActivity.this);
                builder.setCancelable(true);
                builder.setMessage("Confirm to join waitlist");
                builder.setPositiveButton("Confirm",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //addToWaitList();
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
                    joinWaitlistButton.setText("Withdraw");
                }).addOnFailureListener(e -> {
                    Log.e("Firebase", "Error adding entrant to waitlist", e);
                });
            }
        });

    }
}
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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
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
    private static final String USER_ID = "unique_user_id";
    //private String USER_ID;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_detail_activity);
        // get db
        db = FirebaseFirestore.getInstance();
        //USER_ID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        // Get views
        TextView titleTextView = findViewById(R.id.event_title);
        TextView descriptionTextView = findViewById(R.id.event_description);
        ImageView posterImageView = findViewById(R.id.event_poster);
        ImageButton backButton = findViewById(R.id.back_button);
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

        // Back button listener
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();  // Closes EventDetailActivity and returns to MainActivity
            }
        });

        // join waitlist
        joinWaitlistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(EventDetailActivity.this);
                builder.setCancelable(true);
                builder.setMessage("Confirm your registration");
                builder.setPositiveButton("Confirm",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                addToWaitList();
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
                DocumentReference eventRef = db.collection("EVENT_PROFILES").document(Event_id);
                DocumentReference entrantRef = db.collection("USER_PROFILES").document(USER_ID);
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

            ;
        });

    }
}
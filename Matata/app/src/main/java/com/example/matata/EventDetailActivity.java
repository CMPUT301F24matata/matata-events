package com.example.matata;

import android.content.Intent;
import android.os.Bundle;
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

import java.util.HashMap;
import java.util.Map;

public class EventDetailActivity extends AppCompatActivity {
    // sample user to add to waitlist
    private FirebaseFirestore db;
    private static final String USER_ID = "unique_user_id";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_detail_activity);
        // get db
        db = FirebaseFirestore.getInstance();

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
                DocumentReference waitlistRef = db.collection("EVENT_PROFILES").document("sample_event_id").collection("waitlist").document(USER_ID);
                DocumentReference entrantRef = db.collection("USER_PROFILES").document(USER_ID);
                db.runTransaction((Transaction.Function<Void>) transaction -> {
                    // Fetch entrant data
                    DocumentSnapshot entrantSnapshot = transaction.get(entrantRef);
                    Map<String, Object> entrantData = new HashMap<>();
                    entrantData.put("entrantName", entrantSnapshot.getString("username"));
                    entrantData.put("entrantEmail", entrantSnapshot.get("email"));
                    entrantData.put("entrantPhone", entrantSnapshot.get("phone"));
                    entrantData.put("status", "waiting");

                    transaction.set(waitlistRef, entrantData);
                    return null;
                }).addOnSuccessListener(aVoid -> {
                    Log.d("Firebase", "Entrant added to waitlist successfully");
                }).addOnFailureListener(e -> {
                    Log.e("Firebase", "Error adding entrant to waitlist", e);
                });
            };
        });

    }
}
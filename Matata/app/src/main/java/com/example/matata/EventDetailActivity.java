package com.example.matata;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class EventDetailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_detail_activity);

        // Get views
        TextView titleTextView = findViewById(R.id.event_title);
        TextView descriptionTextView = findViewById(R.id.event_description);
        ImageView posterImageView = findViewById(R.id.event_poster);
        ImageButton backButton = findViewById(R.id.back_button);

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
    }
}
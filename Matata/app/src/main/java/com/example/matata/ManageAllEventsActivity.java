package com.example.matata;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ManageAllEventsActivity extends AppCompatActivity {

    private LinearLayout eventDetails; // The dropdown section
    private ImageView btnToggleDetails; // The + or - toggle button
    private boolean isDropdownVisible = false; // Tracks if the dropdown is visible
    private TextView btnViewEvent, btnFreezeEvent, btnDeleteEvent;
    private ImageView btnBack;
    private boolean isEventFrozen = false; // Track event freeze status

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manage_all_events);

        // Initialize views
        eventDetails = findViewById(R.id.event_details);
        btnToggleDetails = findViewById(R.id.add_event);
        btnViewEvent = findViewById(R.id.btn_view_event);
        btnFreezeEvent = findViewById(R.id.btn_freeze_event);
        btnDeleteEvent = findViewById(R.id.btn_delete_event);
        btnBack = findViewById(R.id.btnBack);

        // Initially hide the dropdown
        eventDetails.setVisibility(View.GONE);

        btnBack.setOnClickListener(v -> finish());

        // Set click listener for the toggle button
        btnToggleDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleDropdown();
            }
        });

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

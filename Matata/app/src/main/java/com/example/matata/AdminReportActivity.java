package com.example.matata;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

/**
 * AdminReportActivity displays statistics and reports for events, users, and facilities.
 */
public class AdminReportActivity extends AppCompatActivity {

    private ImageView btnBack;
    private TextView totalEvents, averageAttendance, activeEvents, inactiveEvents;
    private TextView waitlistedParticipants, acceptedParticipants, popularEvent, leastPopularEvent;
    private TextView totalUsers, activeUsers, frozenUsers, entrantUsers, organiserUsers, adminUsers;
    private TextView totalFacilities, activeFacilities, frozenFacilities;
    private ImageView event_chart, user_chart, facility_chart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_reports);

        // Initialize header elements
        btnBack = findViewById(R.id.btnBack);

        // Initialize Event Report TextViews
        totalEvents = findViewById(R.id.totalEvents);
        averageAttendance = findViewById(R.id.averageAttendance);
        activeEvents = findViewById(R.id.activeEvents);
        inactiveEvents = findViewById(R.id.inactiveEvents);
        waitlistedParticipants = findViewById(R.id.waitlistedParticipants);
        acceptedParticipants = findViewById(R.id.acceptedParticipants);

        // Event Engagement
        popularEvent = findViewById(R.id.popular_event);
        leastPopularEvent = findViewById(R.id.un_popular_event);

        // Initialize User Report TextViews
        totalUsers = findViewById(R.id.totalUsers);
        activeUsers = findViewById(R.id.activeUsers);
        frozenUsers = findViewById(R.id.frozenUsers);

        // Initialize User Distribution
        entrantUsers = findViewById(R.id.entrantUsers);
        organiserUsers = findViewById(R.id.organiserUsers);
        adminUsers = findViewById(R.id.adminUsers);

        // Initialize Facility Report TextViews
        totalFacilities = findViewById(R.id.totalFacilities);
        activeFacilities = findViewById(R.id.activeFacilities);
        frozenFacilities = findViewById(R.id.frozenFacilities);

        event_chart = findViewById(R.id.event_chart);
        user_chart = findViewById(R.id.user_chart);
        facility_chart= findViewById(R.id.facility_chart);

        // Set up click listeners
        btnBack.setOnClickListener(v -> finish());

        // Set dummy data or fetch real data from backend
        loadData();
    }

    /**
     * Load data into the TextViews. Replace with real backend calls if necessary.
     */
    private void loadData() {
        // Event Reports
        totalEvents.setText("50");
        averageAttendance.setText("75%");
        activeEvents.setText("30");
        inactiveEvents.setText("20");
        waitlistedParticipants.setText("100");
        acceptedParticipants.setText("300");

        // User Reports
        totalUsers.setText("155");
        activeUsers.setText("100");
        frozenUsers.setText("30");

        // Facility Reports
        totalFacilities.setText("10");
        activeFacilities.setText("8");
        frozenFacilities.setText("2");

        // Example Toast Message (Optional)
        Toast.makeText(this, "Reports Loaded", Toast.LENGTH_SHORT).show();
    }
}

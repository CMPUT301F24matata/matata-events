package com.example.matata;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * AdminReportActivity displays statistics and reports for events, users, and facilities.
 */
public class AdminReportActivity extends AppCompatActivity {

    private ImageView btnBack;
    private TextView totalEvents, averageAttendance, activeEvents, inactiveEvents;
    private TextView waitlistedParticipants, acceptedParticipants, pendingParticipants, rejectedParticipants, popularEvent, lLeastPopularEvent;
    private TextView totalUsers, activeUsers, frozenUsers, entrantUsers, organiserUsers, adminUsers;
    private TextView totalFacilities, activeFacilities, frozenFacilities;
    private ImageView event_chart, user_chart, facility_chart;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_reports);

        db = FirebaseFirestore.getInstance();

        // Initialize header elements
        btnBack = findViewById(R.id.btnBack);

        // Initialize Event Report TextViews
        totalEvents = findViewById(R.id.totalEvents);
        averageAttendance = findViewById(R.id.averageAttendance);
        activeEvents = findViewById(R.id.activeEvents);
        inactiveEvents = findViewById(R.id.inactiveEvents);
        waitlistedParticipants = findViewById(R.id.waitlistedParticipants);
        acceptedParticipants = findViewById(R.id.acceptedParticipants);
        rejectedParticipants = findViewById(R.id.rejectedParticipants);
        pendingParticipants = findViewById(R.id.pendingParticipants);

        // Event Engagement
        popularEvent = findViewById(R.id.popular_event);
        lLeastPopularEvent = findViewById(R.id.un_popular_event);

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
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(AdminReportActivity.this, AdminView.class);
            startActivity(intent);
            finish();
        });

        // Set dummy data or fetch real data from backend
        loadData();
    }

    /**
     * Load data into the TextViews. Replace with real backend calls if necessary.
     */
    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    private void loadData() {

        // AtomicIntegers for event counts
        AtomicInteger totalEventsCount = new AtomicInteger(0);
        AtomicInteger activeEventsCount = new AtomicInteger(0);
        AtomicInteger inactiveEventsCount = new AtomicInteger(0);

        // AtomicIntegers for participant counts
        AtomicInteger waitlistedParticipantsCount = new AtomicInteger(0);
        AtomicInteger acceptedParticipantsCount = new AtomicInteger(0);
        AtomicInteger pendingParticipantsCount = new AtomicInteger(0);
        AtomicInteger rejectedParticipantsCount = new AtomicInteger(0);

        final int[] maxAcceptedParticipants = {0};
        final int[] minAcceptedParticipants = {Integer.MAX_VALUE};
        final String[] mostPopularEvent = {null};
        final String[] leastPopularEvent = {null};

        db.collection("EVENT_PROFILES")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot snapshot = task.getResult();
                        if (snapshot != null) {
                            totalEventsCount.set(snapshot.size()); // Set total events count

                            // Loop through each document to check its status and participant lists
                            snapshot.getDocuments().forEach(document -> {
                                String eventTitle = document.getString("Title");
                                // Event status
                                String status = document.getString("Status");
                                if ("Active".equalsIgnoreCase(status)) {
                                    activeEventsCount.incrementAndGet();

                                    // Safe casting and participant counts
                                    Object waitlistObj = document.get("waitlist");
                                    Object acceptedObj = document.get("accepted");
                                    Object pendingObj = document.get("pending");
                                    Object rejectedObj = document.get("rejected");

                                    int acceptedCount = 0;

                                    if (waitlistObj instanceof List) {
                                        waitlistedParticipantsCount.addAndGet(((List<?>) waitlistObj).size());
                                    }
                                    if (acceptedObj instanceof List) {
                                        acceptedCount = ((List<?>) acceptedObj).size();
                                        acceptedParticipantsCount.addAndGet(acceptedCount);
                                    }
                                    if (pendingObj instanceof List) {
                                        pendingParticipantsCount.addAndGet(((List<?>) pendingObj).size());
                                    }
                                    if (rejectedObj instanceof List) {
                                        rejectedParticipantsCount.addAndGet(((List<?>) rejectedObj).size());
                                    }

                                    // Check for most and least popular events
                                    if (acceptedCount > maxAcceptedParticipants[0]) {
                                        maxAcceptedParticipants[0] = acceptedCount;
                                        mostPopularEvent[0] = eventTitle;
                                    }
                                    if (acceptedCount < minAcceptedParticipants[0]) {
                                        minAcceptedParticipants[0] = acceptedCount;
                                        leastPopularEvent[0] = eventTitle;
                                    }

                                } else if ("Inactive".equalsIgnoreCase(status)) {
                                    inactiveEventsCount.incrementAndGet();
                                }

                            });

                            // Calculate average acceptance rate
                            int totalAccepted = acceptedParticipantsCount.get();
                            int totalPendingAndRejected = pendingParticipantsCount.get() + rejectedParticipantsCount.get();
                            double averageAcceptance;

                            if (totalPendingAndRejected == 0) {
                                averageAcceptance = 100;
                            } else {
                                averageAcceptance = (double) totalAccepted / totalPendingAndRejected;
                            }

                            // Update the TextViews with the fetched data
                            totalEvents.setText(String.valueOf(totalEventsCount.get()));
                            activeEvents.setText(String.valueOf(activeEventsCount.get()));
                            inactiveEvents.setText(String.valueOf(inactiveEventsCount.get()));
                            waitlistedParticipants.setText(String.valueOf(waitlistedParticipantsCount.get()));
                            acceptedParticipants.setText(String.valueOf(acceptedParticipantsCount.get()));
                            pendingParticipants.setText(String.valueOf(pendingParticipantsCount.get()));
                            rejectedParticipants.setText(String.valueOf(rejectedParticipantsCount.get()));
                            averageAttendance.setText(String.format("%.0f%%", averageAcceptance*100));
                            popularEvent.setText(mostPopularEvent[0]);
                            lLeastPopularEvent.setText(leastPopularEvent[0]);
                        } else {
                            // No documents found
                            totalEvents.setText("0");
                            activeEvents.setText("0");
                            inactiveEvents.setText("0");
                            waitlistedParticipants.setText("0");
                            acceptedParticipants.setText("0");
                            pendingParticipants.setText("0");
                            rejectedParticipants.setText("0");
                            averageAttendance.setText("0%");
                            popularEvent.setText("X");
                            lLeastPopularEvent.setText("X");
                        }
                    } else {
                        // Handle errors
                        totalEvents.setText("Error");
                        activeEvents.setText("Error");
                        inactiveEvents.setText("Error");
                        waitlistedParticipants.setText("Error");
                        acceptedParticipants.setText("Error");
                        pendingParticipants.setText("Error");
                        rejectedParticipants.setText("Error");
                        popularEvent.setText("Error");
                        lLeastPopularEvent.setText("Error");
                        Toast.makeText(this, "Failed to fetch event data", Toast.LENGTH_SHORT).show();
                    }
                });

        // AtomicIntegers for user counts
        AtomicInteger totalUsersCount = new AtomicInteger(0);
        AtomicInteger activeUsersCount = new AtomicInteger(0);
        AtomicInteger frozenUsersCount = new AtomicInteger(0);
        AtomicInteger adminUsersCount = new AtomicInteger(0);
        AtomicInteger organizerUsersCount = new AtomicInteger(0);
        AtomicInteger entrantUsersCount = new AtomicInteger(0);

        db.collection("USER_PROFILES")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot snapshot = task.getResult();
                        if (snapshot != null) {
                            totalUsersCount.set(snapshot.size()); // Set total users count

                            // Loop through each document to check freeze status and roles
                            snapshot.getDocuments().forEach(document -> {
                                // Freeze status
                                String freezeStatus = document.getString("freeze");
                                if ("awake".equalsIgnoreCase(freezeStatus)) {
                                    activeUsersCount.incrementAndGet();
                                } else if ("frozen".equalsIgnoreCase(freezeStatus)) {
                                    frozenUsersCount.incrementAndGet();
                                }

                                // User role
                                String userRole = document.getString("admin");
                                if ("admin".equalsIgnoreCase(userRole)) {
                                    adminUsersCount.incrementAndGet();
                                } else if ("organizer".equalsIgnoreCase(userRole)) {
                                    organizerUsersCount.incrementAndGet();
                                } else if ("entrant".equalsIgnoreCase(userRole)) {
                                    entrantUsersCount.incrementAndGet();
                                }
                            });

                            // Update the TextViews with the fetched data
                            totalUsers.setText(String.valueOf(totalUsersCount.get()));
                            activeUsers.setText(String.valueOf(activeUsersCount.get()));
                            frozenUsers.setText(String.valueOf(frozenUsersCount.get()));
                            adminUsers.setText(String.valueOf(adminUsersCount.get()));
                            organiserUsers.setText(String.valueOf(organizerUsersCount.get()));
                            entrantUsers.setText(String.valueOf(entrantUsersCount.get()));
                        } else {
                            // No documents found
                            totalUsers.setText("0");
                            activeUsers.setText("0");
                            frozenUsers.setText("0");
                            adminUsers.setText("0");
                            organiserUsers.setText("0");
                            entrantUsers.setText("0");
                        }
                    } else {
                        // Handle errors
                        totalUsers.setText("Error");
                        activeUsers.setText("Error");
                        frozenUsers.setText("Error");
                        adminUsers.setText("Error");
                        organiserUsers.setText("Error");
                        entrantUsers.setText("Error");
                        Toast.makeText(this, "Failed to fetch user data", Toast.LENGTH_SHORT).show();
                    }
                });

        // AtomicIntegers for facility counts
        AtomicInteger totalFacilitiesCount = new AtomicInteger(0);
        AtomicInteger activeFacilitiesCount = new AtomicInteger(0);
        AtomicInteger frozenFacilitiesCount = new AtomicInteger(0);

        db.collection("FACILITY_PROFILES")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot snapshot = task.getResult();
                        if (snapshot != null) {
                            totalFacilitiesCount.set(snapshot.size()); // Set total facilities count

                            // Loop through each document to check freeze status
                            snapshot.getDocuments().forEach(document -> {
                                // Freeze status
                                String freezeStatus = document.getString("freeze");
                                if ("awake".equalsIgnoreCase(freezeStatus)) {
                                    activeFacilitiesCount.incrementAndGet();
                                } else if ("frozen".equalsIgnoreCase(freezeStatus)) {
                                    frozenFacilitiesCount.incrementAndGet();
                                }
                            });

                            // Update the TextViews with the fetched data
                            totalFacilities.setText(String.valueOf(totalFacilitiesCount.get()));
                            activeFacilities.setText(String.valueOf(activeFacilitiesCount.get()));
                            frozenFacilities.setText(String.valueOf(frozenFacilitiesCount.get()));
                            int activeFacilities = activeFacilitiesCount.get();
                            int frozenFacilities = frozenFacilitiesCount.get();

                            Bitmap pieChartBitmap = drawPieChart(activeFacilities, frozenFacilities);
                            facility_chart.setImageBitmap(pieChartBitmap);
                        } else {
                            // No documents found
                            totalFacilities.setText("0");
                            activeFacilities.setText("0");
                            frozenFacilities.setText("0");
                        }
                    } else {
                        // Handle errors
                        totalFacilities.setText("Error");
                        activeFacilities.setText("Error");
                        frozenFacilities.setText("Error");
                        Toast.makeText(this, "Failed to fetch facility data", Toast.LENGTH_SHORT).show();
                    }
                });

        // Example Toast Message (Optional)
        Toast.makeText(this, "Reports Loaded", Toast.LENGTH_SHORT).show();
    }

    /**
     * Draws a pie chart for the given active and frozen facility counts.
     *
     * @param activeCount Number of active facilities.
     * @param frozenCount Number of frozen facilities.
     * @return Bitmap representing the pie chart.
     */
    private Bitmap drawPieChart(int activeCount, int frozenCount) {
        int total = activeCount + frozenCount;
        if (total == 0) total = 1; // Avoid division by zero

        float activePercentage = (float) activeCount / total * 360;
        float frozenPercentage = (float) frozenCount / total * 360;

        int width = 500; // Chart width
        int height = 500; // Chart height

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        Paint paint = new Paint();
        paint.setAntiAlias(true);

        // Background color
        canvas.drawColor(Color.BLACK);

        // Draw active facilities (Green slice)
        paint.setColor(Color.GREEN);
        canvas.drawArc(50, 50, width - 50, height - 50, 0, activePercentage, true, paint);

        // Draw frozen facilities (Red slice)
        paint.setColor(Color.RED);
        canvas.drawArc(50, 50, width - 50, height - 50, activePercentage, frozenPercentage, true, paint);

        // Optional: Draw chart border
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(4);
        canvas.drawOval(50, 50, width - 50, height - 50, paint);

        return bitmap;
    }
}

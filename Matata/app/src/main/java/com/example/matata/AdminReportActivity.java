package com.example.matata;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
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
 * The {@code AdminReportActivity} class provides an interface for administrators to view reports and statistics
 * related to events, users, and facilities. It presents data visually through pie charts and textually through
 * summarized metrics. The reports cover various aspects such as:
 * - Event statistics: total events, active/inactive events, participant distribution.
 * - User statistics: total users, user roles, and account statuses.
 * - Facility statistics: total facilities, and active/frozen facilities.
 *
 * <p>This class fetches data from Firebase Firestore to populate its views and charts.
 */
public class AdminReportActivity extends AppCompatActivity {

    /**
     * Button for navigating back to the AdminView activity.
     */
    private ImageView btnBack;

    // Event statistics TextViews
    /**
     * TextView to display the total number of events.
     */
    private TextView totalEvents;

    /**
     * TextView to display the average attendance across all events.
     */
    private TextView averageAttendance;

    /**
     * TextView to display the number of active events.
     */
    private TextView activeEvents;

    /**
     * TextView to display the number of inactive events.
     */
    private TextView inactiveEvents;

    /**
     * TextView to display the number of participants on the waitlist.
     */
    private TextView waitlistedParticipants;

    /**
     * TextView to display the number of accepted participants.
     */
    private TextView acceptedParticipants;

    /**
     * TextView to display the number of pending participants.
     */
    private TextView pendingParticipants;

    /**
     * TextView to display the number of rejected participants.
     */
    private TextView rejectedParticipants;

    /**
     * TextView to display the most popular event based on accepted participants.
     */
    private TextView popularEvent;

    /**
     * TextView to display the least popular event based on accepted participants.
     */
    private TextView lLeastPopularEvent;

    // User statistics TextViews
    /**
     * TextView to display the total number of users.
     */
    private TextView totalUsers;

    /**
     * TextView to display the number of active users.
     */
    private TextView activeUsers;

    /**
     * TextView to display the number of frozen users.
     */
    private TextView frozenUsers;

    /**
     * TextView to display the number of entrant users.
     */
    private TextView entrantUsers;

    /**
     * TextView to display the number of organizer users.
     */
    private TextView organiserUsers;

    /**
     * TextView to display the number of admin users.
     */
    private TextView adminUsers;

    // Facility statistics TextViews
    /**
     * TextView to display the total number of facilities.
     */
    private TextView totalFacilities;

    /**
     * TextView to display the number of active facilities.
     */
    private TextView activeFacilities;

    /**
     * TextView to display the number of frozen facilities.
     */
    private TextView frozenFacilities;

    // Charts for visual representation
    /**
     * ImageView to display the pie chart for event statistics.
     */
    private ImageView event_chart;

    /**
     * ImageView to display the pie chart for user statistics.
     */
    private ImageView user_chart;

    /**
     * ImageView to display the pie chart for facility statistics.
     */
    private ImageView facility_chart;

    /**
     * ImageView to display the pie chart for participant distribution.
     */
    private ImageView participants_chart;

    /**
     * ImageView to display the pie chart for user role distribution.
     */
    private ImageView user_dist_chart;

    /**
     * Firebase Firestore instance for accessing database.
     */
    private FirebaseFirestore db;

    /**
     * Initializes the activity and loads the data for reports.
     *
     * @param savedInstanceState The saved instance state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_reports);

        db = FirebaseFirestore.getInstance();

        // Initialize views
        initializeViews();

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
     * Initializes all UI components.
     */
    private void initializeViews() {
        btnBack = findViewById(R.id.btnBack);

        totalEvents = findViewById(R.id.totalEvents);
        averageAttendance = findViewById(R.id.averageAttendance);
        activeEvents = findViewById(R.id.activeEvents);
        inactiveEvents = findViewById(R.id.inactiveEvents);
        waitlistedParticipants = findViewById(R.id.waitlistedParticipants);
        acceptedParticipants = findViewById(R.id.acceptedParticipants);
        rejectedParticipants = findViewById(R.id.rejectedParticipants);
        pendingParticipants = findViewById(R.id.pendingParticipants);
        popularEvent = findViewById(R.id.popular_event);
        lLeastPopularEvent = findViewById(R.id.un_popular_event);

        totalUsers = findViewById(R.id.totalUsers);
        activeUsers = findViewById(R.id.activeUsers);
        frozenUsers = findViewById(R.id.frozenUsers);
        entrantUsers = findViewById(R.id.entrantUsers);
        organiserUsers = findViewById(R.id.organiserUsers);
        adminUsers = findViewById(R.id.adminUsers);

        totalFacilities = findViewById(R.id.totalFacilities);
        activeFacilities = findViewById(R.id.activeFacilities);
        frozenFacilities = findViewById(R.id.frozenFacilities);

        event_chart = findViewById(R.id.event_chart);
        user_chart = findViewById(R.id.user_chart);
        facility_chart = findViewById(R.id.facility_chart);
        participants_chart = findViewById(R.id.participants_chart);
        user_dist_chart = findViewById(R.id.user_dist_chart);
    }

    /**
     * Fetches data from Firebase Firestore and populates TextViews and charts.
     */
    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    private void loadData() {

        // Load data for events
        loadEventData();

        // Load data for users
        loadUserData();

        // Load data for facilities
        loadFacilityData();

        // Example Toast Message (Optional)
        Toast.makeText(this, "Reports Loaded", Toast.LENGTH_SHORT).show();
    }

    /**
     * Fetches facility-related data from Firebase and populates relevant TextViews and charts.
     */
    private void loadFacilityData() {
        // Implementation for fetching facility data
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
    }

    /**
     * Fetches user-related data from Firebase and populates relevant TextViews and charts.
     */
    private void loadUserData() {
        // Implementation for fetching user data
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
                                } else if ("organiser".equalsIgnoreCase(userRole)) {
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

                            int activeFacilities = activeUsersCount.get();
                            int frozenFacilities = frozenUsersCount.get();

                            Bitmap pieChartBitmap = drawPieChart(activeFacilities, frozenFacilities);
                            user_chart.setImageBitmap(pieChartBitmap);

                            int a = entrantUsersCount.get();
                            int p = organizerUsersCount.get();
                            int r = adminUsersCount.get();

                            Bitmap ChartBitmap = generatePieChartBitmap(a, p, r);
                            user_dist_chart.setImageBitmap(ChartBitmap);

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
    }

    /**
     * Fetches event-related data from Firebase and populates relevant TextViews and charts.
     */
    @SuppressLint("SetTextI18n")
    private void loadEventData() {
        // Implementation for fetching event data
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

                            int activeFacilities = activeEventsCount.get();
                            int frozenFacilities = inactiveEventsCount.get();

                            Bitmap pieChartBitmap = drawPieChart(activeFacilities, frozenFacilities);
                            event_chart.setImageBitmap(pieChartBitmap);

                            int a = acceptedParticipantsCount.get();
                            int p = pendingParticipantsCount.get();
                            int r = rejectedParticipantsCount.get();

                            Bitmap ChartBitmap = generatePieChartBitmap(a, p, r);
                            participants_chart.setImageBitmap(ChartBitmap);

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
    }

    /**
     * Draws a pie chart for the given active and frozen counts.
     *
     * @param activeCount Number of active items.
     * @param frozenCount Number of frozen items.
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
        canvas.drawColor(Color.TRANSPARENT);

        // Draw active facilities (Green slice)
        paint.setColor(Color.parseColor("#66BB6A"));
        canvas.drawArc(50, 50, width - 50, height - 50, 0, activePercentage, true, paint);

        // Draw frozen facilities (Red slice)
        paint.setColor(Color.parseColor("#BDBDBD"));
        canvas.drawArc(50, 50, width - 50, height - 50, activePercentage, frozenPercentage, true, paint);

        return bitmap;
    }

    /**
     * Generates a pie chart bitmap for participant distribution.
     *
     * @param acceptedParticipantsCount Number of accepted participants.
     * @param pendingParticipantsCount Number of pending participants.
     * @param rejectedParticipantsCount Number of rejected participants.
     * @return Bitmap representing the pie chart.
     */
    private Bitmap generatePieChartBitmap(int acceptedParticipantsCount, int pendingParticipantsCount, int rejectedParticipantsCount) {
        int width = 500; // Width of the pie chart image
        int height = 500; // Height of the pie chart image

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.TRANSPARENT); // Transparent background

        Paint paint = new Paint();
        paint.setAntiAlias(true);

        int total = acceptedParticipantsCount + pendingParticipantsCount + rejectedParticipantsCount;

        float acceptedPercentage = (float) acceptedParticipantsCount / total;
        float pendingPercentage = (float) pendingParticipantsCount / total;
        float rejectedPercentage = (float) rejectedParticipantsCount / total;

        float startAngle = 0f;

        RectF rectF = new RectF(50, 50, width - 50, height - 50);

        // Accepted Participants (Blue)
        paint.setColor(Color.parseColor("#42A5F5"));
        float sweepAngle = acceptedPercentage * 360;
        canvas.drawArc(rectF, startAngle, sweepAngle, true, paint);
        startAngle += sweepAngle;

        // Pending Participants (Orange)
        paint.setColor(Color.parseColor("#FFA726"));
        sweepAngle = pendingPercentage * 360;
        canvas.drawArc(rectF, startAngle, sweepAngle, true, paint);
        startAngle += sweepAngle;

        // Rejected Participants (Red)
        paint.setColor(Color.parseColor("#EF5350"));
        sweepAngle = rejectedPercentage * 360;
        canvas.drawArc(rectF, startAngle, sweepAngle, true, paint);

        return bitmap;
    }
}

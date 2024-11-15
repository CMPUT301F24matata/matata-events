package com.example.matata;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * MainActivity serves as the main hub of the app, displaying available events in a RecyclerView,
 * allowing users to navigate to different sections, and setting up notifications for waitlist updates.
 * This activity also initializes a user profile if it doesn't already exist in Firestore.
 *
 * Outstanding issues: The `addEventsInit()` method retrieves events without any pagination,
 * which may impact performance if the event list grows significantly. Additionally, the notification
 * logic assumes the app has permission to post notifications, which may not be granted by the user.
 */
public class MainActivity extends AppCompatActivity {

    /**
     * ImageView for displaying the profile icon.
     */
    private ImageView profileIcon;

    /**
     * ImageView for navigating to the new event creation screen.
     */
    private ImageView new_event;

    /**
     * ImageView for accessing event history.
     */
    private LinearLayout eventHistory;

    /**
     * EditText for searching events.
     */
    private LinearLayout explore;

    /**
     * RecyclerView for displaying a list of events.
     */
    private RecyclerView recyclerView;

    /**
     * Adapter for managing the display and interaction with event items in the RecyclerView.
     */
    private EventAdapter eventAdapter;

    /**
     * List of events to be displayed in the RecyclerView.
     */
    private List<Event> eventList;

    /**
     * FloatingActionButton for initiating a QR scanner.
     */
    private FloatingActionButton QR_scanner;

    /**
     * Instance of FirebaseFirestore used for database access.
     */
    private FirebaseFirestore db;

    /**
     * User ID string for accessing user-specific data.
     */
    private String USER_ID = "";

    /**
     * UID string for uniquely identifying the user.
     */
    private String uid = null;

    /**
     * List of statuses related to events.
     */
    private List<String> statusList = new ArrayList<>();

    private Map<String,String> posterUrls= new HashMap<>();

    /**
     * ImageButton for accessing notifications.
     */
    private ImageButton notificationButton;

    /**
     * Launcher for requesting notification permissions.
     */
    private ActivityResultLauncher<String> notificationPermissionLauncher;

    /**
     * Manager for handling notifications.
     */
    private Notification notificationManager;

    /**
     * Channel ID used for waitlist notifications.
     */
    private static final String CHANNEL_ID = "waitlist_notification_channel";

    private ImageView FacilityProfile;

    private ImageButton admin;


    /**
     * Initializes the MainActivity and sets up UI components, database references, and event data.
     * Configures notification channels and permissions for handling waitlist notifications.
     *
     * @param savedInstanceState if the activity is being re-initialized, this contains the data it most recently supplied
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);





        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = FirebaseFirestore.getInstance();
        USER_ID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        // Initialize user profile if not exists
        DocumentReference userRef = db.collection("USER_PROFILES").document(USER_ID);
        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null && !task.getResult().exists()) {
                Map<String, Object> userProfile = new HashMap<>();
                userProfile.put("username", USER_ID);
                userProfile.put("phone", "");
                userProfile.put("email", "");
                userProfile.put("notifications", false);
                userProfile.put("profileUri", "");
                userRef.set(userProfile);
            }
        });

        initializeUI();
        setOnClickListeners();

        // Set up permission launcher and notification manager
        notificationPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> Log.d("NotificationPermission", isGranted ? "Permission granted" : "Permission denied")
        );
        notificationManager = new Notification();
        notificationManager.setNotificationPermissionLauncher(notificationPermissionLauncher);

        // Initialize notification channel
        Notification.initNotificationChannel(this);

        // Load events
        addEventsInit();
    }

    /**
     * Initializes UI elements and sets up the RecyclerView for displaying event data.
     */
    private void initializeUI() {
        profileIcon = findViewById(R.id.profile_picture);
        new_event = findViewById(R.id.add_event);
        QR_scanner = findViewById(R.id.qr_scanner);
        eventHistory = findViewById(R.id.event_history);
        explore = findViewById(R.id.event_map);
        notificationButton = findViewById(R.id.notifiy_button);
        FacilityProfile = findViewById(R.id.FacilityProfile);
        admin = findViewById(R.id.admin);
        recyclerView = findViewById(R.id.recycler_view_events);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(this, R.anim.anim_slide_in);
        recyclerView.setLayoutAnimation(animation);

        eventList = new ArrayList<>();
        eventAdapter = new EventAdapter(this, eventList, statusList,posterUrls);
        recyclerView.setAdapter(eventAdapter);


    }

    /**
     * Sets click listeners for various UI elements to handle navigation and actions.
     */
    private void setOnClickListeners() {
        profileIcon.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), ProfileActivity.class);
            intent.putExtra("Caller", "Not admin");
            startActivity(intent);
        });

        new_event.setOnClickListener(view -> startActivity(new Intent(view.getContext(), AddEvent.class)));

        QR_scanner.setOnClickListener(view -> startActivity(new Intent(view.getContext(), QR_camera.class)));

        eventHistory.setOnClickListener(view -> startActivity(new Intent(view.getContext(), EventHistory.class)));

        notificationButton.setOnClickListener(view -> {
            notificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS);
            Log.d("NotificationTest", "Sending notifications to waitlist...");
            retrieveWaitlistAndNotify();
        });

        explore.setOnClickListener(v -> {
            Intent intent=new Intent(MainActivity.this,ExploreEvents.class);
            startActivity(intent);
            // Add event search logic here
        });

        FacilityProfile.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, FacilityActivity.class);
            startActivity(intent);
        });

        admin.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, AdminView.class);
            startActivity(intent);
        });
    }

    /**
     * Loads and listens for changes to events in Firestore, updating the RecyclerView accordingly.
     */
    private void addEventsInit() {

        db.collection("EVENT_PROFILES")
                .addSnapshotListener((snapshots, e) -> {
                    if (snapshots != null) {
                        eventList.clear();
                        statusList.clear();
                        posterUrls.clear();

                        for (QueryDocumentSnapshot document : snapshots) {
                            DocumentReference entrantRef = db.collection("USER_PROFILES").document(USER_ID);
                            List<DocumentReference> accepted = (List<DocumentReference>) document.get("accepted");
                            List<DocumentReference> pending = (List<DocumentReference>) document.get("pending");
                            List<DocumentReference> waitlist = (List<DocumentReference>) document.get("waitlist");

                            if (accepted != null && accepted.contains(entrantRef)) {
                                statusList.add("Accepted");
                            } else if (pending != null && pending.contains(entrantRef)) {
                                statusList.add("Pending");
                            } else if (waitlist != null && waitlist.contains(entrantRef)) {
                                statusList.add("Waitlist");
                            } else {
                                statusList.add("");
                            }

                            String uid = document.getId();
                            String title = document.getString("Title");
                            String date = document.getString("Date");
                            String time = document.getString("Time");
                            String location = document.getString("Location");
                            String description = document.getString("Description");
                            String organizerId = document.getString("OrganizerId");
                            int capacity = document.getLong("Capacity").intValue();

                            eventList.add(new Event(title, date, time, location, description, capacity, uid, organizerId, -1));

                            String posterUrl = document.getString("Poster");
                            if (posterUrl != null) {
                                posterUrls.put(uid, posterUrl);
                            }
                        }

                        eventAdapter.notifyDataSetChanged();


                    } else if (e != null) {
                        Log.e("FirestoreError", "Error fetching events: ", e);
                    }
                });
        recyclerView.scheduleLayoutAnimation();
    }

    /**
     * Retrieves the waitlist for each event organized by the user and sends notifications to those on the waitlist.
     */
    private void retrieveWaitlistAndNotify() {
        db.collection("EVENT_PROFILES")
                .whereEqualTo("OrganizerID", USER_ID)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String organizerId = document.getString("OrganizerID");
                            Log.d("OrganizerId", "OrganizerId: " + organizerId);  // Log each OrganizerId

                            List<Object> waitlistIds = (List<Object>) document.get("waitlist");
                            if (waitlistIds != null && !waitlistIds.isEmpty()) {
                                String title = "Event Update";
                                String message = "You have been selected for an event!";
                                notificationManager.sendNotificationsToWaitlist(this, waitlistIds, title, message);
                            } else {
                                Log.d("Waitlist Acquisition", "No waitlist entries found.");
                            }
                        }
                    }
                });
    }
}

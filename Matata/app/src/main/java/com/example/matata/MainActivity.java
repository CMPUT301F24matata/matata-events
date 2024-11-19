package com.example.matata;

import static android.content.ContentValues.TAG;

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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
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
import java.util.Objects;

/**
 * MainActivity serves as the central hub of the Matata app, displaying a list of events,
 * allowing users to navigate to different sections, and managing notifications.
 * This activity initializes a user profile in Firestore if it does not already exist,
 * handles event-related actions, and manages user permissions for notifications.
 */
public class MainActivity extends AppCompatActivity {

    /**
     * ImageView for displaying the user profile icon.
     */
    private ImageView profileIcon;

    /**
     * ImageView for navigating to the "Add Event" screen.
     */
    private ImageView new_event;

    /**
     * LinearLayout for accessing the event history section.
     */
    private LinearLayout eventHistory;

    /**
     * LinearLayout for exploring events on the map.
     */
    private LinearLayout explore;

    /**
     * RecyclerView for displaying a list of events.
     */
    //private RecyclerView recyclerView;

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
     * Instance of FirebaseFirestore for database access.
     */
    private FirebaseFirestore db;

    /**
     * String representing the unique user ID for Firestore operations.
     */
    private String USER_ID = "";

    /**
     * UID string for uniquely identifying the user.
     */
    private String uid = null;

    /**
     * List of statuses for the events (e.g., "Accepted," "Pending," "Waitlist").
     */
    private List<String> statusList = new ArrayList<>();

    /**
     * Map storing event poster URLs associated with their event IDs.
     */
    private Map<String, String> posterUrls = new HashMap<>();

    /**
     * ImageButton for accessing notifications.
     */
    private ImageView notificationButton;

    /**
     * Launcher for requesting notification permissions.
     */
    private ActivityResultLauncher<String> notificationPermissionLauncher;

    /**
     * Manager for handling notifications.
     */
    private Notification notificationManager;

    /**
     * Static channel ID used for managing waitlist notifications.
     */
    private static final String CHANNEL_ID = "waitlist_notification_channel";

    /**
     * ImageView for accessing the facility profile section.
     */
    private ImageView FacilityProfile;

    /**
     * ImageButton for accessing the admin section.
     */
    private ImageView admin;


    private RadioButton list_toggle;
    private RadioButton scroll_toggle;
    private RadioGroup Toggle;
    /**
     * Called when the activity is first created. Initializes the user profile in Firestore if necessary,
     * sets up UI components, handles notification permissions, and loads event data.
     *
     * @param savedInstanceState Bundle containing the activity's previously saved state, if any.
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

        notificationPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> Log.d("NotificationPermission", isGranted ? "Permission granted" : "Permission denied")
        );
        notificationManager = new Notification();
        notificationManager.setNotificationPermissionLauncher(notificationPermissionLauncher);


        db = FirebaseFirestore.getInstance();
        USER_ID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        DocumentReference userRef = db.collection("USER_PROFILES").document(USER_ID);
        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                if (!task.getResult().exists()) {

                    Map<String, Object> userProfile = new HashMap<>();
                    userProfile.put("username", USER_ID);
                    userProfile.put("phone", "");
                    userProfile.put("email", "");
                    userProfile.put("notifications", false);
                    userProfile.put("profileUri", "");
                    userProfile.put("freeze", "awake");
                    userProfile.put("admin", "entrant");
                    userRef.set(userProfile);

                    initializeApp();
                } else {
                    // Check the freeze status
                    String freezeStatus = task.getResult().getString("freeze");
                    String admin = task.getResult().getString("admin");
                    if ("awake".equalsIgnoreCase(freezeStatus)) {
                        if("admin".equalsIgnoreCase(admin)) {
                            initializeApp();
                        }
                        else if ("entrant".equalsIgnoreCase(admin)) {
                            initializeEntrantApp();
                        }
                    } else {
                        showFrozenDialog();
                    }
                }
            } else {
                Log.e("MainActivity", "Error fetching user profile", task.getException());
            }
        });

    }




    /**
     * Displays a frozen dialog when the user's account is marked as frozen.
     */
    private void showFrozenDialog() {
        FrozenDialogFragment dialogFragment = new FrozenDialogFragment();
        dialogFragment.setCancelable(false);
        dialogFragment.show(getSupportFragmentManager(), "FrozenDialogFragment");
    }

    /**
     * Initializes the app in entrant mode, which hides admin-specific features.
     * Configures notifications and loads event data for the user.
     */
    private void initializeEntrantApp() {

        initializeUI();
        setOnClickListeners();

        Notification.initNotificationChannel(this);

//        addEventsInit();
    }

    /**
     * Initializes the app with admin features, configuring notifications and loading event data.
     */
    private void initializeApp() {

        initializeUI();
        setOnClickListeners();
        admin.setVisibility(View.VISIBLE);

        Notification.initNotificationChannel(this);

//        addEventsInit();
    }

    /**
     * Sets up the UI components, including the RecyclerView for displaying events.
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
        list_toggle=findViewById(R.id.ListToggle);
        scroll_toggle=findViewById(R.id.ExploreToggle);
        Toggle=findViewById(R.id.toggle);
        Log.wtf(TAG, "hello hello");

        if (findViewById(R.id.ListToggle).isSelected() || ((RadioButton) findViewById(R.id.ListToggle)).isChecked()) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.scrollListFragment, new Recycler_fragment())
                    .commit();
        }
        Toggle.setOnCheckedChangeListener((button,checkedID)->{
            Log.wtf(TAG, "Checked ID: " + checkedID);
            Fragment fragment;
            if (checkedID==R.id.ListToggle){
                Log.wtf(TAG,"List checked");
                fragment=new Recycler_fragment();

            }
            else{
                fragment=null;
            }

            getSupportFragmentManager().beginTransaction().replace(R.id.scrollListFragment,fragment).commit();
        });

//        recyclerView = findViewById(R.id.recycler_view_events);
//        recyclerView.setHasFixedSize(true);
//        recyclerView.setItemViewCacheSize(20);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//
//        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(this, R.anim.anim_slide_in);
//        recyclerView.setLayoutAnimation(animation);
//
//        eventList = new ArrayList<>();
//        eventAdapter = new EventAdapter(this, eventList, statusList,posterUrls);
//        recyclerView.setAdapter(eventAdapter);
    }

    /**
     * Sets up click listeners for various UI elements, such as profile navigation, adding events,
     * QR scanner, and exploring events on the map.
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
     * Loads and listens for changes to event data in Firestore.
     * Updates the RecyclerView dynamically when data changes.
     */
//    private void addEventsInit() {
//
//        db.collection("EVENT_PROFILES")
//                .addSnapshotListener((snapshots, e) -> {
//                    if (snapshots != null) {
//                        eventList.clear();
//                        statusList.clear();
//                        posterUrls.clear();
//
//                        for (QueryDocumentSnapshot document : snapshots) {
//                            DocumentReference entrantRef = db.collection("USER_PROFILES").document(USER_ID);
//                            List<DocumentReference> accepted = (List<DocumentReference>) document.get("accepted");
//                            List<DocumentReference> pending = (List<DocumentReference>) document.get("pending");
//                            List<DocumentReference> waitlist = (List<DocumentReference>) document.get("waitlist");
//
//                            if (accepted != null && accepted.contains(entrantRef)) {
//                                statusList.add("Accepted");
//                            } else if (pending != null && pending.contains(entrantRef)) {
//                                statusList.add("Pending");
//                            } else if (waitlist != null && waitlist.contains(entrantRef)) {
//                                statusList.add("Waitlist");
//                            } else {
//                                statusList.add("");
//                            }
//
//                            String uid = document.getId();
//                            String title = document.getString("Title");
//                            String date = document.getString("Date");
//                            String time = document.getString("Time");
//                            String location = document.getString("Location");
//                            String description = document.getString("Description");
//                            String organizerId = document.getString("OrganizerId");
//                            int capacity = document.getLong("Capacity").intValue();
//                            String status = document.getString("Status");
//
//                            if (Objects.equals(status, "Active")) {
//                                eventList.add(new Event(title, date, time, location, description, capacity, uid, organizerId, -1));
//                                String posterUrl = document.getString("Poster");
//                                if (posterUrl != null) {
//                                    posterUrls.put(uid, posterUrl);
//                                }
//                            }
//
//                        }
//
//                        eventAdapter.notifyDataSetChanged();
//
//
//                    } else if (e != null) {
//                        Log.e("FirestoreError", "Error fetching events: ", e);
//                    }
//                });
//        recyclerView.scheduleLayoutAnimation();
//    }

    /**
     * Retrieves the waitlist for events organized by the user and sends notifications to the waitlisted users.
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

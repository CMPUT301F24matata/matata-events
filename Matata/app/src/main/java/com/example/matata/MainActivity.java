package com.example.matata;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.content.SharedPreferences;
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
import androidx.core.splashscreen.SplashScreen;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * MainActivity serves as the central hub for the Matata app. It provides navigation to key features, such as:
 * - User profiles
 * - Event creation
 * - Event exploration
 * - Event history
 * - Admin and facility-specific sections
 *
 * The activity dynamically initializes user profiles in Firestore if they don't exist and adapts UI
 * based on the user's role (admin or entrant).
 */
public class MainActivity extends AppCompatActivity {

    /**
     * ImageView for navigating to the user profile.
     */
    private ImageView profileIcon;

    /**
     * ImageView for navigating to the "Add Event" screen.
     */
    private ImageView new_event;

    /**
     * LinearLayout for accessing the event history.
     */
    private LinearLayout eventHistory;

    /**
     * LinearLayout for exploring events on a map.
     */
    private LinearLayout explore;

    /**
     * FloatingActionButton for launching the QR scanner.
     */
    private FloatingActionButton QR_scanner;

    /**
     * FirebaseFirestore instance for Firestore operations.
     */
    private FirebaseFirestore db;

    /**
     * User ID retrieved from the device's unique ID settings.
     */
    private String USER_ID = "";

    /**
     * ImageView for accessing notifications.
     */
    private ImageView notificationButton;

    /**
     * ImageView for accessing the facility profile section.
     */
    private ImageView FacilityProfile;

    /**
     * ImageView for accessing the admin section.
     */
    private ImageView admin;

    /**
     * RadioButton for toggling to the event list view.
     */
    private RadioButton list_toggle;

    /**
     * RadioButton for toggling to the event swipe view.
     */
    private RadioButton scroll_toggle;

    /**
     * RadioGroup for managing view toggles.
     */
    private RadioGroup Toggle;

    /**
     * A Hash Map to store previous states of groups (Waitlist, Pending, Accepted, Rejected).
     */
    private final Map<String, Map<String, List<DocumentReference>>> previousStates = new HashMap<>();

    private boolean completeLoad = false;

    /**
     * Called when the activity is first created. Initializes the user profile in Firestore if necessary,
     * sets up UI components, handles notification permissions, and loads event data.
     *
     * @param savedInstanceState Bundle containing the activity's previously saved state, if any.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);
        completeLoad = true;
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        splashScreen.setKeepOnScreenCondition(() -> !completeLoad);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

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
                    userProfile.put("organiser", "no");
                    userRef.set(userProfile);

                    initializeApp();
                    saveFCMTokenToDatabase();
                } else {
                    // Check the freeze status
                    String freezeStatus = task.getResult().getString("freeze");
                    String admin = task.getResult().getString("admin");
                    if ("awake".equalsIgnoreCase(freezeStatus)) {
                        if ("admin".equalsIgnoreCase(admin)) {
                            initializeApp();
                            saveFCMTokenToDatabase();
                        } else {
                            initializeEntrantApp();
                            saveFCMTokenToDatabase();
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

    }

    /**
     * Initializes the app with admin features, configuring notifications and loading event data.
     */
    private void initializeApp() {

        initializeUI();
        setOnClickListeners();
        admin.setVisibility(View.VISIBLE);

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
        list_toggle = findViewById(R.id.ListToggle);
        scroll_toggle = findViewById(R.id.ExploreToggle);
        Toggle = findViewById(R.id.toggle);
        Log.wtf(TAG, "hello hello");

        if (findViewById(R.id.ListToggle).isSelected() || ((RadioButton) findViewById(R.id.ListToggle)).isChecked()) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.scrollListFragment, new Recycler_fragment())
                    .commit();
        }
        Toggle.setOnCheckedChangeListener((button, checkedID) -> {
            Log.wtf(TAG, "Checked ID: " + checkedID);
            Fragment fragment;
            if (checkedID == R.id.ListToggle) {
                Log.wtf(TAG, "List checked");
                fragment = new Recycler_fragment();

            } else if (checkedID == R.id.ExploreToggle) {
                fragment = new SwipeView();
            } else {
                fragment = null;
            }

            getSupportFragmentManager().beginTransaction().replace(R.id.scrollListFragment, fragment).commit();
        });

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


        explore.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ExploreEvents.class);
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

        // Fetch all event documents in EVENT_PROFILES
//        db.collection("EVENT_PROFILES")
//                .get()
//                .addOnSuccessListener(queryDocumentSnapshots -> {
//                    Log.d("realTimeListener", "Fetched " + queryDocumentSnapshots.size() + " event profiles");
//                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
//                        String eventId = document.getId();
//                        listenToEventProfileChanges(eventId);
//                    }
//
//                })
//                .addOnFailureListener(e -> Log.e("MainActivity", "Failed to fetch event profiles", e));

    }


    /**
     * Saves the FCM token to the Firestore database.
     */
    private void saveFCMTokenToDatabase() {
        Log.d("Accessing FCM method", "Saving FCM token to database");
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                String fcmToken = task.getResult();
                // Save FCM token to Firestore
                FirebaseFirestore.getInstance().collection("USER_PROFILES").document(USER_ID)
                        .update("fcmToken", fcmToken)
                        .addOnSuccessListener(aVoid -> {
                            System.out.println("FCM token saved successfully: " + fcmToken);
                        })
                        .addOnFailureListener(e -> {
                            System.err.println("Error saving FCM token: " + e.getMessage());
                        });
            } else {
                System.err.println("Failed to retrieve FCM token: " + (task.getException() != null ? task.getException().getMessage() : "Unknown error"));
            }
        });
    }

    //private void listenToEventProfileChanges(String eventId) {
    //    DocumentReference eventDocRef = db.collection("EVENT_PROFILES").document(eventId);

    //    eventDocRef.addSnapshotListener((snapshot, error) -> {
    //        if (error != null) {
    //            Log.e("MainActivity", "Error listening to event profile changes", error);
    //            return;
    //        }

    //        if (snapshot != null && snapshot.exists()) {
    //            Map<String, Object> data = snapshot.getData();
    //            if (data != null) {
    //                // Extract current state of groups
    //                List<DocumentReference> currentWaitlist = (List<DocumentReference>) snapshot.get("waitlist");
    //                List<DocumentReference> currentPending = (List<DocumentReference>) snapshot.get("pending");
    //                List<DocumentReference> currentAccepted = (List<DocumentReference>) snapshot.get("accepted");
    //                List<DocumentReference> currentRejected = (List<DocumentReference>) snapshot.get("rejected");
    //                Log.d("listenToEventProfileChanges", eventId + " Event data changed " + currentPending);

    //                // Initialize or fetch previous states
    //                Map<String, List<DocumentReference>> previousEventState = previousStates.computeIfAbsent(eventId, k -> new HashMap<>());

    //                processGroupChanges(eventId, "Waitlist", previousEventState.get("Waitlist"), currentWaitlist);
    //                processGroupChanges(eventId, "Pending", previousEventState.get("Pending"), currentPending);
    //                processGroupChanges(eventId, "Accepted", previousEventState.get("Accepted"), currentAccepted);
    //                processGroupChanges(eventId, "Rejected", previousEventState.get("Rejected"), currentRejected);

    //                // Update the previous state with the current state
    /*                previousEventState.put("Waitlist", currentWaitlist);
                    previousEventState.put("Pending", currentPending);
                    previousEventState.put("Accepted", currentAccepted);
                    previousEventState.put("Rejected", currentRejected);
                }
            }
        });
    } */

    //private void processGroupChanges(String eventId, String groupName, List<DocumentReference> previousGroup, List<DocumentReference> currentGroup) {
    //    if (currentGroup == null) currentGroup = new ArrayList<>();
    //    if (previousGroup == null) previousGroup = new ArrayList<>();

    //Notifications notifications = new Notifications();
    //    //FirebaseMessaging messaging = FirebaseMessaging.getInstance();
    //    String topic = groupName + "-" + eventId;

        // Determine which users to unsubscribe (in previousGroup but not in currentGroup)
    //    for (DocumentReference userRef : previousGroup) {
    //        if (!currentGroup.contains(userRef)) {
    //            notifications.unsubscribeFromTopic(topic);
    //                    //.addOnSuccessListener(aVoid -> Log.d("MainActivity", "Unsubscribed from " + topic))
    //                   //.addOnFailureListener(e -> Log.e("MainActivity", "Failed to unsubscribe from " + topic, e));
    //        }
    //    }

        // Determine which users to subscribe (in currentGroup but not in previousGroup)
    //    for (DocumentReference userRef : currentGroup) {
    //        if (!previousGroup.contains(userRef)) {
    //            notifications.subscribeToTopic(topic);
    //                    //.addOnSuccessListener(aVoid -> Log.d("MainActivity", "Subscribed to " + topic))
    //                    //.addOnFailureListener(e -> Log.e("MainActivity", "Failed to subscribe to " + topic, e));
    //        }
    //    }
    //}
}


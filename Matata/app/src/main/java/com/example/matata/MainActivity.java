package com.example.matata;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
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
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.splashscreen.SplashScreen;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * The {@code MainActivity} class serves as the central hub for the Matata app, managing navigation between features,
 * such as user profiles, event creation, event history, and administrative sections.
 *
 * <h2>Key Features:</h2>
 * <ul>
 *     <li>Dynamic initialization of user profiles in Firebase Firestore.</li>
 *     <li>Role-based UI adaptation for admins and entrants.</li>
 *     <li>Navigation to key features, such as QR scanning, event history, and facility profiles.</li>
 *     <li>Support for real-time updates and push notifications.</li>
 * </ul>
 *
 * <h2>Class Responsibilities:</h2>
 * <ul>
 *     <li>Checks and creates user profiles if not already present in Firestore.</li>
 *     <li>Handles role-based customization for admin and entrant users.</li>
 *     <li>Subscribes and unsubscribes users from relevant Firebase Cloud Messaging (FCM) topics.</li>
 *     <li>Loads and listens for event data changes dynamically.</li>
 * </ul>
 *
 * <h2>Dependencies:</h2>
 * This class requires:
 * <ul>
 *     <li>Firebase Firestore for user and event data storage.</li>
 *     <li>Firebase Messaging for push notifications.</li>
 *     <li>UI components defined in {@code activity_main.xml}.</li>
 * </ul>
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

    private List<DocumentReference> myList;





    /**
     * Called when the activity is first created. Initializes user profiles in Firestore if necessary,
     * sets up UI components, handles notification permissions, and loads event data.
     *
     * @param savedInstanceState Bundle containing the activity's previously saved state, if any.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {




        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);

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
        Notifications notifications = new Notifications();
        notifications.subscribeToTopic("All");


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
                    Log.d("MainActivity", "User profile created");

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

        userRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                Log.w("Snapshot listener", "Snapshot listener triggered, try to update subscription. ");
                if (error != null) {
                    Log.w(TAG, "Listen failed.", error);
                    return;
                }

                if (value != null){
                    loadMyEvents();
                }
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

            Typeface boldFont = ResourcesCompat.getFont(this, R.font.sansation_bold);
            Typeface regularFont = ResourcesCompat.getFont(this, R.font.sansation_light);

            if (checkedID == R.id.ListToggle) {

                list_toggle.setTypeface(list_toggle.getTypeface(), Typeface.BOLD);
                scroll_toggle.setTypeface(scroll_toggle.getTypeface(), Typeface.NORMAL);

                list_toggle.setTypeface(boldFont);
                scroll_toggle.setTypeface(regularFont);

                Log.wtf(TAG, "List checked");
                fragment = new Recycler_fragment();

            } else if (checkedID == R.id.ExploreToggle) {
                scroll_toggle.setTypeface(scroll_toggle.getTypeface(), Typeface.BOLD);
                list_toggle.setTypeface(list_toggle.getTypeface(), Typeface.NORMAL);
                scroll_toggle.setTypeface(boldFont);
                list_toggle.setTypeface(regularFont);

                fragment = new SwipeView();
            } else {
                fragment = null;
            }

            getSupportFragmentManager().beginTransaction().replace(R.id.scrollListFragment, fragment).commit();
        });
        completeLoad = true;
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
            intent.putExtra("prev_act","main");
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

    private void loadMyEvents () {
        DocumentReference userRef = db.collection("USER_PROFILES").document(USER_ID);
        userRef.get().addOnCompleteListener(task -> {
            DocumentSnapshot document = task.getResult();
            if (document.exists()) {
                myList = (List<DocumentReference>) document.get("myList");
                Log.d(TAG, "loadMyEvents: " + myList + "update subscription next");
            }
            updateNotificationSubscription();
        });
    }

    private void updateNotificationSubscription () {
        if (myList != null) {
            for (int i = 0; i < myList.size(); i++) {
                Log.d("User list", "subscribe update started");
                DocumentReference currentDocRef = myList.get(i);
                currentDocRef.get().addOnCompleteListener(task -> {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        DocumentReference userRef = db.collection("USER_PROFILES").document(USER_ID);
                        Notifications notifications = new Notifications();


                        List<DocumentReference> currentWaitlist = (List<DocumentReference>) document.get("waitlist");
                        List<DocumentReference> currentPending = (List<DocumentReference>) document.get("pending");
                        List<DocumentReference> currentAccepted = (List<DocumentReference>) document.get("accepted");
                        List<DocumentReference> currentRejected = (List<DocumentReference>) document.get("rejected");

                        if (currentWaitlist != null && currentWaitlist.contains(userRef)) {
                            notifications.subscribeToTopic("Waitlist-" + currentDocRef.getId());
                            notifications.unsubscribeFromTopic("Pending-" + currentDocRef.getId());
                            notifications.unsubscribeFromTopic("Accepted-" + currentDocRef.getId());
                            notifications.unsubscribeFromTopic("Rejected-" + currentDocRef.getId());

                            Log.d("User list", "User in waitlist.");

                        } else if (currentPending != null && currentPending.contains(userRef)) {
                            notifications.subscribeToTopic("Pending-" + currentDocRef.getId());
                            notifications.unsubscribeFromTopic("Waitlist-" + currentDocRef.getId());
                            notifications.unsubscribeFromTopic("Accepted-" + currentDocRef.getId());
                            notifications.unsubscribeFromTopic("Rejected-" + currentDocRef.getId());

                            Log.d("User list", "User in pending.");

                        } else if (currentAccepted != null && currentAccepted.contains(userRef)) {
                            notifications.subscribeToTopic("Accepted-" + currentDocRef.getId());
                            notifications.unsubscribeFromTopic("Waitlist-" + currentDocRef.getId());
                            notifications.unsubscribeFromTopic("Pending-" + currentDocRef.getId());
                            notifications.unsubscribeFromTopic("Rejected-" + currentDocRef.getId());

                            Log.d("User list", "User in accepted.");

                        } else if (currentRejected != null && currentRejected.contains(userRef)) {
                            notifications.subscribeToTopic("Rejected-" + currentDocRef.getId());
                            notifications.unsubscribeFromTopic("Waitlist-" + currentDocRef.getId());
                            notifications.unsubscribeFromTopic("Pending-" + currentDocRef.getId());
                            notifications.unsubscribeFromTopic("Accepted-" + currentDocRef.getId());

                            Log.d("User list", "User in rejected.");

                        } else {
                            notifications.unsubscribeFromTopic("Waitlist-" + currentDocRef.getId());
                            notifications.unsubscribeFromTopic("Pending-" + currentDocRef.getId());
                            notifications.unsubscribeFromTopic("Accepted-" + currentDocRef.getId());
                            notifications.unsubscribeFromTopic("Rejected-" + currentDocRef.getId());

                            Log.d("User list", "User not in any list");
                        }
                    }
                });
                Log.d("User list", "subscribe update ended");
            }
        } else {
            Log.d("User list", "myList is null");
        }
    }
}

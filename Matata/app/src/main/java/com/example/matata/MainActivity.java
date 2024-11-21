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
                } else {
                    // Check the freeze status
                    String freezeStatus = task.getResult().getString("freeze");
                    String admin = task.getResult().getString("admin");
                    if ("awake".equalsIgnoreCase(freezeStatus)) {
                        if("admin".equalsIgnoreCase(admin)) {
                            initializeApp();
                        }
                        else {
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
            else if(checkedID==R.id.ExploreToggle){
                fragment=new SwipeView();
            }
            else{
                fragment=null;
            }

            getSupportFragmentManager().beginTransaction().replace(R.id.scrollListFragment,fragment).commit();
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

}

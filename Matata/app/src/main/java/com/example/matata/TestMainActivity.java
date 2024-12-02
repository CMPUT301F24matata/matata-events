package com.example.matata;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The `TestMainActivity` class simulates the main activity of the Matata app for testing purposes.
 * It does not integrate with Firebase or external services and uses mock data to facilitate UI interactions and testing.
 *
 * <h2>Features:</h2>
 * <ul>
 *     <li>Provides a navigation hub for the application's primary features such as user profiles, event history, and admin tools.</li>
 *     <li>Displays and toggles between different views using fragments (list and swipe views).</li>
 *     <li>Handles mock data initialization for events.</li>
 *     <li>Supports navigation to various app sections through click listeners.</li>
 * </ul>
 */
public class TestMainActivity extends AppCompatActivity {

    /**
     * Tag used for logging.
     */
    private static final String TAG = "TestMainActivity";

    /**
     * ImageView for navigating to the user profile.
     */
    private ImageView profileIcon;

    /**
     * ImageView for navigating to the "Add Event" screen.
     */
    private ImageView newEvent;

    /**
     * LinearLayout for accessing the event history.
     */
    private LinearLayout eventHistory;

    /**
     * LinearLayout for exploring events on a map.
     */
    private LinearLayout explore;

    /**
     * ImageView for accessing the facility profile section.
     */
    private ImageView facilityProfile;

    /**
     * ImageView for accessing the admin section.
     */
    private ImageView admin;

    /**
     * RadioButton for toggling to the event list view.
     */
    private RadioButton listToggle;

    /**
     * RadioButton for toggling to the event swipe view.
     */
    private RadioButton scrollToggle;

    /**
     * RadioGroup for managing the view toggles.
     */
    private RadioGroup toggleGroup;

    /**
     * The unique user ID derived from the device's secure settings.
     */
    private String USER_ID;

    /**
     * Mock list of event data used for testing.
     */
    private List<Map<String, Object>> mockEventList;

    /**
     * Called when the activity is created. Initializes UI components, mock data, and click listeners.
     * Sets up the default fragment view and logs initialization details.
     *
     * @param savedInstanceState The saved state of the activity, if available.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        USER_ID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        initializeUI();
        setOnClickListeners();

        Log.d(TAG, "App initialized successfully for user: " + USER_ID);
    }

    /**
     * Initializes mock data for testing purposes.
     */
    private void initializeMockData() {
        mockEventList = new ArrayList<>();

        mockEventList.add(createMockEvent("Event A", "01/12/2024", "Active"));
        mockEventList.add(createMockEvent("Event B", "15/11/2024", "Inactive"));
        mockEventList.add(createMockEvent("Event C", "20/10/2024", "Active"));

        Log.d(TAG, "Mock event data initialized.");
    }

    /**
     * Creates a mock event map.
     *
     * @param title  Event title.
     * @param date   Event date.
     * @param status Event status (Active/Inactive).
     * @return A map representing the event data.
     */
    private Map<String, Object> createMockEvent(String title, String date, String status) {
        Map<String, Object> event = new HashMap<>();
        event.put("title", title);
        event.put("date", date);
        event.put("status", status);
        return event;
    }

    /**
     * Initializes UI components.
     */
    private void initializeUI() {
        profileIcon = findViewById(R.id.profile_picture);
        newEvent = findViewById(R.id.add_event);
        eventHistory = findViewById(R.id.event_history);
        explore = findViewById(R.id.event_map);
        facilityProfile = findViewById(R.id.FacilityProfile);
        admin = findViewById(R.id.admin);
        listToggle = findViewById(R.id.ListToggle);
        scrollToggle = findViewById(R.id.ExploreToggle);
        toggleGroup = findViewById(R.id.toggle);

        if (listToggle.isChecked()) {
            loadFragment(new Recycler_fragment());
        }

        toggleGroup.setOnCheckedChangeListener((group, checkedId) -> {
            Fragment fragment;
            if (checkedId == R.id.ListToggle) {
                fragment = new Recycler_fragment();
            } else if (checkedId == R.id.ExploreToggle) {
                fragment = new SwipeView();
            } else {
                fragment = null;
            }
            loadFragment(fragment);
        });

        Log.d(TAG, "UI initialized.");
    }

    /**
     * Sets up click listeners for UI components.
     */
    private void setOnClickListeners() {
        profileIcon.setOnClickListener(view -> navigateTo(ProfileActivity.class));

        newEvent.setOnClickListener(view -> navigateTo(AddEvent.class));

        eventHistory.setOnClickListener(view -> navigateTo(EventHistory.class));

        explore.setOnClickListener(view -> navigateTo(ExploreEvents.class));

        facilityProfile.setOnClickListener(view -> navigateTo(FacilityActivity.class));

        admin.setOnClickListener(view -> navigateTo(AdminView.class));

        Log.d(TAG, "Click listeners set up.");
    }

    /**
     * Loads a fragment into the main container.
     *
     * @param fragment The fragment to load.
     */
    private void loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.scrollListFragment, fragment)
                    .commit();
        }
    }

    /**
     * Navigates to a specified activity class.
     *
     * @param activityClass The activity class to navigate to.
     */
    private void navigateTo(Class<?> activityClass) {
        Intent intent = new Intent(this, activityClass);
        startActivity(intent);
    }
}

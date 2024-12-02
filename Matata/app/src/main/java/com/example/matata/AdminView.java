package com.example.matata;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


/**
 * The {@code AdminView} class serves as the main dashboard for administrators within the application.
 * <p>
 * This activity provides navigation and management options for:
 * <ul>
 *   <li>Events</li>
 *   <li>Users</li>
 *   <li>Facilities</li>
 *   <li>Reports</li>
 *   <li>Notifications</li>
 * </ul>
 * Admins can view and manage these entities using expandable dropdown menus, each populated with
 * data fetched from Firebase Firestore. The class also provides navigation to detailed views for
 * individual entities and other administrative features.
 */
public class AdminView extends AppCompatActivity {

    // Dropdowns for displaying lists
    /**
     * A {@link LinearLayout} used to display the dropdown menu for events.
     */
    private LinearLayout eventsDropdown;

    /**
     * A {@link LinearLayout} used to display the dropdown menu for users.
     */
    private LinearLayout usersDropdown;

    /**
     * A {@link LinearLayout} used to display the dropdown menu for facilities.
     */
    private LinearLayout facilitiesDropdown;

    // Dropdown toggle buttons
    /**
     * A {@link TextView} that acts as a button to toggle the visibility of the events dropdown menu.
     */
    private TextView eventsDropdownButton;

    /**
     * A {@link TextView} that acts as a button to toggle the visibility of the users dropdown menu.
     */
    private TextView usersDropdownButton;

    /**
     * A {@link TextView} that acts as a button to toggle the visibility of the facilities dropdown menu.
     */
    private TextView facilitiesDropdownButton;

    // Bottom navigation icons
    /**
     * An {@link ImageView} representing the Dashboard navigation icon in the bottom navigation bar.
     */
    private ImageView iconDashboard;

    /**
     * An {@link ImageView} representing the Users navigation icon in the bottom navigation bar.
     */
    private ImageView iconUsers;

    /**
     * An {@link ImageView} representing the Reports navigation icon in the bottom navigation bar.
     */
    private ImageView iconReports;

    /**
     * An {@link ImageView} representing the Notifications navigation icon in the bottom navigation bar.
     */
    private ImageView iconNotifications;

    /**
     * An {@link ImageView} representing the Settings navigation icon in the bottom navigation bar.
     */
    private ImageView iconSettings;

    // Navigation links for detailed views
    /**
     * A {@link TextView} to navigate to the screen displaying all events.
     */
    private TextView viewAllEvents;

    /**
     * A {@link TextView} to navigate to the screen displaying all users.
     */
    private TextView viewAllUsers;

    /**
     * A {@link TextView} to navigate to the screen displaying all facilities.
     */
    private TextView viewAllFacilities;

    /**
     * A {@link TextView} to navigate to the screen displaying all uploaded images.
     */
    private TextView viewAllImages;

    /**
     * A {@link TextView} to navigate to the screen displaying all hashed QR data.
     */
    private TextView viewAllQR;

    // Resources for styling dropdown items
    /**
     * A {@link Typeface} used to style the text of dropdown items.
     */
    private Typeface sansationBoldTypeface;

    /**
     * A {@link Drawable} used to define the background border for dropdown items.
     */
    private Drawable dropdown_item_border;

    // Firebase Firestore instance
    /**
     * A {@link FirebaseFirestore} instance used to interact with the Firestore database.
     */
    private FirebaseFirestore db;

    /**
     * Admin state for managing notifications.
     */
    private String admin;

    /**
     * Initializes the activity and sets up the UI components, event listeners, and Firestore interactions.
     *
     * @param savedInstanceState A {@link Bundle} containing the activity's previously saved state, if any.
     */
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_view);

        initialise();

        setonClickListeners();

        fetchFromFirestore();
    }

    /**
     * Fetches data from Firestore collections for events, users, and facilities, and populates the respective dropdowns.
     * <p>
     * This method makes asynchronous calls to the Firestore database and updates the UI with the fetched data.
     */
    private void fetchFromFirestore() {
        // Fetch events
        eventsDropdown.removeAllViews();
        db.collection("EVENT_PROFILES")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        for (QueryDocumentSnapshot document : querySnapshot) {
                            String eventTitle = document.getString("Title");
                            String eventId = document.getId();
                            String orgId = document.getString("OrganizerID");
                            if (eventTitle != null) {
                                addTextViewToDropdown(eventsDropdown, eventTitle, v -> {
                                    Intent intent = new Intent(this, ViewEvent.class);
                                    intent.putExtra("EVENT_ID", eventId);
                                    intent.putExtra("ORG_ID", orgId);
                                    intent.putExtra("IS_ADMIN_VIEW", true);
                                    startActivity(intent);
                                });
                            }
                        }
                    } else {
                        showToast("Failed to load events");
                    }
                });


        // Fetch users
        usersDropdown.removeAllViews();
        db.collection("USER_PROFILES")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String username = document.getString("username");
                            String userId = document.getId();
                            if (username != null && !username.isEmpty()) {
                                addTextViewToDropdown(usersDropdown, username, v -> {
                                    Intent intent = new Intent(this, ProfileActivity.class);
                                    intent.putExtra("USER_ID", userId);
                                    startActivity(intent);
                                });
                            }
                        }
                    } else {
                        showToast("Failed to load users");
                    }
                });

        // Fetch facilities
        facilitiesDropdown.removeAllViews();
        db.collection("FACILITY_PROFILES")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String facilityName = document.getString("name");
                            String facilityId = document.getId();

                            if (facilityName != null) {
                                addTextViewToDropdown(facilitiesDropdown, facilityName, v -> {
                                    Intent intent = new Intent(this, FacilityActivity.class);
                                    intent.putExtra("FACILITY_ID", facilityId);
                                    startActivity(intent);
                                });
                            }
                        }
                    } else {
                        showToast("Failed to load facilities");
                    }
                });
    }

    /**
     * Displays a short {@link Toast} message to the user.
     *
     * @param message The message to be displayed in the toast.
     */
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Adds a {@link TextView} to the specified dropdown layout with the given text and click listener.
     * <p>
     * The added {@link TextView} is styled with a background, padding, and a right arrow icon to indicate navigability.
     *
     * @param dropdown       The {@link LinearLayout} to which the {@link TextView} will be added.
     * @param text           The text to display in the {@link TextView}.
     * @param clickListener  The click listener to attach to the {@link TextView}.
     */
    private void addTextViewToDropdown(LinearLayout dropdown, String text, View.OnClickListener clickListener) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setTextColor(ContextCompat.getColor(this, R.color.white));
        textView.setPadding(8, 8, 8, 8);
        textView.setTextSize(16);
        textView.setTypeface(sansationBoldTypeface);
        textView.setBackground(dropdown_item_border);
        textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_right, 0);
        textView.setCompoundDrawablePadding(8);

        if (clickListener != null) {
            textView.setOnClickListener(clickListener);
        }

        dropdown.addView(textView);
    }

    /**
     * Sets onClick listeners for UI components including back button, dropdown toggles, and navigation icons.
     * Defines actions for navigating to other screens or toggling dropdowns.
     */
    private void setonClickListeners() {
        eventsDropdownButton.setOnClickListener(v -> toggleDropdown(eventsDropdown, eventsDropdownButton));
        usersDropdownButton.setOnClickListener(v -> toggleDropdown(usersDropdown, usersDropdownButton));
        facilitiesDropdownButton.setOnClickListener(v -> toggleDropdown(facilitiesDropdown, facilitiesDropdownButton));
        // navigation bar click listeners
        iconDashboard.setOnClickListener(v -> {
            Intent intent = new Intent(AdminView.this, AdminView.class);
            startActivity(intent);
            finish();
        });

        iconReports.setOnClickListener(v -> {
            Intent intent = new Intent(AdminView.this, AdminReportActivity.class);
            startActivity(intent);
            finish();
        });

        iconSettings.setOnClickListener(v -> {
            Intent intent = new Intent(AdminView.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        iconNotifications.setOnClickListener(v -> {
            NotificationDialogFragment dialog = new NotificationDialogFragment();
            // Pass the uid as an argument to the fragment
            Bundle args = new Bundle();
            args.putString("admin", "true");
            dialog.setArguments(args);
            dialog.show(getSupportFragmentManager(), "NotificationDialog");
        });

        viewAllEvents.setOnClickListener(v -> {
            Intent intent = new Intent(AdminView.this, ManageAllEventsActivity.class);
            startActivity(intent);
        });

        viewAllUsers.setOnClickListener(v -> {
            Intent intent = new Intent(AdminView.this, ManageAllUsersActivity.class);
            startActivity(intent);
        });

        viewAllFacilities.setOnClickListener(v -> {
            Intent intent = new Intent(AdminView.this, ManageAllFacilityActivity.class);
            startActivity(intent);
        });

        viewAllImages.setOnClickListener(v -> {
            Intent intent = new Intent(AdminView.this, ManageAllImagesActivity.class);
            startActivity(intent);
        });

        viewAllQR.setOnClickListener(v -> {
            Intent intent = new Intent(AdminView.this, ManageAllQRActivity.class);
            startActivity(intent);
        });
    }

    /**
     * Initializes all UI components, sets typefaces and drawable resources, and initializes the Firestore instance.
     */
    private void initialise() {
        sansationBoldTypeface = ResourcesCompat.getFont(this, R.font.sansation_bold);
        dropdown_item_border = ContextCompat.getDrawable(this, R.drawable.dropdown_item_border);
        db = FirebaseFirestore.getInstance();
        eventsDropdown = findViewById(R.id.events_dropdown);
        usersDropdown = findViewById(R.id.users_dropdown);
        facilitiesDropdown = findViewById(R.id.facilities_dropdown);
        eventsDropdownButton = findViewById(R.id.events_dropdown_button);
        usersDropdownButton = findViewById(R.id.users_dropdown_button);
        facilitiesDropdownButton = findViewById(R.id.facilities_dropdown_button);
        iconDashboard = findViewById(R.id.icon_dashboard);
        iconUsers = findViewById(R.id.icon_users);
        iconReports = findViewById(R.id.icon_reports);
        iconNotifications = findViewById(R.id.icon_notifications);
        iconSettings = findViewById(R.id.icon_settings);
        viewAllEvents = findViewById(R.id.view_all_events);
        viewAllUsers = findViewById(R.id.view_all_users);
        viewAllFacilities = findViewById(R.id.view_all_facilities);
        viewAllImages = findViewById(R.id.view_all_images);
        viewAllQR = findViewById(R.id.view_all_QR);
    }

    /**
     * Toggles the visibility of the specified dropdown layout and updates the icon on the toggle button.
     *
     * @param dropdownLayout The {@link LinearLayout} representing the dropdown menu.
     * @param dropdownButton The {@link TextView} acting as the toggle button.
     */
    private void toggleDropdown(LinearLayout dropdownLayout, TextView dropdownButton) {
        if (dropdownLayout.getVisibility() == View.GONE) {
            dropdownLayout.setVisibility(View.VISIBLE);
            dropdownButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_expand_less, 0);
        } else {
            dropdownLayout.setVisibility(View.GONE);
            dropdownButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_expand_more, 0);
        }
    }
}

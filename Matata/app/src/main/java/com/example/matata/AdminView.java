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

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


/**
 * The AdminView class provides an admin dashboard for managing events, organizers, users, and facilities.
 * Admins can view lists of these entities, navigate to their detailed views, and perform administrative actions.
 * The class fetches data from Firebase Firestore and displays it in expandable dropdown menus.
 */
public class AdminView extends AppCompatActivity {

    /**
     * LinearLayout for displaying the dropdown menu of events.
     */
    private LinearLayout eventsDropdown;

    /**
     * LinearLayout for displaying the dropdown menu of users.
     */
    private LinearLayout usersDropdown;

    /**
     * LinearLayout for displaying the dropdown menu of facilities.
     */
    private LinearLayout facilitiesDropdown;

    /**
     * TextView acting as a button for toggling the events dropdown.
     */
    private TextView eventsDropdownButton;

    /**
     * TextView acting as a button for toggling the users dropdown.
     */
    private TextView usersDropdownButton;

    /**
     * TextView acting as a button for toggling the facilities dropdown.
     */
    private TextView facilitiesDropdownButton;

    /**
     * ImageView for the Dashboard navigation icon in the bottom navigation bar.
     */
    private ImageView iconDashboard;

    /**
     * ImageView for the Users navigation icon in the bottom navigation bar.
     */
    private ImageView iconUsers;

    /**
     * ImageView for the Reports navigation icon in the bottom navigation bar.
     */
    private ImageView iconReports;

    /**
     * ImageView for the Notifications navigation icon in the bottom navigation bar.
     */
    private ImageView iconNotifications;

    /**
     * ImageView for the Settings navigation icon in the bottom navigation bar.
     */
    private ImageView iconSettings;

    /**
     * TextView to navigate to a screen displaying all events.
     */
    private TextView viewAllEvents;

    /**
     * TextView to navigate to a screen displaying all users.
     */
    private TextView viewAllUsers;

    /**
     * TextView to navigate to a screen displaying all facilities.
     */
    private TextView viewAllFacilities;

    /**
     * TextView to navigate to a screen displaying all images.
     */
    private TextView viewAllImages;

    /**
     * TextView to navigate to a screen displaying all hashed QR data.
     */
    private TextView viewAllQR;

    /**
     * Instance of FirebaseFirestore to interact with Firestore.
     */
    private FirebaseFirestore db;

    /**
     * Typeface for dropdown item text.
     */
    private Typeface sansationBoldTypeface;

    /**
     * Drawable for the border of dropdown items.
     */
    private Drawable dropdown_item_border;

    /**
     * Called when the activity is first created.
     * Initializes UI components, sets up event listeners, and fetches data from Firestore.
     *
     * @param savedInstanceState Bundle containing the activity's previously saved state, if any.
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
     * Fetches data from Firestore collections for events, organizers, users, and facilities,
     * and populates the respective dropdowns.
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
     * Displays a toast message with the specified text.
     *
     * @param message The message to display in the toast.
     */
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Adds a TextView to the specified dropdown layout with the given text and click listener.
     * The TextView is styled with a background, padding, and a drawable arrow icon.
     *
     * @param dropdown The LinearLayout to which the TextView will be added.
     * @param text The text to display in the TextView.
     * @param clickListener The click listener to attach to the TextView.
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
     * Toggles the visibility of the specified dropdown layout and updates the dropdown button icon accordingly.
     * Expands the dropdown if it is hidden, or collapses it if it is visible.
     *
     * @param dropdownLayout The LinearLayout representing the dropdown menu.
     * @param dropdownButton The TextView button associated with the dropdown menu.
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

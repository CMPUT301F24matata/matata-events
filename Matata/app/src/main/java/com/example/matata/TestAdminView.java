package com.example.matata;

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

import java.util.Arrays;
import java.util.List;

/**
 * The `TestAdminView` class provides a simplified version of an admin dashboard.
 * It allows administrators to navigate to lists of events, users, and facilities.
 * The class uses hardcoded data to populate the dropdowns for testing purposes.
 *
 * <h2>Features:</h2>
 * <ul>
 *     <li>Dropdowns for viewing events, users, and facilities.</li>
 *     <li>Icons for navigating to different admin-related sections.</li>
 *     <li>Expandable/collapsible dropdowns for better UI interaction.</li>
 * </ul>
 */
public class TestAdminView extends AppCompatActivity {

    /**
     * Layouts for dropdowns displaying events, users, and facilities.
     */
    private LinearLayout eventsDropdown, usersDropdown, facilitiesDropdown;

    /**
     * Buttons for toggling the visibility of dropdown layouts.
     */
    private TextView eventsDropdownButton, usersDropdownButton, facilitiesDropdownButton;

    /**
     * Navigation icons for dashboard, users, reports, notifications, and settings.
     */
    private ImageView iconDashboard, iconUsers, iconReports, iconNotifications, iconSettings;

    /**
     * TextViews for viewing all events, users, and facilities.
     */
    private TextView viewAllEvents, viewAllUsers, viewAllFacilities;

    /**
     * Custom typeface for styling dropdown items.
     */
    private Typeface sansationBoldTypeface;

    /**
     * Drawable used for the borders of dropdown items.
     */
    private Drawable dropdown_item_border;

    /**
     * Called when the activity is created.
     * Initializes UI components, sets up click listeners, and populates dropdowns with hardcoded data.
     *
     * @param savedInstanceState Bundle containing the activity's previously saved state, if any.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_view);

        initialise();
        setOnClickListeners();
        populateData();
    }

    /**
     * Populates hardcoded data for events, users, and facilities into dropdowns.
     */
    private void populateData() {
        // Sample events
        List<String> events = Arrays.asList("Cultural Night", "Tech Fest", "Annual Gala");
        for (String event : events) {
            addTextViewToDropdown(eventsDropdown, event, v -> {
                Intent intent = new Intent(this, ViewEvent.class);
                intent.putExtra("EVENT_NAME", event);
                startActivity(intent);
            });
        }

        // Sample users
        List<String> users = Arrays.asList("Alice Johnson", "Bob Smith", "Charlie Davis");
        for (String user : users) {
            addTextViewToDropdown(usersDropdown, user, v -> {
                Intent intent = new Intent(this, ProfileActivity.class);
                intent.putExtra("USER_NAME", user);
                startActivity(intent);
            });
        }

        // Sample facilities
        List<String> facilities = Arrays.asList("Auditorium", "Sports Complex", "Library Hall");
        for (String facility : facilities) {
            addTextViewToDropdown(facilitiesDropdown, facility, v -> {
                Intent intent = new Intent(this, FacilityActivity.class);
                intent.putExtra("FACILITY_NAME", facility);
                startActivity(intent);
            });
        }
    }

    /**
     * Adds a TextView to the specified dropdown with the given text and click listener.
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
     * Sets onClick listeners for dropdown toggles and navigation icons.
     */
    private void setOnClickListeners() {
        eventsDropdownButton.setOnClickListener(v -> toggleDropdown(eventsDropdown, eventsDropdownButton));
        usersDropdownButton.setOnClickListener(v -> toggleDropdown(usersDropdown, usersDropdownButton));
        facilitiesDropdownButton.setOnClickListener(v -> toggleDropdown(facilitiesDropdown, facilitiesDropdownButton));

        iconDashboard.setOnClickListener(v -> Toast.makeText(this, "Dashboard Clicked", Toast.LENGTH_SHORT).show());
        iconReports.setOnClickListener(v -> Toast.makeText(this, "Reports Clicked", Toast.LENGTH_SHORT).show());
        iconSettings.setOnClickListener(v -> Toast.makeText(this, "Settings Clicked", Toast.LENGTH_SHORT).show());
        iconNotifications.setOnClickListener(v -> Toast.makeText(this, "Notifications Clicked", Toast.LENGTH_SHORT).show());
    }

    /**
     * Toggles the visibility of dropdown layouts.
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

    /**
     * Initializes UI components and resources.
     */
    private void initialise() {
        sansationBoldTypeface = ResourcesCompat.getFont(this, R.font.sansation_bold);
        dropdown_item_border = ContextCompat.getDrawable(this, R.drawable.dropdown_item_border);

        eventsDropdown = findViewById(R.id.events_dropdown);
        usersDropdown = findViewById(R.id.users_dropdown);
        facilitiesDropdown = findViewById(R.id.facilities_dropdown);
        eventsDropdownButton = findViewById(R.id.events_dropdown_button);
        usersDropdownButton = findViewById(R.id.users_dropdown_button);
        facilitiesDropdownButton = findViewById(R.id.facilities_dropdown_button);

        iconDashboard = findViewById(R.id.icon_dashboard);
        iconReports = findViewById(R.id.icon_reports);
        iconNotifications = findViewById(R.id.icon_notifications);
        iconSettings = findViewById(R.id.icon_settings);

        viewAllEvents = findViewById(R.id.view_all_events);
        viewAllUsers = findViewById(R.id.view_all_users);
        viewAllFacilities = findViewById(R.id.view_all_facilities);
    }
}

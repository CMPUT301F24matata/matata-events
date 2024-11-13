package com.example.matata;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class AdminView extends AppCompatActivity {

    private LinearLayout eventsDropdown, organizersDropdown, usersDropdown, facilitiesDropdown;
    private TextView eventsDropdownButton, organizersDropdownButton, usersDropdownButton, facilitiesDropdownButton;
    private ImageView iconDashboard;
    private ImageView iconUsers;
    private ImageView iconReports;
    private ImageView iconNotifications;
    private ImageView iconSettings;
    private TextView viewAllEvents;
    private TextView viewAllOrganizers;
    private TextView viewAllUsers;
    private TextView viewAllFacilities;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_view);

        // Initialize dropdown sections
        eventsDropdown = findViewById(R.id.events_dropdown);
        organizersDropdown = findViewById(R.id.organizers_dropdown);
        usersDropdown = findViewById(R.id.users_dropdown);
        facilitiesDropdown = findViewById(R.id.facilities_dropdown);

        // Initialize dropdown buttons
        eventsDropdownButton = findViewById(R.id.events_dropdown_button);
        organizersDropdownButton = findViewById(R.id.organizers_dropdown_button);
        usersDropdownButton = findViewById(R.id.users_dropdown_button);
        facilitiesDropdownButton = findViewById(R.id.facilities_dropdown_button);

        // Set onClickListeners for dropdown buttons
        eventsDropdownButton.setOnClickListener(v -> toggleDropdown(eventsDropdown, eventsDropdownButton));
        organizersDropdownButton.setOnClickListener(v -> toggleDropdown(organizersDropdown, organizersDropdownButton));
        usersDropdownButton.setOnClickListener(v -> toggleDropdown(usersDropdown, usersDropdownButton));
        facilitiesDropdownButton.setOnClickListener(v -> toggleDropdown(facilitiesDropdown, facilitiesDropdownButton));

        // navigation bar tools
        iconDashboard = findViewById(R.id.icon_dashboard);
        iconUsers = findViewById(R.id.icon_users);
        iconReports = findViewById(R.id.icon_reports);
        iconNotifications = findViewById(R.id.icon_notifications);
        iconSettings = findViewById(R.id.icon_settings);

        // view all buttons on the next to text view
        viewAllEvents = findViewById(R.id.view_all_events);
        viewAllOrganizers = findViewById(R.id.view_all_organizers);
        viewAllUsers = findViewById(R.id.view_all_users);
        viewAllFacilities = findViewById(R.id.view_all_facilities);

    }

    private void toggleDropdown(LinearLayout dropdownLayout, TextView dropdownButton) {
        if (dropdownLayout.getVisibility() == View.GONE) {
            // Show dropdown content and change icon to collapse
            dropdownLayout.setVisibility(View.VISIBLE);
            dropdownButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_expand_less, 0);
        } else {
            // Hide dropdown content and change icon to expand
            dropdownLayout.setVisibility(View.GONE);
            dropdownButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_expand_more, 0);
        }
    }
}

package com.example.matata;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
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

import java.lang.ref.Reference;
import java.util.Objects;

public class AdminView extends AppCompatActivity {

    private LinearLayout eventsDropdown, organizersDropdown, usersDropdown, facilitiesDropdown;
    private TextView eventsDropdownButton, organizersDropdownButton, usersDropdownButton, facilitiesDropdownButton;
    private ImageView iconDashboard;
    private ImageView iconUsers;
    private ImageView iconReports;
    private ImageView iconNotifications;
    private ImageView iconSettings;
    private ImageView backBtn;
    private TextView viewAllEvents;
    private TextView viewAllOrganizers;
    private TextView viewAllUsers;
    private TextView viewAllFacilities;
    private FirebaseFirestore db;
    private Typeface sansationBoldTypeface;
    private Drawable dropdown_item_border;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_view);

        initialise();

        setonClickListeners();

        fetchFromFirestore();
    }

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

        // Fetch organizers
        organizersDropdown.removeAllViews();
        db.collection("ORGANIZER_PROFILES")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot organizerDoc : task.getResult()) {
                            DocumentReference userProfileRef = organizerDoc.getDocumentReference("userReference");
                            if (userProfileRef != null) {
                                userProfileRef.get()
                                        .addOnCompleteListener(userTask -> {
                                            if (userTask.isSuccessful() && userTask.getResult().exists()) {
                                                String username = userTask.getResult().getString("username");
                                                String userId = userProfileRef.getId();
                                                if (username != null && !username.isEmpty()) {
                                                    addTextViewToDropdown(organizersDropdown, username, v -> {
                                                        Intent intent = new Intent(this, ProfileActivity.class);
                                                        intent.putExtra("USER_ID", userId);
                                                        startActivity(intent);
                                                    });
                                                }
                                            } else {
                                                showToast("User profile not found for organizer");
                                            }
                                        })
                                        .addOnFailureListener(e -> showToast("Failed to fetch user profile"));
                            }
                        }
                    } else {
                        showToast("Failed to load organizers");
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


    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

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

    private void setonClickListeners() {
        backBtn.setOnClickListener(v -> finish());
        eventsDropdownButton.setOnClickListener(v -> toggleDropdown(eventsDropdown, eventsDropdownButton));
        organizersDropdownButton.setOnClickListener(v -> toggleDropdown(organizersDropdown, organizersDropdownButton));
        usersDropdownButton.setOnClickListener(v -> toggleDropdown(usersDropdown, usersDropdownButton));
        facilitiesDropdownButton.setOnClickListener(v -> toggleDropdown(facilitiesDropdown, facilitiesDropdownButton));
        // navigation bar click listeners
        iconDashboard.setOnClickListener(v -> {
            Intent intent = new Intent(AdminView.this, AdminView.class);
            startActivity(intent);
        });
    }

    private void initialise() {
        sansationBoldTypeface = ResourcesCompat.getFont(this, R.font.sansation_bold);
        dropdown_item_border = ContextCompat.getDrawable(this, R.drawable.dropdown_item_border);
        db = FirebaseFirestore.getInstance();
        backBtn = findViewById(R.id.btnBack);
        eventsDropdown = findViewById(R.id.events_dropdown);
        organizersDropdown = findViewById(R.id.organizers_dropdown);
        usersDropdown = findViewById(R.id.users_dropdown);
        facilitiesDropdown = findViewById(R.id.facilities_dropdown);
        eventsDropdownButton = findViewById(R.id.events_dropdown_button);
        organizersDropdownButton = findViewById(R.id.organizers_dropdown_button);
        usersDropdownButton = findViewById(R.id.users_dropdown_button);
        facilitiesDropdownButton = findViewById(R.id.facilities_dropdown_button);
        iconDashboard = findViewById(R.id.icon_dashboard);
        iconUsers = findViewById(R.id.icon_users);
        iconReports = findViewById(R.id.icon_reports);
        iconNotifications = findViewById(R.id.icon_notifications);
        iconSettings = findViewById(R.id.icon_settings);
        viewAllEvents = findViewById(R.id.view_all_events);
        viewAllOrganizers = findViewById(R.id.view_all_organizers);
        viewAllUsers = findViewById(R.id.view_all_users);
        viewAllFacilities = findViewById(R.id.view_all_facilities);
    }

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

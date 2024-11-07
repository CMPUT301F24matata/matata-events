package com.example.matata;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private ImageView profileIcon, new_event, eventHistory, eventSearch;
    private RecyclerView recyclerView;
    private EventAdapter eventAdapter;
    private List<Event> eventList;
    private FloatingActionButton QR_scanner;
    private FirebaseFirestore db;
    private String USER_ID = "";
    private String uid = null;
    private ImageButton notificationButton;

    private ActivityResultLauncher<String> notificationPermissionLauncher;
    private Notification notificationManager;
    private static final String CHANNEL_ID = "waitlist_notification_channel";

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

        // Initialize user profile if not exists
        DocumentReference userRef = db.collection("USER_PROFILES").document(USER_ID);
        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null && !task.getResult().exists()) {
                Map<String, Object> userProfile = new HashMap<>();
                userProfile.put("username", "");
                userProfile.put("phone", "");
                userProfile.put("email", "");
                userProfile.put("notifications", false);
                userProfile.put("profileUri", "");
                userRef.set(userProfile);
            }
        });

        initializeUI();
        setOnClickListeners();

        // Set up permission launcher and notification manager
        notificationPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> Log.d("NotificationPermission", isGranted ? "Permission granted" : "Permission denied")
        );
        notificationManager = new Notification();
        notificationManager.setNotificationPermissionLauncher(notificationPermissionLauncher);

        // Initialize notification channel
        Notification.initNotificationChannel(this);

        // Load events
        addEventsInit();
    }

    private void initializeUI() {
        profileIcon = findViewById(R.id.profile_picture);
        new_event = findViewById(R.id.add_event);
        QR_scanner = findViewById(R.id.qr_scanner);
        eventHistory = findViewById(R.id.event_history);
        eventSearch = findViewById(R.id.event_search);
        notificationButton = findViewById(R.id.notifiy_button);

        recyclerView = findViewById(R.id.recycler_view_events);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        eventList = new ArrayList<>();
        eventAdapter = new EventAdapter(this, eventList);
        recyclerView.setAdapter(eventAdapter);
    }

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
            retrieveWaitlistAndNotify();
        });

        eventSearch.setOnClickListener(v -> {
            // Add event search logic here
        });
    }

    private void addEventsInit() {
        db.collection("EVENT_PROFILES")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String title = document.getString("Title");
                            uid = document.getId();
                            String date = document.getString("Date");
                            String time = document.getString("Time");
                            String location = document.getString("Location");
                            String description = document.getString("Description");
                            String organizerId = document.getString("OrganizerId");
                            int capacity = document.getLong("Capacity").intValue();

                            eventList.add(new Event(title, date, time, location, description, capacity, uid, organizerId, -1));
                            eventAdapter.notifyDataSetChanged();
                        }
                    } else {
                        Log.d("Firebase", "Error getting documents: ", task.getException());
                    }
                });
    }

    private void retrieveWaitlistAndNotify() {
        db.collection("EVENT_PROFILES")
                .whereEqualTo("OrganizerId", USER_ID)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            List<String> waitlistIds = (List<String>) document.get("waitlist");
                            String title = "Event Update";
                            String message = "You have been selected for an event!";
                            notificationManager.sendNotificationsToWaitlist(this, waitlistIds, title, message);
                        }
                    }
                });
    }
}

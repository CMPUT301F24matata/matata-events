package com.example.matata;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

/**
 * AdminView Activity provides an interface for administrative users within the Matata application.
 * This activity allows admin users to create new events and access their profile. It connects to
 * Firebase Firestore to manage user profiles and initialize user data if not already present.
 *
 * Outstanding issues:
 * - Improve error handling for Firebase Firestore tasks to notify the user on failure.
 */
public class AdminView extends AppCompatActivity {

    /**
     * ImageView for displaying the user's profile icon.
     */
    private ImageView profileIcon;

    /**
     * ImageView for creating a new event.
     */
    private ImageView newEvent;

    /**
     * FirebaseFirestore instance for accessing Firestore database.
     */
    private FirebaseFirestore db;

    /**
     * String representing the unique ID of the user.
     */
    private String USER_ID;


    /**
     * Initializes the activity, sets up the user interface, configures layout for edge-to-edge display,
     * fetches the user ID, and checks the user profile.
     *
     * @param savedInstanceState Bundle containing the activity's previously saved state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_view);

        initializeUI();
        configureEdgeToEdge();
        fetchUserID();
        setupUserProfile();
        setupListeners();
    }

    /**
     * Initializes the UI components, including the profile icon, the "new event" button, and
     * initializes the Firebase Firestore instance for accessing user data.
     */
    private void initializeUI() {
        profileIcon = findViewById(R.id.profile_picture);
        newEvent = findViewById(R.id.add_event);
        db = FirebaseFirestore.getInstance();
    }

    /**
     * Configures the layout for an edge-to-edge experience by applying system bar insets to padding.
     * This helps ensure that the UI elements are not obscured by system bars.
     */
    private void configureEdgeToEdge() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    /**
     * Retrieves the unique user ID for the current device, used as the document ID for storing
     * and retrieving user-specific data from Firebase Firestore.
     */
    private void fetchUserID() {
        USER_ID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    /**
     * Sets up the user profile in Firebase Firestore. If the profile does not already exist, a
     * new profile document is created with default fields.
     */
    private void setupUserProfile() {
        DocumentReference userRef = db.collection("USER_PROFILES").document(USER_ID);

        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (!document.exists()) {
                    createUserProfile(userRef);
                }
            }
        });
    }

    /**
     * Creates a new user profile in Firebase Firestore with default values.
     *
     * @param userRef Reference to the user document in Firestore where profile data will be saved.
     */
    private void createUserProfile(DocumentReference userRef) {
        Map<String, Object> userProfile = new HashMap<>();
        userProfile.put("username", "");
        userProfile.put("phone", "");
        userProfile.put("email", "");
        userProfile.put("notifications", false);
        userProfile.put("profileUri", "");
        userRef.set(userProfile);
    }

    /**
     * Sets up click listeners for interactive UI elements.
     * - Opens AddEvent activity when "newEvent" button is clicked.
     * - Opens ProfileActivity with an "Admin" identifier when the profile icon is clicked.
     */
    private void setupListeners() {
        newEvent.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), AddEvent.class);
            view.getContext().startActivity(intent);
        });

        profileIcon.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), ProfileActivity.class);
            intent.putExtra("Caller", "Admin");
            view.getContext().startActivity(intent);
        });
    }
}

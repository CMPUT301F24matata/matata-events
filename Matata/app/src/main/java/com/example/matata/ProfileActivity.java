package com.example.matata;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {
    private EditText nameEditText, phoneEditText, emailEditText;
    private ImageView profileIcon;
    private FirebaseFirestore db;
    private String deviceId;

    // Replace with the unique user ID (e.g., FirebaseAuth UID) to identify each user
    private static String USER_ID = "unique_user_id";

    private final ActivityResultLauncher<Intent> profilePicLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    loadProfilePicture();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        USER_ID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        // Initialize UI elements
        profileIcon = findViewById(R.id.profileIcon);
        nameEditText = findViewById(R.id.nameEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        emailEditText = findViewById(R.id.emailEditText);
        Button saveButton = findViewById(R.id.saveButton);
        ImageView back = findViewById(R.id.btnBackProfile);
        Switch notifications = findViewById(R.id.switch_notification);

        back.setOnClickListener(v -> finish());

        // Load profile data from Firestore
        loadProfileData();

        // Set up profile picture click listener
        profileIcon.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, ProfilePicActivity.class);
            profilePicLauncher.launch(intent);
        });

        // Save button click listener to save profile data to Firestore
        saveButton.setOnClickListener(v -> {
            String name = nameEditText.getText().toString().trim();
            String email = emailEditText.getText().toString().trim();
            String phoneNumber = phoneEditText.getText().toString().trim();
            boolean notificationsChecked = notifications.isChecked();

            if (name.isEmpty()) {
                Toast.makeText(this, "No UserName Found", Toast.LENGTH_SHORT).show();
            } else if (email.isEmpty()) {
                Toast.makeText(this, "No Email Found", Toast.LENGTH_SHORT).show();
            } else {
                saveProfileData(name, phoneNumber, email, notificationsChecked);
            }
        });

        loadProfilePicture();
    }

    // Method to save profile data to Firestore
    private void saveProfileData(String name, String phone, String email, boolean notificationsChecked) {
        // Create a map to hold the user data
        Map<String, Object> userProfile = new HashMap<>();
        userProfile.put("username", name);
        userProfile.put("phone", phone);
        userProfile.put("email", email);
        userProfile.put("notifications", notificationsChecked);

        // Save the data in the "USER_PROFILES" collection under a document with the user's unique ID
        db.collection("USER_PROFILES").document(USER_ID)
                .set(userProfile)
                .addOnSuccessListener(aVoid -> Toast.makeText(ProfileActivity.this, "Profile saved successfully", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(ProfileActivity.this, "Failed to save profile", Toast.LENGTH_SHORT).show());
    }

    // Method to load profile data from Firestore
    private void loadProfileData() {
        DocumentReference docRef = db.collection("USER_PROFILES").document(USER_ID);
        docRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String name = documentSnapshot.getString("username");
                        String phone = documentSnapshot.getString("phone");
                        String email = documentSnapshot.getString("email");

                        // Set the values in the EditTexts
                        nameEditText.setText(name != null ? name : "");
                        phoneEditText.setText(phone != null ? phone : "");
                        emailEditText.setText(email != null ? email : "");
                    } else {
                        Toast.makeText(ProfileActivity.this, "No profile found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(ProfileActivity.this, "Failed to load profile", Toast.LENGTH_SHORT).show());
    }

    // Load profile picture from SharedPreferences or show initials as a fallback
    private void loadProfilePicture() {
        String imageUriString = getSharedPreferences("ProfilePrefs", MODE_PRIVATE)
                .getString("profile_image_uri", null);

        if (imageUriString != null) {
            Uri imageUri = Uri.parse(imageUriString);
            Glide.with(this).load(imageUri).into(profileIcon);
        } else {
            profileIcon.setImageResource(R.drawable.ic_upload);
        }
    }
}

package com.example.matata;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

        USER_ID=Settings.Secure.getString(getContentResolver(),Settings.Secure.ANDROID_ID);
        Log.wtf(TAG,USER_ID.toString());
        // Initialize UI elements
        profileIcon = findViewById(R.id.profileIcon);
        nameEditText = findViewById(R.id.nameEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        emailEditText = findViewById(R.id.emailEditText);
        Button saveButton = findViewById(R.id.saveButton);
        ImageView back = findViewById(R.id.btnBackProfile);

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
            String phoneNumber = phoneEditText.getText().toString().trim();
            String email = emailEditText.getText().toString().trim();

            saveProfileData(name, phoneNumber, email);
        });

        loadProfilePicture();
    }

    // Method to save profile data to Firestore
    private void saveProfileData(String name, String phone, String email) {
        // Create a map to hold the user data
        Map<String, Object> userProfile = new HashMap<>();
        userProfile.put("username", name);
        userProfile.put("phone", phone);
        userProfile.put("email", email);

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

//
//package com.example.matata;
//
//import android.content.Context;
//import android.content.Intent;
//import android.net.Uri;
//import android.os.Bundle;
//import android.provider.Settings;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.Toast;
//
//import androidx.activity.result.ActivityResultLauncher;
//import androidx.activity.result.contract.ActivityResultContracts;
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.bumptech.glide.Glide;
//import com.google.firebase.firestore.DocumentReference;
//import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.storage.FirebaseStorage;
//import com.google.firebase.storage.StorageReference;
//import com.google.firebase.storage.UploadTask;
//
//import java.util.HashMap;
//import java.util.Map;
//
//public class ProfileActivity extends AppCompatActivity {
//    private EditText nameEditText, phoneEditText, emailEditText;
//    private ImageView profileIcon;
//    private FirebaseFirestore db;
//    private FirebaseStorage storage;
//    private StorageReference storageRef;
//
//    private String deviceId;
//    private Uri selectedImageUri;
//
//    private final ActivityResultLauncher<Intent> profilePicLauncher = registerForActivityResult(
//            new ActivityResultContracts.StartActivityForResult(),
//            result -> {
//                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
//                    selectedImageUri = result.getData().getData();
//                    uploadProfilePicture();  // Upload the selected image to Firebase Storage
//                }
//            });
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.edit_profile);
//
//        // Initialize Firestore and Firebase Storage
//        db = FirebaseFirestore.getInstance();
//        storage = FirebaseStorage.getInstance();
//        storageRef = storage.getReference();
//
//        // Get the unique Android device ID
//        deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
//
//        // Initialize UI elements
//        profileIcon = findViewById(R.id.profileIcon);
//        nameEditText = findViewById(R.id.nameEditText);
//        phoneEditText = findViewById(R.id.phoneEditText);
//        emailEditText = findViewById(R.id.emailEditText);
//        Button saveButton = findViewById(R.id.saveButton);
//        ImageView back = findViewById(R.id.btnBackProfile);
//
//        back.setOnClickListener(v -> finish());
//
//        // Load profile data from Firestore
//        loadProfileData();
//
//        // Set up profile picture click listener
//        profileIcon.setOnClickListener(v -> {
//            Intent intent = new Intent(ProfileActivity.this, ProfilePicActivity.class);
//            profilePicLauncher.launch(intent);
//        });
//
//        // Save button click listener to save profile data to Firestore
//        saveButton.setOnClickListener(v -> {
//            String name = nameEditText.getText().toString().trim();
//            String phoneNumber = phoneEditText.getText().toString().trim();
//            String email = emailEditText.getText().toString().trim();
//
//            saveProfileData(name, phoneNumber, email);
//        });
//
//        loadProfilePicture();
//    }
//
//    // Method to upload profile picture to Firebase Storage
//    private void uploadProfilePicture() {
//        if (selectedImageUri != null) {
//            // Reference to store the image using the device ID
//            StorageReference profileImageRef = storageRef.child("profile_pictures/" + deviceId + ".jpg");
//
//            // Upload the file
//            UploadTask uploadTask = profileImageRef.putFile(selectedImageUri);
//            uploadTask.addOnSuccessListener(taskSnapshot -> {
//                // Get the download URL and save it to Firestore
//                profileImageRef.getDownloadUrl().addOnSuccessListener(uri -> {
//                    String imageUrl = uri.toString();
//                    saveProfilePictureUrl(imageUrl);
//                    Glide.with(this).load(imageUrl).into(profileIcon);  // Display the image
//                    Toast.makeText(ProfileActivity.this, "Profile picture uploaded successfully", Toast.LENGTH_SHORT).show();
//                });
//            }).addOnFailureListener(e -> {
//                Toast.makeText(ProfileActivity.this, "Failed to upload profile picture", Toast.LENGTH_SHORT).show();
//            });
//        }
//    }
//
//    // Method to save profile picture URL to Firestore
//    private void saveProfilePictureUrl(String imageUrl) {
//        db.collection("USER_PROFILES").document(deviceId)
//                .update("profilePictureUrl", imageUrl)
//                .addOnFailureListener(e -> Toast.makeText(ProfileActivity.this, "Failed to save profile picture URL", Toast.LENGTH_SHORT).show());
//    }
//
//    // Method to save profile data to Firestore
//    private void saveProfileData(String name, String phone, String email) {
//        // Create a map to hold the user data
//        Map<String, Object> userProfile = new HashMap<>();
//        userProfile.put("username", name);
//        userProfile.put("phone", phone);
//        userProfile.put("email", email);
//
//        // Save the data in the "USER_PROFILES" collection under a document with the device ID
//        db.collection("USER_PROFILES").document(deviceId)
//                .set(userProfile)
//                .addOnSuccessListener(aVoid -> Toast.makeText(ProfileActivity.this, "Profile saved successfully", Toast.LENGTH_SHORT).show())
//                .addOnFailureListener(e -> Toast.makeText(ProfileActivity.this, "Failed to save profile", Toast.LENGTH_SHORT).show());
//    }
//
//    // Method to load profile data from Firestore
//    private void loadProfileData() {
//        DocumentReference docRef = db.collection("USER_PROFILES").document(deviceId);
//        docRef.get()
//                .addOnSuccessListener(documentSnapshot -> {
//                    if (documentSnapshot.exists()) {
//                        String name = documentSnapshot.getString("username");
//                        String phone = documentSnapshot.getString("phone");
//                        String email = documentSnapshot.getString("email");
//                        String profilePictureUrl = documentSnapshot.getString("profilePictureUrl");
//
//                        // Set the values in the EditTexts
//                        nameEditText.setText(name != null ? name : "");
//                        phoneEditText.setText(phone != null ? phone : "");
//                        emailEditText.setText(email != null ? email : "");
//
//                        // Load the profile picture if URL exists
//                        if (profilePictureUrl != null) {
//                            Glide.with(this).load(profilePictureUrl).into(profileIcon);
//                        }
//                    } else {
//                        Toast.makeText(ProfileActivity.this, "No profile found", Toast.LENGTH_SHORT).show();
//                    }
//                })
//                .addOnFailureListener(e -> Toast.makeText(ProfileActivity.this, "Failed to load profile", Toast.LENGTH_SHORT).show());
//    }
//
//    // Load profile picture from SharedPreferences or show initials as a fallback
//    private void loadProfilePicture() {
//        String imageUriString = getSharedPreferences("ProfilePrefs", MODE_PRIVATE)
//                .getString("profile_image_uri", null);
//
//        if (imageUriString != null) {
//            Uri imageUri = Uri.parse(imageUriString);
//            Glide.with(this).load(imageUri).into(profileIcon);
//        } else {
//            profileIcon.setImageResource(R.drawable.ic_upload);
//        }
//    }
//}

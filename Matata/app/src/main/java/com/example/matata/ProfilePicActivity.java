package com.example.matata;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class ProfilePicActivity extends AppCompatActivity {

    private ImageView ivProfilePicture;
    private Uri selectedImageUri;

    // ActivityResultLauncher to handle image picking
    private final ActivityResultLauncher<Intent> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    saveProfilePictureUri(selectedImageUri);
                    loadProfilePicture();
                    setResult(RESULT_OK);
                } else {
                    Toast.makeText(this, "Image selection canceled", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_pic);

        ivProfilePicture = findViewById(R.id.ivProfilePicture);
        ImageButton btnBack = findViewById(R.id.btnBack);
        Button btnUploadPicture = findViewById(R.id.btnUploadPicture);
        Button btnDeletePicture = findViewById(R.id.btnDeletePicture);

        loadProfilePicture();

        btnBack.setOnClickListener(v -> finish());

        ivProfilePicture.setOnClickListener(v -> openImagePicker());

        btnUploadPicture.setOnClickListener(v -> {
            if (selectedImageUri != null) {
                saveProfilePictureUri(selectedImageUri);
                Toast.makeText(this, "Profile picture uploaded successfully", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            } else {
                Toast.makeText(this, "No image selected to upload", Toast.LENGTH_SHORT).show();
            }
        });

        btnDeletePicture.setOnClickListener(v -> deleteProfilePicture());
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        pickImageLauncher.launch(Intent.createChooser(intent, "Select Picture"));
    }

    private void saveProfilePictureUri(Uri uri) {
        getSharedPreferences("ProfilePrefs", Context.MODE_PRIVATE)
                .edit()
                .putString("profile_image_uri", uri.toString())
                .apply();
    }

    private void loadProfilePicture() {
        String imageUriString = getSharedPreferences("ProfilePrefs", Context.MODE_PRIVATE)
                .getString("profile_image_uri", null);

        if (imageUriString != null) {
            Uri imageUri = Uri.parse(imageUriString);
            Glide.with(this)
                    .load(imageUri)
                    .into(ivProfilePicture);
        } else {
            ivProfilePicture.setImageResource(R.drawable.ic_upload);
        }
    }

    private void deleteProfilePicture() {
        ivProfilePicture.setImageResource(R.drawable.ic_upload);
        selectedImageUri = null;

        getSharedPreferences("ProfilePrefs", Context.MODE_PRIVATE)
                .edit()
                .remove("profile_image_uri")
                .apply();

        setResult(RESULT_OK);
        finish();
        Toast.makeText(this, "Profile picture deleted", Toast.LENGTH_SHORT).show();
    }
}
//
//package com.example.matata;
//
//import android.annotation.SuppressLint;
//import android.content.Context;
//import android.content.Intent;
//import android.net.Uri;
//import android.os.Bundle;
//import android.provider.Settings;
//import android.widget.Button;
//import android.widget.ImageButton;
//import android.widget.ImageView;
//import android.widget.Toast;
//
//import androidx.activity.result.ActivityResultLauncher;
//import androidx.activity.result.contract.ActivityResultContracts;
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.bumptech.glide.Glide;
//import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.storage.FirebaseStorage;
//import com.google.firebase.storage.StorageReference;
//import com.google.firebase.storage.UploadTask;
//
//import java.util.HashMap;
//import java.util.Map;
//
//public class ProfilePicActivity extends AppCompatActivity {
//
//    private ImageView ivProfilePicture;
//    private Uri selectedImageUri;
//
//    // Firebase variables
//    private FirebaseFirestore db;
//    private FirebaseStorage storage;
//    private String deviceId;
//
//    // ActivityResultLauncher to handle image picking
//    private final ActivityResultLauncher<Intent> pickImageLauncher = registerForActivityResult(
//            new ActivityResultContracts.StartActivityForResult(),
//            result -> {
//                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
//                    selectedImageUri = result.getData().getData();
//                    uploadProfilePictureToFirebase(selectedImageUri);  // Upload to Firebase
//                } else {
//                    Toast.makeText(this, "Image selection canceled", Toast.LENGTH_SHORT).show();
//                }
//            });
//
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.profile_pic);
//
//        // Initialize Firebase Firestore and Storage
//        db = FirebaseFirestore.getInstance();
//        storage = FirebaseStorage.getInstance();
//        deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
//
//        ivProfilePicture = findViewById(R.id.ivProfilePicture);
//        ImageButton btnBack = findViewById(R.id.btnBack);
//        Button btnUploadPicture = findViewById(R.id.btnUploadPicture);
//        Button btnDeletePicture = findViewById(R.id.btnDeletePicture);
//
//        loadProfilePicture();
//
//        btnBack.setOnClickListener(v -> finish());
//
//        ivProfilePicture.setOnClickListener(v -> openImagePicker());
//
//        btnUploadPicture.setOnClickListener(v -> {
//            if (selectedImageUri != null) {
//                uploadProfilePictureToFirebase(selectedImageUri);
//            } else {
//                Toast.makeText(this, "No image selected to upload", Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        btnDeletePicture.setOnClickListener(v -> deleteProfilePicture());
//    }
//
//    private void openImagePicker() {
//        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//        intent.setType("image/*");
//        pickImageLauncher.launch(Intent.createChooser(intent, "Select Picture"));
//    }
//
//    // Upload the selected image to Firebase Storage
//    private void uploadProfilePictureToFirebase(Uri uri) {
//        // Create a storage reference with the device ID
//        StorageReference profileImageRef = storage.getReference()
//                .child("profile_pictures/" + deviceId + ".jpg");
//
//        // Upload the file
//        profileImageRef.putFile(uri)
//                .addOnSuccessListener(taskSnapshot -> {
//                    // Get the download URL and save it to Firestore
//                    profileImageRef.getDownloadUrl().addOnSuccessListener(downloadUri -> {
//                        saveProfilePictureUrlToFirestore(downloadUri.toString());
//                        Glide.with(this).load(downloadUri).into(ivProfilePicture);  // Display the image
//                        Toast.makeText(this, "Profile picture uploaded successfully", Toast.LENGTH_SHORT).show();
//                    });
//                })
//                .addOnFailureListener(e -> Toast.makeText(this, "Failed to upload profile picture", Toast.LENGTH_SHORT).show());
//    }
//
//    // Save the profile picture URL to Firestore under the document with device ID
//    private void saveProfilePictureUrlToFirestore(String downloadUrl) {
//        Map<String, Object> userProfileData = new HashMap<>();
//        userProfileData.put("profilePictureUrl", downloadUrl);
//
//        db.collection("USER_PROFILES").document(deviceId)
//                .set(userProfileData)  // Use `set` to save it as a field within the document
//                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Profile picture URL saved to database", Toast.LENGTH_SHORT).show())
//                .addOnFailureListener(e -> Toast.makeText(this, "Failed to save profile picture URL", Toast.LENGTH_SHORT).show());
//    }
//
//    // Load the profile picture from Firestore
//    private void loadProfilePicture() {
//        db.collection("USER_PROFILES").document(deviceId)
//                .get()
//                .addOnSuccessListener(documentSnapshot -> {
//                    if (documentSnapshot.exists()) {
//                        String firebaseImageUrl = documentSnapshot.getString("profilePictureUrl");
//                        if (firebaseImageUrl != null) {
//                            Glide.with(this).load(firebaseImageUrl).into(ivProfilePicture);
//                        } else {
//                            ivProfilePicture.setImageResource(R.drawable.ic_upload);  // Placeholder if no image
//                        }
//                    } else {
//                        ivProfilePicture.setImageResource(R.drawable.ic_upload);
//                    }
//                })
//                .addOnFailureListener(e -> Toast.makeText(this, "Failed to load profile picture from database", Toast.LENGTH_SHORT).show());
//    }
//
//    // Delete the profile picture from Firebase Storage and remove the field from Firestore
//    private void deleteProfilePicture() {
//        ivProfilePicture.setImageResource(R.drawable.ic_upload);
//        selectedImageUri = null;
//
//        // Remove profile picture URL from Firebase Storage
//        StorageReference profileImageRef = storage.getReference()
//                .child("profile_pictures/" + deviceId + ".jpg");
//
//        profileImageRef.delete().addOnSuccessListener(aVoid -> {
//            // Remove the profile picture URL from Firestore
//            db.collection("USER_PROFILES").document(deviceId)
//                    .update("profilePictureUrl", null)
//                    .addOnSuccessListener(aVoid1 -> Toast.makeText(this, "Profile picture deleted", Toast.LENGTH_SHORT).show())
//                    .addOnFailureListener(e -> Toast.makeText(this, "Failed to update profile picture URL in database", Toast.LENGTH_SHORT).show());
//        }).addOnFailureListener(e -> Toast.makeText(this, "Failed to delete profile picture from Firebase Storage", Toast.LENGTH_SHORT).show());
//    }
//}

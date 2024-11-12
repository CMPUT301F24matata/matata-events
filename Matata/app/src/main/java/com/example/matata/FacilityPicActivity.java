package com.example.matata;

import android.annotation.SuppressLint;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * ProfilePicActivity allows the user to view, select, upload, and delete a profile picture.
 * It utilizes Firestore to save the image URI and SharedPreferences to persist it locally.
 * The activity also includes options for handling the back button and notifying other activities
 * about profile picture changes.
 *
 * Outstanding issues: The activity currently does not handle large image sizes or provide resizing.
 * It also does not explicitly handle storage quotas or Firebase Storage configurations.
 */
public class FacilityPicActivity extends AppCompatActivity {

    private ImageView ivProfilePicture;
    private Uri selectedImageUri;
    private FirebaseFirestore db;
    private String USER_ID;

    /**
     * Launcher to handle image picking from the gallery.
     */
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

    /**
     * Initializes ProfilePicActivity, sets up Firebase, loads current profile picture,
     * and configures button listeners for uploading, deleting, and returning to the previous screen.
     *
     * @param savedInstanceState if the activity is being re-initialized, this contains the data it most recently supplied
     */

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.facility_pic);

        db = FirebaseFirestore.getInstance();
        USER_ID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        ivProfilePicture = findViewById(R.id.ivProfilePicture);
        ImageView btnBack = findViewById(R.id.btnBack);
        Button btnUploadPicture = findViewById(R.id.btnUploadPicture);
        Button btnDeletePicture = findViewById(R.id.btnDeletePicture);

        loadProfilePicture();

        btnBack.setOnClickListener(v -> finish());

        ivProfilePicture.setOnClickListener(v -> openImagePicker());

        btnUploadPicture.setOnClickListener(v -> {
            if (selectedImageUri != null) {
                DocumentReference userRef = db.collection("FACILITY_PROFILES").document(USER_ID);
                userRef.update("profileUri", selectedImageUri);
                saveProfilePictureUri(selectedImageUri);
                Toast.makeText(this, "Profile picture uploaded successfully", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
            } else {
                Toast.makeText(this, "No image selected to upload", Toast.LENGTH_SHORT).show();
            }
        });

        btnDeletePicture.setOnClickListener(v -> deleteProfilePicture());
    }

    /**
     * Opens the image picker for the user to select a profile picture.
     */
    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        pickImageLauncher.launch(Intent.createChooser(intent, "Select Picture"));
    }

    /**
     * Saves the selected profile picture URI to SharedPreferences.
     *
     * @param uri the URI of the selected profile picture
     */
    private void saveProfilePictureUri(Uri uri) {
        getSharedPreferences("FacilityPrefs", Context.MODE_PRIVATE)
                .edit()
                .putString("profile_image_uri", uri.toString())
                .apply();
    }

    /**
     * Loads the current profile picture from SharedPreferences and displays it in the ImageView.
     * If no profile picture is set, a default placeholder image is shown.
     */
    private void loadProfilePicture() {
        String imageUriString = getSharedPreferences("FacilityPrefs", Context.MODE_PRIVATE)
                .getString("profile_image_uri", null);
        if (imageUriString != null) {
            Uri imageUri = Uri.parse(imageUriString);
            Glide.with(this)
                    .load(imageUri)
                    .error(R.drawable.ic_upload)
                    .into(ivProfilePicture);
        } else {
            ivProfilePicture.setImageResource(R.drawable.ic_upload);
        }
    }

    /**
     * Deletes the current profile picture, updates Firestore, and removes the URI from SharedPreferences.
     * Sets a default image as the profile picture and finishes the activity with a success result.
     */
    private void deleteProfilePicture() {
        ivProfilePicture.setImageResource(R.drawable.ic_upload);
        selectedImageUri = null;

        DocumentReference userRef = db.collection("FACILITY_PROFILES").document(USER_ID);
        userRef.update("profileUri", "");

        getSharedPreferences("FacilityPrefs", Context.MODE_PRIVATE)
                .edit()
                .remove("profile_image_uri")
                .apply();

        setResult(RESULT_OK);
        Toast.makeText(this, "Profile picture deleted", Toast.LENGTH_SHORT).show();
    }
}

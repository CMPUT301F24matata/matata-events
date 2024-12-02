package com.example.matata;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

/**
 * The `TestProfilePicActivity` class is a simplified version of the `ProfilePicActivity` designed for UI testing purposes.
 * It allows users to view, select, upload, and delete their profile pictures without integrating Firebase or other external services.
 * The activity uses SharedPreferences to save and retrieve profile picture URIs locally.
 *
 * <h2>Key Features:</h2>
 * <ul>
 *     <li>Allows users to upload a profile picture from their device's gallery.</li>
 *     <li>Displays the selected profile picture in an `ImageView`.</li>
 *     <li>Enables users to delete the current profile picture and reset to a default placeholder.</li>
 *     <li>Persists the profile picture URI locally using SharedPreferences.</li>
 *     <li>Provides basic navigation back to the main screen using a back button.</li>
 * </ul>
 *
 * <h2>Usage:</h2>
 * <p>This activity is intended for UI testing and local functionality validation. It does not include
 * database integration or advanced features like cloud storage or user authentication.</p>
 */
public class TestProfilePicActivity extends AppCompatActivity {

    /**
     * ImageView for displaying the profile picture.
     */
    private ImageView ivProfilePicture;

    /**
     * URI of the currently selected image from the gallery.
     */
    private Uri selectedImageUri;

    /**
     * Launcher for handling the result of the image picker activity.
     */
    final ActivityResultLauncher<Intent> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    saveProfilePictureUri(selectedImageUri);
                    loadProfilePicture();
                    Toast.makeText(this, "Image selected", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Image selection canceled", Toast.LENGTH_SHORT).show();
                }
            });

    /**
     * Called when the activity is first created. Initializes UI components and sets up button click listeners.
     *
     * @param savedInstanceState The previously saved state of the activity, if any.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_pic);

        ivProfilePicture = findViewById(R.id.ivProfilePicture);
        ImageView btnBack = findViewById(R.id.btnBack);
        Button btnUploadPicture = findViewById(R.id.btnUploadPicture);
        Button btnDeletePicture = findViewById(R.id.btnDeletePicture);

        loadProfilePicture();

        btnBack.setOnClickListener(v -> finish());

        ivProfilePicture.setOnClickListener(v -> openImagePicker());

        btnUploadPicture.setOnClickListener(v -> {
            if (selectedImageUri != null) {
                saveProfilePictureUri(selectedImageUri);
                Toast.makeText(this, "Profile picture uploaded successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "No image selected to upload", Toast.LENGTH_SHORT).show();
            }
        });

        btnDeletePicture.setOnClickListener(v -> deleteProfilePicture());
    }

    /**
     * Opens the device's image picker for the user to select a profile picture.
     */
    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        pickImageLauncher.launch(Intent.createChooser(intent, "Select Picture"));
    }

    /**
     * Saves the URI of the selected profile picture to SharedPreferences.
     *
     * @param uri The URI of the selected image.
     */
    private void saveProfilePictureUri(Uri uri) {
        getSharedPreferences("ProfilePrefs", Context.MODE_PRIVATE)
                .edit()
                .putString("profile_image_uri", uri.toString())
                .apply();
    }

    /**
     * Loads the profile picture from SharedPreferences and displays it in the `ImageView`.
     * If no profile picture is found, a default placeholder image is displayed.
     */
    private void loadProfilePicture() {
        String imageUriString = getSharedPreferences("ProfilePrefs", Context.MODE_PRIVATE)
                .getString("profile_image_uri", null);
        if (imageUriString != null) {
            Uri imageUri = Uri.parse(imageUriString);
            ivProfilePicture.setImageURI(imageUri);
        } else {
            ivProfilePicture.setImageResource(R.drawable.ic_upload);
        }
    }

    /**
     * Deletes the current profile picture, removes the URI from SharedPreferences,
     * and resets the `ImageView` to display the default placeholder image.
     */
    private void deleteProfilePicture() {
        ivProfilePicture.setImageResource(R.drawable.ic_upload);
        selectedImageUri = null;

        getSharedPreferences("ProfilePrefs", Context.MODE_PRIVATE)
                .edit()
                .remove("profile_image_uri")
                .apply();

        Toast.makeText(this, "Profile picture deleted", Toast.LENGTH_SHORT).show();
    }
}

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
 * TestFacilityPicActivity is a simplified version of FacilityPicActivity for UI testing.
 * It focuses on local functionality without Firebase or external dependencies.
 */
public class TestFacilityPicActivity extends AppCompatActivity {

    private ImageView ivProfilePicture;
    private Uri selectedImageUri;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.facility_pic);

        ivProfilePicture = findViewById(R.id.ivProfilePicture);
        ImageView btnBack = findViewById(R.id.btnBack);
        Button btnUploadPicture = findViewById(R.id.btnUploadPicture);
        Button btnDeletePicture = findViewById(R.id.btnDeletePicture);

        loadProfilePicture();

        btnBack.setOnClickListener(v -> finish());
        ivProfilePicture.setOnClickListener(v -> openImagePicker());
        btnUploadPicture.setOnClickListener(v -> uploadProfilePicture());
        btnDeletePicture.setOnClickListener(v -> deleteProfilePicture());
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        pickImageLauncher.launch(Intent.createChooser(intent, "Select Picture"));
    }

    private void saveProfilePictureUri(Uri uri) {
        getSharedPreferences("FacilityPrefs", Context.MODE_PRIVATE)
                .edit()
                .putString("profile_image_uri", uri.toString())
                .apply();
    }

    private void loadProfilePicture() {
        String imageUriString = getSharedPreferences("FacilityPrefs", Context.MODE_PRIVATE)
                .getString("profile_image_uri", null);
        if (imageUriString != null) {
            Uri imageUri = Uri.parse(imageUriString);
            ivProfilePicture.setImageURI(imageUri);
        } else {
            ivProfilePicture.setImageResource(R.drawable.ic_upload);
        }
    }

    private void uploadProfilePicture() {
        if (selectedImageUri != null) {
            saveProfilePictureUri(selectedImageUri);
            Toast.makeText(this, "Profile picture uploaded successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "No image selected to upload", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteProfilePicture() {
        ivProfilePicture.setImageResource(R.drawable.ic_upload);
        selectedImageUri = null;

        getSharedPreferences("FacilityPrefs", Context.MODE_PRIVATE)
                .edit()
                .remove("profile_image_uri")
                .apply();

        Toast.makeText(this, "Profile picture deleted", Toast.LENGTH_SHORT).show();
    }
}

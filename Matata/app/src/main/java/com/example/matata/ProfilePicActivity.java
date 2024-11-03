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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class ProfilePicActivity extends AppCompatActivity {

    private ImageView ivProfilePicture;
    private Uri selectedImageUri;
    private FirebaseFirestore db;
    private String USER_ID;
    
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

        db = FirebaseFirestore.getInstance();

        USER_ID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        ivProfilePicture = findViewById(R.id.ivProfilePicture);
        ImageButton btnBack = findViewById(R.id.btnBack);
        Button btnUploadPicture = findViewById(R.id.btnUploadPicture);
        Button btnDeletePicture = findViewById(R.id.btnDeletePicture);

        loadProfilePicture();

        btnBack.setOnClickListener(v -> finish());

        ivProfilePicture.setOnClickListener(v -> openImagePicker());

        btnUploadPicture.setOnClickListener(v -> {
            if (selectedImageUri != null) {
                DocumentReference userRef = db.collection("USER_PROFILES").document(USER_ID);

                userRef.update("profileUri", selectedImageUri);

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
        DocumentReference userRef = db.collection("USER_PROFILES").document(USER_ID);
        userRef.update("profileUri", "");
        getSharedPreferences("ProfilePrefs", Context.MODE_PRIVATE)
                .edit()
                .remove("profile_image_uri")
                .apply();
        setResult(RESULT_OK);
        finish();
        Toast.makeText(this, "Profile picture deleted", Toast.LENGTH_SHORT).show();
    }
}

package com.example.matata;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import java.io.IOException;

public class ProfilePicActivity extends AppCompatActivity {

    private ImageView ivProfilePicture;
    private Uri selectedImageUri;

    // ActivityResultLauncher to handle image picking
    private final ActivityResultLauncher<Intent> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                        ivProfilePicture.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        Log.e("ProfilePicActivity", Log.getStackTraceString(e));
                        Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
                    }
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

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        });

        btnBack.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        ivProfilePicture.setOnClickListener(v -> openImagePicker());

        btnUploadPicture.setOnClickListener(v -> uploadToProfile());

        btnDeletePicture.setOnClickListener(v -> deleteProfilePicture());
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        pickImageLauncher.launch(Intent.createChooser(intent, "Select Picture"));
    }

    private void deleteProfilePicture() {
        ivProfilePicture.setImageResource(R.drawable.ic_upload);
        selectedImageUri = null;

        getSharedPreferences("ProfilePrefs", Context.MODE_PRIVATE)
                .edit()
                .remove("profile_image_uri")
                .apply();

        setResult(Activity.RESULT_OK);
        finish();

        Toast.makeText(this, "Profile picture deleted", Toast.LENGTH_SHORT).show();
    }

    private void uploadToProfile() {
        if (selectedImageUri != null) {
            getSharedPreferences("ProfilePrefs", Context.MODE_PRIVATE)
                    .edit()
                    .putString("profile_image_uri", selectedImageUri.toString())
                    .apply();

            setResult(Activity.RESULT_OK);
            finish();

            Toast.makeText(this, "Profile picture uploaded successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "No image selected to upload", Toast.LENGTH_SHORT).show();
        }
    }
}


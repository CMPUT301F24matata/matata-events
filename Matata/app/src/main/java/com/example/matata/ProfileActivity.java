package com.example.matata;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {
    private EditText nameEditText, phoneEditText, emailEditText;
    private ImageView profileIcon;
    private FirebaseFirestore db;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch notifications;
    private String imageUriString;
    @SuppressLint("HardwareIds")
    private String USER_ID = "";

    private final ActivityResultLauncher<Intent> profilePicLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    loadProfilePicture(getSharedPreferences("ProfilePrefs", MODE_PRIVATE)
                            .getString("profile_image_uri", null));
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile);

        db = FirebaseFirestore.getInstance();

        USER_ID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        profileIcon = findViewById(R.id.profileIcon);
        nameEditText = findViewById(R.id.nameEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        emailEditText = findViewById(R.id.emailEditText);
        Button saveButton = findViewById(R.id.saveButton);
        ImageView back = findViewById(R.id.btnBackProfile);
        notifications = findViewById(R.id.switch_notification);
        TextView initials = findViewById(R.id.initials); // Used to check if entrant/organiser/admin
        CompoundButton adminView = findViewById(R.id.adminView);
        String Caller = getIntent().getStringExtra("Caller");
        if (Caller.equals("Admin")) {
            adminView.setChecked(true);
        }

        imageUriString = getSharedPreferences("ProfilePrefs", MODE_PRIVATE)
                .getString("profile_image_uri", null);

        loadProfilePicture(imageUriString);

        loadProfileData();

        back.setOnClickListener(v -> finish());

        profileIcon.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, ProfilePicActivity.class);
            profilePicLauncher.launch(intent);
        });

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
                saveProfileData(name, phoneNumber, email, notificationsChecked, imageUriString);
            }
        });



        adminView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Intent intent = new Intent(buttonView.getContext(), AdminView.class);
                    buttonView.getContext().startActivity(intent);
                } else {
                    Intent intent = new Intent(buttonView.getContext(), MainActivity.class);
                    buttonView.getContext().startActivity(intent);
                }
            }
        });


    }

    private void saveProfileData(String name, String phone, String email, boolean notificationsChecked, String imageUriString) {

        loadProfilePicture(imageUriString);

        Map<String, Object> userProfile = new HashMap<>();
        userProfile.put("username", name);
        userProfile.put("phone", phone);
        userProfile.put("email", email);
        userProfile.put("notifications", notificationsChecked);
        userProfile.put("profileUri", imageUriString);

        db.collection("USER_PROFILES").document(USER_ID)
                .set(userProfile)
                .addOnSuccessListener(aVoid -> Toast.makeText(ProfileActivity.this, "Profile saved successfully", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(ProfileActivity.this, "Failed to save profile", Toast.LENGTH_SHORT).show());
    }

    private void loadProfileData() {
        DocumentReference docRef = db.collection("USER_PROFILES").document(USER_ID);
        docRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String name = documentSnapshot.getString("username");
                        String phone = documentSnapshot.getString("phone");
                        String email = documentSnapshot.getString("email");
                        boolean notificationsChecked = Boolean.TRUE.equals(documentSnapshot.getBoolean("notifications"));
                        String sImageUri = documentSnapshot.getString("profileUri");

                        nameEditText.setText(name != null ? name : "");
                        phoneEditText.setText(phone != null ? phone : "");
                        emailEditText.setText(email != null ? email : "");
                        notifications.setChecked(notificationsChecked);
                        loadProfilePicture(sImageUri);
                    }

                })
                .addOnFailureListener(e -> Toast.makeText(ProfileActivity.this, "Failed to load profile", Toast.LENGTH_SHORT).show());
    }

    private void loadProfilePicture(String imageUriString) {
        if (imageUriString != null) {
            Uri imageUri = Uri.parse(imageUriString);
            Glide.with(this).load(imageUri).into(profileIcon);
        } else {
            String un = nameEditText.getText().toString().trim();
            if (un.isEmpty()) {
                profileIcon.setImageResource(R.drawable.ic_upload);
            } else {
                StringBuilder initials = new StringBuilder();
                String[] words = un.trim().split("\\s+");
                for (String word : words) {
                    if (!word.isEmpty()) {
                        initials.append(Character.toUpperCase(word.charAt(0)));
                    }
                }
                String initial = initials.toString();
                Uri cimageUri = createImageFromString(this, initial);
                Glide.with(this).load(cimageUri).into(profileIcon);
            }
        }
    }

    public static Uri createImageFromString(Context context, String text) {

        int bitmapWidth = 400;
        int bitmapHeight = 400;
        Bitmap bitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.BLACK);

        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(100);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setAntiAlias(true);

        float textHeight = paint.descent() - paint.ascent();
        float textOffset = (textHeight / 2) - paint.descent();
        canvas.drawText(text, (float) bitmapWidth / 2, ((float) bitmapHeight / 2) + textOffset, paint);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        String base64String = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
        String imageUri = "data:image/png;base64," + base64String;

        return Uri.parse(imageUri);
    }

}

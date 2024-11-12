package com.example.matata;

import static com.example.matata.ProfileActivity.createImageFromString;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Base64;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
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

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class FacilityActivity extends AppCompatActivity {

    private EditText facilityName, facilityAddress, facilityCapacity, facilityContact, facilityEmail, facilityOwner;
    private Switch switchNotification;
    private CheckBox adminView;
    private String imageUriString;
    private String USER_ID = "";
    private FirebaseFirestore db;
    ImageView profileIcon;
    private Button saveButton, clearAllButton;
    private ImageView btnBackProfile;

    // ActivityResultLauncher for facility profile picture selection
    private final ActivityResultLauncher<Intent> profilePicLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    loadFacilityPicture(getSharedPreferences("FacilityPrefs", MODE_PRIVATE)
                            .getString("profile_image_uri", null));
                }
            });

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_facility_profile);

        db = FirebaseFirestore.getInstance();
        USER_ID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        facilityName = findViewById(R.id.facilityName);
        facilityAddress = findViewById(R.id.facilityAddress);
        facilityCapacity = findViewById(R.id.facilityCapacity);
        facilityContact = findViewById(R.id.facilityContact);
        facilityEmail = findViewById(R.id.facilityEmail);
        facilityOwner = findViewById(R.id.facilityOwner);
        switchNotification = findViewById(R.id.switch_notification);
        adminView = findViewById(R.id.adminView);
        saveButton = findViewById(R.id.saveButton);
        clearAllButton = findViewById(R.id.clearAllButton);
        btnBackProfile = findViewById(R.id.btnBackProfile);
        profileIcon = findViewById(R.id.profileIcon);

        btnBackProfile.setOnClickListener(v -> finish());

        imageUriString = getSharedPreferences("FacilityPrefs", MODE_PRIVATE)
                .getString("profile_image_uri", null);

        loadFacilityPicture(imageUriString);

        loadFacilityData();

        profileIcon.setOnClickListener(v -> {
            Intent intent = new Intent(FacilityActivity.this, FacilityPicActivity.class);
            profilePicLauncher.launch(intent);
        });

        saveButton.setOnClickListener(v -> {
            String name = facilityName.getText().toString();
            String address = facilityAddress.getText().toString();
            String capacity = facilityCapacity.getText().toString();
            String contact = facilityContact.getText().toString();
            String email = facilityEmail.getText().toString();
            String owner = facilityOwner.getText().toString();
            boolean notificationsEnabled = switchNotification.isChecked();

            if (name.isEmpty()) {
                Toast.makeText(this, "No UserName Found", Toast.LENGTH_SHORT).show();
            } else if (address.isEmpty()) {
                Toast.makeText(this, "No Address Found", Toast.LENGTH_SHORT).show();
            } else if (contact.isEmpty()) {
                Toast.makeText(this, "No Contact Found", Toast.LENGTH_SHORT).show();
            } else if (capacity.isEmpty()) {
                Toast.makeText(this, "No Capacity Found", Toast.LENGTH_SHORT).show();
            } else if (email.isEmpty()) {
                Toast.makeText(this, "No Email Found", Toast.LENGTH_SHORT).show();
            } else if (owner.isEmpty()) {
                Toast.makeText(this, "No Owner Found", Toast.LENGTH_SHORT).show();
            } else {
                saveFacilityData(name, address, capacity, contact, email, owner, notificationsEnabled, imageUriString);
            }
        });

        clearAllButton.setOnClickListener(v -> {
            facilityName.setText("");
            facilityAddress.setText("");
            facilityCapacity.setText("");
            facilityContact.setText("");
            facilityEmail.setText("");
            facilityOwner.setText("");
            switchNotification.setChecked(false);

            String name = facilityName.getText().toString();
            String address = facilityAddress.getText().toString();
            String capacity = facilityCapacity.getText().toString();
            String contact = facilityContact.getText().toString();
            String email = facilityEmail.getText().toString();
            String owner = facilityOwner.getText().toString();
            boolean notificationsEnabled = switchNotification.isChecked();

            saveFacilityData(name, address, capacity, contact, email, owner, notificationsEnabled, imageUriString);
        });

        adminView.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Intent intent = new Intent(buttonView.getContext(), isChecked ? AdminView.class : MainActivity.class);
            buttonView.getContext().startActivity(intent);
        });
    }


    private void saveFacilityData(String name, String address, String capacity, String contact, String email, String owner, boolean notificationsEnabled, String imageUriString) {
        loadFacilityPicture(imageUriString);

        Map<String, Object> userProfile = new HashMap<>();
        userProfile.put("name", name);
        userProfile.put("address", address);
        userProfile.put("email", email);
        userProfile.put("notifications", notificationsEnabled);
        userProfile.put("profileUri", imageUriString);
        userProfile.put("capacity", capacity);
        userProfile.put("contact", contact);
        userProfile.put("owner", owner);

        db.collection("FACILITY_PROFILES").document(USER_ID)
                .set(userProfile)
                .addOnSuccessListener(aVoid -> Toast.makeText(FacilityActivity.this, "Profile saved successfully", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(FacilityActivity.this, "Failed to save profile", Toast.LENGTH_SHORT).show());
    }

    void loadFacilityPicture(String imageUriString) {
        if (imageUriString != null && !imageUriString.isEmpty()) {
            Uri imageUri = Uri.parse(imageUriString);
            Glide.with(this)
                    .load(imageUri)
                    .into(profileIcon);
        } else {
            String un = facilityName.getText().toString().trim();
            if (un.isEmpty()) {
                profileIcon.setImageResource(R.drawable.ic_facility_profile);
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

    private void loadFacilityData() {
        DocumentReference docRef = db.collection("FACILITY_PROFILES").document(USER_ID);
        docRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String name = documentSnapshot.getString("name");
                        String address = documentSnapshot.getString("address");
                        String email = documentSnapshot.getString("email");
                        boolean notificationsChecked = Boolean.TRUE.equals(documentSnapshot.getBoolean("notifications"));
                        String sImageUri = documentSnapshot.getString("profileUri");
                        String capacity = documentSnapshot.getString("capacity");
                        String contact = documentSnapshot.getString("contact");
                        String owner = documentSnapshot.getString("owner");

                        facilityName.setText(name);
                        facilityAddress.setText(address);
                        facilityCapacity.setText(capacity);
                        facilityContact.setText(contact);
                        facilityEmail.setText(email);
                        facilityOwner.setText(owner);
                        switchNotification.setChecked(notificationsChecked);

                        loadFacilityPicture(sImageUri);
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(FacilityActivity.this, "Failed to load profile", Toast.LENGTH_SHORT).show());
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
        return Uri.parse("data:image/png;base64," + base64String);
    }

}

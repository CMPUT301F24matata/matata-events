package com.example.matata;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * ProfileActivity manages the user's profile, allowing them to edit personal information,
 * view and update profile images, and manage facility data if they are an organizer.
 * It also includes features for notifications and admin views.
 *
 * Outstanding issues: Facility information is only loaded when the user toggles the organizer switch.
 * Additionally, profile image handling relies on shared preferences to store the image URI,
 * which may not persist across devices.
 */
public class TestProfileActivity extends AppCompatActivity {

    /**
     * ImageView to display the user's profile picture.
     */
    ImageView profileIcon;

    /**
     * FirebaseFirestore instance for accessing Firestore database.
     */
    private static FirebaseFirestore db;

    /**
     * Switch to enable or disable notifications for the user.
     */
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch notifications;

    /**
     * String containing the URI of the user's profile image.
     */
    private String imageUriString;

    /**
     * Unique identifier for the user, set to the device ID.
     */
    private String USER_ID = "";

    /**
     * Spinner for selecting the user's gender.
     */
    private Spinner genderSpinner;

    /**
     * EditText for entering the user's date of birth.
     */
    private EditText dobEditText;

    /**
     * EditText for entering the user's email address.
     */
    private EditText emailEditText;

    /**
     * EditText for entering the user's phone number.
     */
    private EditText phoneEditText;

    /**
     * EditText for entering the user's name.
     */
    private EditText nameEditText;

    /**
     * String for storing the user's device ID.
     */
    private String userId;

    private static final int NOTIFICATION_PERMISSION_CODE = 1002;


    /**
     * ActivityResultLauncher for handling profile picture selection.
     */
    private final ActivityResultLauncher<Intent> profilePicLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    loadProfilePicture(getSharedPreferences("ProfilePrefs", MODE_PRIVATE)
                            .getString("profile_image_uri", null));
                }
            });

    /**
     * Initializes the activity, sets up UI components, loads user profile data, and configures event listeners.
     *
     * @param savedInstanceState if the activity is being re-initialized, this contains the data it most recently supplied
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("myTag", "This is my message");
        setContentView(R.layout.edit_profile);

        db = FirebaseFirestore.getInstance();

        userId = getIntent().getStringExtra("USER_ID");
        if (userId != null) {
            USER_ID = userId;
        } else {
            USER_ID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        }

        profileIcon = findViewById(R.id.profileIcon);
        nameEditText = findViewById(R.id.nameEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        emailEditText = findViewById(R.id.emailEditText);
        Button saveButton = findViewById(R.id.saveButton);
        Button clearAllButton = findViewById(R.id.clearAllButton);
        ImageView back = findViewById(R.id.btnBackProfile);
        notifications = findViewById(R.id.switch_notification);
        genderSpinner = findViewById(R.id.genderSpinner);
        dobEditText = findViewById(R.id.dobEditText);

        imageUriString = getSharedPreferences("ProfilePrefs", MODE_PRIVATE)
                .getString("profile_image_uri", null);

        loadProfilePicture(imageUriString);

        back.setOnClickListener(v -> finish());

        profileIcon.setOnClickListener(v -> {
            Intent intent = new Intent(TestProfileActivity.this, ProfilePicActivity.class);
            intent.putExtra("userId", USER_ID);
            profilePicLauncher.launch(intent);
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.gender_options, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(adapter);

        genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedGender = parent.getItemAtPosition(position).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(TestProfileActivity.this, "Please select one of the options", Toast.LENGTH_SHORT).show();
            }
        });

        dobEditText.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        String date = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                        dobEditText.setText(date);
                    }, year, month, day);

            datePickerDialog.show();
        });

        // Opt in and out of notifications.
        notifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                Toast.makeText(TestProfileActivity.this, "Notifications enabled", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(TestProfileActivity.this, "Notifications disabled", Toast.LENGTH_SHORT).show();

            }
        });

        saveButton.setOnClickListener(v -> {
            Toast.makeText(this, "Text saved", Toast.LENGTH_SHORT).show();
        });

        clearAllButton.setOnClickListener(v -> {
            nameEditText.setText("");
            emailEditText.setText("");
            phoneEditText.setText("");
            notifications.setChecked(false);
            genderSpinner.setSelection(0);
            dobEditText.setText("");

        });

    }

    /**
     * Loads the user's profile data from Firestore and populates the UI.
     */
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
                        String sGender = documentSnapshot.getString("gender");
                        String sDOB = documentSnapshot.getString("DOB");

                        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                                R.array.gender_options, android.R.layout.simple_spinner_item);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                        genderSpinner.setSelection(adapter.getPosition(sGender));
                        nameEditText.setText(name != null ? name : "");
                        phoneEditText.setText(phone != null ? phone : "");
                        emailEditText.setText(email != null ? email : "");
                        notifications.setChecked(notificationsChecked);
                        dobEditText.setText(sDOB != null ? sDOB : "");

                        loadProfilePicture(sImageUri);
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(TestProfileActivity.this, "Failed to load profile", Toast.LENGTH_SHORT).show());
    }

    /**
     * Saves user profile data to Firestore.
     *
     * @param name               the name of the user
     * @param phone              the phone number of the user
     * @param email              the email address of the user
     * @param notificationsChecked if notifications are enabled
     * @param imageUriString     the URI of the profile image
     */
    private void saveProfileData(String name, String phone, String email, boolean notificationsChecked, String imageUriString, String selectedGender, String dobText) {
        loadProfilePicture(imageUriString);

        Map<String, Object> userProfile = new HashMap<>();
        userProfile.put("username", name);
        userProfile.put("phone", phone);
        userProfile.put("email", email);
        userProfile.put("notifications", notificationsChecked);
        userProfile.put("profileUri", imageUriString);
        userProfile.put("gender", selectedGender);
        userProfile.put("DOB", dobText);
        userProfile.put("freeze", "awake");
        userProfile.put("admin", "entrant");

        db.collection("USER_PROFILES").document(USER_ID)
                .set(userProfile)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(TestProfileActivity.this, "Profile saved successfully", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(TestProfileActivity.this, "Failed to save profile", Toast.LENGTH_SHORT).show());
    }

    /**
     * Loads the user's profile picture from a URI or creates an initial-based image if no URI is available.
     *
     * @param imageUriString the URI of the profile image as a String
     */
    void loadProfilePicture(String imageUriString) {
        if (imageUriString != null && !imageUriString.isEmpty()) {
            Uri imageUri = Uri.parse(imageUriString);
            Glide.with(this)
                    .load(imageUri)
                    .into(profileIcon);
        } else {
            String un = nameEditText.getText().toString().trim();
            if (un.isEmpty()) {
                profileIcon.setImageResource(R.drawable.ic_user_profile);
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

    /**
     * Creates a URI for an image with the specified initials displayed in the center.
     *
     * @param context the application context for accessing resources
     * @param text    the initials to display on the generated image
     * @return the URI of the generated image
     */
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

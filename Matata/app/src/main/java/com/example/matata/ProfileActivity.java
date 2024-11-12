package com.example.matata;

import android.app.DatePickerDialog;
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
public class ProfileActivity extends AppCompatActivity {

    ImageView profileIcon;
    private FirebaseFirestore db;
    Switch notifications;
    private String imageUriString;
    private String USER_ID = "";
    private CompoundButton adminView;
    private View organizerField;
    private Spinner genderSpinner;
    private EditText dobEditText, emailEditText, phoneEditText, nameEditText;

    // ActivityResultLauncher for user profile picture selection
    private final ActivityResultLauncher<Intent> profilePicLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    loadProfilePicture(getSharedPreferences("ProfilePrefs", MODE_PRIVATE)
                            .getString("profile_image_uri", null));
                }
            });

    /**
     * Initializes ProfileActivity, sets up UI components, loads user and facility data from Firestore,
     * and configures event listeners for saving data and profile image updates.
     *
     * @param savedInstanceState if the activity is being re-initialized, this contains the data it most recently supplied
     */
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
        Button clearAllButton = findViewById(R.id.clearAllButton);
        ImageView back = findViewById(R.id.btnBackProfile);
        notifications = findViewById(R.id.switch_notification);
        adminView = findViewById(R.id.adminView);
        genderSpinner = findViewById(R.id.genderSpinner);
        dobEditText = findViewById(R.id.dobEditText);
        ImageView btnSwitchToFacilityProfile = findViewById(R.id.btnSwitchToFacilityProfile);

        imageUriString = getSharedPreferences("ProfilePrefs", MODE_PRIVATE)
                .getString("profile_image_uri", null);

        loadProfilePicture(imageUriString);
        loadProfileData();

        back.setOnClickListener(v -> finish());

        btnSwitchToFacilityProfile.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, FacilityActivity.class);
            startActivity(intent);
        });

        profileIcon.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, ProfilePicActivity.class);
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
                Toast.makeText(ProfileActivity.this, "Please select one of the options", Toast.LENGTH_SHORT).show();
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

        saveButton.setOnClickListener(v -> {
            String name = nameEditText.getText().toString().trim();
            String email = emailEditText.getText().toString().trim();
            String phoneNumber = phoneEditText.getText().toString().trim();
            boolean notificationsChecked = notifications.isChecked();
            String dobText = dobEditText.getText().toString();
            String selectedGender = genderSpinner.getSelectedItem().toString();

            if (name.isEmpty()) {
                Toast.makeText(this, "No UserName Found", Toast.LENGTH_SHORT).show();
            } else if (email.isEmpty()) {
                Toast.makeText(this, "No Email Found", Toast.LENGTH_SHORT).show();
            } else {
                saveProfileData(name, phoneNumber, email, notificationsChecked, imageUriString, selectedGender, dobText);
            }
        });

        clearAllButton.setOnClickListener(v -> {
            nameEditText.setText("");
            emailEditText.setText("");
            phoneEditText.setText("");
            notifications.setChecked(false);
            genderSpinner.setSelection(0);
            dobEditText.setText("");

            String name = nameEditText.getText().toString().trim();
            String email = emailEditText.getText().toString().trim();
            String phoneNumber = phoneEditText.getText().toString().trim();
            boolean notificationsChecked = notifications.isChecked();
            imageUriString = "";
            String gender = genderSpinner.toString().trim();
            String dobText = dobEditText.getText().toString().trim();

            saveProfileData(name, phoneNumber, email, notificationsChecked, imageUriString, gender, dobText);

        });

        adminView.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Intent intent = new Intent(buttonView.getContext(), isChecked ? AdminView.class : MainActivity.class);
            buttonView.getContext().startActivity(intent);
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
                .addOnFailureListener(e -> Toast.makeText(ProfileActivity.this, "Failed to load profile", Toast.LENGTH_SHORT).show());
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

        db.collection("USER_PROFILES").document(USER_ID)
                .set(userProfile)
                .addOnSuccessListener(aVoid -> Toast.makeText(ProfileActivity.this, "Profile saved successfully", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(ProfileActivity.this, "Failed to save profile", Toast.LENGTH_SHORT).show());
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

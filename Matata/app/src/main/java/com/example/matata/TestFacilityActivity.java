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
import android.provider.Settings;
import android.util.Base64;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;

/**
 * The `TestFacilityActivity` class provides a stripped-down version of the `FacilityActivity` for UI testing purposes.
 * It focuses on testing basic UI interactions, such as editing, saving, clearing, and selecting a profile picture.
 *
 * <h2>Features:</h2>
 * <ul>
 *     <li>Edit and save facility details, such as name, address, capacity, contact, email, and owner.</li>
 *     <li>Toggle notification settings for the facility.</li>
 *     <li>Select a profile picture from the gallery or generate a default image based on initials.</li>
 *     <li>Clear all fields to reset the form.</li>
 * </ul>
 */
public class TestFacilityActivity extends AppCompatActivity {

    /**
     * EditText for entering the facility name.
     */
    private EditText facilityName;

    /**
     * EditText for entering the facility address.
     */
    private EditText facilityAddress;

    /**
     * EditText for entering the facility capacity.
     */
    private EditText facilityCapacity;

    /**
     * EditText for entering the facility contact information.
     */
    private EditText facilityContact;

    /**
     * EditText for entering the facility email address.
     */
    private EditText facilityEmail;

    /**
     * EditText for entering the facility owner's name.
     */
    private EditText facilityOwner;

    /**
     * Switch for enabling or disabling notifications for the facility.
     */
    private Switch switchNotification;

    /**
     * String representing the URI of the selected profile picture.
     */
    private String imageUriString;

    /**
     * String representing the unique ID of the user.
     */
    private String USER_ID = "";

    /**
     * ImageView for displaying the facility's profile picture.
     */
    private ImageView profileIcon;

    /**
     * Button for saving facility data.
     */
    private Button saveButton;

    /**
     * Button for clearing all entered data in the form.
     */
    private Button clearAllButton;

    /**
     * ImageView for handling the back button functionality.
     */
    private ImageView btnBackProfile;

    /**
     * ActivityResultLauncher for handling the result of the profile picture selection.
     */
    private final ActivityResultLauncher<Intent> profilePicLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    loadFacilityPicture(imageUriString);
                }
            });

    /**
     * Called when the activity is created. Sets up the UI components, initializes fields, and handles button interactions.
     *
     * @param savedInstanceState The activity's previously saved state, if any.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_facility_profile);

        facilityName = findViewById(R.id.facilityName);
        facilityAddress = findViewById(R.id.facilityAddress);
        facilityCapacity = findViewById(R.id.facilityCapacity);
        facilityContact = findViewById(R.id.facilityContact);
        facilityEmail = findViewById(R.id.facilityEmail);
        facilityOwner = findViewById(R.id.facilityOwner);
        switchNotification = findViewById(R.id.switch_notification);
        saveButton = findViewById(R.id.saveButton);
        clearAllButton = findViewById(R.id.clearAllButton);
        btnBackProfile = findViewById(R.id.btnBackProfile);
        profileIcon = findViewById(R.id.profileIcon);

        USER_ID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        btnBackProfile.setOnClickListener(v -> finish());

        imageUriString = null;
        loadFacilityPicture(imageUriString);

        profileIcon.setOnClickListener(v -> {
            Intent intent = new Intent(TestFacilityActivity.this, FacilityPicActivity.class);
            intent.putExtra("userId", USER_ID);
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

            if (name.isEmpty() || address.isEmpty() || contact.isEmpty() || capacity.isEmpty() ||
                    email.isEmpty() || owner.isEmpty()) {
                Toast.makeText(this, "All fields must be filled", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Data saved", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(this, "All fields cleared", Toast.LENGTH_SHORT).show();
        });
    }

    /**
     * Loads the facility's profile picture. If no picture is provided, generates a default image based on initials.
     *
     * @param imageUriString URI of the selected profile picture.
     */
    void loadFacilityPicture(String imageUriString) {
        if (imageUriString != null && !imageUriString.isEmpty()) {
            // Placeholder for real image loading logic
            profileIcon.setImageURI(Uri.parse(imageUriString));
        } else {
            String un = facilityName.getText().toString().trim();
            if (un.isEmpty()) {
                profileIcon.setImageResource(R.drawable.ic_facility_profile);
            } else {
                StringBuilder initials = new StringBuilder();
                String[] words = un.split("\\s+");
                for (String word : words) {
                    if (!word.isEmpty()) {
                        initials.append(word.charAt(0));
                    }
                }
                Uri initialsUri = createImageFromString(this, initials.toString());
                profileIcon.setImageURI(initialsUri);
            }
        }
    }

    /**
     * Generates a circular image containing the provided initials.
     *
     * @param context The application context.
     * @param text    The initials to be displayed on the image.
     * @return URI of the generated image.
     */
    public static Uri createImageFromString(Context context, String text) {
        int width = 400, height = 400;
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.BLACK);

        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(100);
        paint.setTextAlign(Paint.Align.CENTER);
        float textOffset = (paint.descent() + paint.ascent()) / 2;
        canvas.drawText(text, width / 2f, height / 2f - textOffset, paint);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        String base64Image = Base64.encodeToString(stream.toByteArray(), Base64.DEFAULT);
        return Uri.parse("data:image/png;base64," + base64Image);
    }
}

package com.example.matata;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

/**
 * The `TestAddEventActivity` class is a simplified version of an event creation activity.
 * It is primarily designed for testing UI interactions such as form validation, QR code generation,
 * image selection, and input management.
 *
 * <h2>Features:</h2>
 * <ul>
 *     <li>Input fields for event title, date, time, location, capacity, and description.</li>
 *     <li>QR code generation for the event ID.</li>
 *     <li>Image selection for uploading a poster.</li>
 *     <li>Form validation to ensure all required fields are filled.</li>
 *     <li>Clear button to reset all input fields.</li>
 * </ul>
 */
public class TestAddEventActivity extends AppCompatActivity {

    /**
     * TextView for selecting the event date.
     */
    private TextView eventDate;

    /**
     * TextView for selecting the event time.
     */
    private TextView eventTime;

    /**
     * TextView for entering the event location.
     */
    private TextView location;

    /**
     * EditText for entering the event title.
     */
    private EditText eventTitle;

    /**
     * EditText for entering the event description.
     */
    private EditText descriptionBox;

    /**
     * EditText for entering the event capacity.
     */
    private EditText capacityBox;

    /**
     * ImageView for uploading and displaying the event poster.
     */
    private ImageView posterImage;

    /**
     * Switch for enabling or disabling geolocation requirements for the event.
     */
    private Switch geoRequirementSwitch;

    /**
     * Button for generating a QR code for the event.
     */
    private Button generateQRButton;

    /**
     * Button for clearing all input fields.
     */
    private Button clearAllButton;

    /**
     * Unique identifier for the event.
     */
    private String eventId;

    /**
     * Launcher for handling image selection from the device's gallery.
     */
    private final ActivityResultLauncher<Intent> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri selectedImageUri = result.getData().getData();
                    posterImage.setImageURI(selectedImageUri);
                    Toast.makeText(this, "Image selected", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Image selection canceled", Toast.LENGTH_SHORT).show();
                }
            });

    /**
     * Called when the activity is created.
     * Initializes all views, sets up click listeners, and generates a unique event ID.
     *
     * @param savedInstanceState Bundle containing the activity's previously saved state, if any.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_new_event);

        initializeViews();

        eventId = UUID.randomUUID().toString();

        eventTime.setOnClickListener(v -> openTimePicker());
        eventDate.setOnClickListener(v -> openDatePicker());

        posterImage.setOnClickListener(v -> openImagePicker());

        generateQRButton.setOnClickListener(v -> {
            if (validateInputs()) {
                Bitmap qrBitmap = generateQRBitmap(eventId);
                Toast.makeText(this, "QR Code Generated", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            }
        });

        clearAllButton.setOnClickListener(v -> clearFields());
    }

    /**
     * Opens the time picker dialog.
     */
    private void openTimePicker() {
        DialogFragment timePicker = new TimePickerFragment();
        timePicker.setCancelable(true);
        timePicker.show(getSupportFragmentManager(), "timePicker");
    }

    /**
     * Opens the date picker dialog.
     */
    private void openDatePicker() {
        DialogFragment datePicker = new DatePickerFragment();
        datePicker.setCancelable(true);
        datePicker.show(getSupportFragmentManager(), "datePicker");
    }

    /**
     * Initializes all views used in the activity.
     */
    private void initializeViews() {
        eventDate = findViewById(R.id.editTextDate);
        eventTime = findViewById(R.id.editTextTime);
        location = findViewById(R.id.editTextLocation);
        eventTitle = findViewById(R.id.eventTitle);
        descriptionBox = findViewById(R.id.desc_box);
        capacityBox = findViewById(R.id.number_of_people_event);
        posterImage = findViewById(R.id.posterPicUpload);
        geoRequirementSwitch = findViewById(R.id.geoRequirement);
        generateQRButton = findViewById(R.id.genQR);
        clearAllButton = findViewById(R.id.clearAllButton);
    }

    /**
     * Validates the user input to ensure that all required fields are filled.
     *
     * @return {@code true} if all required fields are filled; {@code false} otherwise.
     */
    private boolean validateInputs() {
        return !eventTitle.getText().toString().isEmpty() &&
                !descriptionBox.getText().toString().isEmpty() &&
                !eventDate.getText().toString().isEmpty() &&
                !eventTime.getText().toString().isEmpty() &&
                !capacityBox.getText().toString().isEmpty();
    }

    /**
     * Clears all input fields and resets the poster image to the default placeholder.
     */
    private void clearFields() {
        eventTitle.setText("");
        descriptionBox.setText("");
        eventDate.setText("");
        eventTime.setText("");
        capacityBox.setText("");
        location.setText("");
        posterImage.setImageResource(R.drawable.ic_upload);
        Toast.makeText(this, "All Fields Cleared", Toast.LENGTH_SHORT).show();
    }

    /**
     * Opens the device's gallery to allow the user to select an image for the event poster.
     */
    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        pickImageLauncher.launch(Intent.createChooser(intent, "Select Picture"));
    }

    /**
     * Generates a QR code bitmap for the given data using the {@link BarcodeEncoder}.
     *
     * @param data The data to encode in the QR code.
     * @return A {@link Bitmap} representing the generated QR code.
     * @throws RuntimeException If the QR code generation fails.
     */
    private Bitmap generateQRBitmap(String data) {
        try {
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            return barcodeEncoder.encodeBitmap(data, BarcodeFormat.QR_CODE, 500, 500);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate QR Code", e);
        }
    }

    /**
     * Compresses the given bitmap into a Base64-encoded string.
     *
     * @param bitmap The bitmap to compress.
     * @return A Base64-encoded string representing the compressed bitmap.
     */
    public String compressBitmap(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);
    }
}

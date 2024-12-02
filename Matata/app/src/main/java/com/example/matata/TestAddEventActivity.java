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
 * TestAddEventActivity is a simplified version of AddEvent for UI testing.
 * It focuses on testing input fields, QR generation, and UI interactions.
 */
public class TestAddEventActivity extends AppCompatActivity {

    private TextView eventDate, eventTime, location;
    private EditText eventTitle, descriptionBox, capacityBox;
    private ImageView posterImage;
    private Switch geoRequirementSwitch;
    private Button generateQRButton, clearAllButton;
    private String eventId;

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

    private boolean validateInputs() {
        return !eventTitle.getText().toString().isEmpty() &&
                !descriptionBox.getText().toString().isEmpty() &&
                !eventDate.getText().toString().isEmpty() &&
                !eventTime.getText().toString().isEmpty() &&
                !capacityBox.getText().toString().isEmpty();
    }

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

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        pickImageLauncher.launch(Intent.createChooser(intent, "Select Picture"));
    }

    private Bitmap generateQRBitmap(String data) {
        try {
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            return barcodeEncoder.encodeBitmap(data, BarcodeFormat.QR_CODE, 500, 500);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate QR Code", e);
        }
    }

    public String compressBitmap(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);
    }
}

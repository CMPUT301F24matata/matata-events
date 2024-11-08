package com.example.matata;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.provider.Settings;
import android.util.Base64;
import java.io.ByteArrayOutputStream;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * AddEvent Activity for creating new events within the Matata application.
 * This activity allows users to input event details, select a date and time,
 * upload an event poster, and generate a QR code. The event information is stored
 * in Firebase Firestore and the image in Firebase Storage.
 * Implements TimePickerListener and DatePickerListener for selecting event date and time.
 */
public class AddEvent extends AppCompatActivity implements TimePickerListener,DatePickerListener {

    private ImageView backBtn;
    TextView eventTime;
    TextView eventDate;
    private TextView location;
    private LinearLayout dateGroup, timeGroup;
    private ImageView posterPic;
    private FloatingActionButton genrQR;
    private EditText eveTitle, descriptionBox, capacity;
    private FirebaseFirestore db;
    private StorageReference ref;
    private String USER_ID, posterURI;
    private boolean isDefaultImage = true;
    private static final int PICK_IMAGE_REQUEST = 1;

    /**
     * Initializes the activity, sets up the Firebase instances, and assigns view elements.
     * Also, sets up click listeners for back button, date and time pickers, and QR generation.
     *
     * @param savedInstanceState Bundle object containing the activity's previously saved state.
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        db = FirebaseFirestore.getInstance();
        ref = FirebaseStorage.getInstance("gs://matata-d53da.firebasestorage.app").getReference();

        String EVENT_ID = generateRandomEventID();
        USER_ID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_new_event);

        initializeViews();

        backBtn.setOnClickListener(view -> handleBackPress());
        timeGroup.setOnClickListener(v -> openTimePicker());
        dateGroup.setOnClickListener(v -> openDatePicker());
        posterPic.setOnClickListener(v -> openSelector());
        genrQR.setOnClickListener(view -> handleGenerateQR(EVENT_ID, view));
    }

    /**
     * Sets up references to UI components for use within the activity.
     */
    private void initializeViews() {
        backBtn = findViewById(R.id.btnBackCreateEvent);
        eventTime = findViewById(R.id.dateField);
        dateGroup = findViewById(R.id.dateGroup);
        timeGroup = findViewById(R.id.timeGroup);
        eventDate = findViewById(R.id.editTextDate);
        posterPic = findViewById(R.id.posterPicUpload);
        genrQR = findViewById(R.id.genQR);
        eveTitle = findViewById(R.id.eventTitle);
        descriptionBox = findViewById(R.id.desc_box);
        capacity = findViewById(R.id.number_of_people_event);
        location = findViewById(R.id.editTextLocation);
    }

    /**
     * Handles back button press, showing a confirmation dialog if fields are filled.
     */
    private void handleBackPress() {
        if (!eveTitle.getText().toString().isEmpty() ||
                !descriptionBox.getText().toString().isEmpty() ||
                !eventDate.getText().toString().isEmpty() ||
                !eventTime.getText().toString().isEmpty() ||
                !capacity.getText().toString().isEmpty()) {

            ConfirmationFragment backpress = new ConfirmationFragment();
            backpress.show(getSupportFragmentManager(), "BackPressFragment");
        } else {
            finish();
        }
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
     * Validates input fields and initiates event QR generation and storage if fields are complete.
     *
     * @param EVENT_ID Unique identifier for the event.
     * @param view Current view context.
     */
    private void handleGenerateQR(String EVENT_ID, View view) {
        if (!eveTitle.getText().toString().isEmpty() &&
                !descriptionBox.getText().toString().isEmpty() &&
                !eventDate.getText().toString().isEmpty() &&
                !eventTime.getText().toString().isEmpty() &&
                !capacity.getText().toString().isEmpty()) {

            Event event = new Event(
                    eveTitle.getText().toString(),
                    eventDate.getText().toString(),
                    eventTime.getText().toString(),
                    location.getText().toString(),
                    descriptionBox.getText().toString(),
                    Integer.parseInt(capacity.getText().toString()),
                    EVENT_ID, USER_ID, -1
            );

            Intent intent = new Intent(view.getContext(), ViewEvent.class);
            SaveEventInfo(EVENT_ID, event, intent, view);
        } else {
            Toast.makeText(AddEvent.this, "Please fill in all the details", Toast.LENGTH_SHORT).show();
        }
    }

    ActivityResultLauncher<Intent> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    posterPic.setImageURI(imageUri);
                    isDefaultImage = false;
                }
            }
    );

    /**
     * Opens the image selector for uploading a poster.
     */
    public void openSelector() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        pickImageLauncher.launch(intent);
    }

    /**
     * Saves event information to Firestore, including QR code and optional poster image.
     *
     * @param EVENT_ID Unique identifier for the event.
     * @param event Event object containing all event data.
     * @param intent Intent to navigate to the ViewEvent activity.
     * @param view Current view context.
     * @return String EVENT_ID for the created event.
     */
    private String SaveEventInfo(String EVENT_ID, Event event, Intent intent, View view) {
        Bitmap bmp = generateQRbitmap(EVENT_ID);
        String compressedBMP = bmpCompression(bmp);
        StorageReference imagesRef = ref.child("EventsPosters/" + EVENT_ID + ".jpg");

        Map<String, Object> Event_details = new HashMap<>();
        Event_details.put("Poster", posterURI);
        Event_details.put("Title", event.getTitle());
        Event_details.put("Date", event.getDate());
        Event_details.put("Time", event.getTime());
        Event_details.put("Location", event.getLocation());
        Event_details.put("Description", event.getDescription());
        Event_details.put("Capacity", event.getCapacity());
        Event_details.put("WaitlistLimit", event.getWaitlistLimit());
        Event_details.put("bitmap", compressedBMP);
        Event_details.put("OrganizerID", USER_ID);

        if (isDefaultImage) {
            Event_details.put("Poster", "");
            executeDBchange(Event_details, EVENT_ID, intent, view);
        } else {
            Bitmap bmpjpg = ((BitmapDrawable) posterPic.getDrawable()).getBitmap();
            File temp = new File(getCacheDir(), EVENT_ID + ".jpg");

            try (FileOutputStream out = new FileOutputStream(temp)) {
                bmpjpg.compress(Bitmap.CompressFormat.JPEG, 100, out);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            Uri returned = Uri.fromFile(temp);
            imagesRef.putFile(returned)
                    .addOnSuccessListener(v -> imagesRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        posterURI = uri.toString();
                        Event_details.put("Poster", posterURI);
                        temp.delete();
                        executeDBchange(Event_details, EVENT_ID, intent, view);
                    }))
                    .addOnFailureListener(e -> Toast.makeText(this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }

        DocumentReference doc = db.collection("EVENT_PROFILES").document(EVENT_ID);
        doc.get()
                .addOnSuccessListener(documentSnapshot -> {
                    executeDBchange(Event_details, EVENT_ID, intent, view);
                    intent.putExtra("Unique_id", EVENT_ID);
                    view.getContext().startActivity(intent);
                })
                .addOnFailureListener(v -> Toast.makeText(AddEvent.this, "Server upload failed", Toast.LENGTH_SHORT).show());

        return EVENT_ID;
    }

    /**
     * Generates a unique identifier for the event.
     *
     * @return UUID as a String.
     */
    String generateRandomEventID() {
        return UUID.randomUUID().toString();
    }

    /**
     * Executes changes in Firestore to save event details.
     *
     * @param Event_details Map containing event details.
     * @param EVENT_ID Unique identifier for the event.
     * @param intent Intent to navigate to the ViewEvent activity.
     * @param view Current view context.
     */
    private void executeDBchange(Map<String, Object> Event_details, String EVENT_ID, Intent intent, View view) {
        db.collection("EVENT_PROFILES").document(EVENT_ID)
                .set(Event_details, SetOptions.merge())
                .addOnSuccessListener(aVoid -> Toast.makeText(AddEvent.this, "Event saved successfully", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(AddEvent.this, "Failed to save profile", Toast.LENGTH_SHORT).show());
    }

    /**
     * Generates a QR code bitmap for the event ID.
     *
     * @param EVENT_ID Unique identifier for the event.
     * @return Bitmap QR code for the event.
     */
    Bitmap generateQRbitmap(String EVENT_ID) {
        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
        try {
            return barcodeEncoder.encodeBitmap(EVENT_ID, BarcodeFormat.QR_CODE, 500, 500);
        } catch (WriterException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Generates a SHA-256 hash for the input string.
     *
     * @param input Input string to hash.
     * @return SHA-256 hash as a String.
     */
    public String generateHash(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Compresses a bitmap image to a Base64 string format.
     *
     * @param bmp Bitmap image to compress.
     * @return Compressed Base64 encoded string of the bitmap.
     */
    public String bmpCompression(Bitmap bmp) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    /**
     * Sets the selected time on the TextView after time selection.
     *
     * @param hour Selected hour.
     * @param min Selected minute.
     */
    @Override
    public void onTimeSelected(int hour, int min) {
        String time = String.format("%02d:%02d", hour, min);
        Log.wtf(TAG, time);
        eventTime.setText(time);
    }

    /**
     * Sets the selected date on the TextView after date selection.
     *
     * @param year Selected year.
     * @param month Selected month.
     * @param date Selected date.
     */
    @Override
    public void onDateSelected(int year, int month, int date) {
        String date_str = String.format("%02d/%02d/%04d", date, month + 1, year);
        eventDate.setText(date_str);
    }
}

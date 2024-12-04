package com.example.matata;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Base64;
import java.io.ByteArrayOutputStream;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * AddEvent Activity for creating new events within the Matata application.
 * This activity allows users to input event details, select a date and time,
 * upload an event poster, and generate a QR code. The event information is stored
 * in Firebase Firestore, and the image is stored in Firebase Storage.
 * Implements TimePickerListener and DatePickerListener for selecting event date and time.
 */
public class AddEvent extends AppCompatActivity implements TimePickerListener, DatePickerListener {

    /**
     * ImageView for navigating back to the previous screen.
     */
    private ImageView backBtn;

    /**
     * TextView for displaying and selecting the event time.
     */
    private TextView eventTime;

    /**
     * TextView for displaying and selecting the event date.
     */
    private TextView eventDate;

    /**
     * TextView displaying the location of the event.
     */
    private TextView location;

    /**
     * ImageView for uploading or displaying the event poster image.
     */
    private ImageView posterPic;

    /**
     * Button for generating a QR code for the event.
     */
    private Button genrQR;

    /**
     * Button for clearing all input fields in the activity.
     */
    private Button clearAllButton;

    /**
     * EditText for entering the title of the event.
     */
    private EditText eveTitle;

    /**
     * EditText for entering a description of the event.
     */
    private EditText descriptionBox;

    /**
     * EditText for specifying the capacity of the event.
     */
    private EditText capacity;

    /**
     * FirebaseFirestore instance for accessing Firestore database.
     */
    private FirebaseFirestore db;

    /**
     * StorageReference instance for accessing Firebase Storage.
     */
    private StorageReference ref;

    /**
     * String representing the unique ID of the user.
     */
    private String USER_ID;

    /**
     * String representing the unique ID of the event.
     */
    private String EVENT_ID;

    /**
     * String containing the URI of the uploaded poster image.
     */
    private String posterURI;


    private Uri test_uri;
    /**
     * Boolean indicating whether the default image is being used for the poster.
     */
    private boolean isDefaultImage = true;

    /**
     * Flag indicating if the activity should refresh upon resuming.
     */
    private boolean shouldRefreshOnResume = false;

    /**
     * Request code for selecting an image from the gallery.
     */
    private static final int PICK_IMAGE_REQUEST = 1;

    /**
     * Switch for toggling geolocation requirement for the event.
     */
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch geoRequirement;


    /**
     * Initializes the activity, sets up Firebase instances, assigns view elements, and
     * defines click listeners for various components.
     *
     * @param savedInstanceState Bundle object containing the activity's previously saved state.
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        db = FirebaseFirestore.getInstance();
        ref = FirebaseStorage.getInstance("gs://matata-d53da.firebasestorage.app").getReference();

        EVENT_ID = generateRandomEventID();
        USER_ID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_new_event);

        initializeViews();

        backBtn.setOnClickListener(view -> handleBackPress());
        eventTime.setOnClickListener(v -> openTimePicker());
        eventDate.setOnClickListener(v -> openDatePicker());
        posterPic.setOnClickListener(v -> openSelector());
        genrQR.setOnClickListener(view -> handleGenerateQR(EVENT_ID, USER_ID, view));
        clearAllButton.setOnClickListener(v -> {
            eveTitle.setText("");
            descriptionBox.setText("");
            eventTime.setText("");
            eventDate.setText("");
            capacity.setText("");
            posterPic.setImageURI(null);
            Toast.makeText(AddEvent.this, "All Fields Cleared", Toast.LENGTH_SHORT).show();
        });

        DocumentReference docRef = db.collection("FACILITY_PROFILES").document(USER_ID);
        docRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String freeze = documentSnapshot.getString("freeze");

                        if (Objects.equals(freeze, "frozen")) {
                            Intent intent = new Intent(AddEvent.this, FacilityActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        else if (Objects.equals(freeze, "awake")) {
                            String name = documentSnapshot.getString("name");
                            String address = documentSnapshot.getString("address");
                            String email = documentSnapshot.getString("email");
                            boolean notificationsChecked = Boolean.TRUE.equals(documentSnapshot.getBoolean("notifications"));
                            String sImageUri = documentSnapshot.getString("profileUri");
                            String capacity = documentSnapshot.getString("capacity");
                            String contact = documentSnapshot.getString("contact");
                            String owner = documentSnapshot.getString("owner");

                            if (!name.isEmpty() &&
                                    !address.isEmpty() &&
                                    !contact.isEmpty() &&
                                    !capacity.isEmpty() &&
                                    !email.isEmpty() &&
                                    !owner.isEmpty()) {
                                location.setText(address);
                                showFacilityDialog(name, address, capacity, contact, email, owner, notificationsChecked, sImageUri);
                            } else {
                                Toast.makeText(AddEvent.this, "Please Provide Facility Information First", Toast.LENGTH_SHORT).show();
                                shouldRefreshOnResume = true;
                                Intent intent = new Intent(AddEvent.this, FacilityActivity.class);
                                startActivity(intent);
                            }
                        }
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(AddEvent.this, "Failed to load profile", Toast.LENGTH_SHORT).show());

    }

    /**
     * Refreshes the activity if required when it is restarted.
     */
    @Override
    protected void onRestart() {
        super.onRestart();
        if (shouldRefreshOnResume) {
            shouldRefreshOnResume = false;
            recreate();
        }
    }

    /**
     * Sets up references to UI components for use within the activity.
     */
    private void initializeViews() {
        backBtn = findViewById(R.id.btnBackCreateEvent);
        eventDate = findViewById(R.id.editTextDate);
        eventTime = findViewById(R.id.editTextTime);
        posterPic = findViewById(R.id.posterPicUpload);
        genrQR = findViewById(R.id.genQR);
        eveTitle = findViewById(R.id.eventTitle);
        descriptionBox = findViewById(R.id.desc_box);
        capacity = findViewById(R.id.number_of_people_event);
        location = findViewById(R.id.editTextLocation);
        clearAllButton = findViewById(R.id.clearAllButton);
        geoRequirement = findViewById(R.id.geoRequirement);
    }

    /**
     * Handles back button press, showing a confirmation dialog if fields are filled.
     */
    private void handleBackPress() {
        DocumentReference docRef=db.collection("EVENT_PROFILES").document(EVENT_ID);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.wtf(TAG,"task successful");
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    finish();
                } else {
                    if (areFieldsNonEmpty()) {
                        ConfirmationFragment backpress = new ConfirmationFragment();
                        backpress.show(getSupportFragmentManager(), "BackPressFragment");
                    }
                    else{
                        finish();
                    }

                }
            }
            else {
                finish();
            }
        });
    }

    /**
     * Checks if any input fields are non-empty.
     *
     * @return true if fields are non-empty, false otherwise.
     */
    private boolean areFieldsNonEmpty() {
        return !eveTitle.getText().toString().isEmpty() ||
                !descriptionBox.getText().toString().isEmpty() ||
                !eventDate.getText().toString().isEmpty() ||
                !eventTime.getText().toString().isEmpty() ||
                !capacity.getText().toString().isEmpty();
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
     * @param USER_ID Unique identifier for the user creating the event.
     * @param view Current view context.
     */
    private void handleGenerateQR(String EVENT_ID, String USER_ID, View view) {
        CollectionReference userProfilesRef = db.collection("USER_PROFILES");
        CollectionReference facilityProfilesRef = db.collection("FACILITY_PROFILES");
        CollectionReference organizerProfilesRef = db.collection("ORGANIZER_PROFILES");
        CollectionReference eventProfilesRef = db.collection("EVENT_PROFILES");

        DocumentReference userRef = userProfilesRef.document(USER_ID);
        DocumentReference facilityRef = facilityProfilesRef.document(USER_ID);
        DocumentReference organizerRef = organizerProfilesRef.document(USER_ID);
        DocumentReference eventRef = eventProfilesRef.document(EVENT_ID);

        userRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                            if (documentSnapshot.exists()) {
                                if (!"admin".equals(documentSnapshot.getString("admin"))) {
                                    userRef.update("admin", "organiser");
                                    Map<String, Object> data = new HashMap<>();
                                    data.put("organiser", "yes");
                                    userRef.set(data, SetOptions.merge());
                                }

                            }
                        });
        organizerRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Log.d("Firestore", "Organizer profile already exists.");
                    } else {
                        Map<String, Object> organizerData = new HashMap<>();
                        organizerData.put("userReference", userRef);
                        organizerData.put("facilityReference", facilityRef);

                        organizerRef.set(organizerData, SetOptions.merge())
                                .addOnSuccessListener(aVoid -> Log.d("Firestore", "User and Facility references added to organizer successfully"))
                                .addOnFailureListener(e -> Log.w("Firestore", "Error adding references", e));
                    }
                    organizerRef.update("organizedEvents", FieldValue.arrayUnion(eventRef))
                            .addOnSuccessListener(aVoid -> Log.d("Firestore", "Event added to organizer's event list"))
                            .addOnFailureListener(e -> Log.w("Firestore", "Error adding event to organizer's event list", e));
                })
                .addOnFailureListener(e -> Log.w("Firestore", "Error checking if organizer exists", e));

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
                    EVENT_ID, USER_ID,
                    -1,
                    geoRequirement.isChecked()
            );

            Intent intent = new Intent(view.getContext(), ViewEvent.class);
            SaveEventInfo(EVENT_ID, event, intent, view);
        } else {
            Toast.makeText(AddEvent.this, "Please fill in all the details", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Shows a dialog to confirm using existing facility data or editing it.
     *
     * @param name Facility name.
     * @param address Facility address.
     * @param capacity Facility capacity.
     * @param contact Facility contact information.
     * @param email Facility email address.
     * @param owner Facility owner.
     * @param notificationsEnabled Notifications enabled status.
     * @param imageUriString Image URI as a String.
     */
    void showFacilityDialog(String name, String address, String capacity, String contact, String email, String owner, boolean notificationsEnabled, String imageUriString) {
        AlertDialog.Builder builder = new AlertDialog.Builder(AddEvent.this);
        builder.setTitle("Facility Information");
        builder.setMessage("Do you want to use the same facility as before?");

        builder.setPositiveButton("Yes", (dialog, which) -> {

            Toast.makeText(AddEvent.this, "Using previous facility", Toast.LENGTH_SHORT).show();

            FacilityInfoDialogFragment facilityDialog = FacilityInfoDialogFragment.newInstance(
                    name, address, capacity, contact, email, owner, notificationsEnabled
            );

            facilityDialog.show(getSupportFragmentManager(), "FacilityInfoDialogFragment");

        });

        builder.setNegativeButton("No", (dialog, which) -> {
            shouldRefreshOnResume = true;
            Intent intent = new Intent(AddEvent.this, FacilityActivity.class);
            startActivity(intent);
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    ActivityResultLauncher<Intent> pickImageLauncher = registerForActivityResult(

            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    Log.d(TAG, "Selected Image URI: " + imageUri);

                    Glide.with(this).load(imageUri).into(posterPic);
                    test_uri=imageUri;
                    isDefaultImage = false; // Update the flag
                } else {
                    Log.e(TAG, "Image selection failed or canceled");
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

        Date currentDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String formattedDate = dateFormat.format(currentDate);

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
        Event_details.put("Status", "Active");
        Event_details.put("CreationDate", formattedDate);
        Event_details.put("GeoRequirement", event.getGeoRequirement());

        db.collection("USER_PROFILES")
                .document(USER_ID)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            String username = document.getString("username");
                            Event_details.put("OrganiserName", username);
                        } else {
                            Log.d("Firestore", "No such document for USER_ID: " + USER_ID);
                        }
                    } else {
                        Log.e("Firestore", "Failed to fetch user profile", task.getException());
                    }
                });

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
                    intent.putExtra("Parent","AddEvent");
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

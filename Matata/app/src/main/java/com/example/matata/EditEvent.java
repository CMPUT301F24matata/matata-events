package com.example.matata;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * EditEvent activity allows users to edit details of an existing event.
 * It provides options to update event details, upload a new poster image,
 * and save changes to Firebase Firestore.
 * Implements {@link DatePickerListener} and {@link TimePickerListener} interfaces
 * for selecting event date and time.
 */
public class EditEvent extends AppCompatActivity implements DatePickerListener,TimePickerListener {

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
     * Button for updating the event details.
     */
    private Button genrQR;

    /**
     * TextView for the header text, indicating "Edit Event."
     */
    private TextView headerText;

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
     * String representing the unique ID of the event.
     */
    private String EVENT_ID;

    /**
     * String representing the unique ID of the user.
     */
    private String USER_ID;

    /**
     * String containing the URI of the uploaded poster image.
     */
    private String posterURI;

    private Uri global_Uri;

    /**
     * Boolean indicating if the default image is used for the poster.
     */
    private boolean isDefaultImage = true;

    /**
     * Encoded Base64 string representing the QR code bitmap.
     */
    private String argbase64;

    /**
     * Event object containing the details of the event being edited.
     */
    private Event event;

    /**
     * Request code for the image picker intent.
     */
    private static final int PICK_IMAGE_REQUEST = 1;

    /**
     * Switch indicating if the event requires geolocation
     */





    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch geoRequirement;

    /**
     * Called when the activity is created.
     * Initializes UI components, loads event details from Firestore, and sets up click listeners.
     *
     * @param savedInstanceState Bundle containing the activity's previously saved state, if any.
     */
    @Override
    public void onCreate( @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_new_event);

        db = FirebaseFirestore.getInstance();
        ref = FirebaseStorage.getInstance("gs://matata-d53da.firebasestorage.app").getReference();




        initializeViews();
        headerText.setText("Edit Event");

        Intent intent=getIntent();
        EVENT_ID=intent.getStringExtra("Unique_id");
        USER_ID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        loadDetails();

        eventDate.setOnClickListener(v->openDatePicker());
        eventTime.setOnClickListener(v->openTimePicker());

        posterPic.setOnClickListener(v ->openSelector());
        clearAllButton.setOnClickListener(v -> {
            eveTitle.setText("");
            descriptionBox.setText("");
            eventTime.setText("");
            eventDate.setText("");
            capacity.setText("");
            posterPic.setImageResource(R.drawable.ic_upload);
            Toast.makeText(EditEvent.this, "All Fields Cleared", Toast.LENGTH_SHORT).show();
        });

        genrQR.setText("Update Event");
        genrQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        if (!eveTitle.getText().toString().isEmpty() &&
                !descriptionBox.getText().toString().isEmpty() &&
                !eventDate.getText().toString().isEmpty() &&
                !eventTime.getText().toString().isEmpty() &&
                !capacity.getText().toString().isEmpty()) {

             event = new Event(
                    eveTitle.getText().toString(),
                    eventDate.getText().toString(),
                    eventTime.getText().toString(),
                    location.getText().toString(),
                    descriptionBox.getText().toString(),
                    Integer.parseInt(capacity.getText().toString()),
                    EVENT_ID, USER_ID,
                     -1,
                     false
            );

        } else {
            Toast.makeText(EditEvent.this, "Please fill in all the details", Toast.LENGTH_SHORT).show();
        }
        genrQR.setOnClickListener(view->updateEvent(event,intent,view));

        backBtn.setOnClickListener(v->finish());


    }

    /**
     * Loads the details of the event being edited from Firestore and populates the input fields.
     */
    public void loadDetails(){
        DocumentReference doc = db.collection("EVENT_PROFILES").document(EVENT_ID);
        doc.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String Title = documentSnapshot.getString("Title");
                Long Capacity = documentSnapshot.getLong("Capacity");
                String Description = documentSnapshot.getString("Description");
                String Time = documentSnapshot.getString("Time");
                String Date = documentSnapshot.getString("Date");
                String Location = documentSnapshot.getString("Location");
                boolean GeoRequirement = documentSnapshot.getBoolean("GeoRequirement");

                argbase64 = documentSnapshot.getString("bitmap");
                try {
                    String ImageUri = documentSnapshot.getString("Poster");
                    assert ImageUri != null;
                    if (!ImageUri.isEmpty()) {
                        Log.d("Image", "loadDetails: ImgaeUri = (" + ImageUri + ")");
                        Glide.with(this).load(ImageUri).into(posterPic);
                    }
                    else {
                        Glide.with(this).load(R.drawable.ic_upload).into(posterPic);
                    }
                } catch(Exception e){
                    throw e;
                }

                Bitmap QR = decodeBase64toBmp(argbase64);

                eveTitle.setText(Title != null ? Title : "");
                capacity.setText(String.valueOf(Capacity));
                descriptionBox.setText(Description != null ? Description : "");
                eventTime.setText(Time != null ? Time : "");
                eventDate.setText(Date != null ? Date : "");
                location.setText(Location != null ? Location : "");
                geoRequirement.setChecked(GeoRequirement);
            } else {
                Toast.makeText(EditEvent.this, "No event found", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> Toast.makeText(EditEvent.this, "Failed to load event", Toast.LENGTH_SHORT).show());
    }

    /**
     * Initializes UI components by finding views and assigning them to instance variables.
     */
    private void initializeViews() {
        headerText=findViewById(R.id.headerText);
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

    /**
     * Sets the selected time on the TextView after time selection.
     *
     * @param hourOfDay Selected hour.
     * @param minute Selected minute.
     */
    @Override
    public void onTimeSelected(int hourOfDay, int minute) {
        String time = String.format("%02d:%02d", hourOfDay, minute);
        Log.wtf(TAG, time);
        eventTime.setText(time);
    }

    /**
     * Decodes a Base64 encoded string to a Bitmap image.
     *
     * @param bmp64 The Base64 encoded string.
     * @return The decoded Bitmap image.
     */
    public Bitmap decodeBase64toBmp(String bmp64) {
        try {
            byte[] decodedBytes = Base64.decode(bmp64, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        } catch (IllegalArgumentException e) {
            return null;  // Return null if decoding fails
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

    ActivityResultLauncher<Intent> pickImageLauncher = registerForActivityResult(

            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    Log.d(TAG, "Selected Image URI: " + imageUri);
                    global_Uri= imageUri;
                    Glide.with(this).load(imageUri).into(posterPic);
                    isDefaultImage = false;
                    Log.wtf(TAG,"false");
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
     * Updates the event details in Firestore and reflects the changes in the application.
     *
     * @param event Event object containing updated details.
     * @param intent Intent for navigating to another activity after updating.
     * @param view The current view context.
     */
    public void updateEvent(Event event, Intent intent, View view) {
        CollectionReference eventProfilesRef = db.collection("EVENT_PROFILES");
        DocumentReference docRef = eventProfilesRef.document(EVENT_ID);
        StorageReference posterRef = ref.child("EventPosters/" + EVENT_ID + ".jpg");

        Map<String, Object> updates = new HashMap<>();
        updates.put("Date", eventDate.getText().toString());
        updates.put("Time", eventTime.getText().toString());
        updates.put("Title", eveTitle.getText().toString());
        updates.put("Capacity", Integer.parseInt(capacity.getText().toString()));
        updates.put("Description", descriptionBox.getText().toString());
        updates.put("Location", location.getText().toString());
        updates.put("GeoRequirement", geoRequirement.isChecked());

        if (!isDefaultImage) {
            posterRef.putFile(global_Uri)
                    .addOnSuccessListener(taskSnapshot -> {
                        posterRef.getDownloadUrl()
                                .addOnSuccessListener(uri -> {
                                    String downloadUrl = uri.toString();
                                    updates.put("Poster", downloadUrl);
                                    docRef.update(updates)
                                            .addOnSuccessListener(aVoid -> {
                                                Toast.makeText(EditEvent.this, "Event Successfully updated", Toast.LENGTH_SHORT).show();
                                                Intent intentHome = new Intent(EditEvent.this, MainActivity.class);
                                                startActivity(intentHome);
                                                finish();
                                            })
                                            .addOnFailureListener(e -> {
                                                Log.e(TAG, "Error updating event in Firestore", e);
                                                Toast.makeText(EditEvent.this, "Event Update Error", Toast.LENGTH_SHORT).show();
                                            });
                                })
                                .addOnFailureListener(e -> {
                                    Log.e(TAG, "Error getting download URL", e);
                                    Toast.makeText(EditEvent.this, "Failed to update poster URL", Toast.LENGTH_SHORT).show();
                                });
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error uploading poster", e);
                        Toast.makeText(EditEvent.this, "Poster upload failed", Toast.LENGTH_SHORT).show();
                    });
        } else {
            updates.put("Poster", posterURI);
            docRef.update(updates)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(EditEvent.this, "Event Successfully updated", Toast.LENGTH_SHORT).show();
                        Intent intentHome = new Intent(EditEvent.this, MainActivity.class);
                        startActivity(intentHome);
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error updating event in Firestore", e);
                        Toast.makeText(EditEvent.this, "Event Update Error", Toast.LENGTH_SHORT).show();
                    });
        }
    }


}

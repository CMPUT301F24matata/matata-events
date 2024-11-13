package com.example.matata;

import static android.content.ContentValues.TAG;

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
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class EditEvent extends AppCompatActivity implements DatePickerListener,TimePickerListener {

    private ImageView backBtn;
    private TextView eventTime;
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
     * FloatingActionButton for generating a QR code for the event.
     */
    private Button genrQR;

    private TextView headerText;
    private Button clearAllButton;

    private boolean shouldRefreshOnResume = false;
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
    private String EVENT_ID;
    private String USER_ID;
    /**
     * String containing the URI of the uploaded poster image.
     */
    private String posterURI;
    /**
     * Boolean indicating if the default image is used for the poster.
     */

    private Event event;
    private boolean isDefaultImage = true;

    private String argbase64;
    /**
     * Request code for the image picker intent.
     */
    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    public void onCreate( @Nullable Bundle savedInstanceState) {
        db = FirebaseFirestore.getInstance();
        ref = FirebaseStorage.getInstance("gs://matata-d53da.firebasestorage.app").getReference();


        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_new_event);

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
                    EVENT_ID, USER_ID, -1
            );

        } else {
            Toast.makeText(EditEvent.this, "Please fill in all the details", Toast.LENGTH_SHORT).show();
        }
        genrQR.setOnClickListener(view->updateEvent(event,intent,view));

        backBtn.setOnClickListener(v->finish());


    }

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

                argbase64=documentSnapshot.getString("bitmap");
                try{
                    String ImageUri = documentSnapshot.getString("Poster");
                    if (ImageUri!=null) {
                        Glide.with(this).load(ImageUri).into(posterPic);
                    }
                    else{
                        ;
                    }
                }catch(Exception e){
                    throw e;
                }


                Bitmap QR = decodeBase64toBmp(argbase64);

                eveTitle.setText(Title != null ? Title : "");
                capacity.setText(String.valueOf(Capacity));
                descriptionBox.setText(Description != null ? Description : "");
                eventTime.setText(Time != null ? Time : "");
                eventDate.setText(Date != null ? Date : "");
                location.setText(Location != null ? Location : "");
            } else {
                Toast.makeText(EditEvent.this, "No event found", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> Toast.makeText(EditEvent.this, "Failed to load event", Toast.LENGTH_SHORT).show());
    }


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

    public void updateEvent(Event event, Intent intent, View view){
        CollectionReference eventProfilesRef = db.collection("EVENT_PROFILES");
        DocumentReference docRef = eventProfilesRef.document(EVENT_ID);


    }
}


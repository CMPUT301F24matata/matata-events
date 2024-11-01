package com.example.matata;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddEvent extends AppCompatActivity implements TimePickerListener,DatePickerListener{
    private ImageView backBtn;
    private TextView eventTime;
    private LinearLayout dateGroup;
    private LinearLayout timeGroup;
    private TextView eventDate;
    private ImageView posterPic;
    private TextView location;
    private Button genrQR;
    private EditText eveTitle;
    private EditText descriptionBox;
    private EditText capacity;
    private FirebaseFirestore db;

    private final ActivityResultLauncher<Intent> profilePicLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    loadProfilePicture();
                }
            });

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        db = FirebaseFirestore.getInstance();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_new_event);


        backBtn=findViewById(R.id.btnBackCreateEvent);
        eventTime=findViewById(R.id.dateField);
        dateGroup=findViewById(R.id.dateGroup);
        timeGroup=findViewById(R.id.timeGroup);
        eventDate=findViewById(R.id.editTextDate);
        posterPic=findViewById(R.id.posterPicUpload);
        genrQR=findViewById(R.id.genQR);
        eveTitle=findViewById(R.id.eventTitle);
        descriptionBox=findViewById(R.id.desc_box);
        capacity=findViewById(R.id.number_of_people_event);
        location=findViewById(R.id.editTextLocation);


        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!eveTitle.getText().toString().equals("") ||
                        !descriptionBox.getText().toString().equals("") ||
                        !eventDate.getText().toString().equals("") ||
                        !eventTime.getText().toString().equals("") ||
                        !capacity.getText().toString().equals("")
                ){
                    ConfirmationFragment backpress=new ConfirmationFragment();
                    backpress.show(getSupportFragmentManager(),"BackPressFragment");
                }
                else{
                    finish();
                }
            }
        });

        dateGroup.setOnClickListener(v->{

            DialogFragment timePicker=new TimePickerFragment();
            timePicker.show(getSupportFragmentManager(),"timePicker");

        });

        timeGroup.setOnClickListener(v->{
            DialogFragment datePicker=new DatePickerFragment();
            datePicker.show(getSupportFragmentManager(),"datePicker");
        });

        posterPic.setOnClickListener(v -> {
            Intent intent = new Intent(AddEvent.this, ProfilePicActivity.class);
            profilePicLauncher.launch(intent);
        });

        genrQR.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if (!eveTitle.getText().toString().equals("") &&
                        !descriptionBox.getText().toString().equals("") &&
                        !eventDate.getText().toString().equals("") &&
                        !eventTime.getText().toString().equals("") &&
                        !capacity.getText().toString().equals("")
                ){
                    Log.wtf(TAG,"Okayyyy Letts goooo");
                    Event event=new Event(eveTitle.getText().toString(),eventDate.getText().toString(),eventTime.getText().toString(),location.getText().toString(),descriptionBox.getText().toString(), Integer.parseInt(capacity.getText().toString()));
                    SaveEventInfo(event);
                    Intent intent = new Intent(view.getContext(), ViewEvent.class);
                    intent.putExtra("Title", event.getTitle());
                    //intent.putExtra("Poster",)
                    intent.putExtra("Date", event.getDate());
                    intent.putExtra("Time", event.getTime());
                    intent.putExtra("Location",event.getLocation());
                    intent.putExtra("Description",event.getDescription());
                    intent.putExtra("Capacity",event.getCapacity());
                    view.getContext().startActivity(intent);
                }else{
                    Toast.makeText(AddEvent.this, "Please fill in all the details", Toast.LENGTH_SHORT).show();
                }
            }

        });


    }


    @Override
    public void onTimeSelected(int hour, int min) {
        // Handle the selected time
        String time=String.format("%02d:%02d",hour,min);
        Log.wtf(TAG,time);
        eventTime.setText(time);

    }

    @Override
    public void onDateSelected(int year, int month, int date) {
        String date_str=String.format("%02d/%02d/%04d",date,month+1,year);
        eventDate.setText(date_str);
    }


    private void loadProfilePicture() {
        String imageUriString = getSharedPreferences("ProfilePrefs", MODE_PRIVATE).getString("", null);

        if (imageUriString != null) {
            Uri imageUri = Uri.parse(imageUriString);
            Glide.with(this).load(imageUri).into(posterPic);
        } else {
            posterPic.setImageResource(R.drawable.ic_upload);
        }
    }

    private void SaveEventInfo(Event event){

        Map<String, Object> Event_details = new HashMap<>();
        Event_details.put("Title", event.getTitle());
        Event_details.put("Date", event.getDate());
        Event_details.put("Time", event.getTime());
        Event_details.put("Location",event.getLocation());
        Event_details.put("Description",event.getDescription());
        Event_details.put("Capacity",event.getCapacity());

        String TEST_EVENT="Test Event";
        db.collection("EVENT_PROFILES").document(TEST_EVENT)
                .set(Event_details)
                .addOnSuccessListener(aVoid -> Toast.makeText(AddEvent.this, "Event saved successfully", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(AddEvent.this, "Failed to save profile", Toast.LENGTH_SHORT).show());

    }
}



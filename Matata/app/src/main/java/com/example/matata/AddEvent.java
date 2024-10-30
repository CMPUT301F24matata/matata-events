package com.example.matata;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;

public class AddEvent extends AppCompatActivity implements TimePickerListener,DatePickerListener{
    private ImageView backBtn;
    private TextView eventTime;
    private LinearLayout dateGroup;
    private LinearLayout timeGroup;
    private TextView eventDate;
    private ImageView posterPic;

    private final ActivityResultLauncher<Intent> profilePicLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    loadProfilePicture();
                }
            });

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_new_event);


        backBtn=findViewById(R.id.btnBackCreateEvent);
        eventTime=findViewById(R.id.dateField);
        dateGroup=findViewById(R.id.dateGroup);
        timeGroup=findViewById(R.id.timeGroup);
        eventDate=findViewById(R.id.editTextDate);
        posterPic=findViewById(R.id.posterPicUpload);

        backBtn.setOnClickListener(v->finish());

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
}



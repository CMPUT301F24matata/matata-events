package com.example.matata;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class ViewEvent extends AppCompatActivity {
    private ImageView goBack;
    //private ImageView poster;
    private TextView title;
    private TextView capacity;
    private TextView desc;
    private TextView time;
    private TextView date;
    private TextView location;
    private FirebaseFirestore db;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_event_details);
        db= FirebaseFirestore.getInstance();

        title=findViewById(R.id.ViewEventTitle);
        capacity=findViewById(R.id.ViewEventCapacity);
        desc=findViewById(R.id.ViewEventDesc);
        time=findViewById(R.id.ViewEventTime);
        date=findViewById(R.id.ViewEventDate);
        location=findViewById(R.id.ViewEventLoc);



        Intent intent=getIntent();

        String uid=intent.getStringExtra("Unique_id");
        Log.wtf(TAG,uid);


        goBack=findViewById(R.id.go_back_view_event);

        goBack.setOnClickListener(v->{
            finish();
        });

        loadEventDetails(uid);



    }
    public void loadEventDetails(String uid){
        DocumentReference doc = db.collection("EVENT_PROFILES").document(uid);
        doc.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {

                        String Title = documentSnapshot.getString("Title");
                        Long Capacity = documentSnapshot.getLong("Capacity");
                        String Description = documentSnapshot.getString("Description");
                        String Time = documentSnapshot.getString("Time");
                        String Date = documentSnapshot.getString("Date");
                        String Location = documentSnapshot.getString("Location");
                        Log.wtf(TAG,"Okay now what");
                        // Set the values in the EditTexts
                        title.setText(Title != null ? Title : "");
                        capacity.setText(String.valueOf(Capacity.intValue()));
                        desc.setText(Description != null ? Description : "");
                        time.setText(Time != null ? Time : "");
                        date.setText(Date != null ? Date : "");
                        location.setText(Location != null ? Location : "");
                    } else {
                        Toast.makeText(ViewEvent.this, "No event found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(ViewEvent.this, "Failed to load event", Toast.LENGTH_SHORT).show());

    }

}

package com.example.matata;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class ViewEvent extends AppCompatActivity {

    private String argbase64;
    private String posterBase64;
    private ImageView goBack;
    private ImageView poster;
    private TextView title;
    private TextView capacity;
    private TextView desc;
    private TextView time;
    private TextView date;
    private TextView location;
    private FirebaseFirestore db;
    private FloatingActionButton showQR;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_event_details);
        db= FirebaseFirestore.getInstance();

        goBack=findViewById(R.id.go_back_view_event);
        title=findViewById(R.id.ViewEventTitle);
        capacity=findViewById(R.id.ViewEventCapacity);
        desc=findViewById(R.id.ViewEventDesc);
        time=findViewById(R.id.ViewEventTime);
        date=findViewById(R.id.ViewEventDate);
        location=findViewById(R.id.ViewEventLoc);
        poster=findViewById(R.id.poster_pic_Display);
        showQR=findViewById(R.id.show_QR);


        Intent intent=getIntent();

        String uid=intent.getStringExtra("Unique_id");
        Log.wtf(TAG,uid);

        goBack.setOnClickListener(v->{
            finish();
        });

        showQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                QR_displayFragment qrDisplayFragment=QR_displayFragment.newInstance(argbase64);
                qrDisplayFragment.show(getSupportFragmentManager(),"Show QR");
            }
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

                        //TEst
                        argbase64=documentSnapshot.getString("bitmap");
                        posterBase64=documentSnapshot.getString("Poster");

                        Bitmap QR=decodeBase64toBmp(argbase64);
                        Bitmap jpegPoster=decodeBase64toBmp(posterBase64);
                        //Test

                        Log.wtf(TAG,"Okay now what");
                        // Set the values in the EditTexts
                        title.setText(Title != null ? Title : "");
                        capacity.setText(String.valueOf(Capacity.intValue()));
                        desc.setText(Description != null ? Description : "");
                        time.setText(Time != null ? Time : "");
                        date.setText(Date != null ? Date : "");
                        location.setText(Location != null ? Location : "");
                        poster.setImageBitmap(jpegPoster);

                        //

                        //

                    } else {
                        Toast.makeText(ViewEvent.this, "No event found", Toast.LENGTH_SHORT).show();
                    }

                })
                .addOnFailureListener(e -> Toast.makeText(ViewEvent.this, "Failed to load event", Toast.LENGTH_SHORT).show());


    }
    public Bitmap decodeBase64toBmp(String bmp64){
        byte[] decodedBytes = Base64.decode(bmp64, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }



}

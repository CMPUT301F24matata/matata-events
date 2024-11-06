package com.example.matata;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Transaction;

import java.util.ArrayList;
import java.util.List;

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
    private Button waitlistBtn;
    private String USER_ID;
    private DocumentReference eventRef;
    private DocumentReference entrantRef;
    private Button drawBtn;


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
        waitlistBtn = findViewById(R.id.join_waitlist_button);
        drawBtn = findViewById(R.id.draw_button);

        USER_ID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

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

        drawBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(view.getContext(),EventDraw.class);
                intent.putExtra("Unique_id",uid);
                view.getContext().startActivity(intent);
            }
        });

        loadEventDetails(uid);

        eventRef = db.collection("EVENT_PROFILES").document(uid);
        entrantRef = db.collection("USER_PROFILES").document(USER_ID);

        //checkEntrantStatus(uid);
        eventRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                // check the status of entrant
                DocumentSnapshot document = task.getResult();
                List<DocumentReference> waitlist = (List<DocumentReference>) document.get("waitlist");
                List<DocumentReference> pending = (List<DocumentReference>) document.get("pending");

                // Add a condition checking if entrant is in status Pending
                if (pending != null && pending.contains(entrantRef)){
                    waitlistBtn.setText("Pending");
                }
                else if (waitlist != null && waitlist.contains(entrantRef)) {
                    waitlistBtn.setText("Withdraw");
                } else {
                    waitlistBtn.setText("Join Waitlist");
                }

                String joinBtntext = waitlistBtn.getText().toString();
                if (joinBtntext.equals("Pending")) {
                    waitlistBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Create an AlertDialog builder
                            AlertDialog.Builder builder = new AlertDialog.Builder(ViewEvent.this);
                            builder.setTitle("Invitation");
                            builder.setMessage("Do you want to accept or decline the invitation?");

                            // Set the Accept button
                            builder.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // Handle Accept action here
                                    Intent intent = new Intent(ViewEvent.this, SignUpSheet.class);
                                    intent.putExtra("Unique_id",uid);
                                    startActivity(intent);
                                    waitlistBtn.setText("Accepted");
                                    // Additional code for when the invitation is accepted
                                    Toast.makeText(ViewEvent.this, "Invitation Accepted", Toast.LENGTH_SHORT).show();


                                }
                            });

                            // Set the Decline button
                            builder.setNegativeButton("Decline", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // Handle Decline action here
                                    waitlistBtn.setText("Declined");
                                    // Additional code for when the invitation is declined
                                    Toast.makeText(ViewEvent.this, "Invitation Declined", Toast.LENGTH_SHORT).show();
                                }
                            });

                            // Show the dialog
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }
                    });
                }
                else{
                    waitlistBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (waitlistBtn.getText().toString().equals("Withdraw")) {
                                // Show dialog to confirm exiting the waitlist
                                withdrawDialog();
                            } else {
                                confirmationDialog(); // Show confirmation dialog for registration
                            }
                        }
                    });
                }

            }
        });







    }




//    private void checkEntrantStatus(String uid) {
//
//        eventRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                        DocumentSnapshot document = task.getResult();
//                        List<DocumentReference> waitlist = (List<DocumentReference>) document.get("waitlist");
//                        if (waitlist != null && waitlist.contains(entrantRef)) {
//                            waitlistBtn.setText("Withdraw");
//                        } else {
//                            waitlistBtn.setText("Join Waitlist");
//                            //Toast.makeText(ViewEvent.this, "no entrant", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//    }

    private void confirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ViewEvent.this);
        builder.setCancelable(true);
        builder.setMessage("Confirm to join waitlist");
        builder.setPositiveButton("Confirm",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        addToWaitList();
                    }
                });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private void addToWaitList() {
        db.runTransaction((Transaction.Function<Void>) transaction -> {
            DocumentSnapshot eventSnapshot = transaction.get(eventRef);
            List<DocumentReference> waitlist = (List<DocumentReference>) eventSnapshot.get("waitlist");

            if (waitlist == null) {
                waitlist = new ArrayList<>();
            }
            waitlist.add(entrantRef);
            transaction.update(eventRef, "waitlist", waitlist);
            return null;
        }).addOnSuccessListener(aVoid -> {
            Log.d("Firebase", "Entrant added to waitlist successfully");
            waitlistBtn.setText("Withdraw");
        }).addOnFailureListener(e -> {
            Log.e("Firebase", "Error adding entrant to waitlist", e);
        });
    }


    private void withdrawDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ViewEvent.this);
        builder.setCancelable(true);
        builder.setMessage("Confirm to withdraw from waitlist");
        builder.setPositiveButton("Confirm",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        exitWaitlist();
                    }
                });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private void exitWaitlist() {
        eventRef.update("waitlist", FieldValue.arrayRemove(entrantRef))
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firebase", "Entrant withdraw from waitlist successfully");
                    waitlistBtn.setText("Join Waitlist");
                }).addOnFailureListener(e -> {
                    Log.e("Firebase", "Error deleting entrant from waitlist", e);
                });
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
                        String ImageUri=documentSnapshot.getString("Poster");
                        if (ImageUri!=""){
                            Glide.with(this).load(ImageUri);
                        }
                        else{;}

                        Bitmap QR=decodeBase64toBmp(argbase64);

                        //Test

                        Log.wtf(TAG,"Okay now what");
                        // Set the values in the EditTexts
                        title.setText(Title != null ? Title : "");
                        capacity.setText(String.valueOf(Capacity.intValue()));
                        desc.setText(Description != null ? Description : "");
                        time.setText(Time != null ? Time : "");
                        date.setText(Date != null ? Date : "");
                        location.setText(Location != null ? Location : "");

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

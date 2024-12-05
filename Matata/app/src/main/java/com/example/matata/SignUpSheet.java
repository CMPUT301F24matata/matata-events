package com.example.matata;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUpSheet extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private double latitude, longitude;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_sheet);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        ImageButton back = findViewById(R.id.btnBack);
        EditText name = findViewById(R.id.full_name);
        EditText email = findViewById(R.id.email);
        EditText phone = findViewById(R.id.contact_number);
        EditText emergency_name = findViewById(R.id.emergency_contact_name);
        EditText emergency_phone = findViewById(R.id.emergency_contact_number);
        Spinner dietSpinner = findViewById(R.id.dietary_preferences_spinner);
        EditText arrival = findViewById(R.id.arrival_time);
        Spinner accessibilitySpinner = findViewById(R.id.accessibility_needs_spinner);
        CheckBox terms = findViewById(R.id.agree_to_terms);
        Button submit = findViewById(R.id.submit_button);

        Intent intent = getIntent();
        String uid = intent.getStringExtra("Unique_id");
        Log.wtf(TAG, uid);

        // Set up adapters
        ArrayAdapter<CharSequence> dietAdapter = ArrayAdapter.createFromResource(
                this, R.array.dietary_options, android.R.layout.simple_spinner_item);
        dietAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dietSpinner.setAdapter(dietAdapter);

        ArrayAdapter<CharSequence> accessibilityAdapter = ArrayAdapter.createFromResource(
                this, R.array.accessibility_options, android.R.layout.simple_spinner_item);
        accessibilityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        accessibilitySpinner.setAdapter(accessibilityAdapter);

        back.setOnClickListener(v -> {
            Log.d("SignUpSheet", "Back button clicked");
            finish();
        });

        submit.setOnClickListener(v -> {
            String s_name = name.getText().toString().trim();
            String s_email = email.getText().toString().trim();
            String s_phone = phone.getText().toString().trim();
            String s_emergency_name = emergency_name.getText().toString().trim();
            String s_emergency_phone = emergency_phone.getText().toString().trim();
            String s_diet = dietSpinner.getSelectedItem().toString().trim();
            String s_arrival = arrival.getText().toString().trim();
            String s_accessibility = accessibilitySpinner.getSelectedItem().toString().trim();
            boolean s_terms = terms.isChecked();

            if (s_name.isEmpty()) {
                Toast.makeText(this, "No Name Found", Toast.LENGTH_SHORT).show();
            } else if (s_email.isEmpty()) {
                Toast.makeText(this, "No Email Found", Toast.LENGTH_SHORT).show();
            } else if (s_phone.isEmpty()) {
                Toast.makeText(this, "No Contact Number Found", Toast.LENGTH_SHORT).show();
            } else if (s_emergency_name.isEmpty()) {
                Toast.makeText(this, "No Emergency Contact Name Found", Toast.LENGTH_SHORT).show();
            } else if (s_emergency_phone.isEmpty()) {
                Toast.makeText(this, "No Emergency Contact Number Found", Toast.LENGTH_SHORT).show();
            } else if (!s_terms) {
                Toast.makeText(this, "Please Check the Terms and Condition box", Toast.LENGTH_SHORT).show();
            } else {
                // Save user data to Firestore
                DocumentReference event = db.collection("EVENT_PROFILES").document(uid);
                CollectionReference signup = event.collection("SIGNUP_SHEETS");

                Map<String, Object> signup_sheet = new HashMap<>();
                signup_sheet.put("name", s_name);
                signup_sheet.put("email", s_email);
                signup_sheet.put("contact number", s_phone);
                signup_sheet.put("emergency contact", s_emergency_name);
                signup_sheet.put("emergency contact number", s_emergency_phone);
                signup_sheet.put("dietary restrictions", s_diet);
                signup_sheet.put("arrival time", s_arrival);
                signup_sheet.put("accessibility needs", s_accessibility);
                signup_sheet.put("terms and conditions", true);

                signup.add(signup_sheet)
                        .addOnSuccessListener(documentReference ->{
                            Log.d("Firestore", "Signup sheet added");
                            Intent resultIntent = new Intent();
                            setResult(RESULT_OK, resultIntent);
                            finish();
                        })
                        .addOnFailureListener(e -> Log.w("Firestore", "Error adding signup sheet", e));

                // Fetch geolocation
                fetchLocationAndSave(uid);
            }
        });
    }

    private void fetchLocationAndSave(String uid) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                Map<String, Object> locationData = new HashMap<>();
                locationData.put("latitude", latitude);
                locationData.put("longitude", longitude);
                locationData.put("timestamp", System.currentTimeMillis());

                // Add new geolocation as a separate document
                db.collection("EVENT_PROFILES")
                        .document(uid)
                        .collection("USER_LOCATIONS")
                        .add(locationData) // Firestore auto-generates a unique document ID
                        .addOnSuccessListener(documentReference -> Log.d("Firestore", "Location saved with ID: " + documentReference.getId()))
                        .addOnFailureListener(e -> Log.w("Firestore", "Error saving location", e));
            } else {
                Log.w("Location", "Location is null. Unable to save.");
                Toast.makeText(this, "Unable to fetch location. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchLocationAndSave(getIntent().getStringExtra("Unique_id"));
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

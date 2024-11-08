package com.example.matata;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

/**
 * SignUpSheet class handles the creation of a sign-up form for event attendees.
 * Users can input their personal and emergency contact information, dietary preferences,
 * arrival time, and accessibility needs. Once submitted, this data is saved to Firestore.
 *
 * Outstanding issues: Error handling for Firestore interactions could be improved. Currently,
 * feedback is limited to log messages and simple Toast notifications.
 */
public class SignUpSheet extends AppCompatActivity {

    /**
     * Initializes the sign-up form, sets up the UI components, and handles
     * data submission to Firestore upon clicking the submit button.
     *
     * @param savedInstanceState if the activity is being re-initialized, this contains the data it most recently supplied
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_sheet);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

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

        // Set up adapter for dietary preferences
        ArrayAdapter<CharSequence> dietAdapter = ArrayAdapter.createFromResource(
                this, R.array.dietary_options, android.R.layout.simple_spinner_item);
        dietAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dietSpinner.setAdapter(dietAdapter);

        // Set up adapter for accessibility needs
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

            // Validate required fields
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
                // Save data to Firestore
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
                        .addOnSuccessListener(documentReference -> Log.d("Firestore", "Signup sheet added"))
                        .addOnFailureListener(e -> Log.w("Firestore", "Error adding signup sheet", e));

                finish();
            }
        });
    }
}

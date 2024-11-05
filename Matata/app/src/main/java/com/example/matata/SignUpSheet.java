package com.example.matata;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUpSheet extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_sheet);

        FirebaseFirestore db= FirebaseFirestore.getInstance();

        EditText name = findViewById(R.id.full_name);
        EditText email = findViewById(R.id.email);
        EditText phone = findViewById(R.id.contact_number);
        EditText emergency_name = findViewById(R.id.emergency_contact_name);
        EditText emergency_phone = findViewById(R.id.emergency_contact_number);
        EditText diet = findViewById(R.id.dietary_preferences);
        EditText arrival = findViewById(R.id.arrival_time);
        EditText accessibility = findViewById(R.id.accessibility_needs);
        CheckBox terms = findViewById(R.id.agree_to_terms);
        Button submit = findViewById(R.id.submit_button);

        Intent intent=getIntent();

        String uid=intent.getStringExtra("Unique_id");
        Log.wtf(TAG,uid);

        submit.setOnClickListener(v -> {

            String s_name = name.getText().toString().trim();
            String s_email = email.getText().toString().trim();
            String s_phone = phone.getText().toString().trim();
            String s_emergency_name = emergency_name.getText().toString().trim();
            String s_emergency_phone = emergency_phone.getText().toString().trim();
            String s_diet = diet.getText().toString().trim();
            String s_arrival = arrival.getText().toString().trim();
            String s_accessibility  = accessibility .getText().toString().trim();
            boolean s_terms = terms.isChecked();

            if (s_name.isEmpty()) {
                Toast.makeText(this, "No Name Found", Toast.LENGTH_SHORT).show();
            } else if (s_email.isEmpty()) {
                Toast.makeText(this, "No email Found", Toast.LENGTH_SHORT).show();
            } else if (s_phone.isEmpty()) {
                Toast.makeText(this, "No Contact Number Found", Toast.LENGTH_SHORT).show();
            } else if (s_emergency_name.isEmpty()) {
                Toast.makeText(this, "No Emergency Contact Name Found", Toast.LENGTH_SHORT).show();
            } else if (s_emergency_phone.isEmpty()) {
                Toast.makeText(this, "No Emergency Contact Number Found", Toast.LENGTH_SHORT).show();
            } else if (!s_terms) {
                Toast.makeText(this, "Please Check the Terms and Condition box", Toast.LENGTH_SHORT).show();
            } else {

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
                        .addOnSuccessListener(documentReference -> Log.d("Firestone", "Signup sheet added"))
                        .addOnFailureListener(e -> Log.w("Firestone", "error adding signup sheets"));

                finish();
            }

        });

    }
}

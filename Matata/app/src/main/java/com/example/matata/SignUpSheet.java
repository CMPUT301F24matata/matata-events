package com.example.matata;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SignUpSheet extends AppCompatActivity {




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.sign_up_sheet);
        EditText name = findViewById(R.id.full_name);
        EditText email = findViewById((R.id.email));
        EditText phone = findViewById(R.id.phoneEditText);
        EditText emergency_name = findViewById((R.id.emergency_contact_name));
        EditText emergency_phone = findViewById(R.id.emergency_contact_number);
        EditText diet = findViewById((R.id.dietary_preferences));
        EditText arrival = findViewById(R.id.arrival_time);
        EditText accessibility = findViewById((R.id.accessibility_needs));
        CheckBox terms = findViewById(R.id.agree_to_terms);

        Button submit = findViewById(R.id.submit_button);

        submit.setOnClickListener(v -> {

        });

    }
}

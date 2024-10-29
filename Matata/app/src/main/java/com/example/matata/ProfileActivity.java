//package com.example.matata;
//
//import android.content.Intent;
//import android.net.Uri;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.activity.result.ActivityResultLauncher;
//import androidx.activity.result.contract.ActivityResultContracts;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.graphics.Insets;
//import androidx.core.view.ViewCompat;
//import androidx.core.view.WindowCompat;
//import androidx.core.view.WindowInsetsCompat;
//
//import com.bumptech.glide.Glide;
//
//public class ProfileActivity extends AppCompatActivity {
//    private EditText nameEditText, phoneEditText, emailEditText;
//    private ImageView profileIcon;
//
//    private final ActivityResultLauncher<Intent> profilePicLauncher = registerForActivityResult(
//            new ActivityResultContracts.StartActivityForResult(),
//            result -> {
//                if (result.getResultCode() == RESULT_OK) {
//                    loadProfilePicture();
//                }
//            });
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
//        setContentView(R.layout.edit_profile);
//
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
//
//        profileIcon = findViewById(R.id.profileIcon);
//        TextView initials = findViewById(R.id.initials);
//        nameEditText = findViewById(R.id.nameEditText);
//        phoneEditText = findViewById(R.id.phoneEditText);
//        emailEditText = findViewById(R.id.emailEditText);
//        Button saveButton = findViewById(R.id.saveButton);
//
//        if (getSupportActionBar() != null) {
//            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        }
//
//        profileIcon.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(ProfileActivity.this, ProfilePicActivity.class);
//                profilePicLauncher.launch(intent);
//            }
//        });
//
//        saveButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String name = nameEditText.getText().toString();
//                String phoneNumber = phoneEditText.getText().toString();
//                String email = emailEditText.getText().toString();
//
//                // Create an Entrants object
//                Entrant entrant = new Entrant(name, phoneNumber, email);
//
//                // Here you could add code to save Entrant to the database if needed
//                Toast.makeText(ProfileActivity.this, "Profile saved", Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        // Load profile picture from SharedPreferences if available
//        loadProfilePicture();
//    }
//
//    // Load the profile picture from SharedPreferences
//    private void loadProfilePicture() {
//        String imageUriString = getSharedPreferences("ProfilePrefs", MODE_PRIVATE)
//                .getString("profile_image_uri", null);
//
//        if (imageUriString != null) {
//            Uri imageUri = Uri.parse(imageUriString);
//            Glide.with(this)
//                    .load(imageUri)
//                    .into(profileIcon);
//        } else {
//            profileIcon.setImageResource(R.drawable.ic_upload); // Default placeholder
//        }
//    }
//}
package com.example.matata;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class ProfileActivity extends AppCompatActivity {
    private EditText nameEditText, phoneEditText, emailEditText;
    private ImageView profileIcon;

    // ActivityResultLauncher to handle result from ProfilePicActivity
    private final ActivityResultLauncher<Intent> profilePicLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    loadProfilePicture();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile);  // Make sure this layout file exists and has matching IDs

        profileIcon = findViewById(R.id.profileIcon);
        nameEditText = findViewById(R.id.nameEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        emailEditText = findViewById(R.id.emailEditText);
        Button saveButton = findViewById(R.id.saveButton);
        TextView initials = findViewById(R.id.initials);

        // Back button in action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Set click listener to open ProfilePicActivity
        profileIcon.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, ProfilePicActivity.class);
            profilePicLauncher.launch(intent);  // Launch for result
        });

        // Save button click listener
        saveButton.setOnClickListener(v -> {
            String name = nameEditText.getText().toString();
            String phoneNumber = phoneEditText.getText().toString();
            String email = emailEditText.getText().toString();

            // Placeholder for saving entrant info
            Toast.makeText(ProfileActivity.this, "Profile saved", Toast.LENGTH_SHORT).show();
        });

        loadProfilePicture();  // Load profile picture on activity start
    }

    // Load profile picture from SharedPreferences or show initials as a fallback
    private void loadProfilePicture() {
        String imageUriString = getSharedPreferences("ProfilePrefs", MODE_PRIVATE)
                .getString("profile_image_uri", null);

        if (imageUriString != null) {
            Uri imageUri = Uri.parse(imageUriString);
            Glide.with(this).load(imageUri).into(profileIcon);
        } else {
            profileIcon.setImageResource(R.drawable.ic_upload);  // Default placeholder
        }
    }
}

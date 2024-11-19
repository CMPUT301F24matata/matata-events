package com.example.matata;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

/**
 * The ManageAllQRActivity class provides an interface for administrators to manage event posters.
 * Administrators can view and delete posters linked to specific events.
 */
public class ManageAllImagesActivity extends AppCompatActivity {

    private LinearLayout imagesContainer;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manage_all_images);

        db = FirebaseFirestore.getInstance();
        imagesContainer = findViewById(R.id.imagesContainer);
        ImageView btnBack = findViewById(R.id.btnBack);
        ImageView iconDashboard = findViewById(R.id.icon_dashboard);
        ImageView iconUsers = findViewById(R.id.icon_users);
        ImageView iconReports = findViewById(R.id.icon_reports);
        ImageView iconNotifications = findViewById(R.id.icon_notifications);
        ImageView iconSettings = findViewById(R.id.icon_settings);

        btnBack.setOnClickListener(v -> finish());

        iconDashboard.setOnClickListener(v -> {
            Intent intent = new Intent(ManageAllImagesActivity.this, AdminView.class);
            startActivity(intent);
            finish();
        });

        iconSettings.setOnClickListener(v -> {
            Intent intent = new Intent(ManageAllImagesActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        iconReports.setOnClickListener(v -> {
            Intent intent = new Intent(ManageAllImagesActivity.this, AdminReportActivity.class);
            startActivity(intent);
            finish();
        });

        fetchEventPosters();
    }

    /**
     * Fetches event posters from Firestore and Firebase Storage, linking them to events.
     */
    private void fetchEventPosters() {
        imagesContainer.removeAllViews();

        db.collection("EVENT_PROFILES")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        for (QueryDocumentSnapshot document : querySnapshot) {
                            String eventId = document.getId();
                            String posterPath = document.getString("Poster");
                            String eventTitle = document.getString("Title");

                            if (posterPath != null && !posterPath.isEmpty()) {
                                addPosterItem(eventId, posterPath, eventTitle);
                            }
                        }
                    } else {
                        showToast("Failed to fetch events.");
                    }
                });
    }

    /**
     * Dynamically adds an event poster item to the `imagesContainer`.
     *
     * @param eventId    ID of the event.
     * @param posterPath Path of the poster in Firebase Storage.
     */
    private void addPosterItem(String eventId, String posterPath, String eventTitle) {
        View imageView = getLayoutInflater().inflate(R.layout.image_item, imagesContainer, false);

        TextView event = imageView.findViewById(R.id.event_title);
        ImageView eventPoster = imageView.findViewById(R.id.event_poster);
        TextView deletePoster = imageView.findViewById(R.id.btn_delete_event);
        ImageView toggleButton = imageView.findViewById(R.id.add_image);
        LinearLayout imageDetails = imageView.findViewById(R.id.image_details);
        LinearLayout toggle_tile = imageView.findViewById(R.id.toggle_tile);

        event.setText(eventTitle);

        toggle_tile.setOnClickListener(view -> {
            if (imageDetails.getVisibility() == View.VISIBLE) {
                imageDetails.setVisibility(View.GONE);
                toggleButton.setImageResource(R.drawable.ic_add);
            } else {
                imageDetails.setVisibility(View.VISIBLE);
                toggleButton.setImageResource(R.drawable.ic_remove);
            }
        });

        Glide.with(this)
                .load(posterPath)
                .placeholder(R.drawable.placeholder_image)
                .into(eventPoster);


        deletePoster.setOnClickListener(v -> new AlertDialog.Builder(this)
                .setTitle("Confirm Deletion")
                .setMessage("Are you sure you want to delete this poster?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    db.collection("EVENT_PROFILES").document(eventId)
                            .update("Poster", null)
                            .addOnSuccessListener(aVoid -> {
                                imagesContainer.removeView(imageView);
                                showToast("Poster deleted successfully.");
                            })
                            .addOnFailureListener(e -> Log.e("DeletePoster", "Failed to update Firestore: " + e.getMessage()));
                })
                .setNegativeButton("No", null)
                .show());

        imagesContainer.addView(imageView);
    }

    /**
     * Displays a toast message.
     *
     * @param message Message to display.
     */
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}

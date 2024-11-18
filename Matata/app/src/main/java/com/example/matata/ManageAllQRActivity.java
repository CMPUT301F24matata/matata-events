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

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

/**
 * The ManageAllQRActivity class provides an interface for administrators to manage event posters.
 * Administrators can view and delete posters linked to specific events.
 */
public class ManageAllQRActivity extends AppCompatActivity {

    private LinearLayout qrContainer;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manage_all_qr);

        db = FirebaseFirestore.getInstance();
        qrContainer = findViewById(R.id.QRContainer);
        ImageView btnBack = findViewById(R.id.btnBack);
        ImageView iconDashboard = findViewById(R.id.icon_dashboard);
        ImageView iconUsers = findViewById(R.id.icon_users);
        ImageView iconReports = findViewById(R.id.icon_reports);
        ImageView iconNotifications = findViewById(R.id.icon_notifications);
        ImageView iconSettings = findViewById(R.id.icon_settings);

        btnBack.setOnClickListener(v -> finish());

        iconDashboard.setOnClickListener(v -> {
            Intent intent = new Intent(ManageAllQRActivity.this, AdminView.class);
            startActivity(intent);
            finish();
        });

        iconSettings.setOnClickListener(v -> {
            Intent intent = new Intent(ManageAllQRActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        fetchEventPosters();

        fetchEventPosters();
    }

    /**
     * Fetches event posters from Firestore and Firebase Storage, linking them to events.
     */
    private void fetchEventPosters() {
        qrContainer.removeAllViews();

        db.collection("EVENT_PROFILES")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        for (QueryDocumentSnapshot document : querySnapshot) {
                            String eventId = document.getId();
                            String eventTitle = document.getString("Title");
                            addPosterItem(eventId, eventTitle);
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
     */
    private void addPosterItem(String eventId, String eventTitle) {
        View qrView = getLayoutInflater().inflate(R.layout.qr_item, qrContainer, false);

        TextView event = qrView.findViewById(R.id.event_title);
        TextView deletePoster = qrView.findViewById(R.id.btn_delete_event);
        ImageView toggleButton = qrView.findViewById(R.id.add_qr);
        LinearLayout qrDetails = qrView.findViewById(R.id.qr_details);

        event.setText(eventTitle);

        toggleButton.setOnClickListener(view -> {
            if (qrDetails.getVisibility() == View.VISIBLE) {
                qrDetails.setVisibility(View.GONE);
                toggleButton.setImageResource(R.drawable.ic_add);
            } else {
                qrDetails.setVisibility(View.VISIBLE);
                toggleButton.setImageResource(R.drawable.ic_remove);
            }
        });

        deletePoster.setOnClickListener(v -> new AlertDialog.Builder(this)
                .setTitle("Confirm Deletion")
                .setMessage("Are you sure you want to delete this hashed QR Code for this event?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    db.collection("EVENT_PROFILES").document(eventId)
                            .update("bitmap", null)
                            .addOnSuccessListener(aVoid -> {
                                qrContainer.removeView(qrView);
                                showToast("QR code deleted successfully.");
                            })
                            .addOnFailureListener(e -> Log.e("DeleteQRCode", "Failed to update Firestore: " + e.getMessage()));
                })
                .setNegativeButton("No", null)
                .show());

        qrContainer.addView(qrView);
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

package com.example.matata;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
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


public class ManageAllQRActivity extends AppCompatActivity {

    /**
     * Event details encoded as a Base64 string.
     */
    private String argbase64;
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

        fetchEventQR();

    }

    private void fetchEventQR() {
        qrContainer.removeAllViews();

        db.collection("EVENT_PROFILES")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        for (QueryDocumentSnapshot document : querySnapshot) {
                            String eventId = document.getId();
                            String eventTitle = document.getString("Title");
                            argbase64 = document.getString("bitmap");
                            Bitmap QR = decodeBase64toBmp(argbase64);
                            addQRItem(eventId, eventTitle, QR);
                        }
                    } else {
                        showToast("Failed to fetch events.");
                    }
                });
    }

    /**
     * Decodes a Base64 encoded string to a Bitmap image.
     *
     * @param bmp64 The Base64 encoded string.
     * @return The decoded Bitmap image.
     */
    public Bitmap decodeBase64toBmp(String bmp64) {
        try {
            byte[] decodedBytes = Base64.decode(bmp64, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private void addQRItem(String eventId, String eventTitle, Bitmap QR) {
        View qrView = getLayoutInflater().inflate(R.layout.qr_item, qrContainer, false);

        TextView event = qrView.findViewById(R.id.event_title);
        TextView deletePoster = qrView.findViewById(R.id.btn_delete_event);
        ImageView toggleButton = qrView.findViewById(R.id.add_qr);
        LinearLayout qrDetails = qrView.findViewById(R.id.qr_details);
        ImageView qr_code = qrView.findViewById(R.id.qr_code);

        event.setText(eventTitle);

        Glide.with(this)
                .load(QR)
                .placeholder(R.drawable.placeholder_image)
                .into(qr_code);

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

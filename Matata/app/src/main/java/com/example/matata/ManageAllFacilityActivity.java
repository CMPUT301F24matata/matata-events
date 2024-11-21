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

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

/**
 * The ManageAllFacilityActivity class provides an interface for administrators to manage all facilities.
 * It allows viewing, freezing/unfreezing, and deleting facilities. This activity interacts with Firebase Firestore
 * to fetch, display, and update facility data.
 */
public class ManageAllFacilityActivity extends AppCompatActivity {

    /**
     * LinearLayout container for dynamically adding facility items.
     */
    private LinearLayout facilityContainer;

    /**
     * Instance of FirebaseFirestore for database operations.
     */
    private FirebaseFirestore db;

    /**
     * Called when the activity is created. Sets up the UI, initializes Firestore, and fetches facility data.
     *
     * @param savedInstanceState Bundle containing the activity's previously saved state, if any.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manage_all_facilities);

        db = FirebaseFirestore.getInstance();

        ImageView btnBack = findViewById(R.id.btnBack);
        facilityContainer = findViewById(R.id.facilityContainer);
        ImageView iconDashboard = findViewById(R.id.icon_dashboard);
        ImageView iconUsers = findViewById(R.id.icon_users);
        ImageView iconReports = findViewById(R.id.icon_reports);
        ImageView iconNotifications = findViewById(R.id.icon_notifications);
        ImageView iconSettings = findViewById(R.id.icon_settings);

        btnBack.setOnClickListener(v -> finish());

        iconDashboard.setOnClickListener(v -> {
            startActivity(new Intent(this, AdminView.class));
            finish();
        });

        iconSettings.setOnClickListener(v -> {
            Intent intent = new Intent(ManageAllFacilityActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        iconReports.setOnClickListener(v -> {
            Intent intent = new Intent(ManageAllFacilityActivity.this, AdminReportActivity.class);
            startActivity(intent);
            finish();
        });


        fetchFromFirestore();

    }

    /**
     * Fetches all facility profiles from Firestore and adds them to the `facilityContainer`.
     * Only facilities with the "awake" state are displayed.
     */
    private void fetchFromFirestore() {
        facilityContainer.removeAllViews();

        db.collection("FACILITY_PROFILES")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        for (QueryDocumentSnapshot document : querySnapshot) {
                            String name = document.getString("name");
                            String facility_id = document.getId();
                            String frozen = document.getString("freeze");

                            addFacilityItem(name, frozen, facility_id);
                        }
                    } else {
                        showToast();
                    }
                });
    }

    /**
     * Dynamically adds a facility item to the `facilityContainer`. Each facility has options to view,
     * freeze/unfreeze, and delete it.
     *
     * @param facility Name of the facility.
     * @param frozen   Current frozen state ("awake" or "frozen").
     * @param facilityId Unique ID of the facility.
     */
    private void addFacilityItem(String facility, String frozen, String facilityId) {

        View facilityView = getLayoutInflater().inflate(R.layout.facility_item, facilityContainer, false);

        TextView facilityName = facilityView.findViewById(R.id.username);
        ImageView freeze = facilityView.findViewById(R.id.freeze_facility);
        ImageView delete = facilityView.findViewById(R.id.delete_facility);

        facilityName.setText(facility);

        facilityName.setOnClickListener(v -> {
            Intent intent = new Intent(this, FacilityActivity.class);
            intent.putExtra("FACILITY_ID", facilityId);
            startActivity(intent);
        });

        if (Objects.equals(frozen, "awake")) {
            freeze.setImageResource(R.drawable.view);
        } else if (Objects.equals(frozen, "frozen")) {
            freeze.setImageResource(R.drawable.no_view);
        }

        freeze.setOnClickListener(v -> {
            DocumentReference userRef = db.collection("FACILITY_PROFILES").document(facilityId);
            userRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    String isFrozen = documentSnapshot.getString("freeze");
                    if (Objects.equals(isFrozen, "awake")) {
                        freeze.setImageResource(R.drawable.no_view);
                        userRef.update("freeze", "frozen")
                                .addOnSuccessListener(aVoid -> {
                                    Log.d("FreezeFacility", "Facility state updated to 'freeze'");
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("FreezeFacility", "Failed to update Facility state to 'freeze': " + e.getMessage());
                                });
                    } else if (Objects.equals(isFrozen, "frozen")) {
                        freeze.setImageResource(R.drawable.view);
                        userRef.update("freeze", "awake")
                                .addOnSuccessListener(aVoid -> {
                                    Log.d("FreezeFacility", "Facility state updated to 'awake'");
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("FreezeFacility", "Failed to update Facility state to 'awake': " + e.getMessage());
                                });
                    }
                }
            }).addOnFailureListener(e -> {
                Log.e("FreezeFacility", "Failed to fetch Facility data: " + e.getMessage());
            });
        });

        delete.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Confirm Deletion")
                    .setMessage("Are you sure you want to delete this Facility?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        db.collection("FACILITY_PROFILES").document(facilityId)
                                .delete()
                                .addOnSuccessListener(aVoid -> Log.d("ManageAllFacility", "Facility deleted successfully"))
                                .addOnFailureListener(e -> Log.e("ManageAllFacility", "Error deleting Facility", e));
                        facilityContainer.removeView(facilityView);
                    })
                    .setNegativeButton("No", null)
                    .show();
        });

        facilityContainer.addView(facilityView);
    }

    /**
     * Displays a toast message to indicate that facilities failed to load from Firestore.
     */
    private void showToast() {
        Toast.makeText(this, "Failed to load facilities", Toast.LENGTH_SHORT).show();
    }

}

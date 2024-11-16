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

public class ManageAllFacilityActivity extends AppCompatActivity {

    private LinearLayout facilityContainer;
    private FirebaseFirestore db;

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
        });

        fetchFromFirestore();

    }

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

                            if (Objects.equals(frozen, "awake")) {
                                addFacilityItem(name, frozen, facility_id);
                            }

                        }
                    } else {
                        showToast();
                    }
                });
    }

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

    private void showToast() {
        Toast.makeText(this, "Failed to load facilities", Toast.LENGTH_SHORT).show();
    }

}

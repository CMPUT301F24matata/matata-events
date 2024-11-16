package com.example.matata;

import android.app.AlertDialog;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

public class ManageAllUsersActivity extends AppCompatActivity {

    private LinearLayout userContainer;
    private ImageView btnBack;
    private ImageView iconUsers;
    private ImageView iconReports;
    private ImageView iconNotifications;
    private ImageView iconSettings;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manage_all_users);

        db = FirebaseFirestore.getInstance();

        btnBack = findViewById(R.id.btnBack);
        userContainer = findViewById(R.id.userContainer);
        ImageView iconDashboard = findViewById(R.id.icon_dashboard);
        iconUsers = findViewById(R.id.icon_users);
        iconReports = findViewById(R.id.icon_reports);
        iconNotifications = findViewById(R.id.icon_notifications);
        iconSettings = findViewById(R.id.icon_settings);

        btnBack.setOnClickListener(v -> finish());

        iconDashboard.setOnClickListener(v -> {
            startActivity(new Intent(this, AdminView.class));
        });

        fetchFromFirestore();

    }

    private void fetchFromFirestore() {
        userContainer.removeAllViews();

        db.collection("USER_PROFILES")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        for (QueryDocumentSnapshot document : querySnapshot) {
                            String username = document.getString("username");
                            String user_id = document.getId();
                            String frozen = document.getString("freeze");

                            if (Objects.equals(username, "")) {
                                username = user_id;
                            }

                            if (Objects.equals(frozen, "awake")) {
                                addUserItem(username, frozen, user_id);
                            }

                        }
                    } else {
                        showToast();
                    }
                });
    }

    private void addUserItem(String username, String frozen, String userId) {

        View userView = getLayoutInflater().inflate(R.layout.user_item, userContainer, false);

        TextView userTitle = userView.findViewById(R.id.username);
        ImageView freeze = userView.findViewById(R.id.freeze_user);
        ImageView delete = userView.findViewById(R.id.delete_user);
        ImageView admin_user = userView.findViewById(R.id.admin_user);

        userTitle.setText(username);

        userTitle.setOnClickListener(v -> {
            Intent intent = new Intent(this, ProfileActivity.class);
            intent.putExtra("USER_ID", userId);
            startActivity(intent);
        });

        admin_user.setBackground(null);
        DocumentReference SuserRef = db.collection("USER_PROFILES").document(userId);
        SuserRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String isAdmin = documentSnapshot.getString("admin");
                if (Objects.equals(isAdmin, "admin")) {
                    admin_user.setImageResource(R.drawable.admin_icon);
                } else if (Objects.equals(isAdmin, "entrant")) {
                    admin_user.setImageResource(R.drawable.user_icon);
                }
            }
        }).addOnFailureListener(e -> {
            Log.e("adminUser", "Failed to fetch user data: " + e.getMessage());
        });

        admin_user.setOnClickListener(v -> {
            DocumentReference userRef = db.collection("USER_PROFILES").document(userId);
            userRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    String isAdmin = documentSnapshot.getString("admin");
                    if (Objects.equals(isAdmin, "entrant")) {
                        admin_user.setImageResource(R.drawable.admin_icon);
                        userRef.update("admin", "admin")
                                .addOnSuccessListener(aVoid -> {
                                    Log.d("adminUser", "User state updated to 'admin'");
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("adminUser", "Failed to update user state to 'admin': " + e.getMessage());
                                });

                    } else if (Objects.equals(isAdmin, "admin")) {
                        admin_user.setImageResource(R.drawable.user_icon);
                        userRef.update("admin", "entrant")
                                .addOnSuccessListener(aVoid -> {
                                    Log.d("adminUser", "User state updated to 'entrant'");
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("adminUser", "Failed to update user state to 'entrant': " + e.getMessage());
                                });
                    }
                }
            }).addOnFailureListener(e -> {
                Log.e("adminUser", "Failed to fetch user data: " + e.getMessage());
            });
        });

        freeze.setOnClickListener(v -> {
            DocumentReference userRef = db.collection("USER_PROFILES").document(userId);
            userRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    String isFrozen = documentSnapshot.getString("freeze");
                    if (Objects.equals(isFrozen, "awake")) {
                        freeze.setImageResource(R.drawable.no_view);
                        userRef.update("freeze", "frozen")
                                .addOnSuccessListener(aVoid -> {
                                    Log.d("FreezeUser", "User state updated to 'freeze'");
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("FreezeUser", "Failed to update user state to 'freeze': " + e.getMessage());
                                });

                    } else if (Objects.equals(isFrozen, "frozen")) {
                        freeze.setImageResource(R.drawable.view);
                        userRef.update("freeze", "awake")
                                .addOnSuccessListener(aVoid -> {
                                    Log.d("FreezeUser", "User state updated to 'awakw'");
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("FreezeUser", "Failed to update user state to 'awake': " + e.getMessage());
                                });
                    }
                }
        }).addOnFailureListener(e -> {
                Log.e("FreezeUser", "Failed to fetch user data: " + e.getMessage());
            });
        });


        delete.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Confirm Deletion")
                    .setMessage("Are you sure you want to delete this user?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        db.collection("USER_PROFILES").document(userId)
                                .delete()
                                .addOnSuccessListener(aVoid -> Log.d("ManageAllUsers", "User deleted successfully"))
                                .addOnFailureListener(e -> Log.e("ManageAllUsers", "Error deleting User", e));
                        userContainer.removeView(userView);
                    })
                    .setNegativeButton("No", null)
                    .show();
        });

        userContainer.addView(userView);
    }

    private void showToast() {
        Toast.makeText(this, "Failed to load users", Toast.LENGTH_SHORT).show();
    }

}

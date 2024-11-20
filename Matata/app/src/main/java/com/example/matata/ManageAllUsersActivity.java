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

/**
 * The ManageAllUsersActivity class provides an interface for administrators to manage all user profiles.
 * Administrators can view, freeze/unfreeze, delete, and change a user's role between "admin" and "entrant."
 * This activity interacts with Firebase Firestore to fetch, display, and update user data.
 */
public class ManageAllUsersActivity extends AppCompatActivity {

    /**
     * LinearLayout container for dynamically adding user profile items.
     */
    private LinearLayout userContainer;

    /**
     * Instance of FirebaseFirestore for database operations.
     */
    private FirebaseFirestore db;

    /**
     * Called when the activity is created. Sets up the UI, initializes Firestore, and fetches user data.
     *
     * @param savedInstanceState Bundle containing the activity's previously saved state, if any.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manage_all_users);

        db = FirebaseFirestore.getInstance();

        ImageView btnBack = findViewById(R.id.btnBack);
        userContainer = findViewById(R.id.userContainer);
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
            Intent intent = new Intent(ManageAllUsersActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        iconReports.setOnClickListener(v -> {
            Intent intent = new Intent(ManageAllUsersActivity.this, AdminReportActivity.class);
            startActivity(intent);
            finish();
        });

        fetchFromFirestore();

    }

    /**
     * Fetches all user profiles from Firestore and adds them to the `userContainer`.
     * Only users with the "awake" state are displayed.
     */
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

    /**
     * Dynamically adds a user profile item to the `userContainer`.
     * Each user item includes options to view their profile, freeze/unfreeze their account,
     * delete the user, and change their role between "admin" and "entrant."
     *
     * @param username Name of the user.
     * @param frozen   Current frozen state ("awake" or "frozen").
     * @param userId   Unique ID of the user.
     */
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

        DocumentReference SuserRef = db.collection("USER_PROFILES").document(userId);
        SuserRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String isAdmin = documentSnapshot.getString("admin");
                if (Objects.equals(isAdmin, "admin")) {
                    admin_user.setImageResource(R.drawable.admin_icon);
                } else if (Objects.equals(isAdmin, "entrant") || Objects.equals(isAdmin, "organiser")) {
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
                    if (Objects.equals(isAdmin, "entrant") || Objects.equals(isAdmin, "organiser") ) {
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
                        String isOrg= documentSnapshot.getString("organiser");
                        if (Objects.equals(isOrg, "yes")) {
                            userRef.update("admin", "organiser")
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d("adminUser", "User state updated to 'entrant'");
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e("adminUser", "Failed to update user state to 'entrant': " + e.getMessage());
                                    });
                        } else if (Objects.equals(isOrg, "no")) {
                            userRef.update("admin", "entrant")
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d("adminUser", "User state updated to 'entrant'");
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e("adminUser", "Failed to update user state to 'entrant': " + e.getMessage());
                                    });
                        }

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
                                    Log.d("FreezeUser", "User state updated to 'awake'");
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

    /**
     * Displays a toast message to indicate that users failed to load from Firestore.
     */
    private void showToast() {
        Toast.makeText(this, "Failed to load users", Toast.LENGTH_SHORT).show();
    }

}

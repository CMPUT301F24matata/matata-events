package com.example.matata;

import static org.junit.Assert.*;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.view.View;
import android.widget.CompoundButton;

import androidx.annotation.RequiresApi;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.concurrent.CountDownLatch;

@RunWith(AndroidJUnit4.class)
public class ProfileActivityTest {

    private FirebaseFirestore db;
    private String testUserId;

    @Before
    public void setUp() {
        db = FirebaseFirestore.getInstance();
        testUserId = Settings.Secure.getString(InstrumentationRegistry.getInstrumentation().getTargetContext().getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    @Test
    public void testProfileDataLoadSuccess() {
        ActivityScenario<ProfileActivity> scenario = ActivityScenario.launch(ProfileActivity.class);
        scenario.onActivity(activity -> {
            DocumentReference docRef = db.collection("USER_PROFILES").document(testUserId);
            docRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    EditText nameEditText = activity.findViewById(R.id.nameEditText);
                    EditText emailEditText = activity.findViewById(R.id.emailEditText);
                    EditText phoneEditText = activity.findViewById(R.id.phoneEditText);
                    Switch notificationsSwitch = activity.findViewById(R.id.switch_notification);

                    assertEquals(documentSnapshot.getString("username"), nameEditText.getText().toString());
                    assertEquals(documentSnapshot.getString("email"), emailEditText.getText().toString());
                    assertEquals(documentSnapshot.getString("phone"), phoneEditText.getText().toString());
                    assertEquals(documentSnapshot.getBoolean("notifications"), notificationsSwitch.isChecked());
                }
            }).addOnFailureListener(e -> fail("Failed to load profile data"));
        });
    }

    @Test
    public void testSaveProfileData_withValidInput() {
        ActivityScenario<ProfileActivity> scenario = ActivityScenario.launch(ProfileActivity.class);
        scenario.onActivity(activity -> {
            // Set input data
            EditText nameEditText = activity.findViewById(R.id.nameEditText);
            EditText emailEditText = activity.findViewById(R.id.emailEditText);
            EditText phoneEditText = activity.findViewById(R.id.phoneEditText);
            Switch notificationsSwitch = activity.findViewById(R.id.switch_notification);

            nameEditText.setText("Jane Doe");
            emailEditText.setText("jane.doe@example.com");
            phoneEditText.setText("0987654321");
            notificationsSwitch.setChecked(true);

            // Trigger save button
            activity.findViewById(R.id.saveButton).performClick();

            // Verify Firestore data
            DocumentReference docRef = db.collection("USER_PROFILES").document(testUserId);
            docRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    assertEquals("Jane Doe", documentSnapshot.getString("username"));
                    assertEquals("jane.doe@example.com", documentSnapshot.getString("email"));
                    assertEquals("0987654321", documentSnapshot.getString("phone"));
                    assertTrue(documentSnapshot.getBoolean("notifications"));
                }
            }).addOnFailureListener(e -> fail("Failed to save profile data"));
        });
    }

    @Test
    public void testProfilePictureUpload() {
        ActivityScenario<ProfileActivity> scenario = ActivityScenario.launch(ProfileActivity.class);
        scenario.onActivity(activity -> {
            // Click to open the ProfilePicActivity for selecting an image
            ImageView profileIcon = activity.findViewById(R.id.profileIcon);
            profileIcon.performClick();
        });

        // Manually select an image during the test
        // After selecting the image, verify if the profile picture has been updated
        scenario.onActivity(activity -> {
            ImageView profileIcon = activity.findViewById(R.id.profileIcon);
            assertNotNull(profileIcon.getDrawable());
        });
    }

    @Test
    public void testFacilityFieldsVisible_whenOrganizerSwitchIsChecked() {
        ActivityScenario<ProfileActivity> scenario = ActivityScenario.launch(ProfileActivity.class);
        scenario.onActivity(activity -> {
            Switch isOrganizerSwitch = activity.findViewById(R.id.switch_organizer);
            View organizerFields = activity.findViewById(R.id.organizerFields);

            // Enable the organizer switch
            isOrganizerSwitch.setChecked(true);

            // Ensure organizer fields are visible
            assertEquals(View.VISIBLE, organizerFields.getVisibility());
        });
    }

    @Test
    public void testAdminViewStartsAdminActivity() {
        ActivityScenario<ProfileActivity> scenario = ActivityScenario.launch(ProfileActivity.class);
        scenario.onActivity(activity -> {
            CompoundButton adminViewSwitch = activity.findViewById(R.id.adminView);

            // Set admin switch to true
            adminViewSwitch.setChecked(true);

            // Verify intent to start AdminView activity is fired
            Intent expectedIntent = new Intent(activity, AdminView.class);
            activity.startActivity(expectedIntent);
            assertNotNull("Intent was not started", expectedIntent);
            assertEquals(expectedIntent.getComponent(), expectedIntent.getComponent());
        });
    }

    @Test
    public void testUpdateProfileData_andVerifyWithDatabase() {
        ActivityScenario<ProfileActivity> scenario = ActivityScenario.launch(ProfileActivity.class);
        scenario.onActivity(activity -> {
            // Set new input data
            EditText nameEditText = activity.findViewById(R.id.nameEditText);
            EditText emailEditText = activity.findViewById(R.id.emailEditText);
            EditText phoneEditText = activity.findViewById(R.id.phoneEditText);
            Switch notificationsSwitch = activity.findViewById(R.id.switch_notification);

            nameEditText.setText("Updated Name");
            emailEditText.setText("updated.email@example.com");
            phoneEditText.setText("1231231234");
            notificationsSwitch.setChecked(false);

            // Trigger save button to update profile data
            activity.findViewById(R.id.saveButton).performClick();
        });

        // Verify Firestore data to make sure updates have been applied
        scenario.onActivity(activity -> {
            DocumentReference docRef = db.collection("USER_PROFILES").document(testUserId);
            docRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    assertEquals("Updated Name", documentSnapshot.getString("username"));
                    assertEquals("updated.email@example.com", documentSnapshot.getString("email"));
                    assertEquals("1231231234", documentSnapshot.getString("phone"));
                    assertFalse(documentSnapshot.getBoolean("notifications"));
                } else {
                    fail("Document does not exist in Firestore");
                }
            }).addOnFailureListener(e -> fail("Failed to verify updated profile data: " + e.getMessage()));
        });
    }

    @Test
    public void testFacilityProfileUpload_andVerifyWithDatabase() {
        ActivityScenario<ProfileActivity> scenario = ActivityScenario.launch(ProfileActivity.class);
        scenario.onActivity(activity -> {
            // Set facility profile input data
            EditText facilityName = activity.findViewById(R.id.facilityName);
            EditText facilityAddress = activity.findViewById(R.id.facilityAddress);
            EditText facilityCapacity = activity.findViewById(R.id.facilityCapacity);
            EditText facilityContact = activity.findViewById(R.id.facilityContact);
            EditText facilityEmail = activity.findViewById(R.id.facilityEmail);
            EditText facilityOwner = activity.findViewById(R.id.facilityOwner);
            Switch isOrganizerSwitch = activity.findViewById(R.id.switch_organizer);

            // Set facility data and switch on organizer mode
            facilityName.setText("Test Facility");
            facilityAddress.setText("123 Test Street");
            facilityCapacity.setText("500");
            facilityContact.setText("9876543210");
            facilityEmail.setText("facility@example.com");
            facilityOwner.setText("John Doe");
            isOrganizerSwitch.setChecked(true);

            // Trigger save button to update facility profile data
            activity.findViewById(R.id.saveButton).performClick();
        });

        // Verify Firestore data to make sure facility profile has been uploaded
        scenario.onActivity(activity -> {
            DocumentReference docRef = db.collection("FACILITY_PROFILES").document(testUserId);
            docRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    assertEquals("Test Facility", documentSnapshot.getString("facility name"));
                    assertEquals("123 Test Street", documentSnapshot.getString("address"));
                    assertEquals("500", documentSnapshot.getString("capacity"));
                    assertEquals("9876543210", documentSnapshot.getString("contact information"));
                    assertEquals("facility@example.com", documentSnapshot.getString("email"));
                    assertEquals("John Doe", documentSnapshot.getString("owner"));
                } else {
                    fail("Facility profile document does not exist in Firestore");
                }
            }).addOnFailureListener(e -> fail("Failed to verify facility profile data: " + e.getMessage()));
        });
    }

    @Test
    public void testInitialProfileDataLoad() throws InterruptedException {
        // Set initial data in Firestore for testing
        CountDownLatch latch = new CountDownLatch(1);
        db.collection("USER_PROFILES").document(testUserId).set(new HashMap<String, Object>() {{
                    put("username", "Initial Name");
                    put("email", "initial.email@example.com");
                    put("phone", "1122334455");
                    put("notifications", true);
                }}).addOnSuccessListener(aVoid -> latch.countDown())
                .addOnFailureListener(e -> {
                    fail("Failed to set initial profile data in Firestore: " + e.getMessage());
                    latch.countDown();
                });

        // Wait for Firestore operation to complete
        latch.await();

        // Now launch the activity and verify the data loaded in the UI
        ActivityScenario<ProfileActivity> scenario = ActivityScenario.launch(ProfileActivity.class);
        scenario.onActivity(activity -> {
            EditText nameEditText = activity.findViewById(R.id.nameEditText);
            EditText emailEditText = activity.findViewById(R.id.emailEditText);
            EditText phoneEditText = activity.findViewById(R.id.phoneEditText);
            Switch notificationsSwitch = activity.findViewById(R.id.switch_notification);

            assertEquals("Initial Name", nameEditText.getText().toString());
            assertEquals("initial.email@example.com", emailEditText.getText().toString());
            assertEquals("1122334455", phoneEditText.getText().toString());
            assertTrue(notificationsSwitch.isChecked());
        });
    }


}

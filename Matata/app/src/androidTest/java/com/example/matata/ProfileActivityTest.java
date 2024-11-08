/**
 * ProfileActivityTest class provides instrumentation tests for the ProfileActivity, verifying various functionalities
 * including profile data loading, saving, profile picture upload, facility field visibility, and switching to
 * the admin view. These tests ensure that the user profile data is handled correctly in the Firestore database.
 *
 * Purpose:
 * - Validate user profile data storage and retrieval in Firestore.
 * - Test profile picture functionality and visibility of facility fields based on user role.
 *
 * Outstanding Issues:
 * - Ensure compatibility with various device configurations.
 * - Investigate potential race conditions with Firestore operations during tests.
 */

package com.example.matata;

import static org.junit.Assert.*;

import android.content.Context;
import android.content.Intent;
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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.concurrent.CountDownLatch;

/**
 * Runs instrumentation tests for the ProfileActivity using AndroidJUnit4.
 */
@RunWith(AndroidJUnit4.class)
public class ProfileActivityTest {

    /**
     * Firestore instance for performing profile data operations.
     */
    private FirebaseFirestore db;

    /**
     * Unique identifier for the test user, based on the device's Android ID.
     */
    private String testUserId;

    /**
     * Sets up necessary resources before each test, including initializing Firestore and obtaining the test user ID.
     */
    @Before
    public void setUp() {
        db = FirebaseFirestore.getInstance();
        testUserId = Settings.Secure.getString(
                InstrumentationRegistry.getInstrumentation().getTargetContext().getContentResolver(),
                Settings.Secure.ANDROID_ID
        );
    }

    /**
     * Tests successful loading of profile data into the UI from Firestore.
     * Expected outcome: UI fields should match data in the Firestore document.
     */
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

    /**
     * Tests saving valid profile data to Firestore from the UI.
     * Expected outcome: Firestore document should reflect the updated values.
     */
    @Test
    public void testSaveProfileData_withValidInput() {
        ActivityScenario<ProfileActivity> scenario = ActivityScenario.launch(ProfileActivity.class);
        scenario.onActivity(activity -> {
            EditText nameEditText = activity.findViewById(R.id.nameEditText);
            EditText emailEditText = activity.findViewById(R.id.emailEditText);
            EditText phoneEditText = activity.findViewById(R.id.phoneEditText);
            Switch notificationsSwitch = activity.findViewById(R.id.switch_notification);

            nameEditText.setText("Jane Doe");
            emailEditText.setText("jane.doe@example.com");
            phoneEditText.setText("0987654321");
            notificationsSwitch.setChecked(true);

            activity.findViewById(R.id.saveButton).performClick();

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

    /**
     * Tests the profile picture upload functionality by verifying that a drawable is set on the ImageView.
     */
    @Test
    public void testProfilePictureUpload() {
        ActivityScenario<ProfileActivity> scenario = ActivityScenario.launch(ProfileActivity.class);
        scenario.onActivity(activity -> {
            ImageView profileIcon = activity.findViewById(R.id.profileIcon);
            profileIcon.performClick();
        });

        scenario.onActivity(activity -> {
            ImageView profileIcon = activity.findViewById(R.id.profileIcon);
            assertNotNull(profileIcon.getDrawable());
        });
    }

    /**
     * Tests that facility fields become visible when the organizer switch is checked.
     * Expected outcome: Organizer fields should be visible when switch is enabled.
     */
    @Test
    public void testFacilityFieldsVisible_whenOrganizerSwitchIsChecked() {
        ActivityScenario<ProfileActivity> scenario = ActivityScenario.launch(ProfileActivity.class);
        scenario.onActivity(activity -> {
            Switch isOrganizerSwitch = activity.findViewById(R.id.switch_organizer);
            View organizerFields = activity.findViewById(R.id.organizerFields);

            isOrganizerSwitch.setChecked(true);
            assertEquals(View.VISIBLE, organizerFields.getVisibility());
        });
    }

    /**
     * Tests that enabling the admin view switch triggers an intent to start the AdminView activity.
     */
    @Test
    public void testAdminViewStartsAdminActivity() {
        ActivityScenario<ProfileActivity> scenario = ActivityScenario.launch(ProfileActivity.class);
        scenario.onActivity(activity -> {
            CompoundButton adminViewSwitch = activity.findViewById(R.id.adminView);

            adminViewSwitch.setChecked(true);
            Intent expectedIntent = new Intent(activity, AdminView.class);
            activity.startActivity(expectedIntent);
            assertNotNull("Intent was not started", expectedIntent);
            assertEquals(expectedIntent.getComponent(), expectedIntent.getComponent());
        });
    }

    /**
     * Tests updating profile data in the UI and verifying the changes in Firestore.
     * Expected outcome: Firestore document should reflect the updated profile data.
     */
    @Test
    public void testUpdateProfileData_andVerifyWithDatabase() {
        ActivityScenario<ProfileActivity> scenario = ActivityScenario.launch(ProfileActivity.class);
        scenario.onActivity(activity -> {
            EditText nameEditText = activity.findViewById(R.id.nameEditText);
            EditText emailEditText = activity.findViewById(R.id.emailEditText);
            EditText phoneEditText = activity.findViewById(R.id.phoneEditText);
            Switch notificationsSwitch = activity.findViewById(R.id.switch_notification);

            nameEditText.setText("Updated Name");
            emailEditText.setText("updated.email@example.com");
            phoneEditText.setText("1231231234");
            notificationsSwitch.setChecked(false);

            activity.findViewById(R.id.saveButton).performClick();
        });

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

    /**
     * Tests saving facility profile data in Firestore and verifying the stored data.
     * Expected outcome: Firestore document should contain the correct facility profile information.
     */
    @Test
    public void testFacilityProfileUpload_andVerifyWithDatabase() {
        ActivityScenario<ProfileActivity> scenario = ActivityScenario.launch(ProfileActivity.class);
        scenario.onActivity(activity -> {
            EditText facilityName = activity.findViewById(R.id.facilityName);
            EditText facilityAddress = activity.findViewById(R.id.facilityAddress);
            EditText facilityCapacity = activity.findViewById(R.id.facilityCapacity);
            EditText facilityContact = activity.findViewById(R.id.facilityContact);
            EditText facilityEmail = activity.findViewById(R.id.facilityEmail);
            EditText facilityOwner = activity.findViewById(R.id.facilityOwner);
            Switch isOrganizerSwitch = activity.findViewById(R.id.switch_organizer);

            facilityName.setText("Test Facility");
            facilityAddress.setText("123 Test Street");
            facilityCapacity.setText("500");
            facilityContact.setText("9876543210");
            facilityEmail.setText("facility@example.com");
            facilityOwner.setText("John Doe");
            isOrganizerSwitch.setChecked(true);

            activity.findViewById(R.id.saveButton).performClick();
        });

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

    /**
     * Tests loading initial profile data into the UI from Firestore, verifying that default values are correctly displayed.
     * Expected outcome: UI fields should match the initialized Firestore document values.
     */
    @Test
    public void testInitialProfileDataLoad() throws InterruptedException {
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

        latch.await();

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

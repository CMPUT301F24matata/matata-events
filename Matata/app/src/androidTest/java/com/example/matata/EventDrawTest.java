/**
 * The `EventDrawTest` class provides integration tests for the `EventDraw` class.
 * It verifies the correctness of event draw-related functionalities, such as managing the draw process,
 * clearing pending participants, and setting waitlist limits. The tests directly interact with Firebase Firestore,
 * ensuring real-time updates are validated. This test suite is designed for use in a test Firestore environment
 * and should not be executed against a production database.
 *
 * <h2>Purpose</h2>
 * - Validate the functionality of the event draw process.
 * - Ensure correct handling of the waitlist limit.
 * - Test clearing of the pending participants list.
 * - Verify UI components and interactions, such as buttons and switches.
 *
 * <h2>Test Scenarios</h2>
 * <ul>
 *     <li><b>testDraw:</b> Tests the event draw process and verifies updates to the "pending" and "waitlist" fields in Firestore.</li>
 *     <li><b>testClearPendingList:</b> Tests clearing the pending participants list and verifies database updates.</li>
 *     <li><b>testWaitlistLimit:</b> Tests enabling/disabling and setting waitlist limits and ensures Firestore updates.</li>
 * </ul>
 *
 * <h2>Important Notes</h2>
 * <ul>
 *     <li>The Firestore database must contain the necessary "EVENT_PROFILES" collection with the expected schema.</li>
 *     <li>These tests perform actual database operations and require a configured test Firestore environment.</li>
 * </ul>
 */

package com.example.matata;

import android.app.AlertDialog;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Test suite for validating the functionality of the `EventDraw` class.
 */
@RunWith(AndroidJUnit4.class)
public class EventDrawTest {

    /**
     * Instance of FirebaseFirestore for database operations.
     */
    private FirebaseFirestore db;

    /**
     * Reference to a specific test document in the "EVENT_PROFILES" Firestore collection.
     */
    private DocumentReference documentReference;

    /**
     * Initializes Firestore and sets up a test document in the "EVENT_PROFILES" collection.
     * This document is used for all test cases to ensure consistency.
     */
    @Before
    public void setUp() {
        db = FirebaseFirestore.getInstance();
        documentReference = db.collection("EVENT_PROFILES").document("testEvent");
    }

    /**
     * Tests the event draw process by simulating a button click and verifying updates to the
     * "pending" and "waitlist" fields in Firestore.
     */
    @Test
    public void testDraw() {
        ActivityScenario<EventDraw> scenario = ActivityScenario.launch(EventDraw.class);

        scenario.onActivity(activity -> {
            Button drawButton = activity.findViewById(R.id.draw_button);

            // Simulate a click on the draw button
            drawButton.performClick();

            AlertDialog dialog = activity.getCurrentDialog();
            assertTrue("Dialog should be showing", dialog.isShowing());

            Button positive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            positive.performClick();

            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                // Verify database updates
                documentReference.get().addOnSuccessListener(documentSnapshot -> {
                    assertTrue("Pending field should be updated", documentSnapshot.contains("pending"));
                    assertTrue("Waitlist field should be updated", documentSnapshot.contains("waitlist"));
                }).addOnFailureListener(e -> assertFalse("Failed to retrieve document", true));
            }, 50);
        });
    }

    /**
     * Tests clearing the pending participants list by simulating a button click.
     * Verifies the dialog interaction and database updates.
     */
    @Test
    public void testClearPendingList() {
        ActivityScenario<EventDraw> scenario = ActivityScenario.launch(EventDraw.class);

        scenario.onActivity(activity -> {
            Button clearButton = activity.findViewById(R.id.clearPendingList);

            // Simulate a click on the clear button
            clearButton.performClick();
            AlertDialog dialog = activity.getCurrentDialog();
            assertTrue("Dialog should be showing", dialog.isShowing());

            Button positive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            positive.performClick();

            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                // Verify the pending list is cleared in Firestore
                documentReference.get().addOnSuccessListener(documentSnapshot -> {
                    assertTrue("Pending field should be empty", documentSnapshot.get("pending") == null);
                }).addOnFailureListener(e -> assertFalse("Failed to retrieve document", true));
            }, 50);
        });
    }

    /**
     * Tests enabling, setting, and disabling the waitlist limit functionality.
     * Verifies the visibility of UI elements and updates to Firestore.
     */
    @Test
    public void testWaitlistLimit() {
        ActivityScenario<EventDraw> scenario = ActivityScenario.launch(EventDraw.class);

        scenario.onActivity(activity -> {
            Switch limitSwitch = activity.findViewById(R.id.limitSwitch);
            EditText waitlistLimit = activity.findViewById(R.id.waitlistLimit);
            Button saveButton = activity.findViewById(R.id.saveButton);

            // Enable the waitlist limit
            limitSwitch.setChecked(true);
            assertEquals("Waitlist limit field should be visible", 0, waitlistLimit.getVisibility());
            assertEquals("Save button should be visible", 0, saveButton.getVisibility());

            // Set and save the waitlist limit
            waitlistLimit.setText("10");
            saveButton.performClick();

            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                // Verify database update for waitlist limit
                documentReference.get().addOnSuccessListener(documentSnapshot -> {
                    assertEquals("WaitlistLimit field should be updated to 10", 10L, (long) documentSnapshot.getLong("WaitlistLimit"));
                }).addOnFailureListener(e -> assertFalse("Failed to retrieve document", true));
            }, 50);

            // Disable the waitlist limit
            limitSwitch.setChecked(false);
            assertEquals("Waitlist limit field should be hidden", 8, waitlistLimit.getVisibility());
            assertEquals("Save button should be hidden", 8, saveButton.getVisibility());

            saveButton.performClick();
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                // Verify the waitlist limit field is reset in Firestore
                documentReference.get().addOnSuccessListener(documentSnapshot -> {
                    assertEquals("WaitlistLimit field should be updated to -1", -1L, (long) documentSnapshot.getLong("WaitlistLimit"));
                }).addOnFailureListener(e -> assertFalse("Failed to retrieve document", true));
            }, 50);
        });
    }
}

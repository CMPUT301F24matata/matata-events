/**
 * EventDrawTest class provides integration tests for the EventDraw class, which manages the event draw process,
 * waitlist limit functionality, and clearing of pending participants. The class directly interacts with Firebase
 * Firestore without any mocking libraries, so it performs actual database operations.
 *
 * Purpose:
 * - Validate the functionality of the event draw process and waitlist handling.
 * - Test UI elements such as limit switch, draw button, and clear button for correct behavior.
 *
 * Note:
 * - Ensure that the Firestore database contains the necessary "EVENT_PROFILES" collection with the correct schema for testing.
 * - These tests rely on a test environment Firestore database and should not be run on a production database.
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

@RunWith(AndroidJUnit4.class)
public class EventDrawTest {

    private FirebaseFirestore db;
    private DocumentReference documentReference;

    /**
     * Initializes Firestore and sets up a test document in the "EVENT_PROFILES" collection for testing.
     * This document represents a sample event profile for the purpose of the tests.
     */
    @Before
    public void setUp() {
        db = FirebaseFirestore.getInstance();
        documentReference = db.collection("EVENT_PROFILES").document("testEvent");
    }

    /**
     * Tests the event draw process by simulating a button click and verifying transaction updates for the
     * "pending" and "waitlist" fields in Firestore.
     */
    @Test
    public void testDraw() {
        ActivityScenario<EventDraw> scenario = ActivityScenario.launch(EventDraw.class);

        scenario.onActivity(activity -> {
            Button drawButton = activity.findViewById(R.id.draw_button);

            // Simulate click on the draw button
            drawButton.performClick();

            AlertDialog dialog = activity.getCurrentDialog();
            assertTrue("Dialog should be showing", dialog.isShowing());

            Button positive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            positive.performClick();

            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                // Verify that pending and waitlist fields were updated in the database
                documentReference.get().addOnSuccessListener(documentSnapshot -> {
                    assertTrue("Pending field should be updated", documentSnapshot.contains("pending"));
                    assertTrue("Waitlist field should be updated", documentSnapshot.contains("waitlist"));
                }).addOnFailureListener(e -> assertFalse("Failed to retrieve document", true));
            }, 50);
        });
    }

    /**
     * Tests clearing the pending list by simulating a button click and verifying the dialog interaction.
     */
    @Test
    public void testClearPendingList() {
        ActivityScenario<EventDraw> scenario = ActivityScenario.launch(EventDraw.class);

        scenario.onActivity(activity -> {
            Button clearButton = activity.findViewById(R.id.clearPendingList);

            // Perform clear action
            clearButton.performClick();
            AlertDialog dialog = activity.getCurrentDialog();
            assertTrue("Dialog should be showing", dialog.isShowing());

            Button positive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            positive.performClick();

            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                // Verify that the dialog is dismissed after the click
                assertFalse("Dialog should be dismissed", dialog.isShowing());

                // Check if pending list was cleared in Firestore
                documentReference.get().addOnSuccessListener(documentSnapshot -> {
                    assertTrue("Pending field should be empty", documentSnapshot.get("pending") == null);
                }).addOnFailureListener(e -> assertFalse("Failed to retrieve document", true));
            }, 50);
        });
    }

    /**
     * Tests enabling and setting the waitlist limit by interacting with the limit switch and verifying
     * Firestore updates.
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
                documentReference.get().addOnSuccessListener(documentSnapshot -> {
                    assertEquals("WaitlistLimit field should be updated to -1", -1L, (long) documentSnapshot.getLong("WaitlistLimit"));
                }).addOnFailureListener(e -> assertFalse("Failed to retrieve document", true));
            }, 50);
        });
    }
}

/**
 * ScanQRUITest class provides a UI test for the MainActivity to verify that clicking on the QR scanner button
 * navigates the user to the QR_camera activity. This test uses Espresso to simulate user interaction and validate
 * the correct intent is triggered.
 *
 * Purpose:
 * - Confirm that the QR scanner button opens the QR_camera activity as expected.
 *
 * Outstanding Issues:
 * - Ensure device compatibility for testing activities involving camera functionality.
 * - The test uses a fixed delay (Thread.sleep) which may affect reliability on slower devices; consider
 *   using an IdlingResource for more accurate timing.
 */

package com.example.matata;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * Runs UI tests for the QR scanner button in MainActivity using Espresso.
 */
public class ScanQRUITest {

    /**
     * Rule that provides an ActivityScenario for MainActivity, enabling control over its lifecycle.
     */
    @Rule
    public ActivityScenarioRule<MainActivity> activityRule = new ActivityScenarioRule<>(MainActivity.class);

    /**
     * Initializes Espresso Intents before each test to monitor intent handling.
     */
    @Before
    public void setUp() {
        Intents.init();
    }

    /**
     * Releases Espresso Intents after each test to ensure proper cleanup.
     */
    @After
    public void tearDown() {
        Intents.release();
    }

    /**
     * Tests that clicking the QR scanner button triggers an intent to open the QR_camera activity.
     * Expected outcome: Intent to QR_camera activity should be launched.
     *
     * @throws InterruptedException if the thread sleep is interrupted.
     */
    @Test
    public void testQRbutton() throws InterruptedException {
        onView(withId(R.id.qr_scanner)).perform(click());
        Thread.sleep(5000);  // Allow time for navigation
        intended(hasComponent(QR_camera.class.getName()));
    }
}

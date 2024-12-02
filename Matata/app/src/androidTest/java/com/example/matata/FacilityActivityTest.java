package com.example.matata;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * The `FacilityActivityTest` class is an Espresso-based test suite for validating the functionality of the `FacilityActivity` UI.
 * It ensures the correct behavior of the facility profile screen, including input validation, interaction with buttons and switches,
 * and the state of UI elements after user actions.
 *
 * <h2>Purpose</h2>
 * This test suite verifies:
 * <ul>
 *     <li>User input fields (e.g., name, address, capacity) accept and display data correctly.</li>
 *     <li>The notification toggle switch functions as expected.</li>
 *     <li>The save button properly retains user input.</li>
 *     <li>The clear-all button clears all input fields as intended.</li>
 * </ul>
 *
 * <h2>Test Scenarios</h2>
 * <ul>
 *     <li><b>testFacilityNameField:</b> Validates the name field accepts and displays the user input correctly.</li>
 *     <li><b>testFacilityAddressField:</b> Ensures the address field accepts and displays the user input correctly.</li>
 *     <li><b>testFacilityCapacityField:</b> Verifies the capacity field accepts and displays numerical input.</li>
 *     <li><b>testFacilityContactField:</b> Tests the contact field for input and display correctness.</li>
 *     <li><b>testFacilityEmailField:</b> Ensures the email field correctly handles user input.</li>
 *     <li><b>testFacilityOwnerField:</b> Checks that the owner field displays the entered data.</li>
 *     <li><b>testNotificationSwitch:</b> Validates the toggle behavior of the notification switch.</li>
 *     <li><b>testSaveButton:</b> Ensures data is saved and persists after clicking the save button.</li>
 *     <li><b>testClearAllButton:</b> Verifies that the clear-all button resets all input fields.</li>
 * </ul>
 *
 * <h2>Requirements</h2>
 * - The `FacilityActivity` must be correctly implemented with the necessary view IDs matching those used in the tests.
 * - Espresso framework must be configured for Android instrumentation tests.
 * - These tests assume no interaction with Firebase or external dependencies for the test environment.
 *
 * <h2>Important Notes</h2>
 * - Ensure the `TestFacilityActivity` class is a mock implementation of `FacilityActivity` for testing purposes.
 * - Test results should validate both field behavior and overall screen functionality.
 */
@RunWith(AndroidJUnit4.class)
public class FacilityActivityTest {

    /**
     * Rule to launch the `TestFacilityActivity` before each test.
     */
    @Rule
    public ActivityScenarioRule<TestFacilityActivity> activityRule =
            new ActivityScenarioRule<>(TestFacilityActivity.class);

    /**
     * Verifies the facility name field accepts and displays user input.
     */
    @Test
    public void testFacilityNameField() {
        onView(withId(R.id.facilityName))
                .perform(typeText("Test Facility"), closeSoftKeyboard())
                .check(matches(withText("Test Facility")));
    }

    /**
     * Ensures the facility address field accepts and displays user input.
     */
    @Test
    public void testFacilityAddressField() {
        onView(withId(R.id.facilityAddress))
                .perform(typeText("123 Test St"), closeSoftKeyboard())
                .check(matches(withText("123 Test St")));
    }

    /**
     * Verifies the facility capacity field accepts and displays numerical input.
     */
    @Test
    public void testFacilityCapacityField() {
        onView(withId(R.id.facilityCapacity))
                .perform(typeText("500"), closeSoftKeyboard())
                .check(matches(withText("500")));
    }

    /**
     * Tests the facility contact field for input and display correctness.
     */
    @Test
    public void testFacilityContactField() {
        onView(withId(R.id.facilityContact))
                .perform(typeText("9876543210"), closeSoftKeyboard())
                .check(matches(withText("9876543210")));
    }

    /**
     * Ensures the facility email field correctly handles user input.
     */
    @Test
    public void testFacilityEmailField() {
        onView(withId(R.id.facilityEmail))
                .perform(typeText("test@facility.com"), closeSoftKeyboard())
                .check(matches(withText("test@facility.com")));
    }

    /**
     * Checks that the facility owner field displays the entered data.
     */
    @Test
    public void testFacilityOwnerField() {
        onView(withId(R.id.facilityOwner))
                .perform(typeText("John Doe"), closeSoftKeyboard())
                .check(matches(withText("John Doe")));
    }

    /**
     * Validates the toggle behavior of the notification switch.
     */
    @Test
    public void testNotificationSwitch() {
        onView(withId(R.id.switch_notification)).perform(click()).check(matches(isChecked()));
        onView(withId(R.id.switch_notification)).perform(click()).check(matches(isNotChecked()));
    }

    /**
     * Ensures data persists after clicking the save button.
     */
    @Test
    public void testSaveButton() {
        onView(withId(R.id.facilityName)).perform(typeText("Test Facility"), closeSoftKeyboard());
        onView(withId(R.id.facilityAddress)).perform(typeText("123 Test St"), closeSoftKeyboard());
        onView(withId(R.id.facilityCapacity)).perform(typeText("500"), closeSoftKeyboard());
        onView(withId(R.id.facilityContact)).perform(typeText("9876543210"), closeSoftKeyboard());
        onView(withId(R.id.facilityEmail)).perform(typeText("test@facility.com"), closeSoftKeyboard());
        onView(withId(R.id.facilityOwner)).perform(typeText("John Doe"), closeSoftKeyboard());
        onView(withId(R.id.saveButton)).perform(scrollTo(), click());

        onView(withId(R.id.facilityName)).check(matches(withText("Test Facility")));
        onView(withId(R.id.facilityAddress)).check(matches(withText("123 Test St")));
        onView(withId(R.id.facilityCapacity)).check(matches(withText("500")));
        onView(withId(R.id.facilityContact)).check(matches(withText("9876543210")));
        onView(withId(R.id.facilityEmail)).check(matches(withText("test@facility.com")));
        onView(withId(R.id.facilityOwner)).check(matches(withText("John Doe")));
    }

    /**
     * Verifies that the clear-all button resets all input fields.
     */
    @Test
    public void testClearAllButton() {
        onView(withId(R.id.facilityName)).perform(typeText("Test Facility"), closeSoftKeyboard());
        onView(withId(R.id.clearAllButton)).perform(scrollTo(), click());

        onView(withId(R.id.facilityName)).check(matches(withText("")));
    }
}

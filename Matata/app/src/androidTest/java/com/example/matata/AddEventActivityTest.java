package com.example.matata;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * The `AddEventActivityTest` class is an Espresso test suite designed to validate the functionality
 * and user interface of the `TestAddEventActivity`. It includes tests for input fields, buttons,
 * and interactions with time and date pickers.
 *
 * <h2>Features Tested:</h2>
 * <ul>
 *     <li>Initial UI state verification.</li>
 *     <li>Input validation for event fields such as title, description, capacity, and location.</li>
 *     <li>Button functionality for clearing fields and generating QR codes.</li>
 *     <li>Interaction with date and time pickers.</li>
 * </ul>
 *
 * <h2>Test Cases:</h2>
 * <ul>
 *     <li><b>testInitialUIState:</b> Verifies that input fields are empty upon activity launch.</li>
 *     <li><b>testInputFields:</b> Ensures input fields accept and display user-entered data correctly.</li>
 *     <li><b>testClearButton:</b> Validates that the "Clear All" button resets all fields to their default state.</li>
 *     <li><b>testTimeAndDatePicker:</b> Tests the functionality of date and time pickers.</li>
 *     <li><b>testGenerateQRButton:</b> Checks if the "Generate QR" button works when required fields are filled.</li>
 * </ul>
 */
@RunWith(AndroidJUnit4.class)
public class AddEventActivityTest {

    /**
     * ActivityScenarioRule launches the `TestAddEventActivity` for testing.
     * Ensures the activity is initialized before each test and cleaned up afterward.
     */
    @Rule
    public ActivityScenarioRule<TestAddEventActivity> activityRule =
            new ActivityScenarioRule<>(TestAddEventActivity.class);

    /**
     * Tests the initial state of the UI to ensure all fields are empty upon launch.
     */
    @Test
    public void testInitialUIState() {
        onView(withId(R.id.eventTitle)).check(matches(withText("")));
        onView(withId(R.id.editTextDate)).check(matches(withText("")));
        onView(withId(R.id.editTextTime)).check(matches(withText("")));
    }

    /**
     * Verifies that input fields accept user-entered data and display it correctly.
     */
    @Test
    public void testInputFields() {
        onView(withId(R.id.eventTitle)).perform(typeText("Test Event"), closeSoftKeyboard());
        onView(withId(R.id.desc_box)).perform(typeText("This is a test description."), closeSoftKeyboard());
        onView(withId(R.id.number_of_people_event)).perform(typeText("100"), closeSoftKeyboard());

        onView(withId(R.id.eventTitle)).check(matches(withText("Test Event")));
        onView(withId(R.id.desc_box)).check(matches(withText("This is a test description.")));
        onView(withId(R.id.number_of_people_event)).check(matches(withText("100")));
    }

    /**
     * Ensures the "Clear All" button resets all input fields to their default state.
     */
    @Test
    public void testClearButton() {
        onView(withId(R.id.eventTitle)).perform(typeText("Test Event"), closeSoftKeyboard());
        onView(withId(R.id.desc_box)).perform(typeText("This is a test description."), closeSoftKeyboard());
        onView(withId(R.id.number_of_people_event)).perform(typeText("100"), closeSoftKeyboard());

        onView(withId(R.id.editTextDate)).perform(click());
        onView(withText("OK")).perform(click());

        onView(withId(R.id.editTextTime)).perform(click());
        onView(withText("OK")).perform(click());

        onView(withId(R.id.clearAllButton)).perform(click());

        onView(withId(R.id.eventTitle)).check(matches(withText("")));
        onView(withId(R.id.desc_box)).check(matches(withText("")));
        onView(withId(R.id.editTextDate)).check(matches(withText("")));
        onView(withId(R.id.editTextTime)).check(matches(withText("")));
        onView(withId(R.id.number_of_people_event)).check(matches(withText("")));
    }

    /**
     * Tests the functionality of the date and time pickers by interacting with them.
     */
    @Test
    public void testTimeAndDatePicker() {
        onView(withId(R.id.editTextDate)).perform(click());
        onView(withText("OK")).perform(click());
        onView(withId(R.id.editTextTime)).perform(click());
        onView(withText("OK")).perform(click());

        onView(withId(R.id.editTextDate)).check(matches(withHint("Select Date")));
        onView(withId(R.id.editTextTime)).check(matches(withHint("Select Time")));
    }

    /**
     * Verifies that the "Generate QR" button works correctly when all required fields are filled.
     * Ensures that the fields retain their data after generating a QR code.
     */
    @Test
    public void testGenerateQRButton() {
        onView(withId(R.id.editTextDate)).perform(click());
        onView(withText("OK")).perform(click());
        onView(withId(R.id.editTextTime)).perform(click());
        onView(withText("OK")).perform(click());

        onView(withId(R.id.eventTitle)).perform(typeText("Test Event"), closeSoftKeyboard());
        onView(withId(R.id.desc_box)).perform(typeText("This is a test description."), closeSoftKeyboard());
        onView(withId(R.id.editTextLocation)).perform(typeText("sample location"), closeSoftKeyboard());
        onView(withId(R.id.number_of_people_event)).perform(typeText("100"), closeSoftKeyboard());

        onView(withId(R.id.genQR)).perform(click());

        onView(withId(R.id.eventTitle)).check(matches(withText("Test Event")));
        onView(withId(R.id.desc_box)).check(matches(withText("This is a test description.")));
        onView(withId(R.id.editTextLocation)).check(matches(withText("sample location")));
        onView(withId(R.id.editTextDate)).check(matches(withHint("Select Date")));
        onView(withId(R.id.editTextTime)).check(matches(withHint("Select Time")));
        onView(withId(R.id.number_of_people_event)).check(matches(withText("100")));
    }
}

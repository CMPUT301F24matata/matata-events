package com.example.matata;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.example.matata.AddEvent;
import com.example.matata.ViewEvent;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class AddEventUITest {

    @Rule
    public ActivityScenarioRule<AddEvent> activityRule = new ActivityScenarioRule<>(AddEvent.class);

    @Before
    public void setUp() {
        Intents.init();
    }

    @After
    public void tearDown() {
        Intents.release();
    }

    @Test
    public void testEventFieldInputs_DisplayCorrectly() {
        // Simulate typing text in event fields
        onView(withId(R.id.eventTitle)).perform(typeText("Sample Event"));
        closeSoftKeyboard();
        onView(withId(R.id.desc_box)).perform(typeText("Event Description"));
        closeSoftKeyboard();
        onView(withId(R.id.number_of_people_event)).perform(typeText("100"));
        closeSoftKeyboard();
        // Check that text is correctly displayed in the fields
        onView(withId(R.id.eventTitle)).check(matches(withText("Sample Event")));
        onView(withId(R.id.desc_box)).check(matches(withText("Event Description")));
        onView(withId(R.id.number_of_people_event)).check(matches(withText("100")));
    }

    @Test
    public void testBackButtonConfirmationDialog_DisplayedOnNonEmptyFields() {
        // Enter some text in a field
        onView(withId(R.id.eventTitle)).perform(typeText("Sample Event"));
        closeSoftKeyboard();
        // Press the back button
        onView(withId(R.id.btnBackCreateEvent)).perform(click());

        // Check if the confirmation dialog is displayed
        onView(withText("Are you sure you want to discard?")).check(matches(isDisplayed()));
    }

    @Test
    public void testDatePickerAndTimePicker_DisplaysCorrectly() {
        // Open the date picker
        onView(withId(R.id.dateGroup)).perform(click());
        onView(withId(R.id.locationGroup)).perform(click());

        // Open the time picker
        onView(withId(R.id.timeGroup)).perform(click());
        onView(withId(R.id.locationGroup)).perform(click());

    }

    @Test
    public void testGenerateQRButton_NavigatesToViewEvent_WithValidInputs() throws InterruptedException {
        // Enter all required fields
        onView(withId(R.id.eventTitle)).perform(typeText("Sample Event"));
        closeSoftKeyboard();

        onView(withId(R.id.desc_box)).perform(typeText("Event Description"));
        closeSoftKeyboard();

        onView(withId(R.id.editTextDate)).perform(typeText("12/12/2024"));
        onView(withId(R.id.dateField)).perform(typeText("10:00"));
        onView(withId(R.id.number_of_people_event)).perform(typeText("50"));
        closeSoftKeyboard();

        // Click the Generate QR button
        onView(withId(R.id.genQR)).perform(click());

        Thread.sleep(3000);

        // Check that it navigates to ViewEvent activity
        intended(hasComponent(ViewEvent.class.getName()));
    }

    @Test
    public void testImageSelector_OpensImageChooser() {
        // Click on the image selector for uploading a poster
        onView(withId(R.id.posterPicUpload)).perform(click());

        // Check that the image chooser dialog is displayed
        onView(withText("Choose an image")).check(matches(isDisplayed()));
    }
}

///**
// * AddEventUITest class provides UI tests for the AddEvent activity. These tests verify that all UI components,
// * including event fields, date and time pickers, back button, and image selector, function as expected.
// * The class also tests navigation from AddEvent to ViewEvent upon successful event creation.
// *
// * Purpose:
// * - Validate the correct display and behavior of AddEvent UI elements.
// * - Ensure navigation to ViewEvent when the "Generate QR" button is clicked with valid input.
// *
// * Outstanding Issues:
// * - Asynchronous interactions with date and time pickers may require delay handling.
// * - Confirmation dialogs need testing for consistent display across different devices.
// */
//
//package com.example.matata;
//
//import static androidx.test.espresso.Espresso.closeSoftKeyboard;
//import static androidx.test.espresso.Espresso.onView;
//import static androidx.test.espresso.action.ViewActions.click;
//import static androidx.test.espresso.action.ViewActions.typeText;
//import static androidx.test.espresso.assertion.ViewAssertions.matches;
//import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
//import static androidx.test.espresso.matcher.ViewMatchers.withId;
//import static androidx.test.espresso.matcher.ViewMatchers.withText;
//import static androidx.test.espresso.intent.Intents.intended;
//import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
//
//import static org.junit.Assert.assertNotNull;
//import static org.junit.Assert.assertTrue;
//
//import android.os.Handler;
//import android.os.Looper;
//import android.widget.LinearLayout;
//
//import androidx.fragment.app.Fragment;
//import androidx.test.core.app.ActivityScenario;
//import androidx.test.espresso.intent.Intents;
//import androidx.test.ext.junit.rules.ActivityScenarioRule;
//import androidx.test.ext.junit.runners.AndroidJUnit4;
//
//import com.example.matata.AddEvent;
//import com.example.matata.ViewEvent;
//
//import org.junit.After;
//import org.junit.Before;
//import org.junit.Rule;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//
///**
// * Runs UI tests for the AddEvent activity using AndroidJUnit4.
// */
//@RunWith(AndroidJUnit4.class)
//public class AddEventUITest {
//
//    /**
//     * Rule that provides an ActivityScenario for AddEvent, enabling control over its lifecycle.
//     */
//    @Rule
//    public ActivityScenarioRule<AddEvent> activityRule = new ActivityScenarioRule<>(AddEvent.class);
//
//    /**
//     * Initializes Intents for intent verification before each test.
//     */
//    @Before
//    public void setUp() {
//        Intents.init();
//    }
//
//    /**
//     * Releases Intents after each test to ensure proper cleanup.
//     */
//    @After
//    public void tearDown() {
//        Intents.release();
//    }
//
//    /**
//     * Tests that entering text into the event fields displays correctly.
//     * Expected outcome: Text inputted in fields should match the expected text.
//     */
//    @Test
//    public void testEventFieldInputs_DisplayCorrectly() {
//        onView(withId(R.id.eventTitle)).perform(typeText("Sample Event"));
//        closeSoftKeyboard();
//        onView(withId(R.id.desc_box)).perform(typeText("Event Description"));
//        closeSoftKeyboard();
//        onView(withId(R.id.number_of_people_event)).perform(typeText("100"));
//        closeSoftKeyboard();
//
//        onView(withId(R.id.eventTitle)).check(matches(withText("Sample Event")));
//        onView(withId(R.id.desc_box)).check(matches(withText("Event Description")));
//        onView(withId(R.id.number_of_people_event)).check(matches(withText("100")));
//    }
//
//    /**
//     * Tests that a confirmation dialog appears when attempting to exit the activity with non-empty fields.
//     * Expected outcome: Confirmation dialog with discard message should be displayed.
//     */
//    @Test
//    public void testBackButtonConfirmationDialog_DisplayedOnNonEmptyFields() {
//        onView(withId(R.id.eventTitle)).perform(typeText("Sample Event"));
//        closeSoftKeyboard();
//        onView(withId(R.id.btnBackCreateEvent)).perform(click());
//
//        onView(withText("Are you sure you want to discard?")).check(matches(isDisplayed()));
//    }
//
//    /**
//     * Tests that the date and time pickers display correctly when selected.
//     * Expected outcome: Date and time picker dialogs should be visible.
//     */
//    @Test
//    public void testDatePickerAndTimePicker_DisplaysCorrectly() {
//        ActivityScenario<AddEvent> scenario = ActivityScenario.launch(AddEvent.class);
//
//        scenario.onActivity(activity -> {
//            // Open the date picker
//            LinearLayout dateGroup = activity.findViewById(R.id.dateGroup);
//            dateGroup.performClick();
//
//            // Verify if the date picker is displayed
//            Fragment fragment = activity.getSupportFragmentManager().findFragmentByTag("datePicker");
//            new Handler(Looper.getMainLooper()).postDelayed(() -> {
//                assertNotNull("Fragment should be added", fragment);
//                assertTrue("Fragment should be visible", fragment.isVisible());
//            }, 3000);
//        });
//
//        scenario.onActivity(activity -> {
//            // Open the time picker
//            LinearLayout dateGroup = activity.findViewById(R.id.timeGroup);
//            dateGroup.performClick();
//
//            // Verify if the time picker is displayed
//            Fragment fragment = activity.getSupportFragmentManager().findFragmentByTag("timePicker");
//            new Handler(Looper.getMainLooper()).postDelayed(() -> {
//                assertNotNull("Fragment should be added", fragment);
//                assertTrue("Fragment should be visible", fragment.isVisible());
//            }, 3000);
//        });
//    }
//
//    /**
//     * Tests that the "Generate QR" button navigates to ViewEvent when all fields are filled.
//     * Expected outcome: Intent to ViewEvent activity should be launched.
//     *
//     * @throws InterruptedException if the thread sleep is interrupted.
//     */
//    @Test
//    public void testGenerateQRButton_NavigatesToViewEvent_WithValidInputs() throws InterruptedException {
//        onView(withId(R.id.eventTitle)).perform(typeText("Sample Event"));
//        closeSoftKeyboard();
//        onView(withId(R.id.desc_box)).perform(typeText("Event Description"));
//        closeSoftKeyboard();
//        onView(withId(R.id.editTextDate)).perform(typeText("12/12/2024"));
//        onView(withId(R.id.dateField)).perform(typeText("10:00"));
//        onView(withId(R.id.number_of_people_event)).perform(typeText("50"));
//        closeSoftKeyboard();
//
//        onView(withId(R.id.genQR)).perform(click());
//
//        Thread.sleep(3000);  // Allow time for navigation
//
//        intended(hasComponent(ViewEvent.class.getName()));
//    }
//
//    /**
//     * Tests that clicking the image selector opens the image chooser for uploading an event poster.
//     * Expected outcome: Image chooser dialog should be displayed with appropriate options.
//     */
//    @Test
//    public void testImageSelector_OpensImageChooser() {
//        onView(withId(R.id.posterPicUpload)).perform(click());
//
//        onView(withText("Choose an image")).check(matches(isDisplayed()));
//    }
//}

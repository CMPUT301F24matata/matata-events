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

import static org.hamcrest.Matchers.not;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class AddEventActivityTest {

    @Rule
    public ActivityScenarioRule<TestAddEventActivity> activityRule =
            new ActivityScenarioRule<>(TestAddEventActivity.class);

    @Test
    public void testInitialUIState() {
        onView(withId(R.id.eventTitle)).check(matches(withText("")));
        onView(withId(R.id.editTextDate)).check(matches(withText("")));
        onView(withId(R.id.editTextTime)).check(matches(withText("")));
    }

    @Test
    public void testInputFields() {
        onView(withId(R.id.eventTitle)).perform(typeText("Test Event"), closeSoftKeyboard());
        onView(withId(R.id.desc_box)).perform(typeText("This is a test description."), closeSoftKeyboard());
        onView(withId(R.id.number_of_people_event)).perform(typeText("100"), closeSoftKeyboard());

        onView(withId(R.id.eventTitle)).check(matches(withText("Test Event")));
        onView(withId(R.id.desc_box)).check(matches(withText("This is a test description.")));
        onView(withId(R.id.number_of_people_event)).check(matches(withText("100")));
    }

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
        onView(withId(R.id.editTextDate))
                .check(matches(withText("")));
        onView(withId(R.id.editTextTime))
                .check(matches(withText("")));
        onView(withId(R.id.number_of_people_event)).check(matches(withText("")));
    }

    @Test
    public void testTimeAndDatePicker() {
        onView(withId(R.id.editTextDate)).perform(click());
        onView(withText("OK")).perform(click());
        onView(withId(R.id.editTextTime)).perform(click());
        onView(withText("OK")).perform(click());

        onView(withId(R.id.editTextDate))
                .check(matches(withHint("Select Date")));
        onView(withId(R.id.editTextTime))
                .check(matches(withHint("Select Time")));
    }

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
        onView(withId(R.id.editTextDate))
                .check(matches(withHint("Select Date")));
        onView(withId(R.id.editTextTime))
                .check(matches(withHint("Select Time")));
        onView(withId(R.id.number_of_people_event)).check(matches(withText("100")));
    }
}

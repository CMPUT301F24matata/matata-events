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

@RunWith(AndroidJUnit4.class)
public class FacilityActivityTest {

    @Rule
    public ActivityScenarioRule<TestFacilityActivity> activityRule =
            new ActivityScenarioRule<>(TestFacilityActivity.class);

    @Test
    public void testFacilityNameField() {
        onView(withId(R.id.facilityName))
                .perform(typeText("Test Facility"), closeSoftKeyboard())
                .check(matches(withText("Test Facility")));
    }

    @Test
    public void testFacilityAddressField() {
        onView(withId(R.id.facilityAddress))
                .perform(typeText("123 Test St"), closeSoftKeyboard())
                .check(matches(withText("123 Test St")));
    }

    @Test
    public void testFacilityCapacityField() {
        onView(withId(R.id.facilityCapacity))
                .perform(typeText("500"), closeSoftKeyboard())
                .check(matches(withText("500")));
    }

    @Test
    public void testFacilityContactField() {
        onView(withId(R.id.facilityContact))
                .perform(typeText("9876543210"), closeSoftKeyboard())
                .check(matches(withText("9876543210")));
    }

    @Test
    public void testFacilityEmailField() {
        onView(withId(R.id.facilityEmail))
                .perform(typeText("test@facility.com"), closeSoftKeyboard())
                .check(matches(withText("test@facility.com")));
    }

    @Test
    public void testFacilityOwnerField() {
        onView(withId(R.id.facilityOwner))
                .perform(typeText("John Doe"), closeSoftKeyboard())
                .check(matches(withText("John Doe")));
    }

    @Test
    public void testNotificationSwitch() {
        onView(withId(R.id.switch_notification)).perform(click()).check(matches(isChecked()));
        onView(withId(R.id.switch_notification)).perform(click()).check(matches(isNotChecked()));
    }

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

    @Test
    public void testClearAllButton() {
        onView(withId(R.id.facilityName)).perform(typeText("Test Facility"), closeSoftKeyboard());
        onView(withId(R.id.clearAllButton)).perform(scrollTo(), click());

        onView(withId(R.id.facilityName)).check(matches(withText("")));
    }
}

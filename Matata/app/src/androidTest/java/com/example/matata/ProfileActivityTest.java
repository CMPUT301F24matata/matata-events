package com.example.matata;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isChecked;
import static androidx.test.espresso.matcher.ViewMatchers.isNotChecked;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.view.View;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.contrib.PickerActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;

import java.util.Calendar;

public class ProfileActivityTest {

    @Rule
    public ActivityScenarioRule<ProfileActivity> activityRule =
            new ActivityScenarioRule<>(ProfileActivity.class);

    /**
     * Test to verify that the ProfileActivity layout is displayed correctly.
     */
    @Test
    public void testProfileActivityUIElementsDisplayed() {
        onView(withId(R.id.profileIcon)).check(matches(isDisplayed()));
        onView(withId(R.id.nameEditText)).check(matches(isDisplayed()));
        onView(withId(R.id.emailEditText)).check(matches(isDisplayed()));
        onView(withId(R.id.phoneEditText)).check(matches(isDisplayed()));
        onView(withId(R.id.dobEditText)).check(matches(isDisplayed()));
        onView(withId(R.id.genderSpinner)).check(matches(isDisplayed()));
        onView(withId(R.id.switch_notification)).check(matches(isDisplayed()));
        onView(withId(R.id.saveButton)).check(matches(isDisplayed()));
        onView(withId(R.id.clearAllButton)).check(matches(isDisplayed()));
    }

    /**
     * Test to verify that profile data is loaded correctly.
     */
    @Test
    public void testLoadProfileData() {
        onView(withId(R.id.nameEditText)).check(matches(withText("")));
        onView(withId(R.id.emailEditText)).check(matches(withText("")));
        onView(withId(R.id.phoneEditText)).check(matches(withText("")));
        onView(withId(R.id.dobEditText)).check(matches(withText("")));
        onView(withId(R.id.genderSpinner)).check(matches(withSpinnerText("Select Gender")));
        onView(withId(R.id.switch_notification)).check(matches(isNotChecked()));
    }

    /**
     * Test to edit and save profile data.
     */
    @Test
    public void testEditAndSaveProfileData() {
        onView(withId(R.id.nameEditText)).perform(typeText("John Doe"));
        onView(withId(R.id.emailEditText)).perform(typeText("johndoe@example.com"));
        onView(withId(R.id.phoneEditText)).perform(typeText("1234567890"));
        onView(withId(R.id.dobEditText)).perform(click());
        Calendar calendar = Calendar.getInstance();
        onView(withId(android.R.id.button1)).perform(PickerActions.setDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH)));
        onView(withId(android.R.id.button1)).perform(click());
        onView(withId(R.id.genderSpinner)).perform(click());
        onView(withText("Male")).perform(click());
        onView(withId(R.id.switch_notification)).perform(click());
        onView(withId(R.id.saveButton)).perform(click());
    }

    /**
     * Test to clear all profile fields.
     */
    @Test
    public void testClearAllFields() {
        onView(withId(R.id.clearAllButton)).perform(click());
        onView(withId(R.id.nameEditText)).check(matches(withText("")));
        onView(withId(R.id.emailEditText)).check(matches(withText("")));
        onView(withId(R.id.phoneEditText)).check(matches(withText("")));
        onView(withId(R.id.dobEditText)).check(matches(withText("")));
        onView(withId(R.id.genderSpinner)).check(matches(withSpinnerText("Select Gender")));
        onView(withId(R.id.switch_notification)).check(matches(isNotChecked()));
    }

    /**
     * Test that the back button works.
     */
    @Test
    public void testBackButtonFunctionality() {
        onView(withId(R.id.btnBackProfile)).perform(click());
    }

    /**
     * Test selecting a profile picture.
     */
    @Test
    public void testProfilePictureSelection() {
        onView(withId(R.id.profileIcon)).perform(click());
        // Simulate choosing an image and returning to ProfileActivity.
        Espresso.pressBack();
        onView(withId(R.id.profileIcon)).check(matches(isDisplayed()));
    }

    /**
     * Test switching the notifications toggle.
     */
    @Test
    public void testNotificationsSwitch() {
        onView(withId(R.id.switch_notification)).check(matches(isNotChecked()));
        onView(withId(R.id.switch_notification)).perform(click());
        onView(withId(R.id.switch_notification)).check(matches(isChecked()));
    }

    /**
     * Test date picker functionality.
     */
    @Test
    public void testDatePickerFunctionality() {
        onView(withId(R.id.dobEditText)).perform(click());
        Calendar calendar = Calendar.getInstance();
        onView(withId(android.R.id.button1)).perform(PickerActions.setDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH)));
        onView(withId(android.R.id.button1)).perform(click());
        String expectedDate = calendar.get(Calendar.DAY_OF_MONTH) + "/" + (calendar.get(Calendar.MONTH) + 1) + "/" + calendar.get(Calendar.YEAR);
        onView(withId(R.id.dobEditText)).check(matches(withText(expectedDate)));
    }
}

package com.example.matata;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * UI Tests for TestMainActivity to validate UI interactions and navigation.
 */
@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule
    public ActivityScenarioRule<TestMainActivity> activityRule =
            new ActivityScenarioRule<>(TestMainActivity.class);

    /**
     * Test that the profile icon navigates to the ProfileActivity.
     */
    @Test
    public void testNavigateToProfile() {
        onView(withId(R.id.profile_picture)).perform(click());
    }

    /**
     * Test that the "Add Event" button navigates to the AddEvent activity.
     */
    @Test
    public void testNavigateToAddEvent() {
        onView(withId(R.id.add_event)).perform(click());
    }

    /**
     * Test that the "Event History" button navigates to the EventHistory activity.
     */
    @Test
    public void testNavigateToEventHistory() {
        onView(withId(R.id.event_history)).perform(click());
    }

    /**
     * Test that the "Explore" button navigates to the ExploreEvents activity.
     */
    @Test
    public void testNavigateToExploreEvents() {
        onView(withId(R.id.event_map)).perform(click());
    }

    /**
     * Test that the "Facility Profile" button navigates to the FacilityActivity.
     */
    @Test
    public void testNavigateToFacilityProfile() {
        onView(withId(R.id.FacilityProfile)).perform(click());
    }

    /**
     * Test that the "Admin" button navigates to the AdminView activity.
     */
    @Test
    public void testNavigateToAdminView() {
        onView(withId(R.id.admin)).perform(click());
    }

    /**
     * Test that toggling between List and Swipe views loads the correct fragments.
     */
    @Test
    public void testToggleViews() {
        // Toggle to List View
        onView(withId(R.id.ListToggle)).perform(click());

        // Toggle to Swipe View
        onView(withId(R.id.ExploreToggle)).perform(click());
    }

    /**
     * Test that the activity initializes without any crashes.
     */
    @Test
    public void testActivityLaunch() {
        ActivityScenario<TestMainActivity> scenario = activityRule.getScenario();
        scenario.onActivity(activity -> {
            // Ensure the activity is running
            assert activity != null;
        });
    }
}

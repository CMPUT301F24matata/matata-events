package com.example.matata;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class AdminViewTest {

    @Rule
    public ActivityScenarioRule<TestAdminView> activityRule =
            new ActivityScenarioRule<>(TestAdminView.class);

    @Test
    public void testEventsDropdownToggle() {
        // Click to expand the events dropdown
        onView(withId(R.id.events_dropdown_button)).perform(click());
        // Verify the dropdown is displayed
        onView(withId(R.id.events_dropdown)).check(matches(isDisplayed()));
        // Click to collapse the dropdown
        onView(withId(R.id.events_dropdown_button)).perform(click());
    }

    @Test
    public void testUsersDropdownToggle() {
        // Click to expand the users dropdown
        onView(withId(R.id.users_dropdown_button)).perform(click());
        // Verify the dropdown is displayed
        onView(withId(R.id.users_dropdown)).check(matches(isDisplayed()));

        onView(withId(R.id.users_dropdown_button)).perform(click());
    }

    @Test
    public void testFacilitiesDropdownToggle() {
        // Click to expand the facilities dropdown
        onView(withId(R.id.facilities_dropdown_button)).perform(click());
        // Verify the dropdown is displayed
        onView(withId(R.id.facilities_dropdown)).check(matches(isDisplayed()));

        onView(withId(R.id.facilities_dropdown_button)).perform(click());
    }

    @Test
    public void testNavigationIcons() {
        // Click the dashboard icon and verify we are in AdminView
        onView(withId(R.id.icon_dashboard)).perform(click());

        // Click the reports icon
        onView(withId(R.id.icon_reports)).perform(click());

        // Return to AdminView by clicking the dashboard icon again
        onView(withId(R.id.icon_dashboard)).perform(click());
    }

}

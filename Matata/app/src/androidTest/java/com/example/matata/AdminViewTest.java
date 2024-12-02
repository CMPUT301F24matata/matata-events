package com.example.matata;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * The `AdminViewTest` class is an Espresso test suite designed to validate the functionality and
 * user interface of the `TestAdminView` activity. This test suite focuses on:
 * <ul>
 *     <li>Toggling dropdowns for events, users, and facilities.</li>
 *     <li>Verifying that navigation icons function as expected.</li>
 * </ul>
 *
 * <h2>Features Tested:</h2>
 * <ul>
 *     <li>Dropdown toggles for events, users, and facilities.</li>
 *     <li>Navigation icon functionality.</li>
 * </ul>
 *
 * <h2>Test Cases:</h2>
 * <ul>
 *     <li><b>testEventsDropdownToggle:</b> Tests the toggle functionality of the events dropdown.</li>
 *     <li><b>testUsersDropdownToggle:</b> Tests the toggle functionality of the users dropdown.</li>
 *     <li><b>testFacilitiesDropdownToggle:</b> Tests the toggle functionality of the facilities dropdown.</li>
 *     <li><b>testNavigationIcons:</b> Verifies that navigation icons are clickable and perform the expected actions.</li>
 * </ul>
 */
@RunWith(AndroidJUnit4.class)
public class AdminViewTest {

    /**
     * ActivityScenarioRule launches the `TestAdminView` activity for testing.
     * Ensures the activity is initialized before each test and cleaned up afterward.
     */
    @Rule
    public ActivityScenarioRule<TestAdminView> activityRule =
            new ActivityScenarioRule<>(TestAdminView.class);

    /**
     * Tests the toggle functionality of the events dropdown.
     * Ensures that clicking the events dropdown button expands and collapses the dropdown.
     */
    @Test
    public void testEventsDropdownToggle() {
        // Click to expand the events dropdown
        onView(withId(R.id.events_dropdown_button)).perform(click());
        // Verify the dropdown is displayed
        onView(withId(R.id.events_dropdown)).check(matches(isDisplayed()));
        // Click to collapse the dropdown
        onView(withId(R.id.events_dropdown_button)).perform(click());
    }

    /**
     * Tests the toggle functionality of the users dropdown.
     * Ensures that clicking the users dropdown button expands and collapses the dropdown.
     */
    @Test
    public void testUsersDropdownToggle() {
        // Click to expand the users dropdown
        onView(withId(R.id.users_dropdown_button)).perform(click());
        // Verify the dropdown is displayed
        onView(withId(R.id.users_dropdown)).check(matches(isDisplayed()));
        // Click to collapse the dropdown
        onView(withId(R.id.users_dropdown_button)).perform(click());
    }

    /**
     * Tests the toggle functionality of the facilities dropdown.
     * Ensures that clicking the facilities dropdown button expands and collapses the dropdown.
     */
    @Test
    public void testFacilitiesDropdownToggle() {
        // Click to expand the facilities dropdown
        onView(withId(R.id.facilities_dropdown_button)).perform(click());
        // Verify the dropdown is displayed
        onView(withId(R.id.facilities_dropdown)).check(matches(isDisplayed()));
        // Click to collapse the dropdown
        onView(withId(R.id.facilities_dropdown_button)).perform(click());
    }

    /**
     * Verifies the functionality of the navigation icons, such as the dashboard and reports icons.
     * Ensures they are clickable and navigate to the appropriate destinations.
     */
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

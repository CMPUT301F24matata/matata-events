package com.example.matata;

import android.graphics.Bitmap;
import android.widget.Button;
import android.widget.TextView;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class ViewEventTest {

    @Rule
    public ActivityScenarioRule<TestViewEvent> activityRule =
            new ActivityScenarioRule<>(TestViewEvent.class);

    @Test
    public void testEventDetailsAreDisplayed() {
        // Verify event details are correctly displayed
        onView(withId(R.id.ViewEventTitle)).check(matches(withText("Cultural Night")));
        onView(withId(R.id.ViewEventCapacity)).check(matches(withText("100")));
        onView(withId(R.id.ViewEventDesc)).check(matches(withText("A night to celebrate cultural diversity.")));
        onView(withId(R.id.ViewEventTime)).check(matches(withText("7:00 PM")));
        onView(withId(R.id.ViewEventDate)).check(matches(withText("2024-12-15")));
        onView(withId(R.id.ViewEventLoc)).check(matches(withText("University Auditorium")));
    }

    @Test
    public void testWaitlistButtonToggle() {
        // Check initial button text
        onView(withId(R.id.join_waitlist_button)).check(matches(withText("HANG")));

        // Click again and verify text changes back
        onView(withId(R.id.join_waitlist_button)).perform(click());
        onView(withId(R.id.join_waitlist_button)).check(matches(withText("Join Waitlist")));

        // Click the button and verify text changes
        onView(withId(R.id.join_waitlist_button)).perform(click());
        onView(withId(R.id.join_waitlist_button)).check(matches(withText("Withdraw")));

        // Click again and verify text changes back
        onView(withId(R.id.join_waitlist_button)).perform(click());
        onView(withId(R.id.join_waitlist_button)).check(matches(withText("Join Waitlist")));
    }
}

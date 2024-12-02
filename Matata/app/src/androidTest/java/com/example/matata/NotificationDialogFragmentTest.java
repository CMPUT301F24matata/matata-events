package com.example.matata;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;

import android.os.Bundle;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class NotificationDialogFragmentTest {

    @Test
    public void testNotificationDialog() {
        // Launch the DialogFragment with arguments
        Bundle args = new Bundle();
        args.putString("admin", "false");
        args.putString("uid", "12345");

        FragmentScenario<NotificationDialogFragment> scenario = FragmentScenario.launchInContainer(
                NotificationDialogFragment.class, args);

        // Verify the dialog title is displayed
        onView(withText("Notification Manager")).check(matches(isDisplayed()));

        // Verify the spinner is displayed
        onView(withId(R.id.group_spinner)).check(matches(isDisplayed()));

        // Enter text in the title EditText
        onView(withId(R.id.title_EditText)).perform(typeText("Test Title"));

        // Enter text in the message EditText
        onView(withId(R.id.message_EditText)).perform(typeText("Test Message"));

        // Click the send notification button
        onView(withId(R.id.send_notification_button)).perform(click());

    }
}
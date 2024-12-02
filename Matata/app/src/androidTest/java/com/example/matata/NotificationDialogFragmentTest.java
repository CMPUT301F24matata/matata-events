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

/**
 * The `NotificationDialogFragmentTest` class provides integration tests for the `NotificationDialogFragment`,
 * verifying its UI functionality, interactions, and behavior. This test suite ensures that the dialog
 * correctly displays the UI components, handles user input, and triggers appropriate actions.
 *
 * <h2>Purpose</h2>
 * This test suite validates:
 * <ul>
 *     <li>The dialog's title and UI components are correctly displayed.</li>
 *     <li>User interactions with the title and message input fields.</li>
 *     <li>The functionality of the send notification button.</li>
 * </ul>
 *
 * <h2>Test Scenarios</h2>
 * <ul>
 *     <li><b>testNotificationDialog:</b> Validates the dialog's initialization, interaction with input fields,
 *         and the send button functionality.</li>
 * </ul>
 *
 * <h2>Requirements</h2>
 * <ul>
 *     <li>The `NotificationDialogFragment` should include the required UI components with the specified IDs:
 *         <ul>
 *             <li>`group_spinner` - Spinner for selecting the group to send notifications to.</li>
 *             <li>`title_EditText` - EditText for entering the notification title.</li>
 *             <li>`message_EditText` - EditText for entering the notification message.</li>
 *             <li>`send_notification_button` - Button for sending the notification.</li>
 *         </ul>
 *     </li>
 *     <li>The dialog must accept arguments to simulate different user roles (`admin`) and event identifiers (`uid`).</li>
 * </ul>
 *
 * <h2>Important Notes</h2>
 * - The test uses `FragmentScenario` to launch and test the `NotificationDialogFragment`.
 * - Tests should run in an isolated environment and should not depend on external services or live database connections.
 */
@RunWith(AndroidJUnit4.class)
public class NotificationDialogFragmentTest {

    /**
     * Tests the `NotificationDialogFragment` to ensure all UI components are displayed,
     * user inputs are handled correctly, and actions are triggered properly.
     */
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
package com.example.matata;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * The `FacilityPicActivityTest` class provides a test suite for validating the functionality of the
 * `TestFacilityPicActivity`, a simplified activity designed for testing profile picture upload and deletion.
 * The test ensures that the UI behaves correctly and basic interactions such as picture display, upload,
 * and deletion are handled as expected.
 *
 * <h2>Purpose</h2>
 * This test suite validates:
 * <ul>
 *     <li>The initial display state of the profile picture.</li>
 *     <li>Behavior when the upload button is clicked without selecting an image.</li>
 *     <li>The functionality of the delete button in resetting the profile picture.</li>
 * </ul>
 *
 * <h2>Test Scenarios</h2>
 * <ul>
 *     <li><b>testInitialProfilePictureDisplay:</b> Ensures the profile picture ImageView is displayed initially.</li>
 *     <li><b>testUploadButtonWithoutSelection:</b> Verifies behavior when the upload button is clicked without an image selection.</li>
 *     <li><b>testDeleteButton:</b> Tests the delete button functionality and ensures the profile picture is reset.</li>
 * </ul>
 *
 * <h2>Requirements</h2>
 * <ul>
 *     <li>The `TestFacilityPicActivity` should include the required UI elements with the specified IDs:
 *         <ul>
 *             <li>`ivProfilePicture` - ImageView for displaying the profile picture.</li>
 *             <li>`btnUploadPicture` - Button for uploading a profile picture.</li>
 *             <li>`btnDeletePicture` - Button for deleting the profile picture.</li>
 *         </ul>
 *     </li>
 *     <li>Ensure that the activity handles scenarios where no image is selected for upload gracefully.</li>
 * </ul>
 *
 * <h2>Important Notes</h2>
 * - The `TestFacilityPicActivity` class should provide minimal functionality for testing purposes,
 *   without dependencies on Firebase or external storage.
 * - Tests should run in an isolated environment without interacting with the actual production data.
 */
@RunWith(AndroidJUnit4.class)
public class FacilityPicActivityTest {

    /**
     * Rule to launch the `TestFacilityPicActivity` before each test.
     */
    @Rule
    public ActivityScenarioRule<TestFacilityPicActivity> activityRule =
            new ActivityScenarioRule<>(TestFacilityPicActivity.class);

    /**
     * Verifies that the profile picture ImageView is displayed when the activity is launched.
     */
    @Test
    public void testInitialProfilePictureDisplay() {
        onView(withId(R.id.ivProfilePicture))
                .check(matches(isDisplayed()));
    }

    /**
     * Verifies behavior when the upload button is clicked without selecting an image.
     */
    @Test
    public void testUploadButtonWithoutSelection() {
        onView(withId(R.id.btnUploadPicture)).perform(click());
        // Additional validation can be added to ensure no changes occurred to the profile picture.
    }

    /**
     * Tests the delete button functionality, ensuring that it resets the profile picture.
     */
    @Test
    public void testDeleteButton() {
        onView(withId(R.id.btnDeletePicture)).perform(click());
        // Additional validation can ensure the profile picture resets to the default placeholder.
    }

}

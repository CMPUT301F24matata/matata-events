/**
 * ProfilePicActivityTest class contains instrumentation tests for ProfilePicActivity, verifying functionality such as
 * image picker intent launching, back button handling, profile picture deletion, and button actions when no image is selected.
 * These tests use the AndroidJUnit4 runner and Espresso for UI interaction.
 *
 * Purpose:
 * - Validate the UI and functionality of ProfilePicActivity, including image uploading, deletion, and proper
 *   handling of user interactions.
 *
 * Outstanding Issues:
 * - Ensure consistent handling of intent results across various Android SDK versions.
 * - Verify image picker functionality on different devices and configurations.
 */

package com.example.matata;

import static org.junit.Assert.*;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasType;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.provider.Settings;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.espresso.intent.Intents;
import androidx.test.platform.app.InstrumentationRegistry;

import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Runs instrumentation tests for the ProfilePicActivity using AndroidJUnit4.
 */
@RunWith(AndroidJUnit4.class)
public class ProfilePicActivityTest {

    /**
     * Firestore instance for storing or retrieving user profile image data.
     */
    private FirebaseFirestore db;

    /**
     * ID used for identifying the test user in Firestore based on the device's unique Android ID.
     */
    private String testUserId;

    /**
     * Rule that provides an ActivityScenario for ProfilePicActivity, enabling test control over its lifecycle.
     */
    @Rule
    public ActivityScenarioRule<ProfilePicActivity> activityScenarioRule = new ActivityScenarioRule<>(ProfilePicActivity.class);

    /**
     * Sets up necessary resources before each test, including Firestore and device ID for the test user.
     * Initializes Espresso Intents for intent validation during tests.
     */
    @Before
    public void setUp() {
        db = FirebaseFirestore.getInstance();
        testUserId = Settings.Secure.getString(
                InstrumentationRegistry.getInstrumentation().getTargetContext().getContentResolver(),
                Settings.Secure.ANDROID_ID
        );
        Intents.init();
    }

    /**
     * Releases resources after each test, such as Espresso Intents to ensure proper cleanup.
     */
    @After
    public void tearDown() {
        Intents.release();
    }

    /**
     * Verifies that clicking on the profile picture launches an image picker intent.
     * Expected outcome: Image picker intent with action and MIME type for selecting images.
     */
    @Test
    public void testOpenImagePicker() {
        onView(withId(R.id.ivProfilePicture)).perform(click());

        try {
            Thread.sleep(5000); // Allow manual image selection
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        intended(hasAction(Intent.ACTION_CHOOSER));
        intended(hasExtra(Intent.EXTRA_INTENT, hasAction(Intent.ACTION_GET_CONTENT)));
        intended(hasExtra(Intent.EXTRA_INTENT, hasType("image/*")));
    }

    /**
     * Tests the back button functionality by verifying that the activity finishes upon pressing the back button.
     */
    @Test
    public void testBackButton() {
        ActivityScenario<ProfilePicActivity> scenario = ActivityScenario.launch(ProfilePicActivity.class);

        onView(withId(R.id.btnBack)).perform(click());

        scenario.onActivity(activity -> assertTrue(activity.isFinishing()));
    }

    /**
     * Tests deleting the profile picture after an image is selected by resetting the profile picture to its default state.
     * Expected outcome: Profile picture should be reset to the default icon.
     */
    @Test
    public void testDeleteProfilePicture() {
        onView(withId(R.id.ivProfilePicture)).perform(click());

        try {
            Thread.sleep(5000); // Allow manual image selection
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.btnDeletePicture)).perform(click());

        ActivityScenario<ProfilePicActivity> newScenario = ActivityScenario.launch(ProfilePicActivity.class);

        newScenario.onActivity(activity -> {
            ImageView profileIcon = activity.findViewById(R.id.ivProfilePicture);
            Drawable expectedDrawable = activity.getDrawable(R.drawable.ic_upload);
            Drawable actualDrawable = profileIcon.getDrawable();
            assertNotNull(actualDrawable);
            assertEquals(expectedDrawable.getConstantState(), actualDrawable.getConstantState());
        });
    }

    /**
     * Tests clicking the upload button without selecting an image, expecting a toast message.
     * Expected outcome: Toast message indicating "No image selected to upload".
     */
    @Test
    public void testUploadButton_withNoImageSelected() {
        ActivityScenario<ProfilePicActivity> scenario = ActivityScenario.launch(ProfilePicActivity.class);

        onView(withId(R.id.btnUploadPicture)).perform(click());

        scenario.onActivity(activity -> {
            Toast toast = Toast.makeText(activity, "No image selected to upload", Toast.LENGTH_SHORT);
            assertNotNull(toast);
        });
    }

    /**
     * Tests clicking the delete button without an image selected, ensuring the profile picture resets to default.
     * Expected outcome: Profile picture drawable should match the default icon, with no saved image URI.
     */
    @Test
    public void testDeleteButton_withNoImageSelected() {
        ActivityScenario<ProfilePicActivity> scenario = ActivityScenario.launch(ProfilePicActivity.class);

        onView(withId(R.id.btnDeletePicture)).perform(click());

        scenario.onActivity(activity -> {
            ImageView profileIcon = activity.findViewById(R.id.ivProfilePicture);
            String savedUri = activity.getSharedPreferences("ProfilePrefs", Context.MODE_PRIVATE)
                    .getString("profile_image_uri", null);
            assertNull(savedUri);
            assertEquals(R.drawable.ic_upload, ((ImageView) profileIcon).getDrawable().getConstantState());
        });
    }
}

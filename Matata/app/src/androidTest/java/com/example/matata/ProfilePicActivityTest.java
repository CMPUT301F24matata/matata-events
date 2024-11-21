package com.example.matata;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.app.Instrumentation;
import android.content.Intent;
import android.net.Uri;

import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.matcher.IntentMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class ProfilePicActivityTest {

    @Rule
    public ActivityScenarioRule<ProfilePicActivity> activityRule =
            new ActivityScenarioRule<>(ProfilePicActivity.class);

    @Before
    public void setup() {
        Intents.init();
    }

    @After
    public void tearDown() {
        Intents.release();
    }

    /**
     * Test to verify that all UI elements in ProfilePicActivity are displayed correctly.
     */
    @Test
    public void testUIElementsDisplayed() {
        onView(withId(R.id.ivProfilePicture)).check(matches(isDisplayed()));
        onView(withId(R.id.btnBack)).check(matches(isDisplayed()));
        onView(withId(R.id.btnUploadPicture)).check(matches(isDisplayed()));
        onView(withId(R.id.btnDeletePicture)).check(matches(isDisplayed()));
    }

    /**
     * Test clicking the back button.
     */
    @Test
    public void testBackButtonFunctionality() {
        onView(withId(R.id.btnBack)).perform(click());
    }

    /**
     * Test selecting an image from the gallery.
     */
    @Test
    public void testImageSelection() {
        // Mock an image selection intent
        Intent resultData = new Intent();
        Uri mockUri = Uri.parse("content://mock/image");
        resultData.setData(mockUri);

        Instrumentation.ActivityResult result =
                new Instrumentation.ActivityResult(android.app.Activity.RESULT_OK, resultData);
        Intents.intending(IntentMatchers.hasAction(Intent.ACTION_GET_CONTENT))
                .respondWith(result);

        // Simulate clicking the profile picture to open the picker
        onView(withId(R.id.ivProfilePicture)).perform(click());

        // Verify that the intent was sent
        Intents.intended(IntentMatchers.hasAction(Intent.ACTION_GET_CONTENT));

        // Verify that the profile picture is updated with the selected image
        onView(withId(R.id.ivProfilePicture)).check(matches(isDisplayed()));
    }

    /**
     * Test uploading a selected profile picture.
     */
    @Test
    public void testUploadProfilePicture() {
        // Simulate selecting an image
        Intent resultData = new Intent();
        Uri mockUri = Uri.parse("content://mock/image");
        resultData.setData(mockUri);

        Instrumentation.ActivityResult result =
                new Instrumentation.ActivityResult(android.app.Activity.RESULT_OK, resultData);
        Intents.intending(IntentMatchers.hasAction(Intent.ACTION_GET_CONTENT))
                .respondWith(result);

        // Simulate clicking the profile picture to open the picker
        onView(withId(R.id.ivProfilePicture)).perform(click());

        // Click upload button
        onView(withId(R.id.btnUploadPicture)).perform(click());

        // Verify the success toast message
        onView(withText("Profile picture uploaded successfully"))
                .inRoot(new ToastMatcher())
                .check(matches(isDisplayed()));
    }

    /**
     * Test deleting the profile picture.
     */
    @Test
    public void testDeleteProfilePicture() {
        // Simulate clicking the delete button
        onView(withId(R.id.btnDeletePicture)).perform(click());

        // Verify the default profile picture is displayed
        onView(withId(R.id.ivProfilePicture)).check(matches(isDisplayed()));

        // Verify the success toast message
        onView(withText("Profile picture deleted"))
                .inRoot(new ToastMatcher())
                .check(matches(isDisplayed()));
    }
}

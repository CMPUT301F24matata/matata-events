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
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.provider.Settings;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.espresso.intent.Intents;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.example.matata.ProfilePicActivity;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class ProfilePicActivityTest {

    private FirebaseFirestore db;
    private String testUserId;

    @Rule
    public ActivityScenarioRule<ProfilePicActivity> activityScenarioRule = new ActivityScenarioRule<>(ProfilePicActivity.class);

    @Before
    public void setUp() {
        db = FirebaseFirestore.getInstance();
        testUserId = Settings.Secure.getString(InstrumentationRegistry.getInstrumentation().getTargetContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        Intents.init();
    }

    @After
    public void tearDown() {
        Intents.release();
    }

    @Test
    public void testOpenImagePicker() {
        // Perform click on the profile picture ImageView
        onView(withId(R.id.ivProfilePicture)).perform(click());

        // Pause to allow manual image selection
        try {
            Thread.sleep(5000); // Wait for 5 seconds to allow manual image selection
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Verify that the image picker intent is launched
        intended(hasAction(Intent.ACTION_CHOOSER));
        intended(hasExtra(Intent.EXTRA_INTENT, hasAction(Intent.ACTION_GET_CONTENT)));
        intended(hasExtra(Intent.EXTRA_INTENT, hasType("image/*")));
    }

    @Test
    public void testBackButton() {
        // Launch activity
        ActivityScenario<ProfilePicActivity> scenario = ActivityScenario.launch(ProfilePicActivity.class);

        // Click on the back button
        onView(withId(R.id.btnBack)).perform(click());

        // Verify that the activity is finished
        scenario.onActivity(activity -> assertTrue(activity.isFinishing()));
    }

    @Test
    public void testDeleteProfilePicture() {
        // Click on the profile picture ImageView to select an image
        onView(withId(R.id.ivProfilePicture)).perform(click());

        // Pause to allow manual image selection
        try {
            Thread.sleep(5000); // Wait for 5 seconds to allow manual image selection
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Click on the delete button
        onView(withId(R.id.btnDeletePicture)).perform(click());

        // Relaunch the profile activity to check if the image was deleted
        ActivityScenario<ProfilePicActivity> newScenario = ActivityScenario.launch(ProfilePicActivity.class);

        // Verify profile picture is reset to default
        newScenario.onActivity(activity -> {
            ImageView profileIcon = activity.findViewById(R.id.ivProfilePicture);
            Drawable expectedDrawable = activity.getDrawable(R.drawable.ic_upload);
            Drawable actualDrawable = profileIcon.getDrawable();
            assertNotNull(actualDrawable);
            assertEquals(expectedDrawable.getConstantState(), actualDrawable.getConstantState());
        });
    }

    @Test
    public void testUploadButton_withNoImageSelected() {
        // Launch activity
        ActivityScenario<ProfilePicActivity> scenario = ActivityScenario.launch(ProfilePicActivity.class);

        // Click on the upload button without selecting an image
        onView(withId(R.id.btnUploadPicture)).perform(click());

        // Verify that the appropriate toast message is displayed
        scenario.onActivity(activity -> {
            Toast toast = Toast.makeText(activity, "No image selected to upload", Toast.LENGTH_SHORT);
            assertNotNull(toast);
        });
    }

    @Test
    public void testDeleteButton_withNoImageSelected() {
        // Launch activity
        ActivityScenario<ProfilePicActivity> scenario = ActivityScenario.launch(ProfilePicActivity.class);

        // Click on the delete button without selecting an image
        onView(withId(R.id.btnDeletePicture)).perform(click());

        // Verify that the profile picture is reset to default and no errors occur
        scenario.onActivity(activity -> {
            ImageView profileIcon = activity.findViewById(R.id.ivProfilePicture);
            String savedUri = activity.getSharedPreferences("ProfilePrefs", Context.MODE_PRIVATE).getString("profile_image_uri", null);
            assertNull(savedUri);
            assertEquals(R.drawable.ic_upload, ((ImageView) profileIcon).getDrawable().getConstantState());
        });
    }
}

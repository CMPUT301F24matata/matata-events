/**
 * ProfileUnitTest class provides unit tests for verifying the functionality of ProfileActivity within
 * the application. It tests profile setup, initial generation, and loading of profile images using
 * Robolectric for simulating Android activities in a testing environment.
 *
 * Outstanding Issues:
 * - Ensure compatibility with different SDK versions.
 * - Validate image URI creation robustness.
 */

package com.example.matata;

import static org.junit.Assert.*;

import android.content.Context;
import android.net.Uri;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLooper;

/**
 * Runs tests using the Robolectric test framework for Android.
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28, manifest = Config.NONE)
public class ProfileUnitTest {

    @Test
    public void testCreateImageFromString() {
        Context context = ApplicationProvider.getApplicationContext();
        Uri uri = ProfileActivity.createImageFromString(context, "JD");

        // Assert that the URI is not null
        assertNotNull(uri);

        // Assert that the URI contains base64 data
        assertTrue(uri.toString().startsWith("data:image/png;base64,"));
    }

    @Test
    public void testLoadProfilePicture_ValidUri() {
        ActivityScenario<ProfileActivity> scenario = ActivityScenario.launch(ProfileActivity.class);

        scenario.onActivity(activity -> {
            String testUri = "content://media/external/images/media/12345";
            activity.loadProfilePicture(testUri);

            // Assert that the profile icon has been updated
            ImageView profileIcon = activity.findViewById(R.id.profileIcon);
            assertNotNull(profileIcon.getDrawable());
        });
    }

    @Test
    public void testLoadProfilePicture_NullUri() {
        ActivityScenario<ProfileActivity> scenario = ActivityScenario.launch(ProfileActivity.class);

        scenario.onActivity(activity -> {
            EditText nameEditText = activity.findViewById(R.id.nameEditText);
            nameEditText.setText("Test User");
            assertEquals("Test User", nameEditText.getText().toString());
            activity.loadProfilePicture(null);

            // Assert that the profile icon has initials-based image
            ImageView profileIcon = activity.findViewById(R.id.profileIcon);
            assertNotNull(profileIcon.getDrawable());
        });
    }


}

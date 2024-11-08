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

    /**
     * Mock instance of ProfileActivity to simulate profile setup and testing.
     */
    private ProfileActivity mockprofile;

    /**
     * Controller to manage the lifecycle of the mock ProfileActivity.
     */
    private ActivityController<ProfileActivity> controller;

    /**
     * Sets up the ProfileActivity mock and initializes fields for testing.
     */
    @Before
    public void setUp() {
        controller = Robolectric.buildActivity(ProfileActivity.class);
        mockprofile = controller.get();

        mockprofile.nameEditText = new EditText(ApplicationProvider.getApplicationContext());
        mockprofile.phoneEditText = new EditText(ApplicationProvider.getApplicationContext());
        mockprofile.emailEditText = new EditText(ApplicationProvider.getApplicationContext());
        mockprofile.notifications = new Switch(ApplicationProvider.getApplicationContext());
        mockprofile.isOrganizer = new Switch(ApplicationProvider.getApplicationContext());
        mockprofile.profileIcon = new ImageView(ApplicationProvider.getApplicationContext());
    }

    /**
     * Tests the getUserInitials method by setting a name and verifying
     * if initials are correctly generated.
     *
     * Expected output: Initials derived from "Test Name" should be "TN".
     */
    @Test
    public void testInitials() {
        System.out.println(mockprofile.nameEditText.getText().toString());
        mockprofile.nameEditText.setText("Test Name");
        String initials = mockprofile.getUserInitials(mockprofile.nameEditText.getText().toString());
        assertEquals("TN", initials);
    }

    /**
     * Tests the createImageFromString method by generating a bitmap
     * from initials and checking if a valid URI is produced.
     *
     * @see ProfileActivity#createImageFromString(Context, String)
     * Expected outcome: URI starts with "data:image/png;base64,".
     */
    @Test
    public void testGenerateInitialsBitmap() {
        String initials = "TN";
        Context context = ApplicationProvider.getApplicationContext();
        Uri generatedUri = mockprofile.createImageFromString(context, initials);
        assertNotNull(generatedUri);
        String uriString = generatedUri.toString();
        assertTrue(uriString.startsWith("data:image/png;base64,"));
    }

    /**
     * Tests loading a profile picture with a null URI to ensure no image is loaded
     * if the URI is null.
     *
     * Expected outcome: profileIcon drawable should remain null.
     */
    @Test
    public void testLoadProfilePictureWithNullUri() {
        Uri mockUri = null;
        mockprofile.loadProfilePicture(String.valueOf(mockUri));
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();
        assertNull(mockprofile.profileIcon.getDrawable());
    }
}

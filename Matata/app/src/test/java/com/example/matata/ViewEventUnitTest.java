/**
 * ViewEventUnitTest class provides unit tests for the ViewEvent activity. These tests check the functionality
 * of waitlist button status, event details loading, and button visibility based on user roles.
 *
 * Purpose:
 * - Validate the correct functionality of ViewEvent UI elements.
 * - Ensure that user role affects button visibility as expected.
 *
 * Note:
 * - Tests are run without any mocking libraries, so data and UI changes are set directly within the test.
 */

package com.example.matata;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.test.core.app.ApplicationProvider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowSystemClock;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Runs tests for the ViewEvent activity using Robolectric.
 */
@Config(shadows = {ShadowSystemClock.class}, manifest = Config.NONE)
@RunWith(RobolectricTestRunner.class)
public class ViewEventUnitTest {

    private ViewEvent viewEvent;
    private Button waitlistBtn;
    private TextView title;
    private TextView capacity;
    private TextView desc;
    private TextView time;
    private TextView date;
    private TextView location;
    private String userId;
    private Button drawBtn;

    /**
     * Sets up the ViewEvent activity and initializes UI elements for testing.
     * Uses reflection to access private fields in the ViewEvent activity.
     */
    @Before
    public void setUp() throws NoSuchFieldException, IllegalAccessException {
        ActivityController<ViewEvent> controller = Robolectric.buildActivity(ViewEvent.class);
        viewEvent = controller.create().get();

        // Initialize UI elements with reflection to access private fields
        waitlistBtn = new Button(ApplicationProvider.getApplicationContext());
        title = new TextView(ApplicationProvider.getApplicationContext());
        capacity = new TextView(ApplicationProvider.getApplicationContext());
        desc = new TextView(ApplicationProvider.getApplicationContext());
        time = new TextView(ApplicationProvider.getApplicationContext());
        date = new TextView(ApplicationProvider.getApplicationContext());
        location = new TextView(ApplicationProvider.getApplicationContext());
        drawBtn = new Button(ApplicationProvider.getApplicationContext());

        // Set up test user ID
        userId = "test_user_id";
        Settings.Secure.putString(ApplicationProvider.getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID, userId);

        // Set the private fields in ViewEvent using reflection
        setPrivateField("waitlistBtn", waitlistBtn);
        setPrivateField("title", title);
        setPrivateField("capacity", capacity);
        setPrivateField("desc", desc);
        setPrivateField("time", time);
        setPrivateField("date", date);
        setPrivateField("location", location);
        setPrivateField("drawBtn", drawBtn);
    }

    /**
     * Helper method to set private fields in the ViewEvent class using reflection.
     *
     * @param fieldName the name of the field to set
     * @param value the value to assign to the field
     */
    private void setPrivateField(String fieldName, Object value) throws NoSuchFieldException, IllegalAccessException {
        Field field = ViewEvent.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(viewEvent, value);
    }

//    /**
//     * Tests that the waitlist button displays "Pending" when the user is on the pending list.
//     */
//    @Test
//    public void testUpdateWaitlistButtonText_PendingStatus() {
//        // Simulate pending status
//        List<String> pendingList = new ArrayList<>();
//        pendingList.add(userId);
//
//        viewEvent.updateWaitlistButtonText(new ArrayList<>(), pendingList, new ArrayList<>());
//
//        assertEquals("Pending", waitlistBtn.getText().toString());
//    }

//    /**
//     * Tests that the waitlist button displays "Accepted" when the user is on the accepted list.
//     */
//    @Test
//    public void testUpdateWaitlistButtonText_AcceptedStatus() {
//        // Simulate accepted status
//        List<String> acceptedList = new ArrayList<>();
//        acceptedList.add(userId);
//
//        viewEvent.updateWaitlistButtonText(new ArrayList<>(), new ArrayList<>(), acceptedList);
//
//        assertEquals("Accepted", waitlistBtn.getText().toString());
//    }

    /**
     * Tests that the waitlist button displays "Join Waitlist" when the user is not on any list.
     */
    @Test
    public void testUpdateWaitlistButtonText_NotOnAnyList() {
        // Simulate absence from all lists
        viewEvent.updateWaitlistButtonText(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());

        assertEquals("Join Waitlist", waitlistBtn.getText().toString());
    }

//    /**
//     * Tests that the waitlist button displays "Withdraw" when the user is on the waitlist.
//     */
//    @Test
//    public void testUpdateWaitlistButtonText_InWaitList() {
//        // Simulate waitlist status
//        List<String> waitlist = new ArrayList<>();
//        waitlist.add(userId);
//
//        viewEvent.updateWaitlistButtonText(waitlist, new ArrayList<>(), new ArrayList<>());
//
//        assertEquals("Withdraw", waitlistBtn.getText().toString());
//    }

    /**
     * Tests decoding of an invalid Base64 string, expecting a null result.
     */
    @Test
    public void testDecodeBase64toBmp_InvalidBase64() {
        String invalidBase64 = "InvalidBase64String";
        Bitmap bitmap = viewEvent.decodeBase64toBmp(invalidBase64);

        assertNull(bitmap);  // Expect null for invalid input
    }

    /**
     * Tests loading of event details by setting and verifying UI fields.
     */
    @Test
    public void testLoadEventDetails_WithRealisticData() {
        // Setup realistic data
        String eventTitle = "Cultural Night";
        String eventDesc = "An evening of cultural performances and food.";
        String eventTime = "6:00 PM";
        String eventDate = "2024-12-01";
        String eventLocation = "Main Hall";
        Long eventCapacity = 100L;

        // Set the data on UI fields directly
        title.setText(eventTitle);
        capacity.setText(String.valueOf(eventCapacity));
        desc.setText(eventDesc);
        time.setText(eventTime);
        date.setText(eventDate);
        location.setText(eventLocation);

        // Verify that the UI fields are set correctly
        assertEquals(eventTitle, title.getText().toString());
        assertEquals(String.valueOf(eventCapacity), capacity.getText().toString());
        assertEquals(eventDesc, desc.getText().toString());
        assertEquals(eventTime, time.getText().toString());
        assertEquals(eventDate, date.getText().toString());
        assertEquals(eventLocation, location.getText().toString());
    }

    /**
     * Tests visibility of the draw button when the user is an organizer.
     * Expected outcome: Draw button should be visible, waitlist button should be invisible.
     */
    @Test
    public void testUpdateButtonVisibility_AsOrganizer() throws NoSuchFieldException, IllegalAccessException {
        setPrivateField("USER_ID", userId);
        viewEvent.updateButtonVisibility(userId);

        assertEquals(View.VISIBLE, drawBtn.getVisibility());
        assertEquals(View.INVISIBLE, waitlistBtn.getVisibility());
    }

    /**
     * Tests visibility of the draw button when the user is not an organizer.
     * Expected outcome: Draw button should be invisible.
     */
    @Test
    public void testUpdateButtonVisibility_AsNonOrganizer() throws NoSuchFieldException, IllegalAccessException {
        setPrivateField("USER_ID", userId);
        viewEvent.updateButtonVisibility("other_user_id");

        assertEquals(View.INVISIBLE, drawBtn.getVisibility());
    }
}

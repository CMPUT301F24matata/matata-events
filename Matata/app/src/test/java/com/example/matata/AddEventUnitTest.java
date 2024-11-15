/**
 * AddEventUnitTest class contains unit tests for the AddEvent class, verifying the functionality
 * of event ID generation, hash generation, bitmap compression, QR code generation, and event date
 * and time selection. These tests use the Robolectric framework to simulate Android components.
 *
 * Purpose:
 * - Validate the correctness and reliability of the AddEvent functionalities.
 *
 * Outstanding Issues:
 * - Confirm that QR code generation produces scannable images with accurate encoding.
 * - Ensure that bitmap compression does not degrade image quality for event visuals.
 */

package com.example.matata;

import static org.junit.Assert.*;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.widget.TextView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import androidx.test.core.app.ApplicationProvider;

/**
 * Runs tests using the Robolectric test framework for Android.
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28, manifest = Config.NONE)
public class AddEventUnitTest {

    /**
     * Instance of AddEvent used to test various event-related functionalities.
     */
    private AddEvent addEvent;

    /**
     * Initializes the AddEvent instance before each test.
     */
    @Before
    public void setUp() {
        addEvent = new AddEvent();
    }

    /**
     * Tests that each generated event ID is unique.
     *
     * Expected outcome: The two generated event IDs should be different.
     */
    @Test
    public void testGenerateRandomEventID() {
        String eventId1 = addEvent.generateRandomEventID();
        String eventId2 = addEvent.generateRandomEventID();
        assertNotEquals(eventId1, eventId2);
    }

    /**
     * Tests hash generation for a given string input.
     *
     * @see AddEvent#generateHash(String)
     * Expected outcome: The hash should not be null and should have a length of 64 characters.
     */
    @Test
    public void testGenerateHash() {
        String input = "TestString";
        String hash = addEvent.generateHash(input);
        assertNotNull(hash);
        assertEquals(64, hash.length());
    }

    /**
     * Tests bitmap compression by encoding and decoding a bitmap.
     *
     * Expected outcome: The compressed bitmap should be successfully decoded back to a Bitmap object.
     */
    @Test
    public void testBmpCompression() {
        Bitmap bmp = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
        String compressedBmp = addEvent.bmpCompression(bmp);
        assertNotNull(compressedBmp);

        byte[] decodedBytes = Base64.decode(compressedBmp, Base64.DEFAULT);
        Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        assertNotNull(decodedBitmap);
    }

    /**
     * Tests the QR code bitmap generation for a given event ID.
     *
     * @see AddEvent#generateQRbitmap(String)
     * Expected outcome: A QR code bitmap should be generated with width and height of 500 pixels.
     */
    @Test
    public void testGenerateQRbitmap() {
        String testEventId = "12345";
        Bitmap bitmap = addEvent.generateQRbitmap(testEventId);
        assertNotNull(bitmap);
        assertEquals(500, bitmap.getWidth());
        assertEquals(500, bitmap.getHeight());
    }

//    /**
//     * Tests that the correct time is displayed in the eventTime TextView after time selection.
//     *
//     * Expected outcome: The eventTime TextView should display the time as "10:30".
//     */
//    @Test
//    public void testOnTimeSelected_SetsCorrectTime() {
//        addEvent.eventTime = new TextView(ApplicationProvider.getApplicationContext());
//        addEvent.onTimeSelected(10, 30);
//        assertEquals("10:30", addEvent.eventTime.getText().toString());
//    }

//    /**
//     * Tests that the correct date is displayed in the eventDate TextView after date selection.
//     *
//     * Expected outcome: The eventDate TextView should display the date as "08/12/2024".
//     */
//    @Test
//    public void testOnDateSelected_SetsCorrectDate() {
//        addEvent.eventDate = new TextView(ApplicationProvider.getApplicationContext());
//        addEvent.onDateSelected(2024, 11, 8);
//        assertEquals("08/12/2024", addEvent.eventDate.getText().toString());
//    }
}

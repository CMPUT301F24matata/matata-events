package com.example.matata;

import static org.junit.Assert.*;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.widget.TextView;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import androidx.test.core.app.ApplicationProvider;

//import com.google.ar.core.Config;

import org.junit.Before;
import org.junit.Test;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28, manifest = Config.NONE)
public class AddEventUnitTest {

    private AddEvent addEvent;

    @Before
    public void setUp() {
        addEvent = new AddEvent();
    }

    @Test
    public void testGenerateRandomEventID() {
        String eventId1 = addEvent.generateRandomEventID();
        String eventId2 = addEvent.generateRandomEventID();
        assertNotEquals(eventId1, eventId2);
    }

    @Test
    public void testGenerateHash() {
        String input = "TestString";
        String hash = addEvent.generateHash(input);
        assertNotNull(hash);
        assertEquals(64, hash.length());
    }

    @Test
    public void testBmpCompression() {
        Bitmap bmp = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
        String compressedBmp = addEvent.bmpCompression(bmp);
        assertNotNull(compressedBmp);
        byte[] decodedBytes = Base64.decode(compressedBmp, Base64.DEFAULT);
        Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        assertNotNull(decodedBitmap);
    }

    @Test
    public void testGenerateQRbitmap() {
        String testEventId = "12345";
        Bitmap bitmap = addEvent.generateQRbitmap(testEventId);
        assertNotNull(bitmap);
        assertEquals(500, bitmap.getWidth());
        assertEquals(500, bitmap.getHeight());
    }

    @Test
    public void testOnTimeSelected_SetsCorrectTime() {
        addEvent.eventTime = new TextView(ApplicationProvider.getApplicationContext());
        addEvent.onTimeSelected(10, 30);
        assertEquals("10:30", addEvent.eventTime.getText().toString());
    }

    @Test
    public void testOnDateSelected_SetsCorrectDate() {
        addEvent.eventDate = new TextView(ApplicationProvider.getApplicationContext());
        addEvent.onDateSelected(2024, 11, 8);
        assertEquals("08/12/2024", addEvent.eventDate.getText().toString());
    }
}

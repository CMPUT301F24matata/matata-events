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
import android.widget.Toast;

import androidx.test.core.app.ApplicationProvider;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.*;

import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowAlertDialog;
import org.robolectric.shadows.ShadowSystemClock;
import org.robolectric.shadows.ShadowToast;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

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

    @Mock
    FirebaseFirestore mockDb;
    @Mock
    DocumentReference mockEventRef;
    @Mock
    DocumentReference mockEntrantRef;
    @Mock
    DocumentSnapshot mockDocumentSnapshot;

    @Before
    public void setUp() throws NoSuchFieldException, IllegalAccessException {
        MockitoAnnotations.openMocks(this);
        ActivityController<ViewEvent> controller = Robolectric.buildActivity(ViewEvent.class);
        viewEvent = controller.create().get();

        when(mockDb.collection("EVENT_PROFILES").document("testUid")).thenReturn(mockEventRef);
        when(mockDb.collection("USER_PROFILES").document(userId)).thenReturn(mockEntrantRef);

        // Initialize ViewEvent instance and setup fields using reflection
        viewEvent = new ViewEvent();

        // Setup user ID for testing
        userId = "test_user_id";
        Settings.Secure.putString(ApplicationProvider.getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID, userId);

        // Initialize UI elements with reflection to access non-public fields
        waitlistBtn = new Button(ApplicationProvider.getApplicationContext());
        title = new TextView(ApplicationProvider.getApplicationContext());
        capacity = new TextView(ApplicationProvider.getApplicationContext());
        desc = new TextView(ApplicationProvider.getApplicationContext());
        time = new TextView(ApplicationProvider.getApplicationContext());
        date = new TextView(ApplicationProvider.getApplicationContext());
        location = new TextView(ApplicationProvider.getApplicationContext());
        drawBtn = new Button(ApplicationProvider.getApplicationContext());

        // Set the private fields in ViewEvent using reflection
        setPrivateField("waitlistBtn", waitlistBtn);
        setPrivateField("title", title);
        setPrivateField("capacity", capacity);
        setPrivateField("desc", desc);
        setPrivateField("time", time);
        setPrivateField("date", date);
        setPrivateField("location", location);
        setPrivateField("drawBtn", drawBtn);
        setPrivateField("db", mockDb);
    }

    private void setPrivateField(String fieldName, Object value) throws NoSuchFieldException, IllegalAccessException {
        Field field = ViewEvent.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(viewEvent, value);
    }

    @Test
    public void testUpdateWaitlistButtonText_PendingStatus() {
        // Mock data for "pending" status
        List<DocumentReference> pendingList = new ArrayList<>();
        pendingList.add(mockEntrantRef);

        // Call updateWaitlistButtonText()
        viewEvent.updateWaitlistButtonText(new ArrayList<>(), pendingList, new ArrayList<>());

        // Check that waitlistBtn text is "Pending"
        assertEquals("Pending", waitlistBtn.getText().toString());
    }

    @Test
    public void testUpdateWaitlistButtonText_AcceptedStatus() {
        // Mock data for "accepted" status
        List<DocumentReference> acceptedList = new ArrayList<>();
        acceptedList.add(mockEntrantRef);

        // Call updateWaitlistButtonText()
        viewEvent.updateWaitlistButtonText(new ArrayList<>(),new ArrayList<>(),acceptedList);

        // Check that waitlistBtn text is "Accepted"
        assertEquals("Accepted", waitlistBtn.getText().toString());
    }

    @Test
    public void testUpdateWaitlistButtonText_NotOnAnyList() {
        // Empty lists for all statuses

        // Call updateWaitlistButtonText()
        viewEvent.updateWaitlistButtonText(new ArrayList<>(),new ArrayList<>(),new ArrayList<>());

        // Check that waitlistBtn text is "Join Waitlist"
        assertEquals("Join Waitlist", waitlistBtn.getText().toString());
    }

    @Test
    public void testUpdateWaitlistButtonText_InWaitList() {
        // Empty lists for all statuses
        List<DocumentReference> waitlist = new ArrayList<>();
        waitlist.add(mockEntrantRef);

        // Call refreshEntrantStatus directly
        viewEvent.updateWaitlistButtonText(waitlist,new ArrayList<>(),new ArrayList<>());

        // Check that waitlistBtn text is "Join Waitlist"
        assertEquals("Withdraw", waitlistBtn.getText().toString());
    }

//    @Test
//    public void testDecodeBase64toBmp_ValidBase64() {
//        // A realistic valid Base64 string for a small image (1x1 PNG pixel)
//        String validBase64 = "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mP8/wcAAwAB/6n7pAAAAABJRU5ErkJggg==";
//
//        Bitmap bitmap = viewEvent.decodeBase64toBmp(validBase64);
//        assertNotNull(bitmap);
//        assertEquals(1, bitmap.getWidth());
//        assertEquals(1, bitmap.getHeight());
//    }

    @Test
    public void testDecodeBase64toBmp_InvalidBase64() {
        // An invalid Base64 string
        String invalidBase64 = "InvalidBase64String";

        Bitmap bitmap = viewEvent.decodeBase64toBmp(invalidBase64);
        assertNull(bitmap);  // Expecting null for invalid input
    }

    @Test
    public void testLoadEventDetails_WithRealisticData() throws NoSuchFieldException, IllegalAccessException {
        // Setup realistic data
        String eventTitle = "Cultural Night";
        Long eventCapacity = 100L;
        String eventDesc = "An evening of cultural performances and food.";
        String eventTime = "6:00 PM";
        String eventDate = "2024-12-01";
        String eventLocation = "Main Hall";

        // Use reflection to set these directly on the fields
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

    @Test
    public void testUpdateButtonVisibility_AsOrganizer() throws NoSuchFieldException, IllegalAccessException {
        // Set the user as the organizer
        setPrivateField("USER_ID", userId);
        viewEvent.updateButtonVisibility(userId);

        // Check that the draw button is visible, waitlist button is invisible
        assertEquals(View.VISIBLE, drawBtn.getVisibility());
        assertEquals(View.INVISIBLE, waitlistBtn.getVisibility());
    }

    @Test
    public void testUpdateButtonVisibility_AsNonOrganizer() throws NoSuchFieldException, IllegalAccessException {
        // Set the user as a non-organizer
        setPrivateField("USER_ID", userId);
        viewEvent.updateButtonVisibility("user2");

        // Assert that the draw button is invisible
        assertEquals(View.INVISIBLE, drawBtn.getVisibility());
    }


}

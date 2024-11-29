package com.example.matata;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static java.util.regex.Pattern.matches;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

@RunWith(AndroidJUnit4.class)
public class ViewEventTest {
    private FirebaseFirestore mockFirestore;
    private DocumentReference documentReference;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        EventDraw.injectFirestore(mockFirestore);
        EventDraw.injectUid("test_uid");

    }

//    @Test
//    public void testLoadEventDetail() {
//        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), ViewEvent.class);
//        intent.putExtra("Unique_id", "event_id_123"); // Mock event ID
//        intent.putExtra("IS_ADMIN_VIEW", false);
//        ActivityScenario<ViewEvent> scenario = ActivityScenario.launch(intent);
//
//        FirebaseFirestore mockDb = Mockito.mock(FirebaseFirestore.class);
//        DocumentReference mockEventRef = Mockito.mock(DocumentReference.class);
//        Mockito.when(mockDb.collection("EVENT_PROFILES").document("event_id_123"))
//                .thenReturn(mockEventRef);
//
//        DocumentSnapshot mockSnapshot = Mockito.mock(DocumentSnapshot.class);
//        Mockito.when(mockSnapshot.exists()).thenReturn(true);
//        Mockito.when(mockSnapshot.getString("Title")).thenReturn("Sample Event");
//        Mockito.when(mockSnapshot.getString("Description")).thenReturn("This is a test event.");
//
//        Mockito.doAnswer(invocation -> {
//            ((OnSuccessListener<DocumentSnapshot>) invocation.getArgument(0)).onSuccess(mockSnapshot);
//            return null;
//        }).when(mockEventRef).get();
//
//        // Verify UI elements
//        //onView(withId(R.id.ViewEventTitle)).check(matches(withText("Sample Event")));
//        //onView(withId(R.id.ViewEventDesc)).check(matches(withText("This is a test event")));
//    }
//
//    @Test
//    public void testJoinWaitlist() {
//        // Launch the ViewEvent Activity
//        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), ViewEvent.class);
//        intent.putExtra("Unique_id", "event_id_123"); // Mock event ID
//        ActivityScenario<ViewEvent> scenario = ActivityScenario.launch(intent);
//
//        // Mock Firestore references
//        FirebaseFirestore mockDb = Mockito.mock(FirebaseFirestore.class);
//        DocumentReference mockEventRef = Mockito.mock(DocumentReference.class);
//        DocumentReference mockUserRef = Mockito.mock(DocumentReference.class);
//
//        // Simulate Firestore behavior
//        Mockito.when(mockDb.collection("EVENT_PROFILES").document("event_id_123"))
//                .thenReturn(mockEventRef);
//        Mockito.when(mockDb.collection("USER_PROFILES").document("user_id_123"))
//                .thenReturn(mockUserRef);
//
//        // Mock button click
//        onView(withId(R.id.join_waitlist_button)).perform(click());
//
//        // Verify Firestore update
//        Mockito.verify(mockEventRef).update(Mockito.eq("waitlist"), Mockito.any(FieldValue.class));
//    }
}

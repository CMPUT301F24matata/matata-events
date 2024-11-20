package com.example.matata;

import android.app.AlertDialog;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;

import org.junit.Before;
import org.junit.Test;

import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowSystemClock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;


@Config(shadows = {ShadowSystemClock.class}, manifest = Config.NONE)
@RunWith(RobolectricTestRunner.class)
public class EventDrawUnitTest {
    private EventDraw eventDraw;
    private String userId = "mockId";
    private String eventId = "testId";
    private String limitEditText = "WaitlistLimit";
    private long limitValue = 10;
    private List<DocumentReference> waitlist = new ArrayList<>();
    private Button draw_button;
    private Button clearButton;
    private Switch limitSwitch;
    private EditText waitlistLimit;
    private Button saveButton;

    @Mock
    FirebaseFirestore mockDb;
    @Mock
    CollectionReference mockEntrantProfile;
    @Mock
    CollectionReference mockEventProfile;
    @Mock
    DocumentReference mockEventRef;
    @Mock
    DocumentReference mockEntrantRef;
    @Mock
    DocumentSnapshot mockDocumentSnapshot;
    @Mock
    Transaction mockTransaction;

/*
    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        EventDraw.injectFirestore(mockDb);
        EventDraw.injectUid("mockId");

        // Set up the mock behavior
        when(mockDb.collection("EVENT_PROFILES")).thenReturn(mockCollection);
        when(mockCollection.document("mockId")).thenReturn(mockDocument);

        when(mockDocumentSnapshot.exists()).thenReturn(true);
        when(mockDocumentSnapshot.getLong("WaitlistLimit")).thenReturn(10L);

        when(mockDocument.get()).thenReturn(Tasks.forResult(mockDocumentSnapshot)); //Assume task completes successfully, run code in OnSuccessListener
        //when(mockDocument.get()).thenReturn(Tasks.forException(mockDocumentSnapshot)); //Assume task failed, run codes in OnFailureListener

        when(mockDb.runTransaction(any(Transaction.Function.class))).thenAnswer(invocation -> {
            // Simulate the transaction function and call the update
            Transaction.Function<Void> function = invocation.getArgument(0);
            function.apply(mockTransaction);
            return Tasks.forResult(null); // Simulate success
        });
    }*/

    @Before
    public void setUp() throws NoSuchFieldException, IllegalAccessException {
        MockitoAnnotations.openMocks(this);
        ActivityController<EventDraw> controller = Robolectric.buildActivity(EventDraw.class);
        eventDraw = controller.create().get();

        //when(mockDb.collection("EVENT_PROFILES").document("testUid")).thenReturn(mockEventRef);
        //when(mockDb.collection("USER_PROFILES").document(userId)).thenReturn(mockEntrantRef);
        // Set up the mock behavior
        when(mockDb.collection("EVENT_PROFILES")).thenReturn(mockEventProfile);
        when(mockEventProfile.document(userId)).thenReturn(mockEntrantRef);
        when(mockDb.collection("USER_PROFILES")).thenReturn(mockEntrantProfile);
        when(mockEntrantProfile.document(eventId)).thenReturn(mockEntrantRef);
        when(mockDocumentSnapshot.exists()).thenReturn(true);
        when(mockDocumentSnapshot.getLong(limitEditText)).thenReturn(limitValue);
        when(mockEventRef.get()).thenReturn(Tasks.forResult(mockDocumentSnapshot)); //Assume task completes successfully, run code in OnSuccessListener
        when(mockEntrantRef.get()).thenReturn(Tasks.forResult(mockDocumentSnapshot));
        when(mockDb.runTransaction(any(Transaction.Function.class))).thenAnswer(invocation -> {
            // Simulate the transaction function and call the update
            Transaction.Function<Void> function = invocation.getArgument(0);
            function.apply(mockTransaction);
            return Tasks.forResult(null); // Simulate success
        });

        // Initialize ViewEvent instance and setup fields using reflection
        eventDraw = new EventDraw();

        // Initialize UI elements with reflection to access non-public fields
        draw_button = new Button(ApplicationProvider.getApplicationContext());
        clearButton = new Button(ApplicationProvider.getApplicationContext());
        limitSwitch = new Switch(ApplicationProvider.getApplicationContext());
        waitlistLimit = new EditText(ApplicationProvider.getApplicationContext());
        saveButton = new Button(ApplicationProvider.getApplicationContext());

        // Set the private fields in ViewEvent using reflection
        setPrivateField("draw_button", draw_button);
        setPrivateField("clearButton", clearButton);
        setPrivateField("limitSwitch", limitSwitch);
        setPrivateField("waitlistLimit", waitlistLimit);
        setPrivateField("saveButton", saveButton);
        setPrivateField("db", mockDb);
    }

    private void setPrivateField(String fieldName, Object value) throws NoSuchFieldException, IllegalAccessException {
        Field field = ViewEvent.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(eventDraw, value);
    }

    @Test
    public void drawUnitTest() throws FirebaseFirestoreException {
        when(mockTransaction.get(mockEventRef)).thenReturn(mockDocumentSnapshot);

        List<DocumentReference> waitlist = new ArrayList<>();
        waitlist.add(mockEntrantRef);
        when(mockDocumentSnapshot.get("waitlist")).thenReturn(waitlist);

        ActivityScenario<EventDraw> scenario = ActivityScenario.launch(EventDraw.class);

        scenario.onActivity(activity -> {
            Button draw_button = activity.findViewById(R.id.draw_button);

            draw_button.performClick();

            AlertDialog dialog = activity.getCurrentDialog();
            Button positive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            positive.performClick();

            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                verify(mockTransaction).update(eq(mockEventRef), eq("pending"), anyList());
                verify(mockTransaction).update(eq(mockEventRef), eq("waitlist"), anyList());
            }, 50);
        });
    }
/*
    @Test
    public void drawUITest() throws FirebaseFirestoreException {
        //when(mockDocumentSnapshot.get("waitlist")).thenReturn(waitlist);
        when(mockTransaction.get(mockDocument)).thenReturn(mockDocumentSnapshot);

        List<DocumentReference> waitlist = new ArrayList<>();
        waitlist.add(mockDocument);
        when(mockDocumentSnapshot.get("waitlist")).thenReturn(waitlist);

        ActivityScenario<EventDraw> scenario = ActivityScenario.launch(EventDraw.class);

        scenario.onActivity(activity -> {
            Button draw_button = activity.findViewById(R.id.draw_button);

            draw_button.performClick();

            AlertDialog dialog = activity.getCurrentDialog();
            Button positive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            positive.performClick();

            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                verify(mockTransaction).update(eq(mockDocument), eq("pending"), anyList());
                verify(mockTransaction).update(eq(mockDocument), eq("waitlist"), anyList());
            }, 50);
        });
    }


    @Test
    public void testClearPendingList() {
        ActivityScenario<EventDraw> scenario = ActivityScenario.launch(EventDraw.class);

        scenario.onActivity(activity -> {
            Button clearButton = activity.findViewById(R.id.clearPendingList);

            // Perform clear
            clearButton.performClick();
            AlertDialog dialog = activity.getCurrentDialog();
            assertTrue("Dialog should be showing", dialog.isShowing());

            Button positive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            positive.performClick();
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                // Verify the dialog is dismissed after the click
                assertFalse("Dialog should be dismissed", dialog.isShowing());
            }, 50);

            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                verify(mockTransaction).update(eq(mockDocument), eq("pending"), anyList());
            }, 50);
        });

    }

    @Test
    public void testWaitlistLimit() {
        ActivityScenario<EventDraw> scenario = ActivityScenario.launch(EventDraw.class);

        scenario.onActivity(activity -> {
            Switch limitSwitch = activity.findViewById(R.id.limitSwitch);
            EditText waitlistLimit = activity.findViewById(R.id.waitlistLimit);
            Button saveButton = activity.findViewById(R.id.saveButton);

            limitSwitch.setChecked(true);
            assertEquals(0 ,waitlistLimit.getVisibility());
            assertEquals(0, saveButton.getVisibility());

            waitlistLimit.setText("10");
            saveButton.performClick();
            verify(mockDocument).update("WaitlistLimit", 10);


            limitSwitch.setChecked(false);
            assertEquals(8 ,waitlistLimit.getVisibility());
            assertEquals(8, saveButton.getVisibility());
            verify(mockDocument).update("WaitlistLimit", -1);
        });

    }*/
}

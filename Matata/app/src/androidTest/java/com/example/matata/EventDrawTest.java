package com.example.matata;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.android.gms.tasks.Task;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class EventDrawTest {

    @Mock
    FirebaseFirestore mockDb;
    @Mock
    CollectionReference mockCollection;
    @Mock
    DocumentReference mockDocument;
    @Mock
    DocumentSnapshot mockDocumentSnapshot;
    @Mock
    Transaction mockTransaction;


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
    }

    @Test
    public void testDraw() throws FirebaseFirestoreException {
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

    }
}

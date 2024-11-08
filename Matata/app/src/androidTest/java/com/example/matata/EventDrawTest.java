package com.example.matata;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
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
import com.google.firebase.firestore.Transaction;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

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

        //when(mockDocument.get()).thenReturn(Tasks.forResult(mockDocumentSnapshot)); //Assume task completes successfully, run code in OnSuccessListener
        //when(mockDocument.get()).thenReturn(Tasks.forException(mockDocumentSnapshot)); //Assume task failed, run codes in OnFailureListener

        when(mockDocumentSnapshot.exists()).thenReturn(true);
        when(mockDocumentSnapshot.getLong("WaitlistLimit")).thenReturn(10L);
    }

    @Test
    public void testClearPendingList() {
        when(mockDocument.get()).thenReturn(Tasks.forResult(mockDocumentSnapshot)); //Assume task completes successfully, run code in OnSuccessListener

        ActivityScenario<EventDraw> scenario = ActivityScenario.launch(EventDraw.class);

        scenario.onActivity(activity -> {
            Button clearButton = activity.findViewById(R.id.clearPendingList);

            when(mockDb.runTransaction(any(Transaction.Function.class))).thenAnswer(invocation -> {
                // Simulate the transaction function and call the update
                Transaction.Function<Void> function = invocation.getArgument(0);
                function.apply(mockTransaction);
                return Tasks.forResult(null); // Simulate success
            });

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
                // Verify the dialog is dismissed after the click
                verify(mockTransaction).update(eq(mockDocument), eq("pending"), anyList());
            }, 50);
        });

    }
}

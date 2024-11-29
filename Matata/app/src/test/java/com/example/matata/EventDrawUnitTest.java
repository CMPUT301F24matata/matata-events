package com.example.matata;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.annotation.Config;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28, manifest = Config.NONE)
public class EventDrawUnitTest {

    private EventDraw eventDraw;

    @Mock
    private FirebaseFirestore mockFirestore;

    @Mock
    private DocumentReference mockDocRef;

    @Mock
    private DocumentSnapshot mockDocumentSnapshot;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        EventDraw.injectFirestore(mockFirestore);
        EventDraw.injectUid("test_uid");

        eventDraw = new EventDraw();
    }

    @Test
    public void testLoadList_waitlist(){
//        Method loadListMethod = EventDraw.class.getDeclaredMethod(
//                "loadList",
//                List.class, List.class, EntrantAdapter.class, String.class
//        );
//        loadListMethod.setAccessible(true);

        EntrantAdapter mockAdapter = mock(EntrantAdapter.class);
        List<DocumentReference> mockWaitlist = new ArrayList<>();
        mockWaitlist.add(mockDocRef);

        when(mockDocumentSnapshot.exists()).thenReturn(true);
        when(mockDocumentSnapshot.getString("username")).thenReturn("User1");
        when(mockDocumentSnapshot.getString("phone")).thenReturn("1234567890");
        when(mockDocumentSnapshot.getString("email")).thenReturn("user1@example.com");

        List<Entrant> entrantList = new ArrayList<>();

//        loadListMethod.invoke(eventDraw, mockWaitlist, entrantList, mockAdapter, "waitlist");
        eventDraw.loadList(mockWaitlist, entrantList, mockAdapter, "waitlist");

        // Assert
        assertEquals(1, entrantList.size());
        verify(mockAdapter).notifyDataSetChanged();
    }
}

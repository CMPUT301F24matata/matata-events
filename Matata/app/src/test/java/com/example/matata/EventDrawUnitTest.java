package com.example.matata;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
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
//@RunWith(MockitoJUnitRunner.class)
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

        Task<DocumentSnapshot> mockTask = mock(Task.class);
        when(mockTask.isComplete()).thenReturn(true);
        when(mockTask.getResult()).thenReturn(mockDocumentSnapshot);

        when(mockDocRef.get()).thenReturn(mockTask);

        List<Entrant> entrantList = new ArrayList<>();

//        loadListMethod.invoke(eventDraw, mockWaitlist, entrantList, mockAdapter, "waitlist");
        eventDraw.loadList(mockWaitlist, entrantList, mockAdapter, "waitlist");

        // Assert
        assertEquals(1, entrantList.size());
        assertEquals("User1", entrantList.get(0).getName());
        assertEquals("1234567890", entrantList.get(0).getPhoneNumber());
        assertEquals("user1@example.com", entrantList.get(0).getEmail());

        verify(mockAdapter).notifyDataSetChanged();
    }
//
//    @Test
//    public void testLoadList_pendingList(){
//        EntrantAdapter mockAdapter = mock(EntrantAdapter.class);
//        List<DocumentReference> mockPendingList = new ArrayList<>();
//        mockPendingList.add(mockDocRef);
//
//        when(mockDocumentSnapshot.exists()).thenReturn(true);
//        when(mockDocumentSnapshot.getString("username")).thenReturn("User1");
//        when(mockDocumentSnapshot.getString("phone")).thenReturn("1234567890");
//        when(mockDocumentSnapshot.getString("email")).thenReturn("user1@example.com");
//
//        List<Entrant> selectedList = new ArrayList<>();
//
//        eventDraw.loadList(mockPendingList, selectedList, mockAdapter, "waitlist");
//
//        assertEquals(1, selectedList.size());
//        verify(mockAdapter).notifyDataSetChanged();
//    }
//
//    @Test
//    public void testLoadList_acceptedList(){
//        EntrantAdapter mockAdapter = mock(EntrantAdapter.class);
//        List<DocumentReference> mockAcceptedList = new ArrayList<>();
//        mockAcceptedList.add(mockDocRef);
//
//        when(mockDocumentSnapshot.exists()).thenReturn(true);
//        when(mockDocumentSnapshot.getString("username")).thenReturn("User1");
//        when(mockDocumentSnapshot.getString("phone")).thenReturn("1234567890");
//        when(mockDocumentSnapshot.getString("email")).thenReturn("user1@example.com");
//
//        List<Entrant> acceptedList = new ArrayList<>();
//
//        eventDraw.loadList(mockAcceptedList, acceptedList, mockAdapter, "waitlist");
//
//        assertEquals(1, acceptedList.size());
//        verify(mockAdapter).notifyDataSetChanged();
//    }
//
//
//    @Test
//    public void testLoadList_rejectedList(){
//        EntrantAdapter mockAdapter = mock(EntrantAdapter.class);
//        List<DocumentReference> mockWaitlist = new ArrayList<>();
//        mockWaitlist.add(mockDocRef);
//
//        when(mockDocumentSnapshot.exists()).thenReturn(true);
//        when(mockDocumentSnapshot.getString("username")).thenReturn("User1");
//        when(mockDocumentSnapshot.getString("phone")).thenReturn("1234567890");
//        when(mockDocumentSnapshot.getString("email")).thenReturn("user1@example.com");
//
//        List<Entrant> rejectedList = new ArrayList<>();
//
//        eventDraw.loadList(mockWaitlist, rejectedList, mockAdapter, "waitlist");
//
//        assertEquals(1, rejectedList.size());
//        verify(mockAdapter).notifyDataSetChanged();
//    }
//
//    @Test
//    public void testSetSelectedEntrant_correctNumberOfEntrantsSelected() throws Exception {
//        List<Entrant> mockEntrants = new ArrayList<>();
//        mockEntrants.add(new Entrant("User1", "1234567890", "user1@example.com"));
//        mockEntrants.add(new Entrant("User2", "1234567891", "user2@example.com"));
//
//        Method entrantListSetter = EventDraw.class.getDeclaredMethod("setEntrantList", List.class);
//        entrantListSetter.setAccessible(true);
//        entrantListSetter.invoke(eventDraw, mockEntrants);
//
//        // Call setSelectedEntrant()
//        Method setSelectedEntrant = EventDraw.class.getDeclaredMethod("setSelectedEntrant");
//        setSelectedEntrant.setAccessible(true);
//        setSelectedEntrant.invoke(eventDraw);
//
//        // Assert
//        Method getSelectedListMethod = EventDraw.class.getDeclaredMethod("getSelectedList");
//        getSelectedListMethod.setAccessible(true);
//        List<Entrant> selectedList = (List<Entrant>) getSelectedListMethod.invoke(eventDraw);
//
//        assertNotNull(selectedList);
//        assertEquals(mockEntrants.size(), selectedList.size());
//    }
}

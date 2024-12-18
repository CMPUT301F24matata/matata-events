package com.example.matata;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * {@code EventHistoryFragment} is a fragment that displays a list of events created by the user in a {@link RecyclerView}.
 * It retrieves event data from Firebase Firestore and dynamically updates the UI. The user can view all events they have
 * organized, with event details such as title, date, time, and location displayed in the list.
 *
 * <h2>Features:</h2>
 * <ul>
 *     <li>Fetches event data from Firestore based on the user's ID.</li>
 *     <li>Displays event details in a dynamically populated RecyclerView.</li>
 *     <li>Maintains a map of event poster URLs for efficient image loading.</li>
 *     <li>Filters events using a search bar (to be implemented).</li>
 * </ul>
 *
 * <h2>Usage:</h2>
 * This fragment is used in activities that require displaying a history of events organized by the user. To include this fragment:
 * <pre>
 * FragmentManager fragmentManager = getSupportFragmentManager();
 * FragmentTransaction transaction = fragmentManager.beginTransaction();
 * transaction.replace(R.id.container, new EventHistoryFragment());
 * transaction.commit();
 * </pre>
 *
 * <h2>Limitations:</h2>
 * <ul>
 *     <li>The search bar functionality is not implemented yet.</li>
 *     <li>Error handling for Firestore operations is minimal.</li>
 *     <li>No pagination is implemented, which might cause performance issues with a large dataset.</li>
 * </ul>
 */
public class EventHistoryFragment extends Fragment {

    private String uid;
    /**
     * RecyclerView for displaying a list of events.
     */
    private RecyclerView recyclerView;

    /**
     * Adapter for managing and displaying event data in the RecyclerView.
     */
    private EventAdapter eventAdapter;

    /**
     * List to store all retrieved events.
     */
    private List<Event> eventList;

    /**
     * Instance of FirebaseFirestore for database operations.
     */
    private FirebaseFirestore db;

    /**
     * Unique user identifier obtained from device settings or other sources.
     */
    private String USER_ID = "";

    /**
     * Map storing event poster URLs associated with their event IDs.
     */
    private Map<String, String> posterUrls = new HashMap<>();

    /**
     * List storing the status of events (e.g., "Accepted," "Pending," "Waitlist").
     */
    private List<String> statusList = new ArrayList<>();

    /**
     * EditText for entering search queries to filter events.
     */

    /**
     * Called to inflate the fragment's view hierarchy and initialize components.
     *
     * @param inflater           The LayoutInflater object used to inflate views.
     * @param container          The parent container in which the fragment is displayed.
     * @param savedInstanceState A Bundle containing the fragment's previously saved state.
     * @return The root view of the fragment.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.event_history_recycler, container, false);
        recyclerView=view.findViewById(R.id.recycler_view_event_history);


        db = FirebaseFirestore.getInstance();
        USER_ID = Settings.Secure.getString(requireContext().getContentResolver(), Settings.Secure.ANDROID_ID);


        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        eventList = new ArrayList<>();
        eventAdapter = new EventAdapter(getContext(), eventList, statusList, posterUrls);
        recyclerView.setAdapter(eventAdapter);
        loadEvents();
        Log.d("EventHistoryFragment", "eventList size: " + eventList.size());
        Log.wtf(TAG,posterUrls.toString());


        return view;
    }

    /**
     * Loads events created by the user from Firebase Firestore and populates the RecyclerView.
     */
    private void loadEvents() {
        db.collection("EVENT_PROFILES")
                .whereEqualTo("OrganizerID", USER_ID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Log.d("Firebase", "Query successful. Processing documents...");
                            QuerySnapshot result = task.getResult();
                            if (result == null || result.isEmpty()) {
                                Log.d("Firebase", "No documents found matching the query.");
                                Toast.makeText(requireContext(), "No events created.", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                statusList.add("");
                                Log.d("Firebase", "Document found: " + document.getId());

                                String title = document.getString("Title");
                                uid = document.getId();
                                String date = document.getString("Date");
                                String time = document.getString("Time");
                                String location = document.getString("Location");
                                String description = document.getString("Description");
                                int capacity = document.getLong("Capacity").intValue();
                                String poster=document.getString("Poster");
                                int waitlistLimit = document.getLong("WaitlistLimit").intValue();
                                boolean geoRequirement = document.getBoolean("GeoRequirement");

                                eventList.add(new Event(title, date, time, location, description, capacity, uid, USER_ID, waitlistLimit, geoRequirement));
                                posterUrls.put(uid,poster);
                            }
                            eventAdapter.notifyDataSetChanged();
                            eventAdapter.filter("");
                        } else {
                            Log.d("Firebase", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
}

package com.example.matata;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SearchView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Recycler_fragment is a fragment used to display a list of events in a RecyclerView.
 * It retrieves event data from Firebase Firestore and provides a search bar to filter events by their titles.
 */
public class Recycler_fragment extends Fragment {

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
    private EditText eventSearch;

    private ImageButton clearSearch;

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
        View view=inflater.inflate(R.layout.fragment_events, container, false);
        recyclerView=view.findViewById(R.id.recycler_view_events);
        eventSearch = view.findViewById(R.id.event_search);
        clearSearch =  view.findViewById(R.id.clear_search_button);

        db = FirebaseFirestore.getInstance();
        USER_ID = Settings.Secure.getString(requireContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        addEventsInit();

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        eventList = new ArrayList<>();
        eventAdapter = new EventAdapter(getContext(), eventList, statusList, posterUrls);
        recyclerView.setAdapter(eventAdapter);

        eventSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                eventAdapter.filter(s.toString());
                clearSearch.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        clearSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventSearch.setText("");
                clearSearch.setVisibility(View.GONE);
            }
        });

        return view;
    }

    /**
     * Fetches event data from Firestore and initializes the RecyclerView with the retrieved events.
     * This method listens for changes in the Firestore database and updates the UI dynamically.
     */
    private void addEventsInit() {
        db.collection("EVENT_PROFILES")
                .addSnapshotListener((snapshots, e) -> {
                    if (snapshots != null) {
                        eventList.clear();
                        statusList.clear();
                        posterUrls.clear();

                        for (QueryDocumentSnapshot document : snapshots) {
                            DocumentReference entrantRef = db.collection("USER_PROFILES").document(USER_ID);
                            List<DocumentReference> accepted = (List<DocumentReference>) document.get("accepted");
                            List<DocumentReference> pending = (List<DocumentReference>) document.get("pending");
                            List<DocumentReference> waitlist = (List<DocumentReference>) document.get("waitlist");

                            if (accepted != null && accepted.contains(entrantRef)) {
                                statusList.add("Accepted");
                            } else if (pending != null && pending.contains(entrantRef)) {
                                statusList.add("Pending");
                            } else if (waitlist != null && waitlist.contains(entrantRef)) {
                                statusList.add("Waitlist");
                            } else {
                                statusList.add("");
                            }

                            String uid = document.getId();
                            String title = document.getString("Title");
                            String date = document.getString("Date");
                            String time = document.getString("Time");
                            String location = document.getString("Location");
                            String description = document.getString("Description");
                            String organizerId = document.getString("OrganizerID");
                            int capacity = document.getLong("Capacity").intValue();
                            String status = document.getString("Status");
                            int waitlistLimit = document.getLong("WaitlistLimit").intValue();


                            if (Objects.equals(status, "Active")) {
                                eventList.add(new Event(title, date, time, location, description, capacity, uid, organizerId, waitlistLimit, false));
                                String posterUrl = document.getString("Poster");
                                if (posterUrl != null) {
                                    posterUrls.put(uid, posterUrl);
                                }
                            }

                        }

                        eventAdapter.notifyDataSetChanged();
                        eventAdapter.filter("");


                    } else if (e != null) {
                        Log.e("FirestoreError", "Error fetching events: ", e);
                    }
                });
        recyclerView.scheduleLayoutAnimation();

    }
}
package com.example.matata;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * The `SwipeView` class is a fragment that provides a swipe-based interface
 * for viewing event details. Events are retrieved from Firebase Firestore
 * and displayed using a `ViewPager2` with an `EventPagerAdapter`.
 */
public class SwipeView extends Fragment {

    /**
     * ViewPager2 for displaying event details with swipe navigation.
     */
    private ViewPager2 viewPager2;

    /**
     * Instance of FirebaseFirestore for database access.
     */
    private FirebaseFirestore db;

    /**
     * Adapter for managing and displaying event data in the ViewPager2.
     */
    private EventAdapter eventAdapter;

    /**
     * List of events to be displayed in the ViewPager2.
     */
    private List<Event> eventList;

    /**
     * Unique identifier for the user obtained from device settings.
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
     * Inflates the layout for the fragment and initializes the `ViewPager2`.
     *
     * @param inflater           The LayoutInflater object to inflate views.
     * @param container          The parent container for the fragment's UI.
     * @param savedInstanceState A Bundle containing the fragment's previously saved state.
     * @return The root view of the fragment.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        USER_ID = Settings.Secure.getString(requireContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        db=FirebaseFirestore.getInstance();
        View view=inflater.inflate(R.layout.swipe_scroll, container, false);

        eventList = new ArrayList<>();
        viewPager2=view.findViewById(R.id.viewPager);
        addEventsInit();

        return view;
    }

    /**
     * Fetches event data from Firestore and populates the `ViewPager2` with the event details.
     * The data is dynamically updated when Firestore detects changes.
     */
    private void addEventsInit() {
        db.collection("EVENT_PROFILES")
                .addSnapshotListener((snapshots, e) -> {
                    if (snapshots != null) {


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
                            //Log.wtf(TAG,uid+title+date+time+location+description+organizerId+status);
                            int waitlistLimit = document.getLong("WaitlistLimit").intValue();
                            boolean geoRequirement = document.getBoolean("GeoRequirement");


                            if (Objects.equals(status, "Active")) {
                                eventList.add(new Event(title, date, time, location, description, capacity, uid, organizerId, waitlistLimit, geoRequirement));
                                String posterUrl = document.getString("Poster");

                                if (posterUrl != null) {
                                    posterUrls.put(uid, posterUrl);
                                }
                            }

                        }
                        EventPagerAdapter adapter = new EventPagerAdapter(requireActivity(), eventList,posterUrls);
                        viewPager2.setAdapter(adapter);
                        viewPager2.setOffscreenPageLimit(1);

                    } else if (e != null) {
                        Log.e("FirestoreError", "Error fetching events: ", e);
                    }
                });
    }
}

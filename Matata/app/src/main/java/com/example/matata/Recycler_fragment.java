package com.example.matata;

import static android.content.ContentValues.TAG;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Recycler_fragment extends Fragment {


    private RecyclerView recyclerView;
    private EventAdapter eventAdapter;
    private List<Event> eventList;
    private FirebaseFirestore db;
    private String USER_ID = "";
    private String uid = null;
    private List<String> statusList = new ArrayList<>();
    private Map<String, String> posterUrls = new HashMap<>();



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_events, container, false);
        recyclerView=view.findViewById(R.id.recycler_view_events);

        db = FirebaseFirestore.getInstance();
        USER_ID = Settings.Secure.getString(requireContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        addEventsInit();

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        eventList = new ArrayList<>();
        eventAdapter = new EventAdapter(getContext(), eventList, statusList, posterUrls);
        recyclerView.setAdapter(eventAdapter);


        return view;
    }

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


                    } else if (e != null) {
                        Log.e("FirestoreError", "Error fetching events: ", e);
                    }
                });
        recyclerView.scheduleLayoutAnimation();

    }
}
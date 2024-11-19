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

public class SwipeView extends Fragment {

    private ViewPager2 viewPager2;
    private FirebaseFirestore db;
    private EventAdapter eventAdapter;
    private List<Event> eventList;
    private String USER_ID = "";
    private String uid = null;
    private List<String> statusList = new ArrayList<>();
    private Map<String, String> posterUrls = new HashMap<>();

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



                            if (Objects.equals(status, "Active")) {
                                eventList.add(new Event(title, date, time, location, description, capacity, uid, organizerId, -1));
                                String posterUrl = document.getString("Poster");
                                if (posterUrl != null) {
                                    posterUrls.put(uid, posterUrl);
                                }
                            }

                        }
                        EventPagerAdapter adapter = new EventPagerAdapter(requireActivity(), eventList);
                        viewPager2.setAdapter(adapter);
                        viewPager2.setOffscreenPageLimit(1);




                    } else if (e != null) {
                        Log.e("FirestoreError", "Error fetching events: ", e);
                    }
                });


    }
}

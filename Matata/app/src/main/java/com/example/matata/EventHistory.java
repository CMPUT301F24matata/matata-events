package com.example.matata;

import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
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
 * EventHistory activity displays a list of past events created by the user, allowing them to view details of each event.
 * This activity retrieves data from Firebase Firestore and populates it into a RecyclerView using an EventAdapter.
 *
 * Outstanding issues: This class fetches events based on the device's ID, which may lead to inconsistent results
 * if users switch devices. Additionally, only the title, date, time, location, description, and capacity are retrieved,
 * while some event details, such as the status list, are currently not fully utilized.
 */
public class EventHistory extends AppCompatActivity {

    /**
     * RecyclerView to display the event history.
     */
    private RecyclerView eventHistoryRecyclerView;

    /**
     * Adapter for managing the display of events in the event history.
     */
    private EventAdapter eventAdapter;

    /**
     * List of past events in the event history.
     */
    private List<Event> eventHistoryList = new ArrayList<>();

    /**
     * Firebase Firestore instance for database interactions.
     */
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    /**
     * Unique identifier for the user in the Firestore database.
     */
    private String uid;

    /**
     * Unique user identifier obtained from device settings or other sources.
     */
    private String USER_ID;

    /**
     * ImageView for back navigation in the activity.
     */
    private ImageView backBtn;

    /**
     * List to store the status of each event in the event history.
     */
    private List<String> statusList = new ArrayList<>();

    private Map<String,String>posterUrls=new HashMap<>();

    /**
     * Initializes the EventHistory activity, setting up the RecyclerView and loading event data from Firestore.
     *
     * @param savedInstanceState if the activity is being re-initialized, this contains the data it most recently supplied
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_history_activity);

        eventHistoryRecyclerView = findViewById(R.id.recycler_view_event_history);
        eventHistoryRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        USER_ID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        Log.d("EventHistory", "Device USER_ID: " + USER_ID);

        eventAdapter = new EventAdapter(this, eventHistoryList, statusList,posterUrls);
        eventHistoryRecyclerView.setAdapter(eventAdapter);

        backBtn = findViewById(R.id.go_back_event_history);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();  // Closes EventDetailActivity and returns to MainActivity
            }
        });

        loadEvents();
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
                                Toast.makeText(EventHistory.this, "No events created.", Toast.LENGTH_SHORT).show();
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

                                eventHistoryList.add(new Event(title, date, time, location, description, capacity, uid, USER_ID, -1));
                                eventAdapter.notifyDataSetChanged();
                            }
                        } else {
                            Log.d("Firebase", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
}

package com.example.matata;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
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
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Recycler_fragment is a fragment used to display a list of events in a RecyclerView.
 * It retrieves event data from Firebase Firestore and provides a search bar to filter events by their titles.
 */
public class EventHistory extends AppCompatActivity {

    /**
     * RecyclerView to display the event history.
     */

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

    /**
     * Map storing poster URLs for events, with the event ID as the key.
     */
    private Map<String, String> posterUrls = new HashMap<>();

    /**
     * Initializes the EventHistory activity, setting up the RecyclerView and loading event data from Firestore.
     *
     * @param savedInstanceState if the activity is being re-initialized, this contains the data it most recently supplied
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_history_activity);


        USER_ID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        Log.d("EventHistory", "Device USER_ID: " + USER_ID);


        backBtn = findViewById(R.id.go_back_event_history);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();  // Closes EventDetailActivity and returns to MainActivity
            }
        });



    }


}



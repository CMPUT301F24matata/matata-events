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
 * The {@code EventHistory} class represents an activity that displays the user's event history.
 * This activity interacts with Firebase Firestore to fetch and display details of past events.
 * Users can navigate back to the main activity using the back button provided.
 *
 * <h2>Features:</h2>
 * <ul>
 *     <li>Displays a list of past events using a RecyclerView.</li>
 *     <li>Fetches event details such as title, status, and poster image from Firebase Firestore.</li>
 *     <li>Includes a search feature to filter events by title.</li>
 *     <li>Allows users to navigate back to the main screen using a back button.</li>
 * </ul>
 *
 * <h2>Implementation:</h2>
 * <ul>
 *     <li>Data is fetched from Firestore collections and displayed in a RecyclerView using an adapter.</li>
 *     <li>The device ID is used to uniquely identify the user in the database.</li>
 *     <li>The back button closes the activity and navigates back to the previous screen.</li>
 * </ul>
 *
 * <h2>Limitations:</h2>
 * <ul>
 *     <li>No implementation for loading or displaying event details in the current version.</li>
 *     <li>Search functionality is not yet implemented.</li>
 *     <li>Error handling for Firestore interactions is minimal.</li>
 * </ul>
 *
 * <h2>Usage:</h2>
 * This activity can be launched from another activity using:
 * <pre>
 * Intent intent = new Intent(context, EventHistory.class);
 * context.startActivity(intent);
 * </pre>
 */
public class EventHistory extends AppCompatActivity {

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

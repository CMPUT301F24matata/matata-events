package com.example.matata;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

/**
 * The {@code CancelledListActivity} class is responsible for displaying a list of entrants
 * whose participation has been cancelled, either due to being rejected or removed from the pending list.
 *
 * <p>This activity combines two separate lists passed via an {@code Intent}—one for cleared pending entrants
 * and another for rejected entrants—into a single list and displays it using a {@link RecyclerView}.
 * If the combined list is empty, a message is shown to the user indicating no entrants are available to display.
 */
public class CancelledListActivity extends AppCompatActivity {

    /**
     * RecyclerView for displaying the list of cancelled entrants.
     */
    private RecyclerView recyclerView;

    /**
     * Adapter for populating the {@link RecyclerView} with entrant data.
     */
    private EntrantAdapter entrantAdapter;

    /**
     * Called when the activity is created. Initializes the views, retrieves entrant lists from the intent,
     * combines them, and sets up the RecyclerView to display the combined list.
     *
     * @param savedInstanceState The saved state of the activity, if any.
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancelled_list);

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.cancelled_list_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Get the lists from the Intent
        ArrayList<Entrant> clearPendingEntrants = (ArrayList<Entrant>) getIntent().getSerializableExtra("clearPendingList");
        ArrayList<Entrant> rejectedEntrants = (ArrayList<Entrant>) getIntent().getSerializableExtra("rejectedList");

        // Combine the lists into a single list
        ArrayList<Entrant> combinedList = new ArrayList<>();
        if (clearPendingEntrants != null) {
            combinedList.addAll(clearPendingEntrants);
        }
        if (rejectedEntrants != null) {
            combinedList.addAll(rejectedEntrants);
        }

        // Check if the combined list is empty
        if (combinedList.isEmpty()) {
            Toast.makeText(this, "No entrants to display.", Toast.LENGTH_SHORT).show();
        }

        // Set up the adapter and attach it to the RecyclerView
        entrantAdapter = new EntrantAdapter(this, combinedList,"Cancelled");
        recyclerView.setAdapter(entrantAdapter);
    }
}
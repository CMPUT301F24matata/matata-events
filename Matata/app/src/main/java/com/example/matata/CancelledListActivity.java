package com.example.matata;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CancelledListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EntrantAdapter entrantAdapter;

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
        entrantAdapter = new EntrantAdapter(this, combinedList);
        recyclerView.setAdapter(entrantAdapter);
    }
}


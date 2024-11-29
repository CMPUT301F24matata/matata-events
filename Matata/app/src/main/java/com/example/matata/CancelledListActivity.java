package com.example.matata;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CancelledListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancelled_list);

        RecyclerView recyclerView = findViewById(R.id.cancelled_list_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Retrieve the cancelled and rejected lists from the Intent
        ArrayList<Entrant> cancelledList = (ArrayList<Entrant>) getIntent().getSerializableExtra("cancelledList");
        ArrayList<Entrant> rejectedList = (ArrayList<Entrant>) getIntent().getSerializableExtra("rejectedList");

        // Combine the two lists
        ArrayList<Entrant> combinedList = new ArrayList<>();
        if (cancelledList != null) {
            combinedList.addAll(cancelledList);
        }
        if (rejectedList != null) {
            combinedList.addAll(rejectedList);
        }

        if (combinedList.isEmpty()) {
            Toast.makeText(this, "No entrants to display.", Toast.LENGTH_SHORT).show();
        }

        // Set up the RecyclerView with an adapter
        EntrantAdapter adapter = new EntrantAdapter(this, combinedList);
        recyclerView.setAdapter(adapter);
    }
}


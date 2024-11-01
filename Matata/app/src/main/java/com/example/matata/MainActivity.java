package com.example.matata;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private ImageView profileIcon;
    private RecyclerView recyclerView;
    private EventAdapter eventAdapter;
    private List<Event> eventList;
    private ImageView new_event;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        profileIcon = findViewById(R.id.profile_picture);
        new_event=findViewById(R.id.add_event);

        new_event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), AddEvent.class);
                view.getContext().startActivity(intent);
            }
        });

        profileIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), ProfileActivity.class);
                view.getContext().startActivity(intent);
            }
        });

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recycler_view_events); // Ensure you have this ID in your layout
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Hard coded events for convenience
        eventList = new ArrayList<>();
        eventList.add(new Event("Community Cleanup", "31-12-2023", "10:00 AM", "Community Park", "Join us for a community cleanup day!",25));
        //eventList.add(new Event("Swim Lessons", "01-01-2024", "9:00 AM", "City Park", "Join for beginner swim lessons!"));

        // Set adapter
        eventAdapter = new EventAdapter(this, eventList);
        recyclerView.setAdapter(eventAdapter);
    }
}

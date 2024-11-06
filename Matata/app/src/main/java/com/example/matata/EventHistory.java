package com.example.matata;

import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class EventHistory extends AppCompatActivity {
    private RecyclerView eventHistoryRecyclerView;
    private EventAdapter eventAdapter;
    private List<Event> eventHistoryList = new ArrayList<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String uid;
    private String USER_ID;
    private ImageView backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_history_activity);

        eventHistoryRecyclerView = findViewById(R.id.recycler_view_event_history);
        eventHistoryRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        eventHistoryList = new ArrayList<Event>();
        USER_ID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        eventAdapter = new EventAdapter(this, eventHistoryList);
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

    private void loadEvents() {
        db.collection("EVENT_PROFILES")
                .whereEqualTo("OrganizerId", USER_ID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                String Title = document.getString("Title");
                                uid= document.getId();
                                String Date = document.getString("Date");
                                String Time = document.getString("Time");
                                String Location = document.getString("Location");
                                String Description = document.getString("Description");
                                int Capacity = document.getLong("Capacity").intValue();

                                eventHistoryList.add(new Event(Title, Date, Time, Location, Description,Capacity,uid,USER_ID));
                                eventAdapter.notifyDataSetChanged();
                            }
                        } else {
                            //Toast.makeText(getApplicationContext(), "Error getting documents", Toast.LENGTH_SHORT).show();
                            Log.d("Firebase", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
}

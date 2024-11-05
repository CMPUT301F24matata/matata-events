package com.example.matata;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
//import com.google.zxing.integration.android.IntentIntegrator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private ImageView profileIcon;
    private RecyclerView recyclerView;
    private EventAdapter eventAdapter;
    private List<Event> eventList;
    private FloatingActionButton QR_scanner;
    private ImageView new_event;
    private FirebaseFirestore db;
    private String USER_ID = "";
    private String uid=null;

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



        db = FirebaseFirestore.getInstance();

        USER_ID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        DocumentReference userRef = db.collection("USER_PROFILES").document(USER_ID);

        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (!document.exists()) {
                    Map<String, Object> userProfile = new HashMap<>();
                    userProfile.put("username", "");
                    userProfile.put("phone", "");
                    userProfile.put("email", "");
                    userProfile.put("notifications", false);
                    userProfile.put("profileUri", "");
                    userRef.set(userProfile);
                }
            }
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
                intent.putExtra("Caller", "Not admin");
                view.getContext().startActivity(intent);
            }
        });

        QR_scanner=findViewById(R.id.qr_scanner);
        QR_scanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(view.getContext(),QR_camera.class);
                view.getContext().startActivity(intent);
            }
        });

        recyclerView = findViewById(R.id.recycler_view_events); // Ensure you have this ID in your layout
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        eventList = new ArrayList<>();
        addEventsInit();

        eventAdapter = new EventAdapter(this, eventList);
        recyclerView.setAdapter(eventAdapter);
    }

    private void addEventsInit() {
        //eventList.add(new Event("Community Cleanup", "31-12-2023", "10:00 AM", "Community Park", "Join us for a community cleanup day!",25));

        db.collection("EVENT_PROFILES")
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
                                
                                eventList.add(new Event(Title, Date, Time, Location, Description,Capacity,uid));
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

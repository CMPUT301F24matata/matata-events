package com.example.matata;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class EventDraw extends AppCompatActivity {
    private FirebaseFirestore db;
    private String uid;
    private List<Entrant> entrantList;
    private EntrantAdapter entrantAdapter;
    private RecyclerView pendingRecyclerView;
    private TextView title;
    private ImageView backBtn;
    private Button drawBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_draw_activity);
        db= FirebaseFirestore.getInstance();

        Intent intent=getIntent();
        String uid=intent.getStringExtra("Unique_id");

        title = findViewById(R.id.event_title_draw_event);
        drawBtn = findViewById(R.id.draw_button);
        backBtn = findViewById(R.id.go_back_draw_event);

        pendingRecyclerView = findViewById(R.id.pending_recyclerView);
        pendingRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        entrantList = new ArrayList<Entrant>();
        entrantAdapter = new EntrantAdapter(this, entrantList);
        pendingRecyclerView.setAdapter(entrantAdapter);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        db.collection("EVENT_PROFILES").document(uid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot document = task.getResult();
                        title.setText(document.getString("Title"));
                        List<DocumentReference> waitlist = (List<DocumentReference>) document.get("waitlist");
                        if (waitlist != null){
                            for (DocumentReference entrantRef : waitlist){
                                entrantRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        DocumentSnapshot entrantDocument = task.getResult();
                                        if (entrantDocument.exists()) {
                                            String Name = entrantDocument.getString("username");
                                            String PhoneNumber = entrantDocument.getString("phone");
                                            String Email = entrantDocument.getString("email");
                                            entrantList.add(new Entrant(Name, PhoneNumber, Email));
                                            entrantAdapter.notifyDataSetChanged();
                                        }
                                    }
                                });


                            }

                        }

                    }
                })

        ;

        }


}

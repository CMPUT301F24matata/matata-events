package com.example.matata;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Transaction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class EventDraw extends AppCompatActivity {
    private FirebaseFirestore db;
    private String uid;
    private List<Entrant> entrantList, selectedList;
    private EntrantAdapter pendingAdapter, waitlistAdapter;
    private RecyclerView waitlistRecyclerView, acceptedRecyclerView, pendingRecyclerView, rejectedRecyclerView;
    private Map<Entrant, String> entrantMap;
    private List<String> selectedIdList;
    private int drawNum;
    private TextView title;
    private ImageView backBtn;
    private Button drawBtn;
    private Button clearPendingList;
    private Switch limitSwitch;
    private EditText waitlistLimit;
    private Button saveButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_draw_activity);
        db= FirebaseFirestore.getInstance();

        Intent intent=getIntent();
        uid=intent.getStringExtra("Unique_id");

        title = findViewById(R.id.event_title_draw_event);
        drawBtn = findViewById(R.id.draw_button);
        backBtn = findViewById(R.id.go_back_draw_event);

        pendingRecyclerView = findViewById(R.id.pending_recyclerView);
        acceptedRecyclerView = findViewById(R.id.accepted_recyclerView);
        rejectedRecyclerView = findViewById(R.id.rejected_recyclerView);
        waitlistRecyclerView = findViewById(R.id.waitlist_recyclerView);

        entrantList = new ArrayList<Entrant>();
        waitlistAdapter = new EntrantAdapter(this, entrantList);
        waitlistRecyclerView.setAdapter(waitlistAdapter);
        waitlistRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        selectedList = new ArrayList<Entrant>();
        pendingAdapter = new EntrantAdapter(this, selectedList);
        pendingRecyclerView.setAdapter(pendingAdapter);
        pendingRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        entrantMap = new LinkedHashMap<>();
        selectedIdList = new ArrayList<String>();

        //loadEntrants();

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        drawBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawConfirmDialog();
            }
        });

        clearPendingList = findViewById(R.id.clearPendingList);
        clearPendingList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearConfirmDialog();
                Log.d("Selected List", "Selected List Cleared");
                pendingAdapter.notifyDataSetChanged();
            }
        });

        limitSwitch = findViewById(R.id.limitSwitch);
        waitlistLimit = findViewById(R.id.waitlistLimit);
        saveButton = findViewById(R.id.saveButton);

        limitSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    waitlistLimit.setVisibility(View.VISIBLE);
                    saveButton.setVisibility(View.VISIBLE);
                } else {
                    waitlistLimit.setVisibility(View.GONE);
                    saveButton.setVisibility(View.GONE);
                }
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (waitlistLimit != null) {
                    try {
                        int limit = Integer.parseInt(waitlistLimit.getText().toString().trim());
                        db.collection("EVENT_PROFILES").document(uid).update("WaitlistLimit", Integer.valueOf(waitlistLimit.getText().toString().trim()));
                        Toast.makeText(EventDraw.this, "Limit Saved", Toast.LENGTH_SHORT).show();
                    } catch (NumberFormatException e) {
                        waitlistLimit.setError("Invalid number, please endter an integer");
                        waitlistLimit.requestFocus();
                    }
                }
            }
        });

        db.collection("EVENT_PROFILES").document(uid).get()
                .addOnCompleteListener(task -> {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        title.setText(document.getString("Title"));
                        drawNum = document.getLong("Capacity").intValue();

                        List<DocumentReference> waitlist = (List<DocumentReference>) document.get("waitlist");
                        List<DocumentReference> pending = (List<DocumentReference>) document.get("pending");

                        // Load all entrants from waitlist and pending lists
                        loadList(waitlist, entrantList, waitlistAdapter, "waitlist");
                        loadList(pending, selectedList, pendingAdapter, "pending");
                    }
                });

        }

    private void drawConfirmDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(EventDraw.this);
        builder.setCancelable(true);
        builder.setMessage("You are about to draw " + drawNum + " out of "+ entrantList.size() + " people.\nProceed?");
        builder.setPositiveButton("Confirm",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setSelectedEntrant();
                    }
                });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void clearConfirmDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(EventDraw.this);
        builder.setCancelable(true);
        builder.setMessage("You are about remove all entrants that haven't accepted the invitation yet.\nProceed?");
        builder.setPositiveButton("Confirm",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        clearSelectedEntrant();
                    }
                });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void setSelectedEntrant() {
        List<Map.Entry<Entrant, String>> tempList = new ArrayList<>(entrantMap.entrySet());
        Collections.shuffle(entrantList);
        DocumentReference eventRef = db.collection("EVENT_PROFILES").document(uid);

        selectedList.clear();
        selectedIdList.clear();
        db.runTransaction((Transaction.Function<Void>) transaction -> {
            DocumentSnapshot eventSnapshot = transaction.get(eventRef);
            List<DocumentReference> pending = (List<DocumentReference>) eventSnapshot.get("pending");
            List<DocumentReference> waitlist= (List<DocumentReference>) eventSnapshot.get("waitlist");

            if (pending == null) {
                pending = new ArrayList<>();
            }
            for (int i = 0; i < Math.min(drawNum, tempList.size()); i++) {
                Map.Entry<Entrant, String> entry = tempList.get(i);
                selectedList.add(entry.getKey());
                selectedIdList.add(entry.getValue());
                //pendingAdapter.notifyDataSetChanged();

                DocumentReference entrantRef = db.collection("USER_PROFILES").document(entry.getValue());
                pending.add(entrantRef);
                waitlist.remove(entrantRef);
                entrantList.remove(entry.getKey());
            }
            transaction.update(eventRef, "pending", pending);
            transaction.update(eventRef, "waitlist",waitlist);
            return null;
        }).addOnSuccessListener(aVoid -> {
            Log.d("Firebase", "Entrant added to pending list successfully");
            pendingAdapter.notifyDataSetChanged();
            waitlistAdapter.notifyDataSetChanged();
        }).addOnFailureListener(e -> {
            Log.e("Firebase", "Error adding entrant to pending list", e);
        });

    }

    private void clearSelectedEntrant() {
        DocumentReference eventRef = db.collection("EVENT_PROFILES").document(uid);

        selectedList.clear();
        selectedIdList.clear();

        db.runTransaction((Transaction.Function<Void>) transaction -> {

            transaction.update(eventRef, "pending", new ArrayList<>());
            return null;
        }).addOnSuccessListener(aVoid -> {
            Log.d("Firebase", "Clearing pending list successfully");
            pendingAdapter.notifyDataSetChanged();
        }).addOnFailureListener(e -> {
            Log.e("Firebase", "Error clearing pending list", e);
        });

    }

    private void loadList(List<DocumentReference> ref, List<Entrant> list, EntrantAdapter adapter, String listType) {
        if (ref == null || ref.isEmpty()){
            return;
        }

        List<Task<DocumentSnapshot>> tasks = new ArrayList<>();
        for (DocumentReference doc : ref) {
            tasks.add(doc.get());
        }

        Tasks.whenAllComplete(tasks).addOnCompleteListener(t -> {
            for (Task<DocumentSnapshot> task : tasks) {
                DocumentSnapshot snapshot = task.getResult();
                if (snapshot.exists()) {
                    String name = snapshot.getString("username");
                    String phone = snapshot.getString("phone");
                    String email = snapshot.getString("email");

                    Entrant entrant = new Entrant(name, phone, email);
                    list.add(entrant);
                    if (listType == "waitlist"){
                        entrantMap.put(entrant, snapshot.getId());
                    }
                }
            }
            adapter.notifyDataSetChanged();
        });
    }


}

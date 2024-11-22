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
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
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
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * The `EventDraw` activity allows event organizers to manage a draw process for selecting participants
 * from a waitlist. The organizer can set a waitlist limit, randomly draw participants, and manage
 * accepted, rejected, and pending lists of entrants.
 * This activity interacts with Firebase Firestore to store and update event-related data.
 */
public class EventDraw extends AppCompatActivity {

    /**
     * Instance of FirebaseFirestore used for database operations.
     */
    private FirebaseFirestore db;

    /**
     * Unique identifier for the event.
     */
    private String uid;

    /**
     * List of all entrants for the event.
     */
    private List<Entrant> entrantList;

    /**
     * List of selected entrants.
     */
    private List<Entrant> selectedList;

    /**
     * List of accepted entrants.
     */
    private List<Entrant> acceptedList;

    /**
     * List of rejected entrants.
     */
    private List<Entrant> rejectedList;

    /**
     * Adapter for displaying pending entrants.
     */
    private EntrantAdapter pendingAdapter;

    /**
     * Adapter for displaying waitlisted entrants.
     */
    private EntrantAdapter waitlistAdapter;

    /**
     * Adapter for displaying accepted entrants.
     */
    private EntrantAdapter acceptedAdapter;

    /**
     * Adapter for displaying rejected entrants.
     */
    private EntrantAdapter rejectedAdapter;

    /**
     * RecyclerView for displaying the waitlist.
     */
    private RecyclerView waitlistRecyclerView;

    /**
     * RecyclerView for displaying accepted entrants.
     */
    private RecyclerView acceptedRecyclerView;

    /**
     * RecyclerView for displaying pending entrants.
     */
    private RecyclerView pendingRecyclerView;

    /**
     * RecyclerView for displaying rejected entrants.
     */
    private RecyclerView rejectedRecyclerView;

    /**
     * TextView showing the total number of entrants.
     */
    private TextView totalEntrant;

    /**
     * TextView showing the remaining positions available in the event.
     */
    private TextView remainingPosition;

    /**
     * TextView representing the accepted section header.
     */
    private TextView acceptedSectionText;

    /**
     * TextView representing the rejected section header.
     */
    private TextView rejectedSectionText;

    /**
     * TextView representing the pending section header.
     */
    private TextView pendingSectionText;

    /**
     * TextView representing the waitlist section header.
     */
    private TextView waitlistSectionText;

    /**
     * LinearLayout for the accepted entrants section.
     */
    private LinearLayout acceptedLinearLayout;

    /**
     * LinearLayout for the rejected entrants section.
     */
    private LinearLayout rejectedLinearLayout;

    /**
     * LinearLayout for the pending entrants section.
     */
    private LinearLayout pendingLinearLayout;

    /**
     * LinearLayout for the waitlist entrants section.
     */
    private LinearLayout waitlistLinearLayout;

    /**
     * Map linking each entrant to their status (e.g., accepted, rejected).
     */
    private Map<String, Entrant> entrantMap;

    /**
     * List of IDs for selected entrants.
     */
    private List<String> selectedIdList;

    /**
     * The number of entrants to draw in the current draw operation.
     */
    private int drawNum;

    /**
     * The number of remaining positions available in the event.
     */
    private int remainNum;

    /**
     * The maximum capacity of the event.
     */
    private int capacity;

    /**
     * Button to initiate the draw operation.
     */
    private Button drawBtn;

    /**
     * Button to clear all pending entrants from the list.
     */
    private Button clearPendingList;

    /**
     * Switch to toggle the visibility of the waitlist limit input field.
     */
    private Switch limitSwitch;

    /**
     * EditText field for entering the waitlist limit.
     */
    private EditText waitlistLimit;

    /**
     * Button to save the waitlist limit.
     */
    private Button saveButton;

    /**
     * The currently displayed AlertDialog instance.
     */
    private AlertDialog currentDialog;

    /**
     * Static Firestore instance for dependency injection, primarily used for testing.
     */
    private static FirebaseFirestore injectedFirestore;

    /**
     * Static UID for dependency injection, primarily used for testing.
     */
    private static String injectedUid;

    /**
     * Initializes the EventDraw activity and sets up Firebase, RecyclerViews, and various controls.
     *
     * @param savedInstanceState if the activity is being re-initialized, this contains the data it most recently supplied
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_draw_activity);

        if (injectedFirestore != null) {
            db = injectedFirestore;
        } else {
            db= FirebaseFirestore.getInstance();
        }

        if (injectedUid != null) {
            uid = injectedUid;
        } else {
            Intent intent=getIntent();
            uid=intent.getStringExtra("Unique_id");
        }

        totalEntrant = findViewById(R.id.total_entrant_text);
        remainingPosition = findViewById(R.id.remaining_text);
        acceptedSectionText = findViewById(R.id.accepted_section_text);
        rejectedSectionText = findViewById(R.id.rejected_section_text);
        pendingSectionText = findViewById(R.id.pending_section_text);
        waitlistSectionText = findViewById(R.id.waiting_section_text);

        acceptedLinearLayout = findViewById(R.id.accepted_section);
        rejectedLinearLayout = findViewById(R.id.rejected_section);
        pendingLinearLayout = findViewById(R.id.pending_section);
        waitlistLinearLayout = findViewById(R.id.waitlist_section);

        drawBtn = findViewById(R.id.draw_button);
        ImageView backBtn = findViewById(R.id.go_back_draw_event);

        pendingRecyclerView = findViewById(R.id.pending_recyclerView);
        acceptedRecyclerView = findViewById(R.id.accepted_recyclerView);
        rejectedRecyclerView = findViewById(R.id.rejected_recyclerView);
        waitlistRecyclerView = findViewById(R.id.waitlist_recyclerView);

        entrantList = new ArrayList<>();
        waitlistAdapter = new EntrantAdapter(this, entrantList);
        waitlistRecyclerView.setAdapter(waitlistAdapter);
        waitlistRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        selectedList = new ArrayList<>();
        pendingAdapter = new EntrantAdapter(this, selectedList);
        pendingRecyclerView.setAdapter(pendingAdapter);
        pendingRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        rejectedList = new ArrayList<>();
        rejectedAdapter = new EntrantAdapter(this, rejectedList);
        rejectedRecyclerView.setAdapter(rejectedAdapter);
        rejectedRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        acceptedList = new ArrayList<>();
        acceptedAdapter = new EntrantAdapter(this, acceptedList);
        acceptedRecyclerView.setAdapter(acceptedAdapter);
        acceptedRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        entrantMap = new LinkedHashMap<>();
        selectedIdList = new ArrayList<>();

        loadLimit(uid);

        // Back button to close the activity
        backBtn.setOnClickListener(v -> finish());

        // Draw button opens a confirmation dialog
        //drawBtn.setOnClickListener(view -> drawConfirmDialog());
        drawBtn.setOnClickListener(view -> checkDrawStatus());

        clearPendingList = findViewById(R.id.clearPendingList);
        clearPendingList.setOnClickListener(v -> {
            clearConfirmDialog();
            Log.d("Selected List", "Selected List Cleared");
            pendingAdapter.notifyDataSetChanged();
        });

        limitSwitch = findViewById(R.id.limitSwitch);
        waitlistLimit = findViewById(R.id.waitlistLimit);
        saveButton = findViewById(R.id.saveButton);

        // Handle toggling of waitlist limit visibility
        limitSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                waitlistLimit.setVisibility(View.VISIBLE);
                saveButton.setVisibility(View.VISIBLE);
            } else {
                removeLimit(uid);
                waitlistLimit.setVisibility(View.GONE);
                saveButton.setVisibility(View.GONE);
            }
        });

        // Save button to set a waitlist limit
        saveButton.setOnClickListener(v -> {
            if (!waitlistLimit.getText().toString().isEmpty()) {
                setLimit(uid);
            }
        });

        acceptedSectionText.setOnClickListener(v->{ linearLayoutDropDown(acceptedLinearLayout,acceptedSectionText); });
        rejectedSectionText.setOnClickListener(v->{ linearLayoutDropDown(rejectedLinearLayout,rejectedSectionText); });
        pendingSectionText.setOnClickListener(v->{ linearLayoutDropDown(pendingLinearLayout,pendingSectionText); });
        waitlistSectionText.setOnClickListener(v->{ linearLayoutDropDown(waitlistLinearLayout,waitlistSectionText); });

        // Load event data from Firestore
        db.collection("EVENT_PROFILES").document(uid).get()
                .addOnCompleteListener(task -> {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        //title.setText(document.getString("Title"));

                        List<DocumentReference> waitlist = (List<DocumentReference>) document.get("waitlist");
                        List<DocumentReference> pending = (List<DocumentReference>) document.get("pending");
                        List<DocumentReference> accepted = (List<DocumentReference>) document.get("accepted");
                        List<DocumentReference> rejected = (List<DocumentReference>) document.get("rejected");

                        // Load entrants for each list
                        loadList(pending, selectedList, pendingAdapter, "pending");
                        loadList(accepted, acceptedList, acceptedAdapter, "accepted");
                        loadList(rejected, rejectedList, rejectedAdapter, "rejected");
                        loadList(waitlist, entrantList, waitlistAdapter, "waitlist");

                        capacity = document.getLong("Capacity").intValue();
                        remainNum = capacity;
                        if(pending != null){
                            remainNum -= pending.size();
                        }
                        if(accepted != null){
                            remainNum -= accepted.size();
                        }
                        remainNum = Math.max(remainNum,0);
                        if (waitlist != null){
                            drawNum = Math.min(remainNum, waitlist.size());
                            totalEntrant.setText("From: " + waitlist.size());
                        }else{
                            drawNum = 0;
                        }
                        remainingPosition.setText("Remaining Position: " + remainNum);

                        if (accepted!= null && accepted.size()==capacity){
                            acceptedSectionText.setText("Final List");
                            totalEntrant.setText("Event Full");
                            pendingLinearLayout.setVisibility(View.GONE);
                            pendingSectionText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_expand_more, 0);
                            rejectedLinearLayout.setVisibility(View.GONE);
                            rejectedSectionText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_expand_more, 0);
                            waitlistLinearLayout.setVisibility(View.GONE);
                            waitlistSectionText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_expand_more, 0);
                        }
                    }
                });
    }


    public static void injectFirestore(FirebaseFirestore firestore) {
        injectedFirestore = firestore;
    }

    public static void injectUid(String uid) {
        injectedUid = uid;
    }

    private void checkDrawStatus() {
        if (entrantList.isEmpty()){
            Toast.makeText(EventDraw.this, "No one is in the waiting list", Toast.LENGTH_SHORT).show();
        }else if (acceptedList.size() + selectedList.size() < capacity ){
            drawConfirmDialog();
        }else{
            Toast.makeText(EventDraw.this, "You cannot draw now because there is no remaining position", Toast.LENGTH_SHORT).show();
        }
    }

    private void linearLayoutDropDown(LinearLayout dropdownLayout, TextView dropdownButton) {
        if (dropdownLayout.getVisibility() == View.GONE) {
            dropdownLayout.setVisibility(View.VISIBLE);
            dropdownButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_expand_less, 0);
        } else {
            dropdownLayout.setVisibility(View.GONE);
            dropdownButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_expand_more, 0);
        }
    }

    /**
     * Opens a confirmation dialog for drawing entrants.
     */
    private void drawConfirmDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(EventDraw.this);
        builder.setCancelable(true);
        builder.setMessage("You are about to draw " + drawNum + " out of " + entrantList.size() + " people.\nProceed?");
        builder.setPositiveButton("Confirm", (dialog, which) -> setSelectedEntrant());
        builder.setNegativeButton(android.R.string.cancel, null);
        AlertDialog dialog = builder.create();
        currentDialog = dialog;
        dialog.show();
    }

    /**
     * Opens a confirmation dialog to clear all pending entrants.
     */
    private void clearConfirmDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(EventDraw.this);
        builder.setCancelable(true);
        builder.setMessage("You are about to remove all entrants that haven't accepted the invitation yet.\nProceed?");
        builder.setPositiveButton("Confirm", (dialog, which) -> clearSelectedEntrant());
        builder.setNegativeButton(android.R.string.cancel, null);
        AlertDialog dialog = builder.create();
        dialog.show();
        currentDialog = dialog;
    }

    /**
     * Sets the selected entrants and updates Firestore accordingly.
     */
    private void setSelectedEntrant() {
        DocumentReference eventRef = db.collection("EVENT_PROFILES").document(uid);

        List<Map.Entry<String, Entrant>> tempList = new ArrayList<>(entrantMap.entrySet());
        Collections.shuffle(tempList);

        db.runTransaction((Transaction.Function<Void>) transaction -> {
            DocumentSnapshot eventSnapshot = transaction.get(eventRef);
            List<DocumentReference> pending = (List<DocumentReference>) eventSnapshot.get("pending");
            List<DocumentReference> waitlist = (List<DocumentReference>) eventSnapshot.get("waitlist");

            if (pending == null) {
                pending = new ArrayList<>();
            }
            for (int i = 0; i < drawNum; i++) {
                Map.Entry<String, Entrant> entrant = tempList.get(i);
                selectedList.add(entrant.getValue());
                selectedIdList.add(entrant.getKey());

                DocumentReference entrantRef = db.collection("USER_PROFILES").document(entrant.getKey());
                pending.add(entrantRef);
                waitlist.remove(entrantRef);
                entrantList.remove(entrant.getValue());
                entrantMap.remove(entrant.getKey());
            }
            transaction.update(eventRef, "pending", pending);
            transaction.update(eventRef, "waitlist", waitlist);
            remainNum -= drawNum;
            remainNum = Math.max(remainNum,0);
            return null;
        }).addOnSuccessListener(aVoid -> {
            Log.d("Firebase", "Entrant added to pending list successfully");
            pendingAdapter.notifyDataSetChanged();
            waitlistAdapter.notifyDataSetChanged();
            remainingPosition.setText("Remaining Position: " + remainNum);
            totalEntrant.setText("From: " + entrantList.size());
            Toast.makeText(EventDraw.this, "Successfully sampled " + drawNum + " entrants to pending list", Toast.LENGTH_SHORT).show();
            drawNum = Math.min(drawNum,entrantList.size());
        }).addOnFailureListener(e -> Log.e("Firebase", "Error adding entrant to pending list", e));
    }

    /**
     * Clears selected entrants from the pending list in Firestore.
     */
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
        }).addOnFailureListener(e -> Log.e("Firebase", "Error clearing pending list", e));
    }

    /**
     * Loads entrants into the specified list from Firestore references.
     *
     * @param ref a list of DocumentReferences to load entrant data
     * @param list the list to populate with Entrants
     * @param adapter the adapter to notify of data changes
     * @param listType the type of list being loaded ("waitlist", "pending", etc.)
     */
    private void loadList(List<DocumentReference> ref, List<Entrant> list, EntrantAdapter adapter, String listType) {
        if (ref == null || ref.isEmpty()) {
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
                    if ("waitlist".equals(listType)) {
                        entrantMap.put(snapshot.getId(), entrant);
                    }
                }
            }
            adapter.notifyDataSetChanged();
        });
    }

    /**
     * Sets the waitlist limit in Firestore based on user input.
     *
     * @param uid the unique event ID
     */
    private void setLimit(String uid) {
        DocumentReference eventRef = db.collection("EVENT_PROFILES").document(uid);
        eventRef.update("WaitlistLimit", Integer.valueOf(waitlistLimit.getText().toString().trim()));
        Toast.makeText(EventDraw.this, "Limit Saved", Toast.LENGTH_SHORT).show();
    }

    /**
     * Removes the waitlist limit in Firestore.
     *
     * @param uid the unique event ID
     */
    private void removeLimit(String uid) {
        DocumentReference eventRef = db.collection("EVENT_PROFILES").document(uid);
        eventRef.update("WaitlistLimit", -1);
        Toast.makeText(EventDraw.this, "Limit Removed", Toast.LENGTH_SHORT).show();
    }

    /**
     * Loads the current waitlist limit from Firestore and updates the UI.
     *
     * @param uid the unique event ID
     */
    private void loadLimit(String uid) {
        DocumentReference eventRef = db.collection("EVENT_PROFILES").document(uid);
        eventRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        int limit = documentSnapshot.getLong("WaitlistLimit").intValue();
                        String stringLimit = String.valueOf(limit);
                        if (limit != -1) {
                            limitSwitch.setChecked(true);
                            waitlistLimit.setText(stringLimit != null ? stringLimit : "");
                        }
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(EventDraw.this, "Failed to load limit", Toast.LENGTH_SHORT).show());
    }


    public AlertDialog getCurrentDialog () {
        return currentDialog;
    }


}

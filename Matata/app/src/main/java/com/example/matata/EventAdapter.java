package com.example.matata;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {
    private List<String> statusList;
    private Context context;
    private List<Event> eventList;


    public EventAdapter(Context context, List<Event> eventList, List<String> status) {
        this.context = context;
        this.eventList = eventList;
        this.statusList = status;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.event_card, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = eventList.get(position);
        holder.titleTextView.setText(event.getTitle());
        holder.dateTextView.setText(event.getDate());
        holder.timeTextView.setText(event.getTime());
        holder.locationTextView.setText(event.getLocation());
        String description = event.getDescription();
        if (description.length() > 100){
            description = description.substring(0, 100) + "...";
        }
        holder.descriptionTextView.setText(description);
        holder.eventStatus.setText(statusList.get(position));


        // Set click listener on the entire event card
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create intent to open EventDetailActivity
                Intent intent = new Intent(context, ViewEvent.class);
                intent.putExtra("Unique_id",event.getEventid().toString());

                // Start EventDetailActivity
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, dateTextView, timeTextView, locationTextView, descriptionTextView, eventStatus;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            timeTextView = itemView.findViewById(R.id.timeTextView);
            locationTextView = itemView.findViewById(R.id.locationTextView);
            descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
            eventStatus = itemView.findViewById(R.id.status_event_card);
        }
    }
}
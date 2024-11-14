package com.example.matata;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Map;

/**
 * EventAdapter is a RecyclerView adapter that binds a list of Event objects to views displayed in a RecyclerView.
 * Each Event is displayed in an item layout defined by event_card.xml, and clicking on an event item opens
 * the ViewEvent activity to display detailed information about the selected event.
 *
 * Outstanding issues: Currently, the adapter only displays a truncated version of the event description if it
 * exceeds 100 characters. Additional customization might be required to dynamically adjust this limit
 * or expand the text based on user interaction.
 */
public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {


    /**
     * A list of status strings representing the current status of each event.
     */
    private List<String> statusList;

    private String EVENT_ID;
    private FirebaseFirestore db;
    /**
     * The context in which the adapter or activity is operating.
     */
    private Context context;

    /**
     * A list of events containing details of each event.
     */
    private List<Event> eventList;
    private Map<String, String> posterUrls;
    /**
     * Constructs an EventAdapter with a specified context, list of events, and list of statuses.
     *
     * @param context the context in which the RecyclerView is being used
     * @param eventList the list of Event objects to display in the RecyclerView
     * @param status the list of event statuses corresponding to each event in the eventList
     */
    public EventAdapter(Context context, List<Event> eventList, List<String> status,  Map<String, String> posterUrls) {
        this.context = context;
        this.eventList = eventList;
        this.statusList = status;
        this.posterUrls= posterUrls;
    }

    /**
     * Creates a new EventViewHolder by inflating the event_card layout.
     *
     * @param parent the parent ViewGroup into which the new view will be added
     * @param viewType the view type of the new View
     * @return a new EventViewHolder holding an inflated event_card layout
     */
    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.event_card, parent, false);
        return new EventViewHolder(view);
    }

    /**
     * Binds data from an Event object to the views in the EventViewHolder.
     *
     * @param holder the view holder containing views for the Event item
     * @param position the position of the Event item within the list
     */
    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {

        Event event = eventList.get(position);
        String posterUrl = posterUrls.get(event.getEventid());

        if (posterUrl != null) {
            Glide.with(holder.itemView.getContext())
                    .load(posterUrl)
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.failed_image)
                    .into(holder.poster);
        } else {
            holder.poster.setImageResource(R.drawable.placeholder_image);
        }

        holder.titleTextView.setText(event.getTitle());
        holder.dateTextView.setText(event.getDate());
        holder.timeTextView.setText(event.getTime());
        holder.locationTextView.setText(event.getLocation());



//        String description = event.getDescription();
//        if (description.length() > 100) {
//            description = description.substring(0, 100) + "...";
//        }
        //holder.descriptionTextView.setText(description);
        holder.eventStatus.setText(statusList.get(position));

        // Set click listener on the entire event card
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create intent to open ViewEvent activity
                Intent intent = new Intent(context, ViewEvent.class);
                intent.putExtra("Unique_id", event.getEventid());

                // Start ViewEvent activity
                context.startActivity(intent);
            }
        });
    }

    /**
     * Returns the total number of items in the event list.
     *
     * @return the number of Event items to display
     */
    @Override
    public int getItemCount() {
        return eventList.size();
    }

    /**
     * EventViewHolder is a view holder for Event items in the RecyclerView. It holds references to
     * the TextViews in the event_card layout to display event information such as title, date, time,
     * location, description, and status.
     */
    public static class EventViewHolder extends RecyclerView.ViewHolder {

        TextView  titleTextView, dateTextView, timeTextView, locationTextView, eventStatus;
        ImageView poster;
        /**
         * Constructs an EventViewHolder with the specified itemView, binding its views to the layout elements.
         *
         * @param itemView the item view representing an event item in the RecyclerView
         */
        public EventViewHolder(@NonNull View itemView) {
            super(itemView);

            poster = itemView.findViewById(R.id.card_poster);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            timeTextView = itemView.findViewById(R.id.timeTextView);
            locationTextView = itemView.findViewById(R.id.locationTextView);
            eventStatus = itemView.findViewById(R.id.status_event_card);
        }
    }
}

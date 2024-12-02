package com.example.matata;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import android.graphics.drawable.GradientDrawable;
import androidx.palette.graphics.Palette;
import com.bumptech.glide.request.RequestOptions;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * {@code EventAdapter} is a {@link RecyclerView.Adapter} implementation that binds a list of {@link Event}
 * objects to views displayed in a {@link RecyclerView}. This adapter supports dynamic image loading,
 * background color generation using Glide and Palette, animations for a smooth user experience,
 * and search filtering for event titles.
 *
 * <h2>Features:</h2>
 * <ul>
 *     <li>Binds {@code Event} objects to a custom item layout defined in {@code event_card.xml}.</li>
 *     <li>Loads event poster images dynamically using Glide.</li>
 *     <li>Implements animations to enhance the visual appearance of items as they appear in the RecyclerView.</li>
 *     <li>Filters events based on a search query, updating the RecyclerView in real-time.</li>
 *     <li>Handles user interactions, such as opening an event's detailed view when a card is clicked.</li>
 * </ul>
 *
 * <h2>Usage:</h2>
 * <pre>
 * // Example setup for EventAdapter
 * List&lt;Event&gt; events = getEventList();
 * List&lt;String&gt; statuses = getStatuses();
 * Map&lt;String, String&gt; posterUrls = getPosterUrls();
 *
 * EventAdapter adapter = new EventAdapter(context, events, statuses, posterUrls);
 * recyclerView.setAdapter(adapter);
 * </pre>
 *
 * <h2>Outstanding Issues:</h2>
 * <ul>
 *     <li>The background color of the card is static; dynamic background color generation using Palette is not fully implemented.</li>
 *     <li>No caching mechanism for poster URLs is implemented; Glide handles caching internally.</li>
 * </ul>
 */
public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    /**
     * List of status strings representing the status of each event.
     */
    private List<String> statusList;

    /**
     * Context in which the adapter is used.
     */
    private Context context;

    /**
     * Index of the last animated item to prevent duplicate animations.
     */
    private int lastPosition = -1;

    /**
     * List of all Event objects.
     */
    private List<Event> eventList;

    /**
     * Filtered list of Event objects based on user search input.
     */
    private List<Event> searchList;

    /**
     * Map of event poster URLs keyed by event ID.
     */
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
        this.searchList = new ArrayList<>(eventList);
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
        Event event = searchList.get(position);
        String posterUrl = posterUrls.get(event.getEventid());
        holder.poster.setClipToOutline(true);
        Glide.with(holder.itemView.getContext())
                .load(posterUrl)
                .error(R.drawable.placeholder_image)
                .into(holder.poster);

        holder.titleTextView.setText(event.getTitle());
        holder.dateTextView.setText(event.getDate());
        holder.timeTextView.setText(event.getTime());
        holder.locationTextView.setText(event.getLocation());

        holder.eventStatus.setText(statusList.get(position));

        setAnimation(holder.itemView, position);
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
        return searchList.size();
    }

    /**
     * EventViewHolder holds references to the views for each Event item in the RecyclerView.
     */
    public static class EventViewHolder extends RecyclerView.ViewHolder {

        /**
         * LinearLayout for the card background.
         */
        LinearLayout card_bg;

        /**
         * TextView for the event title.
         */
        TextView titleTextView;

        /**
         * TextView for the event date.
         */
        TextView dateTextView;

        /**
         * TextView for the event time.
         */
        TextView timeTextView;

        /**
         * TextView for the event location.
         */
        TextView locationTextView;

        /**
         * TextView for the event status.
         */
        TextView eventStatus;

        /**
         * ImageView for the event poster.
         */
        ImageView poster;

        /**
         * Constructs an EventViewHolder with the specified itemView, binding its views to the layout elements.
         *
         * @param itemView the item view representing an event item in the RecyclerView
         */
        public EventViewHolder(@NonNull View itemView) {
            super(itemView);

            card_bg=itemView.findViewById(R.id.card_bg);
            poster = itemView.findViewById(R.id.card_poster);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            timeTextView = itemView.findViewById(R.id.timeTextView);
            locationTextView = itemView.findViewById(R.id.locationTextView);
            eventStatus = itemView.findViewById(R.id.status_event_card);
        }
    }

    /**
     * Animates the appearance of the RecyclerView item.
     *
     * @param animateView The view to animate.
     * @param pos         The position of the item in the list.
     */
    public void setAnimation(View animateView,int pos) {
        if (pos > lastPosition) {
            Animation animation =AnimationUtils.loadAnimation(context, R.anim.slide_in);
            animateView.startAnimation(animation);
            lastPosition=pos;
        }
    }

    /**
     * Filters the event list based on a search query and updates the RecyclerView.
     *
     * @param query The search query to filter events.
     */
    public void filter(String query) {
        searchList.clear();
        if (query.isEmpty()) {
            searchList.addAll(eventList);
        } else {
            for (Event event : eventList) {
                if (event.getTitle().toLowerCase().contains(query.toLowerCase())) {
                    searchList.add(event);
                }
            }
        }
        notifyDataSetChanged();
    }
}

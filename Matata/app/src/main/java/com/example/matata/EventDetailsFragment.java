package com.example.matata;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.matata.R;

/**
 * {@code EventDetailsFragment} is a {@link Fragment} that displays details of an event, including
 * its title, date, time, and a poster image. The fragment provides a tappable card interface
 * that allows users to navigate to the event's detailed page when clicked.
 *
 * <h2>Features:</h2>
 * <ul>
 *     <li>Displays the event's title, date, time, and poster image in a card layout.</li>
 *     <li>Supports dynamic image loading for the event poster using Glide.</li>
 *     <li>Includes a tappable card that opens the detailed view of the event when clicked.</li>
 * </ul>
 *
 * <h2>Usage:</h2>
 * <pre>
 * EventDetailsFragment fragment = EventDetailsFragment.newInstance(
 *         "Event Title", "2024-12-25", "19:00", "event123", "https://example.com/poster.jpg"
 * );
 * getSupportFragmentManager().beginTransaction()
 *         .replace(R.id.fragment_container, fragment)
 *         .commit();
 * </pre>
 *
 * <h2>Outstanding Issues:</h2>
 * <ul>
 *     <li>The fragment assumes that the event details (e.g., title, date, time, and poster URL)
 *         are passed correctly via the arguments. No validation is performed.</li>
 *     <li>The default placeholder and error images for Glide are static. Consider making them configurable.</li>
 * </ul>
 */
public class EventDetailsFragment extends Fragment {

    /**
     * Title of the event.
     */
    private String title;

    /**
     * Date of the event.
     */
    private String date;

    /**
     * Time of the event.
     */
    private String time;

    /**
     * Unique identifier of the event.
     */
    private String eventId;

    /**
     * URL of the event poster image.
     */
    private String posterUrl;

    /**
     * LinearLayout representing the tappable card for navigating to the event details page.
     */
    private LinearLayout cardTap;

    /**
     * Factory method to create a new instance of this fragment using the provided parameters.
     *
     * @param title     The title of the event.
     * @param date      The date of the event.
     * @param time      The time of the event.
     * @param eventId   The unique identifier of the event.
     * @param posterUrl The URL of the event poster image.
     * @return A new instance of EventDetailsFragment.
     */
    public static EventDetailsFragment newInstance(String title, String date, String time,
                                                    String eventId,String posterUrl) {
        EventDetailsFragment fragment = new EventDetailsFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("date", date);
        args.putString("time", time);
        args.putString("eventId", eventId);
        args.putString("posterUrl", posterUrl);
        Log.wtf(TAG,posterUrl);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Called when the fragment is created.
     * Retrieves arguments passed during fragment creation.
     *
     * @param savedInstanceState If the fragment is being re-created, this contains the data it most recently supplied.
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            title = getArguments().getString("title");

            date = getArguments().getString("date");
            time = getArguments().getString("time");

            eventId = getArguments().getString("eventId");
            posterUrl=getArguments().getString("posterUrl");
        }
    }

    /**
     * Called to create the view hierarchy for the fragment.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate views in the fragment.
     * @param container          If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
     * @return The root view of the fragment's layout.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.event_card_swipe, container, false);
        // Bind views
        TextView eventTitle = view.findViewById(R.id.SwipeTitle);
        TextView eventDate = view.findViewById(R.id.SwipeDate);
        TextView eventTime = view.findViewById(R.id.SwipeTime);
        ImageView poster=view.findViewById(R.id.SwipePoster);

        cardTap=view.findViewById(R.id.swipe_preview);
        cardTap.setOnClickListener(v-> {
            Intent intent = new Intent(requireContext(), ViewEvent.class);
            intent.putExtra("Unique_id", eventId);
            startActivity(intent);
        });

        eventTitle.setText(title);
        eventDate.setText(date);
        eventTime.setText(time);

        Glide.with(this).load(posterUrl).placeholder(R.drawable.placeholder_image).error(R.drawable.failed_image).into(poster);

        return view;
    }
}

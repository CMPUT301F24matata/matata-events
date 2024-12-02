package com.example.matata;

import static android.content.ContentValues.TAG;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.List;
import java.util.Map;

/**
 * The {@code EventPagerAdapter} is an adapter for managing fragments in a {@link androidx.viewpager2.widget.ViewPager2}.
 * Each fragment represents the details of a specific event, allowing users to swipe through events in a paginated format.
 *
 * <h2>Features:</h2>
 * <ul>
 *     <li>Binds a list of {@link Event} objects to fragments in the ViewPager2.</li>
 *     <li>Fetches and displays event-specific poster images using a provided map of poster URLs.</li>
 *     <li>Dynamically creates fragments for each event, ensuring smooth swiping between pages.</li>
 * </ul>
 *
 * <h2>Usage:</h2>
 * The adapter is used in conjunction with a ViewPager2 to display event details in a swipeable format. To set up:
 * <pre>
 * ViewPager2 viewPager = findViewById(R.id.viewPager);
 * EventPagerAdapter adapter = new EventPagerAdapter(this, eventList, posterUrls);
 * viewPager.setAdapter(adapter);
 * </pre>
 *
 * <h2>Limitations:</h2>
 * <ul>
 *     <li>The adapter requires the {@link EventDetailsFragment} to properly display event details.</li>
 *     <li>No caching or preloading of fragments is implemented; fragments are created on demand.</li>
 * </ul>
 */
public class EventPagerAdapter extends FragmentStateAdapter {

    /**
     * List of events to be displayed in the ViewPager2.
     */
    private final List<Event> eventList;

    /**
     * Map containing the poster URLs for the events, where the key is the event ID.
     */
    private final Map<String, String> posterUrls;

    /**
     * Constructs an `EventPagerAdapter` with the given fragment activity, event list, and poster URLs.
     *
     * @param fragmentActivity The activity hosting the ViewPager2.
     * @param eventList        The list of events to display.
     * @param posterUrls       A map of event IDs to their corresponding poster URLs.
     */
    public EventPagerAdapter(@NonNull FragmentActivity fragmentActivity, List<Event> eventList, Map<String, String> posterUrls) {
        super(fragmentActivity);
        this.eventList = eventList;
        this.posterUrls=posterUrls;

    }

    /**
     * Creates a fragment for the given position in the ViewPager2.
     * Each fragment displays the details of a specific event.
     *
     * @param position The position of the event in the event list.
     * @return A fragment containing the details of the event at the given position.
     */
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Event event = eventList.get(position);
        String posterUrl=posterUrls.get(event.getEventid());
        Log.wtf("Test", posterUrl);
        return EventDetailsFragment.newInstance(
                event.getTitle(),
                event.getDate(),
                event.getTime(),
                event.getEventid(),
                posterUrl
        );
    }

    /**
     * Returns the total number of events in the list.
     *
     * @return The number of events to display in the ViewPager2.
     */
    @Override
    public int getItemCount() {
        return eventList.size();
    }
}

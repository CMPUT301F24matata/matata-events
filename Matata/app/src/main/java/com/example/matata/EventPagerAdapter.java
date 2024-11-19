package com.example.matata;

import static android.content.ContentValues.TAG;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.List;
import java.util.Map;

public class EventPagerAdapter extends FragmentStateAdapter {

    private final List<Event> eventList;
    private final Map<String,String>posterUrls;

    public EventPagerAdapter(@NonNull FragmentActivity fragmentActivity, List<Event> eventList, Map<String, String> posterUrls) {
        super(fragmentActivity);
        this.eventList = eventList;
        this.posterUrls=posterUrls;

    }

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
    @Override
    public int getItemCount() {
        return eventList.size();
    }
}
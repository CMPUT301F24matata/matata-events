package com.example.matata;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.matata.Event;

import java.util.List;

public class EventPagerAdapter extends FragmentStateAdapter {

    private final List<Event> eventList;

    public EventPagerAdapter(@NonNull FragmentActivity fragmentActivity, List<Event> eventList) {
        super(fragmentActivity);
        this.eventList = eventList;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Event event = eventList.get(position);
        return EventDetailsFragment.newInstance(
                event.getTitle(),
                event.getDate(),
                event.getTime(),
                event.getEventid()
        );
    }
    @Override
    public int getItemCount() {
        return eventList.size();
    }
}
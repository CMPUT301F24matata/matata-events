package com.example.matata;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.matata.R;

public class EventDetailsFragment extends Fragment {

    private String title,  date, time, eventId;


    public static EventDetailsFragment newInstance(String title, String date, String time,
                                                    String eventId) {
        EventDetailsFragment fragment = new EventDetailsFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("date", date);
        args.putString("time", time);
        args.putString("eventId", eventId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            title = getArguments().getString("title");

            date = getArguments().getString("date");
            time = getArguments().getString("time");

            eventId = getArguments().getString("eventId");


        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.event_card_swipe, container, false);

        // Bind views
        TextView eventTitle = view.findViewById(R.id.SwipeTitle);

        TextView eventDate = view.findViewById(R.id.SwipeDate);
        TextView eventTime = view.findViewById(R.id.SwipeTime);


        // Set data
        eventTitle.setText(title);
        eventDate.setText(date);
        eventTime.setText(time);
        return view;
    }
}
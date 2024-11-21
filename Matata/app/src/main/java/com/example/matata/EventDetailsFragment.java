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

public class EventDetailsFragment extends Fragment {

    private String title,  date, time, eventId,posterUrl;
    private LinearLayout cardTap;


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
package com.example.matata;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ViewEvent extends AppCompatActivity {
    private ImageView goBack;
    //private ImageView poster;
    private TextView title;
    private TextView capacity;
    private TextView desc;
    private TextView time;
    private TextView date;
    private TextView location;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        title=findViewById(R.id.ViewEventTitle);
        capacity=findViewById(R.id.ViewEventCapacity);
        desc=findViewById(R.id.ViewEventDesc);
        time=findViewById(R.id.ViewEventTime);
        date=findViewById(R.id.ViewEventDate);
        location=findViewById(R.id.ViewEventLoc);



        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_event_details);

        goBack=findViewById(R.id.go_back_view_event);

        goBack.setOnClickListener(v->{
            finish();
        });



    }

}

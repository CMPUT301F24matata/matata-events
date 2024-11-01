package com.example.matata;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ViewEvent extends AppCompatActivity {
    private ImageView goBack;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_event_details);

        goBack=findViewById(R.id.go_back_view_event);

        goBack.setOnClickListener(v->{
            finish();
        });


    }

}

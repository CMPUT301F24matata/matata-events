package com.example.matata;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.maps.MapFragment;

public class ExploreEvents extends AppCompatActivity {

    private ImageView goBack;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.explore_events);

        goBack=findViewById(R.id.goBackMap);



        goBack.setOnClickListener(v->finish());
        loadMapFragment();

    }

    public void loadMapFragment(){

        FragmentManager fragmentManager =getSupportFragmentManager();
        FragmentTransaction fragmentTransaction =fragmentManager.beginTransaction();
        MapsActivity mapsActivity = new MapsActivity();
        fragmentTransaction.replace(R.id.map_display, mapsActivity);
        fragmentTransaction.commit();

    }
}

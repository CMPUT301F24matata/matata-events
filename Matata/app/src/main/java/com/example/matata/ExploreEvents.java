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

/**
 * The ExploreEvents class provides an interface for users to explore events on a map.
 * This activity includes functionality for displaying a map fragment and a back button to navigate to the previous screen.
 */
public class ExploreEvents extends AppCompatActivity {
    /**
     * ImageView for navigating back to the previous screen.
     */
    private ImageView goBack;

    /**
     * Called when the activity is created.
     * Initializes the UI components, sets up the back button listener, and loads the map fragment.
     *
     * @param savedInstanceState Bundle containing the activity's previously saved state, if any.
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.explore_events);

        goBack=findViewById(R.id.goBackMap);

        goBack.setOnClickListener(v->finish());
        loadMapFragment();

    }

    /**
     * Loads the map fragment into the container specified in the layout.
     * Uses the FragmentManager to handle the fragment transaction.
     */
    public void loadMapFragment(){

        FragmentManager fragmentManager =getSupportFragmentManager();
        FragmentTransaction fragmentTransaction =fragmentManager.beginTransaction();
        MapsActivity mapsActivity = new MapsActivity();
        fragmentTransaction.replace(R.id.map_display, mapsActivity);
        fragmentTransaction.commit();

    }
}

package com.example.matata;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.maps.MapFragment;

/**
 * The {@code ExploreEvents} class provides an activity that allows users to explore events
 * using an interactive map interface. The activity includes functionality to load a map
 * fragment dynamically and provides a back button for navigation to the previous screen.
 *
 * <h2>Features:</h2>
 * <ul>
 *     <li>Displays an interactive map where users can view and explore events.</li>
 *     <li>Allows navigation back to the previous activity using a back button.</li>
 *     <li>Integrates with a {@link MapsActivity} fragment to handle map-related functionality.</li>
 * </ul>
 *
 * <h2>Usage:</h2>
 * This activity is designed to be launched from another part of the application where users
 * want to explore events in a geographic context. It initializes a map fragment to handle
 * location-based interactions.
 *
 * <h2>Limitations:</h2>
 * <ul>
 *     <li>The activity assumes the presence of a layout file {@code explore_events.xml} with the required container and back button.</li>
 *     <li>Relies on {@link MapsActivity} for map functionality; ensure that class is implemented correctly.</li>
 * </ul>
 */
public class ExploreEvents extends AppCompatActivity {
    /**
     * ImageView for navigating back to the previous screen.
     */
    private ImageView goBack;
    private String par_act;
    private String EventID;
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
        par_act=getIntent().getStringExtra("prev_act");
        try{
            EventID=getIntent().getStringExtra("Unique_id");
        }catch (Exception e){
            throw e;
        }
        goBack.setOnClickListener(v->finish());
        loadMapFragment();

    }

    /**
     * Loads the {@link MapsActivity} fragment into the container specified in the layout.
     * Uses the {@link FragmentManager} to perform a fragment transaction.
     *
     * <h3>Behavior:</h3>
     * <ul>
     *     <li>Replaces the current fragment in the container with the {@link MapsActivity} fragment.</li>
     *     <li>Commits the transaction to display the map fragment.</li>
     * </ul>
     */
    public void loadMapFragment(){

        FragmentManager fragmentManager =getSupportFragmentManager();
        FragmentTransaction fragmentTransaction =fragmentManager.beginTransaction();
        Fragment mapsActivity;

        if (par_act.equals("main")) {
            mapsActivity = new MapsActivity();
        } else if (par_act.equals("draw")) {
            mapsActivity = new MapsActivityEntrant();

            if (EventID != null) {
                Bundle args = new Bundle();
                args.putString("Unique_id", EventID);
                mapsActivity.setArguments(args);
            } else {
                Log.e("LoadMapFragment", "EventID is null!");
            }
        } else {
            throw new IllegalArgumentException("Unknown activity type: " + par_act);
        }

        fragmentTransaction.replace(R.id.map_display, mapsActivity);
        fragmentTransaction.commit();

    }
}

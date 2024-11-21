package com.example.matata;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.matata.databinding.ActivityMapsBinding;

/**
 * The MapsActivity class is a fragment that integrates a Google Map.
 * It displays a styled map with a marker at a predefined location (Edmonton).
 */
public class MapsActivity extends Fragment implements OnMapReadyCallback {

    /**
     * GoogleMap instance to handle map operations.
     */
    private GoogleMap mMap;

    /**
     * Inflates the fragment layout and initializes the SupportMapFragment.
     * Registers the fragment as the callback for the map when it is ready.
     *
     * @param inflater  The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container The parent view that the fragment's UI should be attached to.
     * @param savedInstanceState A Bundle containing the fragment's previously saved state, if any.
     * @return The root View for the fragment's UI, or null.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_maps, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        return view;
    }

    /**
     * Called when the Google Map is ready to be used.
     * Sets a custom map style, places a marker at Edmonton, and animates the camera to that location.
     *
     * @param googleMap The GoogleMap object associated with the fragment.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        try {
            googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.map_style));
        } catch (Exception e) {
            e.printStackTrace(); // Handle the exception gracefully
        }

        LatLng edmonton = new LatLng(53.5461, -113.4937);
        mMap.addMarker(new MarkerOptions().position(edmonton).title("Marker in Edmonton"));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(edmonton, 11));
    }
}
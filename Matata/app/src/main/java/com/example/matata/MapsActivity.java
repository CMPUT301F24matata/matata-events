package com.example.matata;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

/**
 * Displays a Google Map with event markers fetched from Firestore.
 */
public class MapsActivity extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_maps, container, false);
        db = FirebaseFirestore.getInstance();

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng edmonton = new LatLng(53.5461, -113.4937);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(edmonton, 11));
        try {
            googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.map_style));
        } catch (Exception e) {
            e.printStackTrace();
        }

        fetchEventMarkers();
    }

    /**
     * Fetches events from Firestore and adds them to the map as markers.
     */
    private void fetchEventMarkers() {
        db.collection("FACILITY_PROFILES")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String name = document.getString("name");
                        GeoPoint geoPoint = document.getGeoPoint("location");
                        String owner = document.getString("owner");

                        if (geoPoint != null) {
                            LatLng facilityLatLng = new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude());
                            addMarker(facilityLatLng, name, "Owner: " + owner);
                        } else {
                            Log.e("Firestore", "GeoPoint is null for document: " + document.getId());
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Failed to fetch events: " + e.getMessage());
                });
    }

    /**
     * Parses a string location (latitude,longitude) into a LatLng object.
     *
     * @param location String location in the format "latitude,longitude"
     * @return LatLng object or null if the format is invalid
     */
    private LatLng getLatLngFromLocation(String location) {
        try {
            // Remove brackets and split by comma
            location = location.replace("[", "").replace("]", "");
            String[] parts = location.split(",");
            double latitude = Double.parseDouble(parts[0].trim());
            double longitude = Double.parseDouble(parts[1].trim());
            return new LatLng(latitude, longitude);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Adds a marker to the map with the given position, title, and snippet.
     *
     * @param position LatLng position of the marker
     * @param title    Title for the marker
     * @param snippet  Additional information (e.g., date)
     */
    private void addMarker(LatLng position, String title, String snippet) {
        if (position != null) {
            mMap.addMarker(new MarkerOptions()
                    .position(position)
                    .title(title)
                    .snippet(snippet)); // Add additional info (e.g., owner)
        }
    }
}


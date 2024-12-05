package com.example.matata;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.content.Intent;
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
    private final Map<String, Integer> facilityEventCount = new HashMap<>();

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

        try {
            googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.map_style));
        } catch (Exception e) {
            e.printStackTrace();
        }
        LatLng edmonton = new LatLng(53.5461, -113.4937);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(edmonton, 11));

        fetchEventMarkers();

        mMap.setOnInfoWindowClickListener(marker -> {
            String eventId = marker.getSnippet();
            Log.wtf("EVENT ID",eventId);
            if (eventId != null) {
                Intent intent = new Intent(requireContext(), ViewEvent.class);
                intent.putExtra("Unique_id", eventId);
                startActivity(intent);
            }
        });
    }

    /**
     * Fetches events from Firestore and adds them to the map as markers.
     */
    private void fetchEventMarkers() {
        db.collection("EVENT_PROFILES") // Replace with your events collection name
                .get()
                .addOnSuccessListener(eventSnapshots -> {
                    for (QueryDocumentSnapshot eventDoc : eventSnapshots) {

                        String eventTitle = eventDoc.getString("Title");
                        String organizerId = eventDoc.getString("OrganizerID");
                        String eventId = eventDoc.getId();

                        if (organizerId != null) {
                            fetchFacilityLocation(organizerId, eventTitle, eventId);
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error fetching events: " + e.getMessage()));
    }


    private void fetchFacilityLocation(String facilityId, String eventTitle, String eventId) {
        db.collection("FACILITY_PROFILES").document(facilityId)
                .get()
                .addOnSuccessListener(facilityDoc -> {
                    if (facilityDoc.exists()) {
                        GeoPoint geoPoint = facilityDoc.getGeoPoint("location");
                        if (geoPoint != null) {
                            // Convert GeoPoint to LatLng
                            LatLng facilityLatLng = new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude());

                            // Check if this facility already has markers (to add noise)
                            int count = facilityEventCount.getOrDefault(facilityId, 0);
                            LatLng adjustedLatLng = addNoiseToLatLng(facilityLatLng, count);

                            // Update event count for this facility
                            facilityEventCount.put(facilityId, count + 1);

                            // Add marker to the map with event details
                            addMarker(adjustedLatLng, eventTitle, eventId);
                        } else {
                            Log.e("FirestoreDebug", "GeoPoint is null for facility: " + facilityId);
                        }
                    } else {
                        Log.e("FirestoreDebug", "Facility not found for ID: " + facilityId);
                    }
                })
                .addOnFailureListener(e -> Log.e("FirestoreDebug", "Error fetching facility: " + e.getMessage()));
    }
    private LatLng addNoiseToLatLng(LatLng originalLatLng, int count) {
        double noiseFactor = 0.0001; // Adjust this value for larger/smaller noise
        double noiseLat = (Math.random() - 0.5) * noiseFactor; // Random noise for latitude
        double noiseLng = (Math.random() - 0.5) * noiseFactor; // Random noise for longitude

        // Add noise based on count to create consistent offset
        double adjustedLat = originalLatLng.latitude + (noiseLat * count);
        double adjustedLng = originalLatLng.longitude + (noiseLng * count);

        return new LatLng(adjustedLat, adjustedLng);
    }



    /**
     * Adds a marker to the map with the given position, title, and snippet.
     *
     * @param position LatLng position of the marker
     * @param title    Title for the marker
     * @param eventId  Additional information (e.g., date)
     */
    private void addMarker(LatLng position, String title, String eventId) {
        if (position != null) {
            mMap.addMarker(new MarkerOptions()
                    .position(position)
                    .title(title)
                    .snippet(eventId));
        }
    }
}


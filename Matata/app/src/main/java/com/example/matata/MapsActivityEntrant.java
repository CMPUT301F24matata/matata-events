package com.example.matata;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class MapsActivityEntrant extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FirebaseFirestore db;
    private String EventID;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_maps, container, false);
        db = FirebaseFirestore.getInstance();
        if (getArguments() != null) {
            EventID=getArguments().getString("Unique_id");
        }

        if (EventID == null) {
            Log.e("MapsActivityEntrant", "EventID is null!");
        } else {
            Log.d("MapsActivityEntrant", "Received EventID: " + EventID);
        }

        Log.wtf("GOT IT",EventID);
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
        if (EventID != null) {
            fetchAndDisplayMarkers(EventID);
        }
    }
    private void fetchAndDisplayMarkers(String eventId) {
        db = FirebaseFirestore.getInstance();

        db.collection("EVENT_PROFILES").document(eventId).collection("USER_LOCATIONS")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        double latitude = doc.getDouble("latitude");
                        double longitude = doc.getDouble("longitude");
                        String name = doc.getString("name");

                        LatLng position = new LatLng(latitude, longitude);
                        mMap.addMarker(new MarkerOptions()
                                .position(position)
                                .title(name != null ? name : "Unnamed Marker"));
                    }
                })
                .addOnFailureListener(e -> Log.e("MapsActivityEntrant", "Error fetching markers", e));
    }
}
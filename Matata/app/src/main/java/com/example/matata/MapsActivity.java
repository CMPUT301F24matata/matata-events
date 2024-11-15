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

public class MapsActivity extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;

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

        @Override
        public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        try {
            googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle( requireContext(), R.raw.map_style));


        }
        catch (Exception e){
            throw e;
        }
        LatLng edmonton = new LatLng(53.5461, -113.4937);
        mMap.addMarker(new MarkerOptions().position(edmonton).title("Marker in Edmonton"));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(edmonton, 11));
    }
}
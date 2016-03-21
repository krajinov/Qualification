package com.krajinov.qualification;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private String name;
    private float lat;
    private float lon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        name = getIntent().getStringExtra(MainActivity.PERSON_NAME);
        lat = getIntent().getFloatExtra(MainActivity.MAP_LAT, 0);
        lon = getIntent().getFloatExtra(MainActivity.MAP_LONG, 0);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng perLoc = new LatLng(lat, lon);
        mMap.addMarker(new MarkerOptions().position(perLoc).title(name));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(perLoc));
    }
}

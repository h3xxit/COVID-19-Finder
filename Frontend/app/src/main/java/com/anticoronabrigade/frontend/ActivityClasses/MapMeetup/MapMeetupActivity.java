package com.anticoronabrigade.frontend.ActivityClasses.MapMeetup;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.anticoronabrigade.frontend.ActivityClasses.BackgroundServices.GetLocationService;
import com.anticoronabrigade.frontend.ActivityClasses.Main.MeetupLocationsDto;
import com.anticoronabrigade.frontend.ActivityClasses.Main.MeetupPointDto;
import com.anticoronabrigade.frontend.ActivityClasses.Main.PointDto;
import com.anticoronabrigade.frontend.R;
import com.anticoronabrigade.frontend.UtilityClasses.Const;
import com.anticoronabrigade.frontend.UtilityClasses.CustomInfoAdapter;
import com.anticoronabrigade.frontend.UtilityClasses.ParcelableUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MapMeetupActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {

    GoogleMap map;
    MarkerOptions place1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        Toolbar toolbar = findViewById(R.id.toolbar_map);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        ImageView swapView = toolbar.findViewById(R.id.swap_view);
        swapView.setVisibility(View.INVISIBLE);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        registerReceiver(pressOnBack, new IntentFilter(Const.MAP_ACTIVITY_BACK));

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                Criteria criteria = new Criteria();

                @SuppressLint("MissingPermission") Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
                if (location != null)
                {
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 13));

                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(new LatLng(location.getLatitude(), location.getLongitude()))      // Sets the center of the map to location user
                            .zoom(17)                   // Sets the zoom
                            .build();                   // Creates a CameraPosition from the builder
                    map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                }
            }
        });
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Add a marker in Sydney, Australia,
        // and move the map's camera to the same location.
        map = googleMap;
        Intent intent = getIntent();
        byte[] bytes = intent.getByteArrayExtra("MeetUpLocations");
        MeetupLocationsDto path = ParcelableUtil.unmarshall(bytes, MeetupLocationsDto.CREATOR);

        googleMap.setMyLocationEnabled(true);

        for(MeetupPointDto meetupPointDto : path.getPoints()) {
            PointDto point = new PointDto(meetupPointDto.getPoint());
            Long date = meetupPointDto.getDate();

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(date);
            DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
            String dateToShow = dateFormatter.format(calendar.getTime());

            googleMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(point.getLatitude(), point.getLongitude()))
                                    .snippet(dateToShow)
                                );
            googleMap.setInfoWindowAdapter(new CustomInfoAdapter(getLayoutInflater()));
            map.setOnInfoWindowClickListener(this);
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(point.getLatitude(), point.getLongitude())));
            map.getUiSettings().setMapToolbarEnabled(false);
        }

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();

        Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
        if (location != null)
        {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 13));

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(location.getLatitude(), location.getLongitude()))      // Sets the center of the map to location user
                    .zoom(17)                   // Sets the zoom
                    .build();                   // Creates a CameraPosition from the builder
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(pressOnBack);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(pressOnBack, new IntentFilter(Const.MAP_ACTIVITY_BACK));
    }

    BroadcastReceiver pressOnBack = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("pressOnBack", "received onBackPress");
            onBackPressed();
        }
    };
}

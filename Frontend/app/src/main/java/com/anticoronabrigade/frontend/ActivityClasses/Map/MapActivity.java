package com.anticoronabrigade.frontend.ActivityClasses.Map;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.anticoronabrigade.frontend.ActivityClasses.Main.MeetupLocationsDto;
import com.anticoronabrigade.frontend.ActivityClasses.Main.MeetupPointDto;
import com.anticoronabrigade.frontend.ActivityClasses.Main.PointDto;
import com.anticoronabrigade.frontend.ActivityClasses.BackgroundServices.GetLocationService;
import com.anticoronabrigade.frontend.ObjectClasses.MapLocation;
import com.anticoronabrigade.frontend.ObjectClasses.WalkedPath;
import com.anticoronabrigade.frontend.R;
import com.anticoronabrigade.frontend.UtilityClasses.Const;
import com.anticoronabrigade.frontend.UtilityClasses.ParcelableUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.Calendar;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

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

    private Long map(Long x, Long in_min, Long in_max, Long out_min, Long out_max) {
        return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Add a marker in Sydney, Australia,
        // and move the map's camera to the same location.
        map = googleMap;
        Intent intent = getIntent();
        byte[] bytes = intent.getByteArrayExtra("mapPaths");
        MeetupLocationsDto path = ParcelableUtil.unmarshall(bytes, MeetupLocationsDto.CREATOR);

        googleMap.setMyLocationEnabled(true);

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

        for(MeetupPointDto meetupPointDto : path.getPoints()) {
            PointDto point = new PointDto(meetupPointDto.getPoint());
            Long date = meetupPointDto.getDate();
            Long currentDate = Calendar.getInstance().getTimeInMillis();

            googleMap.addCircle(new CircleOptions()
                    .center(new LatLng(point.getLatitude(), point.getLongitude()))
                    .radius(10)
                    .strokeWidth(0)
                    .fillColor(Color.argb(60, 255, map(Math.max(Const.MIN_DAYS_IN_MILLISECONDS, currentDate-date), Const.MIN_DAYS_IN_MILLISECONDS, Const.MAX_DAYS_IN_MILLISECONDS, 0L, 255L).intValue(), 0)));
        }
        /*LatLng sydney = new LatLng(-33.852, 151.211);
        googleMap.addMarker(new MarkerOptions().position(sydney)
                .title("Marker in Sydney"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/
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

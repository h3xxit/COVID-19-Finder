package com.anticoronabrigade.frontend.ActivityClasses.MapHistory;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.anticoronabrigade.frontend.ActivityClasses.BackgroundServices.GetLocationService;
import com.anticoronabrigade.frontend.ActivityClasses.HistoryActivity.H2Activity;
import com.anticoronabrigade.frontend.ActivityClasses.HistoryActivity.PathHistory;
import com.anticoronabrigade.frontend.ActivityClasses.Main.MainActivity;
import com.anticoronabrigade.frontend.ActivityClasses.Main.MeetupLocationsDto;
import com.anticoronabrigade.frontend.ActivityClasses.Main.MeetupPointDto;
import com.anticoronabrigade.frontend.ActivityClasses.Main.PointDto;
import com.anticoronabrigade.frontend.DatabaseSimulator;
import com.anticoronabrigade.frontend.ObjectClasses.MapLocation;
import com.anticoronabrigade.frontend.ObjectClasses.WalkedPath;
import com.anticoronabrigade.frontend.R;
import com.anticoronabrigade.frontend.UtilityClasses.Const;
import com.anticoronabrigade.frontend.UtilityClasses.CustomInfoAdapter;
import com.anticoronabrigade.frontend.UtilityClasses.ParcelableUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.nio.file.Path;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MapMeetupHistoryActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {

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
        swapView.setVisibility(View.VISIBLE);
        swapView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
                Intent intent = new Intent(MapMeetupHistoryActivity.this, H2Activity.class);
                startActivity(intent);
            }
        });

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


        googleMap.setMyLocationEnabled(true);

        /*List<WalkedPath> walkedPaths = DatabaseSimulator.allWalkedPaths;
        MeetupLocationsDto points = new MeetupLocationsDto();

        if(walkedPaths!=null){
            for (WalkedPath path : walkedPaths) {
                Long initialDate = path.getDate();
                for (MapLocation loc : path.getPoints()) {
                    points.getPoints().add(new MeetupPointDto(new PointDto(loc.latitude, loc.longitude), initialDate+TICK_RATE*1000));
                }
            }
        }

        for(MeetupPointDto meetupPointDto : points.getPoints()) {
            PointDto point = new PointDto(meetupPointDto.getPoint());
            Long date = meetupPointDto.getDate();

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date);
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        String dateToShow = dateFormatter.format(calendar.getTime());

        googleMap.addMarker(new MarkerOptions()
                                .position(new LatLng(point.getLatitude(), point.getLongitude()))
                                .title(dateToShow)
                            );
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(point.getLatitude(), point.getLongitude())));
        }*/

        for(WalkedPath s : DatabaseSimulator.allWalkedPaths)
        {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(s.getDate());
            DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
            String firstDate = dateFormatter.format(calendar.getTime());
            calendar.setTimeInMillis(s.getDate() + (s.getPoints().size()-1)* Const.TICK_RATE * 1000);
            String secondDate = dateFormatter.format(calendar.getTime());

            googleMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(s.getPoints().get(0).latitude, s.getPoints().get(0).longitude))
                                    .snippet(getResources().getString(R.string.path_start_date) + firstDate +"\n"+ getResources().getString(R.string.path_stop_date) + secondDate)
                                );
            googleMap.setInfoWindowAdapter(new CustomInfoAdapter(getLayoutInflater()));
            map.setOnInfoWindowClickListener(this);
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(s.getPoints().get(0).latitude, s.getPoints().get(0).longitude)));
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

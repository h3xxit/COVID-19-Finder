package com.anticoronabrigade.frontend.ActivityClasses.ViewInfectedMeetupPoints;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.ComponentCallbacks2;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.anticoronabrigade.frontend.ActivityClasses.BackgroundServices.GetLocationService;
import com.anticoronabrigade.frontend.ActivityClasses.HistoryActivity.PathHistory;
import com.anticoronabrigade.frontend.ActivityClasses.HistoryActivity.RecyclerViewAdapter;
import com.anticoronabrigade.frontend.ActivityClasses.Main.MeetupLocationsDto;
import com.anticoronabrigade.frontend.ActivityClasses.Main.MeetupPointDto;
import com.anticoronabrigade.frontend.DatabaseSimulator;
import com.anticoronabrigade.frontend.ObjectClasses.WalkedPath;
import com.anticoronabrigade.frontend.R;
import com.anticoronabrigade.frontend.UtilityClasses.ParcelableUtil;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ViewInfectedMeetsActivity extends AppCompatActivity implements RecyclerViewAdapter.OnNoteListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_infected_meets);

        Toolbar toolbar = findViewById(R.id.toolbar_infected);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.view_infected_rv);
        //recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        addToList();

        RecyclerViewAdapter adapter = new RecyclerViewAdapter(PathsToShow, this);
        recyclerView.setAdapter(adapter);
    }

    private List<PathHistory> PathsToShow;

    public void addToList(){
        Intent intent = getIntent();
        byte[] bytes = intent.getByteArrayExtra("MeetUpLocations");
        MeetupLocationsDto meetupLocationsDto = ParcelableUtil.unmarshall(bytes, MeetupLocationsDto.CREATOR);

        PathsToShow = new ArrayList<>();

        for(MeetupPointDto s : meetupLocationsDto.getPoints())
        {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(s.getDate());
            DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
            PathsToShow.add(new PathHistory(s.getPoint().toString(), s.getPoint().toString(), dateFormatter.format(calendar.getTime()), getResources().getString(R.string.met_with_inf_at), getResources().getString(R.string.meeting_date)));
        }
    }

    @Override
    public void onNoteClick(int position) {
        Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + PathsToShow.get(position).getLocation() + " (" + getResources().getString(R.string.meetup_location_google_maps) + ")");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}

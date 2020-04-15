package com.anticoronabrigade.frontend.ActivityClasses.HistoryActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.anticoronabrigade.frontend.ActivityClasses.Main.MainActivity;
import com.anticoronabrigade.frontend.ActivityClasses.MapHistory.MapMeetupHistoryActivity;
import com.anticoronabrigade.frontend.DatabaseSimulator;
import com.anticoronabrigade.frontend.ObjectClasses.WalkedPath;
import com.anticoronabrigade.frontend.R;
import com.anticoronabrigade.frontend.UtilityClasses.Const;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class H2Activity extends AppCompatActivity implements RecyclerViewAdapter.OnNoteListener{

    private List<PathHistory> PathsToShow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_h2);
        Toolbar toolbar = findViewById(R.id.toolbar_history);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycle_view);
        //recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        ImageView swapView = toolbar.findViewById(R.id.swap_view);
        swapView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
                Intent intent = new Intent(H2Activity.this, MapMeetupHistoryActivity.class);
                startActivity(intent);
            }
        });

        addToList();

        RecyclerViewAdapter adapter = new RecyclerViewAdapter(PathsToShow, this);
        recyclerView.setAdapter(adapter);
    }

    public void addToList(){
        PathsToShow = new ArrayList<>();

        for(WalkedPath s : DatabaseSimulator.allWalkedPaths)
        {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(s.getDate());
            DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
            String firstDate = dateFormatter.format(calendar.getTime());
            calendar.setTimeInMillis(s.getDate() + (s.getPoints().size()-1)* Const.TICK_RATE * 1000);
            String secondDate = dateFormatter.format(calendar.getTime());
            PathsToShow.add(new PathHistory(s.getPoints().get(0).toString(), firstDate, secondDate, getResources().getString(R.string.path_start_date), getResources().getString(R.string.path_stop_date)));
        }
    }

    @Override
    public void onNoteClick(int position) {
        Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + PathsToShow.get(position).getLocation() + " (" + getResources().getString(R.string.start_path_location_google_maps) + ")");
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

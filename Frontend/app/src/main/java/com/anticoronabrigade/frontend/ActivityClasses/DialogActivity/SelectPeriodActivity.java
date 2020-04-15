package com.anticoronabrigade.frontend.ActivityClasses.DialogActivity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.anticoronabrigade.frontend.ActivityClasses.BackgroundServices.GetLocationService;
import com.anticoronabrigade.frontend.ActivityClasses.Main.CustomRunnable;
import com.anticoronabrigade.frontend.ActivityClasses.Main.Loading;
import com.anticoronabrigade.frontend.ActivityClasses.Main.MainActivity;
import com.anticoronabrigade.frontend.ActivityClasses.Main.SelectPeriod;
import com.anticoronabrigade.frontend.DatabaseSimulator;
import com.anticoronabrigade.frontend.ObjectClasses.MapLocation;
import com.anticoronabrigade.frontend.ObjectClasses.WalkedPath;
import com.anticoronabrigade.frontend.R;
import com.anticoronabrigade.frontend.UtilityClasses.Const;

import net.grandcentrix.tray.AppPreferences;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Calendar;

public class SelectPeriodActivity extends AppCompatActivity {

    AppPreferences appPreferences;
    Spinner spinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_period);
        spinner = findViewById(R.id.periodSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getApplicationContext(),
                R.array.select_hours_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        Button ok = findViewById(R.id.okButton);
        Button cancel = findViewById(R.id.cancelButton);
        appPreferences = new AppPreferences(getApplicationContext());
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                appPreferences.put("service", "service");
                Intent intent = new Intent(getApplicationContext(), GetLocationService.class);
                int startSec = Calendar.getInstance().get(Calendar.SECOND);
                int delay = calculateDelay(startSec) * 1000;
                intent.putExtra("delay", delay);
                long selectedItem = spinner.getSelectedItemId();
                long time = (selectedItem == 0) ? Const.ONE_MINUTE_IN_MILLISECONS * 30
                        : (selectedItem == 1) ? Const.ONE_MINUTE_IN_MILLISECONS * 60
                        : (selectedItem == 2) ? Const.ONE_MINUTE_IN_MILLISECONS * 90
                        : Const.ONE_MINUTE_IN_MILLISECONS * 120;
                intent.putExtra("maxDuration", time);
                //intent.putExtra("maxDuration", 10000);
                startService(intent);

                finish();
                //startTrackingBtn.setText(R.string.stop_tracking);
            }
        });
    }

    private int calculateDelay(int seconds) {
        int delay = Const.TICK_RATE;
        while (delay < 60) {
            if (seconds < delay)
                return delay - seconds;
            delay += Const.TICK_RATE;
        }
        return 60 - seconds;
    }

}

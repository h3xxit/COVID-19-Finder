package com.anticoronabrigade.frontend.ActivityClasses.BackgroundServices;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.anticoronabrigade.frontend.ActivityClasses.Main.MainActivity;
import com.anticoronabrigade.frontend.ObjectClasses.MapLocation;
import com.anticoronabrigade.frontend.ObjectClasses.WalkedPath;
import com.anticoronabrigade.frontend.R;
import com.anticoronabrigade.frontend.UtilityClasses.Const;
import com.anticoronabrigade.frontend.UtilityClasses.ParcelableUtil;

import net.grandcentrix.tray.AppPreferences;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class GetLocationService extends Service implements LocationListener {
    boolean isGPSEnable = false;
    boolean isNetworkEnable = false;
    double latitude,longitude;
    LocationManager locationManager;
    Location location;
    private Handler mHandler = new Handler();
    private Timer mTimer = null;

    AppPreferences appPreferences;

    long uptime;

    PowerManager powerManager;
    PowerManager.WakeLock powerManagerWakeLock;

    private List<MapLocation> currentPath;
    private int currentPathIndex;
    private Long startDate;

    File myFile;

    public GetLocationService() {

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        appPreferences = new AppPreferences(getApplicationContext());
        powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        powerManagerWakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "corona:GetLocationWakeLock");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(mTimer != null) {
            mTimer.cancel();
            mTimer.purge();
        }

        myFile = new File(getFilesDir(), "CrashFix");
        try {
            myFile.createNewFile();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            currentPath = new ArrayList<>();
            currentPathIndex = 0;
            startDate = null;

            int delay = 0;
            if (intent.hasExtra("delay")) {
                delay = intent.getIntExtra("delay", 0);
            }
            uptime = Calendar.getInstance().getTimeInMillis() + intent.getLongExtra("maxDuration", 30 * 60000);
            mTimer = new Timer();
            mTimer.schedule(new TimerTaskToGetLocation(), delay, Const.TICK_RATE * 1000);
            if (powerManagerWakeLock != null)
                powerManagerWakeLock.acquire();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel("default", "ServiceNotification", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "default");
        builder.setSmallIcon(R.drawable.logo);
        builder.setContentTitle(getResources().getString(R.string.currently_tracking));
        builder.setContentText(getResources().getString(R.string.click_to_open_app));
        builder.setAutoCancel(false);
        PendingIntent notifIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class).putExtra("widgetPress", false).setAction("Ping"), 0);
        builder.setContentIntent(notifIntent);

        Notification notification = builder.build();

        startForeground(1, notification);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private void fn_getlocation(){
        currentPathIndex++;

        try {
            locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
            isGPSEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            isNetworkEnable = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnable && !isNetworkEnable) {
            } else {

                if (isGPSEnable) {
                    location = null;
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);
                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (location != null) {
                            Log.e("latitude", location.getLatitude() + "");
                            Log.e("longitude", location.getLongitude() + "");
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                            fn_update(location);
                            return;
                        }
                    }
                }

                if (isNetworkEnable) {
                    location = null;
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, this);
                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {

                            Log.e("latitude", location.getLatitude() + "");
                            Log.e("longitude", location.getLongitude() + "");

                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                            fn_update(location);
                            return;
                        }
                    }

                }
            }
        }
        catch (SecurityException e) {

        }
        catch (Exception e){
            e.printStackTrace();
        }

        if(startDate != null && startDate != 0L)
        {
            FileOutputStream fOut = null;
            OutputStreamWriter myOutWriter = null;
            try {
                fOut = new FileOutputStream(new File(getFilesDir(), "CrashFix"), true);
                myOutWriter = new OutputStreamWriter(fOut);
                MapLocation mapLocation = new MapLocation(1000d, 1000d);
                Log.e("latitude", 1000d + "");
                Log.e("longitude", 1000d + "");
                currentPath.add(mapLocation);
                myOutWriter.append(mapLocation.stringSeparatedByEnter()).append("\n");
                myOutWriter.flush();
                myOutWriter.close();
                fOut.flush();
                fOut.close();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if(myOutWriter != null)
                        myOutWriter.close();
                    if(fOut != null)
                        fOut.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private class TimerTaskToGetLocation extends TimerTask {
        @Override
        public void run() {
            try {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (Calendar.getInstance().getTimeInMillis() > uptime) {
                                Log.e("Service", "Process stopped because it exceded uptime");
                                stopSelf();
                            } else if (appPreferences.getString("service", "").matches("service"))
                                fn_getlocation();
                            else {
                                Log.e("Service", "Process stopped");
                                stopSelf();
                            }
                        } catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void fn_update(Location location){
        FileOutputStream fOut = null;
        OutputStreamWriter myOutWriter = null;
        try {
            fOut = new FileOutputStream(new File(getFilesDir(), "CrashFix"), true);
            myOutWriter = new OutputStreamWriter(fOut);
            MapLocation mapLocation = new MapLocation(location.getLatitude(), location.getLongitude());
            if(startDate == null || startDate==0L)
            {
                startDate = Calendar.getInstance().getTimeInMillis();
                myOutWriter.append(startDate.toString()).append("\n");
            }
            currentPath.add(mapLocation);
            myOutWriter.append(mapLocation.stringSeparatedByEnter()).append("\n");
            myOutWriter.flush();
            myOutWriter.close();
            fOut.flush();
            fOut.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                if(myOutWriter != null)
                    myOutWriter.close();
                if(fOut != null)
                    fOut.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        /*intent.putExtra("latutide",location.getLatitude()+"");
        intent.putExtra("longitude",location.getLongitude()+"");
        sendBroadcast(intent);*/
    }

    @Override
    public void onDestroy() {
        Log.e("Service", "Process exited");

        stopForeground(true);

        if (powerManagerWakeLock != null)
            powerManagerWakeLock.release();

        if (startDate == null)
            startDate = -1L;

        if (appPreferences.getString("service", "").matches("service")) {
            Intent setOnBackPress = new Intent(Const.MAP_ACTIVITY_BACK);
            sendBroadcast(setOnBackPress);

            Intent intent = new Intent(Const.SERVICE_FINISHED_RECEIVER);
            sendBroadcast(intent);

            appPreferences.put("service", "crash");
        }
        /*else {
            intent.putExtra("pathArray", bytes);
            sendBroadcast(intent);
        }*/
        mTimer.cancel();
        mTimer.purge();
    }
}

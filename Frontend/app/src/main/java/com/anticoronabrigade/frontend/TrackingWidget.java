package com.anticoronabrigade.frontend;

import android.Manifest;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.anticoronabrigade.frontend.ActivityClasses.BackgroundServices.GetLocationService;
import com.anticoronabrigade.frontend.ActivityClasses.DialogActivity.SelectPeriodActivity;
import com.anticoronabrigade.frontend.ActivityClasses.Main.ApiUtils;
import com.anticoronabrigade.frontend.ActivityClasses.Main.CustomRunnable;
import com.anticoronabrigade.frontend.ActivityClasses.Main.Loading;
import com.anticoronabrigade.frontend.ActivityClasses.Main.MainActivity;
import com.anticoronabrigade.frontend.ActivityClasses.Main.PathListDto;
import com.anticoronabrigade.frontend.ActivityClasses.Main.SelectPeriod;
import com.anticoronabrigade.frontend.ActivityClasses.Main.UserAPI;
import com.anticoronabrigade.frontend.ActivityClasses.Main.UserDto;
import com.anticoronabrigade.frontend.ObjectClasses.MapLocation;
import com.anticoronabrigade.frontend.ObjectClasses.WalkedPath;
import com.anticoronabrigade.frontend.UtilityClasses.Const;

import net.grandcentrix.tray.AppPreferences;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Calendar;
import java.util.Random;

import javax.security.auth.login.LoginException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TrackingWidget extends AppWidgetProvider {

    AppPreferences appPreferences;
    public void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.tracking_widget);
        ComponentName widget = new ComponentName(context, TrackingWidget.class);

        views.setOnClickPendingIntent(R.id.widgetStartTracking, getPendingSelfIntent(context, Const.MyOnClick));

        appWidgetManager.updateAppWidget(widget, views);
    }

    protected PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("widgetPress", true);
        Random random = new Random();
        intent.setAction("Yeet" + random.nextInt());
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;
    }

    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        if(Const.MyOnClick.equals(intent.getAction())) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

            RemoteViews remoteViews;
            ComponentName watchWidget;

            remoteViews = new RemoteViews(context.getPackageName(), R.layout.tracking_widget);
            watchWidget = new ComponentName(context, TrackingWidget.class);

            onTrackingButtonPressed(context, remoteViews, watchWidget);

            appWidgetManager.updateAppWidget(watchWidget, remoteViews);
        }
    }

    public void onTrackingButtonPressed(Context context, RemoteViews remoteViews, ComponentName watchWidget) {
        /*appPreferences = new AppPreferences(context.getApplicationContext());

        if (appPreferences.getString("service", "").matches("")) {
            if (checkLocationPermissions(context)) {
                Intent intent = new Intent(context, SelectPeriodActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
                remoteViews.setOnClickPendingIntent(R.id.widgetStartTracking, pendingIntent);
            }
            else {
                //Toast.makeText(context, R.string.widget_permissions_missing, Toast.LENGTH_LONG).show();
            }
        }
        else {
            File myFile = new File(context.getFilesDir(), "CrashFix");
            try {
                WalkedPath path = new WalkedPath();
                FileInputStream fIn = new FileInputStream(myFile);
                BufferedReader myReader = new BufferedReader(new InputStreamReader(fIn));
                String aDataRow = "";
                String aBuffer = "";
                aDataRow = myReader.readLine();
                path.setDate(Long.parseLong(aDataRow));
                while ((aDataRow = myReader.readLine()) != null) {
                    Double lat = Double.parseDouble(aDataRow);
                    aDataRow = myReader.readLine();
                    Double lon = Double.parseDouble(aDataRow);
                    path.getPoints().add(new MapLocation(lat, lon));
                }
                myReader.close();
                myFile.delete();
                DatabaseSimulator.allWalkedPaths.add(path);
                if (DatabaseSimulator.currentUser.getIsInfected() == 0) {
                    FileOutputStream fOut = null;
                    OutputStreamWriter myOutWriter = null;
                    try {
                        fOut = new FileOutputStream(new File(context.getFilesDir(), Const.WALKED_PATH_HISTORY_FILE), true);
                        myOutWriter = new OutputStreamWriter(fOut);


                        myOutWriter.append(path.getDate().toString());
                        myOutWriter.append('|');
                        for (MapLocation mapLocation : path.getPoints()) {
                            if(mapLocation == null)
                                break;
                            myOutWriter.append(mapLocation.latitude.toString());
                            myOutWriter.append('&');
                            myOutWriter.append(mapLocation.longitude.toString());
                            myOutWriter.append(' ');
                        }
                        myOutWriter.append('\n');

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
                } else {
                    addPaths(context);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            appPreferences.put("service", "");
        }*/
    }

    public void addPaths(Context context) {
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED) {
        } else {
            PathListDto pathDto = new PathListDto();
            pathDto.setAllPaths(DatabaseSimulator.allWalkedPaths);
            //pathDto.setEmail(DatabaseSimulator.currentUser.getEmail());
            //pathDto.setPassword(DatabaseSimulator.currentUser.getPassword());
            UserAPI userAPI = ApiUtils.getAPIService();
            userAPI.addPaths(pathDto).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        //Toast toast=Toast.makeText(MainActivity.this, "Path added!", Toast.LENGTH_LONG);
                        //toast.show();
                        System.out.println("Paths added");
                    } else {
                        //Toast toast=Toast.makeText(MainActivity.this, "Something went wrong. Please sign out and open app again. Thank you!", Toast.LENGTH_LONG);
                        //toast.show();
                        System.out.println("Error when trying to upload paths in addPaths " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {

                    Toast toast = Toast.makeText(context, R.string.network_failed, Toast.LENGTH_LONG);
                    toast.show();
                    System.out.println("Network failed");
                    System.out.println(t.getMessage());
                }
            });
        }
    }

    private boolean checkLocationPermissions(Context context) {

        final LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage(R.string.please_location)
                    .setCancelable(true)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, final int id) {
                            dialog.dismiss();
                        }
                    });
            final AlertDialog alert = builder.create();
            alert.show();
            return false;
        }

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            int permissionCheck = 0;
            permissionCheck += context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION);
            permissionCheck += context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
            permissionCheck += context.checkSelfPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION);
            if (permissionCheck == -3) {

                return false;
            }
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent intent = new Intent();
            String packageName = context.getPackageName();
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }


}


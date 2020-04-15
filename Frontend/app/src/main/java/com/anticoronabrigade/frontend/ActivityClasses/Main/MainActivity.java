package com.anticoronabrigade.frontend.ActivityClasses.Main;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.anticoronabrigade.frontend.ActivityClasses.BackgroundServices.GetLocationService;
import com.anticoronabrigade.frontend.ActivityClasses.HistoryActivity.H2Activity;
import com.anticoronabrigade.frontend.ActivityClasses.Map.MapActivity;
import com.anticoronabrigade.frontend.DatabaseSimulator;
import com.anticoronabrigade.frontend.ObjectClasses.MapLocation;
import com.anticoronabrigade.frontend.ObjectClasses.WalkedPath;
import com.anticoronabrigade.frontend.R;
import com.anticoronabrigade.frontend.UtilityClasses.Const;
import com.anticoronabrigade.frontend.UtilityClasses.ParcelableUtil;
import com.anticoronabrigade.frontend.UtilityClasses.ReadWriteFileHandler;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import android.os.PowerManager;
import android.provider.Settings;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import net.grandcentrix.tray.AppPreferences;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    //region Variables

    CardView startTrackingCV, stopTrackingCV;
    CardView reportInfectedCV, testPathCV;

    TextView startTrackingTV, stopTrackingTV;
    TextView reportInfectedTV, testPathTV;

    ReadWriteFileHandler IOFiles;

    TextView textViewEmail;
    FusedLocationProviderClient fusedLocationProviderClient;

    Double latitude, longitude;
    Loading loading;

    AppPreferences appPreferences;

    Menu nav_menu;

    public static boolean startedByWidget;
    //endregion

    //region Init
    private UserAPI userAPI;
    private static final int INTERNET_PERMISSION = 103;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userAPI = ApiUtils.getAPIService();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar();
        AppBarLayout appBarLayout = findViewById(R.id.appbarLayoutMain);
        StateListAnimator stateListAnimator = new StateListAnimator();
        stateListAnimator.addState(new int[0], ObjectAnimator.ofFloat(appBarLayout, "elevation", 0));
        appBarLayout.setStateListAnimator(stateListAnimator);

        /*StateListAnimator stateListAnimator = new StateListAnimator();
        stateListAnimator.addState(new int[0], ObjectAnimator.ofFloat(view, "elevation", 0));
        AppBarLayout appBarLayout = findViewById(R.id.appbarLayoutMain);
        appBarLayout.setStateListAnimator(stateListAnimator);*/
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        nav_menu = navigationView.getMenu();

        appPreferences = new AppPreferences(getApplicationContext());

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
                if(slideOffset != 0 && DatabaseSimulator.currentUser!=null) {
                    if(DatabaseSimulator.currentUser.getIsInfected() == 0)
                        nav_menu.findItem(R.id.nav_history).setVisible(true);
                    else
                        nav_menu.findItem(R.id.nav_history).setVisible(false);

                    if(appPreferences.getString("service", "").matches("")) {
                        nav_menu.findItem(R.id.nav_map).setVisible(false);
                    }
                    else
                        nav_menu.findItem(R.id.nav_map).setVisible(true);
                }
            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });

        startTrackingCV = findViewById(R.id.startTrackingCV);
        stopTrackingCV = findViewById(R.id.stopTrackingCV);
        reportInfectedCV = findViewById(R.id.reportInfectedCV);
        testPathCV = findViewById(R.id.testPathCV);
        startTrackingTV = findViewById(R.id.startTrackingTV);
        stopTrackingTV = findViewById(R.id.stopTrackingTV);
        reportInfectedTV = findViewById(R.id.reportInfectedTV);
        testPathTV = findViewById(R.id.testPathTV);

        testPathCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPaths();
            }
        });

        IOFiles = new ReadWriteFileHandler(this);
        DatabaseSimulator.allWalkedPaths = IOFiles.protectedReadPathsFromFile(Const.WALKED_PATH_HISTORY_FILE);

        //textViewEmail = navigationView.getHeaderView(0).findViewById(R.id.textViewEmail);
        //textViewEmail.setText(R.string.not_logged_in);

        registerReceiver(broadcastReceiver, new IntentFilter(Const.SERVICE_FINISHED_RECEIVER));

        openDialog();
    }

    private void openDialog() {
        ReadWriteFileHandler readWriteFileHandler = new ReadWriteFileHandler(this);
        DatabaseSimulator.currentUser = readWriteFileHandler.protectedReadUserFromFile(Const.USER_FILE_NAME);
        //textViewEmail.setText("");
        if(DatabaseSimulator.currentUser == null) {
            DatabaseSimulator.currentUser = new UserDto("", "", 0);
            readWriteFileHandler.protectedWriteToFile(Const.USER_FILE_NAME, DatabaseSimulator.currentUser);
            return;
        }
        if(DatabaseSimulator.currentUser.getIsInfected() == 1) {
            reportInfectedTV.setText(R.string.report_not_infected);
            reportInfectedCV.setBackground(getDrawable(R.drawable.btn_rounded_complement));
        }
        else{
            reportInfectedTV.setText(R.string.report_infected);
            reportInfectedCV.setBackground(getDrawable(R.drawable.btn_rounded));
        }

        /*if (DatabaseSimulator.currentUser == null) {
            Login login = new Login(getSupportFragmentManager(), userAPI, MainActivity.this, this, textViewEmail, reportInfectedCV);
            login.show(login.getFrgManager(), "Login");
            login.setCancelable(false);
        } else {
            loading=new Loading(getSupportFragmentManager(), startTrackingCV, reportInfectedCV, testPathCV);
            loading.show(loading.getFrgManager(), "Loading");
            loading.setCancelable(false);

            login();
        }*/
    }
    //endregion

    //region ClientToServer
    public void login(){
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.INTERNET},
                    INTERNET_PERMISSION);
            if(loading != null)
                loading.closeDialog();
        } else {
            User user = new User();
            user.setEmail(DatabaseSimulator.currentUser.getEmail());
            user.setPassword(DatabaseSimulator.currentUser.getPassword());

            Login log = new Login(getSupportFragmentManager(), userAPI, MainActivity.this, this, textViewEmail, reportInfectedCV, reportInfectedTV);

            userAPI.findUser(user).enqueue(new Callback<Boolean>() {
                @Override
                public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                    if(response.isSuccessful()){
                        Integer infected = response.body() ? 1 : 0;
                        DatabaseSimulator.currentUser.setIsInfected(infected);
                        if(loading != null)
                            loading.closeDialog();
                        //textViewEmail.setText(DatabaseSimulator.currentUser.getEmail());
                        if(DatabaseSimulator.currentUser.getIsInfected() == 1) {
                            reportInfectedTV.setText(R.string.report_not_infected);
                            reportInfectedCV.setBackground(getDrawable(R.drawable.btn_rounded_complement));
                        }
                        else{
                            reportInfectedTV.setText(R.string.report_infected);
                            reportInfectedCV.setBackground(getDrawable(R.drawable.btn_rounded));
                        }
                    } else {
                        if(loading != null)
                            loading.closeDialog();
                        log.show(getSupportFragmentManager(), "Login");
                        log.setCancelable(false);
                    }
                }

                @Override
                public void onFailure(Call<Boolean> call, Throwable t) {
                    Toast toast = Toast.makeText(MainActivity.this, R.string.network_failed, Toast.LENGTH_LONG);
                    toast.show();
                    System.out.println("Network failed");
                    System.out.println(t.getMessage());
                    if(loading != null)
                        loading.closeDialog();

                    log.show(getSupportFragmentManager(), "Login");
                    log.setCancelable(false);
                }
            });
        }
    }

    public void addPaths() {
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.INTERNET},
                    INTERNET_PERMISSION);
            if(loading!=null)
                loading.closeDialog();
        } else {
            PathListDto pathDto = new PathListDto();
            pathDto.setAllPaths(DatabaseSimulator.allWalkedPaths);
            //pathDto.setEmail(DatabaseSimulator.currentUser.getEmail());
            //pathDto.setPassword(DatabaseSimulator.currentUser.getPassword());

            userAPI.addPaths(pathDto).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        //Toast toast=Toast.makeText(MainActivity.this, "Path added!", Toast.LENGTH_LONG);
                        //toast.show();
                        IOFiles.protectedWriteToFile(Const.USER_FILE_NAME, new UserDto("", "", 1));

                        DatabaseSimulator.currentUser.setIsInfected(1);
                        reportInfectedTV.setText(R.string.report_not_infected);
                        reportInfectedCV.setBackground(getDrawable(R.drawable.btn_rounded_complement));

                        DatabaseSimulator.allWalkedPaths.clear();
                        try {
                            deleteFile(Const.WALKED_PATH_HISTORY_FILE);
                        } catch (Exception e) {

                        }
                        if(loading!=null)
                            loading.closeDialog();
                        System.out.println("Paths added");
                    } else {
                        //Toast toast=Toast.makeText(MainActivity.this, "Something went wrong. Please sign out and open app again. Thank you!", Toast.LENGTH_LONG);
                        //toast.show();
                        if(loading!=null)
                            loading.closeDialog();
                        System.out.println("Error when trying to upload paths in addPaths " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    if(loading!=null)
                        loading.closeDialog();
                    Toast toast = Toast.makeText(MainActivity.this, R.string.network_failed, Toast.LENGTH_LONG);
                    toast.show();
                    System.out.println("Network failed");
                    System.out.println(t.getMessage());
                }
            });
        }
    }

    public void changeInfected(String email, final Boolean infected) {
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.INTERNET},
                    INTERNET_PERMISSION);
            if(loading != null)
                loading.closeDialog();
        } else {
            InfectedDto user = new InfectedDto();

            user.setEmail(DatabaseSimulator.currentUser.getEmail());
            user.setPassword(DatabaseSimulator.currentUser.getPassword());
            user.setInfected(infected);
            userAPI.changeInfected(user).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if(loading != null)
                        loading.closeDialog();
                    if (response.isSuccessful() && response.body() != null) {
                        if(infected) {
                            IOFiles.protectedWriteToFile(Const.USER_FILE_NAME, new UserDto(DatabaseSimulator.currentUser.getEmail(), DatabaseSimulator.currentUser.getPassword(), 1));

                            DatabaseSimulator.currentUser.setIsInfected(1);
                            reportInfectedTV.setText(R.string.report_not_infected);
                            reportInfectedCV.setBackground(getDrawable(R.drawable.btn_rounded_complement));
                        }
                        else {
                            IOFiles.protectedWriteToFile(Const.USER_FILE_NAME, new UserDto(DatabaseSimulator.currentUser.getEmail(), DatabaseSimulator.currentUser.getPassword(), 0));

                            DatabaseSimulator.currentUser.setIsInfected(0);
                            reportInfectedTV.setText(R.string.report_infected);
                            reportInfectedCV.setBackground(getDrawable(R.drawable.btn_rounded));
                        }
                        Toast toast = Toast.makeText(MainActivity.this, R.string.status_changed, Toast.LENGTH_LONG);
                        toast.show();
                    } else {
                        Toast toast = Toast.makeText(MainActivity.this, R.string.problem_sign_out_restart, Toast.LENGTH_LONG);
                        toast.show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    if(loading != null)
                        loading.closeDialog();
                    Toast toast = Toast.makeText(MainActivity.this, R.string.network_failed, Toast.LENGTH_LONG);
                    toast.show();
                    System.out.println("Network failed");
                    System.out.println(t.getMessage());
                }
            });
        }
    }

    public void checkPaths() {
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.INTERNET},
                    INTERNET_PERMISSION);
            if(loading!=null)
                loading.closeDialog();
        } else {
            startTrackingCV.setEnabled(false);
            reportInfectedCV.setEnabled(false);
            testPathCV.setEnabled(false);
            loading = new Loading(getSupportFragmentManager(), startTrackingCV, reportInfectedCV, testPathCV);
            loading.show(loading.getFrgManager(), "Loading");
            loading.setCancelable(false);
            PathListDto pathDto = new PathListDto();
            pathDto.setAllPaths(DatabaseSimulator.allWalkedPaths);
            //pathDto.setEmail(DatabaseSimulator.currentUser.getEmail());
            //pathDto.setPassword(DatabaseSimulator.currentUser.getPassword());
            userAPI.checkPaths(pathDto).enqueue(new Callback<MeetupLocationsDto>() {
                @Override
                public void onResponse(Call<MeetupLocationsDto> call, Response<MeetupLocationsDto> response) {
                    if(loading != null)
                        loading.closeDialog();
                    if (response.isSuccessful() && response.body() != null) {
                        MeetupLocationsDto meetupLocationsDto = response.body();
                        if(meetupLocationsDto.getPoints().size()>0) {
                            InfectedPath infectedPath = new InfectedPath(getSupportFragmentManager(), MainActivity.this, meetupLocationsDto);
                            infectedPath.show(infectedPath.getFrgManager(), "Infected Paths");
                            infectedPath.setCancelable(false);
                        }
                        else
                            Toast.makeText(MainActivity.this, R.string.not_meet_infected, Toast.LENGTH_LONG).show();

                    } else {
                        System.out.println("Error when trying to upload paths in checkPath " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<MeetupLocationsDto> call, Throwable t) {
                    if(loading != null)
                        loading.closeDialog();
                    Toast toast = Toast.makeText(MainActivity.this, R.string.network_failed, Toast.LENGTH_LONG);
                    toast.show();
                    System.out.println("Network failed");
                    System.out.println(t.getMessage());
                }
            });
        }
    }

    public void getPaths(){
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.INTERNET},
                    INTERNET_PERMISSION);
            if(loading!=null)
                loading.closeDialog();
        } else {
            startTrackingCV.setEnabled(false);
            reportInfectedCV.setEnabled(false);
            testPathCV.setEnabled(false);
            loading = new Loading(getSupportFragmentManager(), startTrackingCV, reportInfectedCV, testPathCV);
            loading.show(loading.getFrgManager(), "Loading");
            loading.setCancelable(false);
            if(checkLocationPermissions()){
                fusedLocationProviderClient.getLastLocation()
                        .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                if(location!=null){
                                    userAPI.getInfectedPathsNearYou(location.getLatitude(), location.getLongitude()).enqueue(new Callback<List<WalkedPath>>() {
                                        @Override
                                        public void onResponse(Call<List<WalkedPath>> call, Response<List<WalkedPath>> response) {
                                            if (response.isSuccessful()) {

                                                if(loading != null)
                                                    loading.closeDialog();

                                                try {
                                                    Intent intent = new Intent(MainActivity.this, MapActivity.class);

                                                    List<WalkedPath> walkedPaths = response.body();
                                                    MeetupLocationsDto points = new MeetupLocationsDto();

                                                    if(walkedPaths!=null){
                                                        for (WalkedPath path : walkedPaths) {
                                                            Long initialDate = path.getDate();
                                                            for (MapLocation location : path.getPoints()) {
                                                                points.getPoints().add(new MeetupPointDto(new PointDto(location.latitude, location.longitude), initialDate+Const.TICK_RATE*1000));
                                                            }
                                                        }
                                                    }

                                                    byte[] bytes = ParcelableUtil.marshall(points);
                                                    intent.putExtra("mapPaths", bytes);
                                                    startActivity(intent);

                                                } catch (NullPointerException e) {
                                                    e.printStackTrace();
                                                }

                                            } else {
                                                if(loading != null)
                                                    loading.closeDialog();
                                                Toast.makeText(MainActivity.this, getResources().getString(R.string.error_unauthorized), Toast.LENGTH_LONG).show();
                                            }

                                        }

                                        @Override
                                        public void onFailure(Call<List<WalkedPath>> call, Throwable t) {
                                            Toast toast = Toast.makeText(MainActivity.this, R.string.network_failed, Toast.LENGTH_LONG);
                                            toast.show();
                                            System.out.println("Network failed");
                                            System.out.println(t.getMessage());
                                            if(loading != null)
                                                loading.closeDialog();
                                        }
                                    });
                                } else {
                                    Toast.makeText(MainActivity.this, getResources().getString(R.string.error_location), Toast.LENGTH_LONG).show();
                                    if(loading != null)
                                        loading.closeDialog();
                                }
                            }
                        })
                        .addOnFailureListener(this, new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MainActivity.this, getResources().getString(R.string.error_location), Toast.LENGTH_LONG).show();
                                if(loading != null)
                                    loading.closeDialog();
                            }
                        });
            } else {
                loading.closeDialog();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case INTERNET_PERMISSION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    changeInfected(DatabaseSimulator.currentUser.getEmail(), false);
                } else {
                    Context context = MainActivity.this;
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, R.string.internet_permission, duration);
                    toast.show();
                }
                return;
            }
        }
    }
    //endregion

    //region Tracking
    public void onClickReportInfected(View view) {
        startTrackingCV.setEnabled(false);
        reportInfectedCV.setEnabled(false);
        testPathCV.setEnabled(false);
        AreYouSure areYouSure = new AreYouSure(getSupportFragmentManager(), new Runnable(){
            @Override
            public void run() {
                /*if (DatabaseSimulator.currentUser == null) {
                    //Toast.makeText(this, "Error try logging in again", Toast.LENGTH_LONG).show();
                    startTrackingCV.setEnabled(true);
                    reportInfectedCV.setEnabled(true);
                    testPathCV.setEnabled(true);
                    return;
                }
                if (DatabaseSimulator.currentUser.getIsInfected() == 1) {
                    loading = new Loading(getSupportFragmentManager(), startTrackingCV, reportInfectedCV, testPathCV);
                    loading.show(loading.getFrgManager(), "Loading");
                    loading.setCancelable(false);
                    changeInfected(DatabaseSimulator.currentUser.getEmail(), false);
                } else {
                    loading = new Loading(getSupportFragmentManager(), startTrackingCV, reportInfectedCV, testPathCV);
                    loading.show(loading.getFrgManager(), "Loading");
                    loading.setCancelable(false);
                    addPaths();
                    changeInfected(DatabaseSimulator.currentUser.getEmail(), true);

                }*/
                if(DatabaseSimulator.currentUser.getIsInfected() == 0) {
                    loading = new Loading(getSupportFragmentManager(), startTrackingCV, reportInfectedCV, testPathCV);
                    loading.show(loading.getFrgManager(), "Loading");
                    loading.setCancelable(false);
                    addPaths();
                }
                else {
                    IOFiles.protectedWriteToFile(Const.USER_FILE_NAME, new UserDto("", "", 0));

                    DatabaseSimulator.currentUser.setIsInfected(0);
                    reportInfectedTV.setText(R.string.report_infected);
                    reportInfectedCV.setBackground(getDrawable(R.drawable.btn_rounded));
                    startTrackingCV.setEnabled(true);
                    reportInfectedCV.setEnabled(true);
                    testPathCV.setEnabled(true);
                }
            }
        }, startTrackingCV, reportInfectedCV, testPathCV);
        areYouSure.show(areYouSure.getFrgManager(), "AreYouSure");
        areYouSure.setCancelable(false);
    }

    public void onClickStartTracking(View view) {
        //if (appPreferences.getString("service", "").matches("")) {
        if (checkLocationPermissions()) {
            startTrackingCV.setEnabled(false);
            reportInfectedCV.setEnabled(false);
            testPathCV.setEnabled(false);
            MainActivity mainActivity = this;
            SelectPeriod selectPeriod = new SelectPeriod(getSupportFragmentManager(), this, new CustomRunnable() {
                @Override
                public void run() {
                    appPreferences.put("service", "service");
                    Intent intent = new Intent(getApplicationContext(), GetLocationService.class);
                    int startSec = Calendar.getInstance().get(Calendar.SECOND);
                    int delay = calculateDelay(startSec) * 1000;
                    intent.putExtra("delay", delay);
                    intent.putExtra("maxDuration", getTime());
                    //intent.putExtra("maxDuration", 10000);
                    startService(intent);
                    //startTrackingCV.setText(R.string.stop_tracking);
                    startTrackingCV.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimaryLightContrast)));
                    startTrackingCV.setEnabled(false);
                    stopTrackingCV.setBackgroundTintList(null);
                    stopTrackingCV.setEnabled(true);

                    reportInfectedCV.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimaryLightContrast)));
                    testPathCV.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimaryLightContrast)));
                    reportInfectedCV.setEnabled(false);
                    testPathCV.setEnabled(false);
                    startTrackingCV.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimaryLightContrast)));
                    if(MainActivity.startedByWidget) {
                        MainActivity.startedByWidget = false;
                        Intent i = new Intent(Intent.ACTION_MAIN);
                        i.addCategory(Intent.CATEGORY_HOME);
                        startActivity(i);
                        onBackPressed();
                    }
                }
            }, startTrackingCV, reportInfectedCV, testPathCV);
            selectPeriod.show(selectPeriod.getFrgManager(), "SelectPeriod");
            selectPeriod.setCancelable(false);
        } else {
            if(MainActivity.startedByWidget) {
                MainActivity.startedByWidget = false;
            }
        }
    }

    public void onClickStopTracking(View view) {
        startTrackingCV.setEnabled(false);
        reportInfectedCV.setEnabled(false);
        testPathCV.setEnabled(false);
        File myFile = new File(getFilesDir(), "CrashFix");
        try {
            WalkedPath path = new WalkedPath();
            FileInputStream fIn = new FileInputStream(myFile);
            BufferedReader myReader = new BufferedReader(new InputStreamReader(fIn));
            String aDataRow = "";
            String aBuffer = "";
            aDataRow = myReader.readLine();
            if(aDataRow == null || "".equals(aDataRow))
                throw new Exception();
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
                IOFiles.protectedWriteToFile(Const.WALKED_PATH_HISTORY_FILE, DatabaseSimulator.allWalkedPaths);
            } else {
                loading=new Loading(getSupportFragmentManager());
                loading.show(loading.getFrgManager(), "Loading");
                loading.setCancelable(false);
                addPaths();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        appPreferences.put("service", "");
        //startTrackingCV.setText(R.string.start_tracking);
        startTrackingCV.setBackgroundTintList(null);
        startTrackingCV.setBackground(getDrawable(R.drawable.btn_rounded));
        startTrackingCV.setEnabled(true);

        stopTrackingCV.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimaryLightContrast)));
        stopTrackingTV.setText(getResources().getString(R.string.stop_tracking));
        stopTrackingCV.setEnabled(false);

        reportInfectedCV.setBackgroundTintList(null);
        testPathCV.setBackgroundTintList(null);
        reportInfectedCV.setEnabled(true);
        testPathCV.setEnabled(true);
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

    private boolean checkLocationPermissions() {

        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
            permissionCheck += this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION);
            permissionCheck += this.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
            permissionCheck += this.checkSelfPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION);
            if (permissionCheck != 0) {

                this.requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION}, 1); //Any number
                if (permissionCheck == -3) {
                    Toast.makeText(this, R.string.location_permission, Toast.LENGTH_LONG).show();
                    return false;
                }
            }
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent intent = new Intent();
            String packageName = getPackageName();
            PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + packageName));
                startActivity(intent);
                Toast.makeText(this, R.string.background_permission, Toast.LENGTH_LONG).show();
                return false;
            }
        }
        return true;
    }

    public BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(stopTrackingTV.getText() == getResources().getString(R.string.stop_tracking) && stopTrackingCV.getBackgroundTintList() == null)
                stopTrackingTV.setText(R.string.save_path);
        }
    };
    //endregion

    //region App lifecycle
    @Override
    protected void onResume() {
        super.onResume();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
        registerReceiver(broadcastReceiver, new IntentFilter(Const.SERVICE_FINISHED_RECEIVER));
        if(!appPreferences.getString("service", "").matches("")) {
            if(appPreferences.getString("service", "").matches("service"))
                stopTrackingTV.setText(R.string.stop_tracking);
            else
                stopTrackingTV.setText(R.string.save_path);

            startTrackingCV.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimaryLightContrast)));
            startTrackingCV.setEnabled(false);
            stopTrackingCV.setBackgroundTintList(null);
            stopTrackingCV.setEnabled(true);

            reportInfectedCV.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimaryLightContrast)));
            testPathCV.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimaryLightContrast)));
            reportInfectedCV.setEnabled(false);
            testPathCV.setEnabled(false);
        } else {
            stopTrackingCV.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimaryLightContrast)));
            stopTrackingCV.setEnabled(false);
            startTrackingCV.setBackgroundTintList(null);
            startTrackingCV.setEnabled(true);

            reportInfectedCV.setBackgroundTintList(null);
            testPathCV.setBackgroundTintList(null);
            reportInfectedCV.setEnabled(true);
            testPathCV.setEnabled(true);
        }

        if(!appPreferences.getString("service", "").matches("")) {
            if(getIntent() != null && getIntent().hasExtra("widgetPress") && getIntent().getBooleanExtra("widgetPress", true)) {
                setIntent(null);
                onClickStopTracking(null);
                sendNotification();
                Intent i = new Intent(Intent.ACTION_MAIN);
                i.addCategory(Intent.CATEGORY_HOME);
                startActivity(i);
                onBackPressed();
            }
        } else {
            if(getIntent() != null && getIntent().hasExtra("widgetPress") && getIntent().getBooleanExtra("widgetPress", true)) {
                setIntent(null);
                startedByWidget = true;
                onClickStartTracking(null);
            }
        }
    }

    private void sendNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel("stopping", "Tracking stopping...", NotificationManager.IMPORTANCE_HIGH);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "stopping");
        builder.setTimeoutAfter(10000);
        builder.setSmallIcon(R.drawable.logo);
        builder.setContentTitle(getResources().getString(R.string.tracking_stopping));
        builder.setAutoCancel(true);
        builder.setOngoing(false);
        builder.setContentText(getResources().getString(R.string.tracking_stopping));
        builder.setPriority(NotificationCompat.PRIORITY_HIGH);

        //builder.setAutoCancel(false);
        /*PendingIntent notifIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);
        builder.setContentIntent(notifIntent);*/

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(2, builder.build());
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }
    //endregion

    //region Drawer
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        /*if (id == R.id.nav_home) {
            // Handle the camera action
        } else */
        if (id == R.id.nav_map) {
            getPaths();
        }
        else if (id == R.id.nav_history) {
            //Intent intent = new Intent(MainActivity.this, MapMeetupHistoryActivity.class);
            Intent intent = new Intent(MainActivity.this, H2Activity.class);
            startActivity(intent);
        } else if (id == R.id.logout) {
            deleteFile(Const.USER_FILE_NAME);
            DatabaseSimulator.currentUser = null;
            openDialog();
        } else if (id == R.id.nav_share) {
            try {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "My application name");
                String shareMessage = getResources().getString(R.string.recommand_app) + Const.LINK_TO_DOWNLOAD;
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                startActivity(Intent.createChooser(shareIntent, getResources().getString(R.string.choose_sharing_method)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (id == R.id.symptoms) {
            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(Const.WHO_SYMPTOMS_URL));
            startActivity(i);
        } else if (id == R.id.tutorial) {
            Tutorial tutorial = new Tutorial(getSupportFragmentManager());
            tutorial.show(tutorial.getFrgManager(),  "Tutorial");
            tutorial.setCancelable(false);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //endregion
}

package com.example.hw2p1;


import static android.Manifest.permission.ACCESS_FINE_LOCATION;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.result.ActivityResultLauncher;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Group0 extends AppCompatActivity {

    private boolean run_status;
    private int unit_flag;
    private double tmp_speed_ms;
    private TextView txt_speed;
    private TextView txt_location;
    private Button btn_start,btn_unit,bbtn_help;
    private LocationManager myLocationManager;
    private String[] unit_options = {"M/s", "Mile/h", "Km/h"};
    private int fontsize;
    private String help_text="Click start to trace position and speed, if you want to change unit or font-size, just click the item you want to change.";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txt_speed = findViewById(R.id.txt_speed);
        txt_location = findViewById(R.id.txt_location);
        //txt_help=findViewById(R.id.txt_help);
        btn_start = findViewById(R.id.btn_start);
        btn_unit=findViewById(R.id.btn_unit);
        bbtn_help=findViewById(R.id.btn_help);
        run_status = false;
        final boolean[] help_statue = {false};
        unit_flag=0;
        tmp_speed_ms=0;
        fontsize=62;

        myLocationManager= (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        bbtn_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if(!help_statue[0]){
//                    txt_help.setText(help_text);
//                    help_statue[0] =true;
//                }else{
//                    txt_help.setText("");
//                    help_statue[0] =false;
//                }
            }
        });

        txt_speed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fontsize+=5;
                if(fontsize>72){
                    fontsize=52;
                }
                System.out.println(fontsize);
                txt_speed.setTextSize(2,fontsize);
            }
        });

        txt_speed.setTextColor(0xff808B96);
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(run_status){
                    myLocationManager.removeUpdates(myLocationListener);
                    System.out.println("Location tracking stopped");
                    Toast.makeText(Group0.this,
                            "Location tracking stopped", Toast.LENGTH_SHORT).show();
                    btn_start.setText("Start");
                    txt_location.setText("0.000, 0.000");
                    txt_speed.setTextColor(0xff808B96);
                    txt_speed.setText("0.00");
                    tmp_speed_ms=0;
                    run_status=false;
                }else {
                    updateInfo();
                    System.out.println("Location tracking started");
                    Toast.makeText(Group0.this,
                            "Location tracking started", Toast.LENGTH_SHORT).show();
                    btn_start.setText("Stop");

                    run_status=true;
                }
            }

        });
        btn_unit.setText(unit_options[unit_flag]);
        // This is use to change unit
        btn_unit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if (unit_flag == 0) {
                    btn_unit.setText(unit_options[1]);
                    unit_flag=1;
                    double speed_mph=tmp_speed_ms*3600/1000;
                    txt_speed.setText(String.valueOf(speed_mph));
                    System.out.println("Unit changed");
                }else if(unit_flag==1){
                    btn_unit.setText(unit_options[2]);
                    unit_flag=2;
                    txt_speed.setText(String.valueOf(tmp_speed_ms));
                    System.out.println("Unit changed");
                }else {
                    btn_unit.setText(unit_options[0]);
                    unit_flag=0;
                    double speed_mph=tmp_speed_ms*3600/1000/1.61;
                    txt_speed.setText(String.valueOf(speed_mph));
                    System.out.println("Unit changed");
                }
                btn_unit.setText(unit_options[(unit_flag+1)%3]);

                unit_flag=(unit_flag+1)%3;
            }
        });

        System.out.println("App started");


    }

    //Use to handle messages from location manager
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if(msg.what==1){
                txt_location.setText((CharSequence) msg.obj);
                System.out.println("Location refreshed");
            }
            else if(msg.what==2){
                double speed_double=Double.parseDouble((String) msg.obj);
                tmp_speed_ms=speed_double;
                // Select color
                if(speed_double<10){
                    txt_speed.setTextColor(0xff17202A);
                }else if(speed_double>=10&&speed_double<25){
                    txt_speed.setTextColor(0xff2ECC71);
                }else if(speed_double>=25&&speed_double<50){
                    txt_speed.setTextColor(0xff3498DB);
                }else if(speed_double>=50&&speed_double<80){
                    txt_speed.setTextColor(0xff9B59B6);
                } else {
                    txt_speed.setTextColor(0xffE74C3C);
                }

                // Calculate speed in unit selected
                if(unit_flag==1){
                    double speed_mph=speed_double*3600/1000/1.61;
                    txt_speed.setText(String.valueOf(speed_mph));
                }else if(unit_flag==2){
                    double speed_kmh=speed_double*3600/1000;
                    txt_speed.setText(String.valueOf(speed_kmh));
                }
                else{
                    txt_speed.setText((CharSequence) msg.obj);
                }
                System.out.println("Speed refreshed");
            }
            return true;
        }
    });

    private LocationListener myLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            // If GPS data changes
            refreshInfo(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

            // Ask user for location permission
            if (checkCallingOrSelfPermission(ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                Toast.makeText(Group0.this, "Please allow the location permission", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                ActivityResultLauncher<Intent> startActivityIntent = registerForActivityResult(
                        new ActivityResultContracts.StartActivityForResult(),
                        new ActivityResultCallback<ActivityResult>() {
                            @Override
                            public void onActivityResult(ActivityResult result) {
                            }
                        });
                startActivityIntent.launch(intent);
                return;
            }

            // Refresh location, speed
            Location location=myLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            refreshInfo(location);
        }
    };

    private void updateInfo() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(Group0.this, "No location permission, function disabled", Toast.LENGTH_SHORT).show();
            return;
        }
        myLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 5,myLocationListener);
        Location location = myLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        refreshInfo(location);
    }

    // This is used to send message to handler to refresh data on screen
    private void refreshInfo(Location location){
        if(location!=null){
            Message message1=handler.obtainMessage(1,1,1,String.format("%.4f", location.getLongitude()) +", "+ String.format("%.4f", location.getLatitude()));
            Message message2=handler.obtainMessage(2,1,1,String.valueOf(location.getSpeed()));
            handler.sendMessage(message1);
            handler.sendMessage(message2);
        }
        else{
            System.out.println("Location is null");
        }

    }

}
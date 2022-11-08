package com.example.hw2p1;


import static android.Manifest.permission.ACCESS_FINE_LOCATION;

import static java.lang.Double.NaN;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Console;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Timer;
import java.util.TimerTask;

public class Group0 extends AppCompatActivity {

    private static final double EARTH_RADIUS = 6371000;
    private double prev_longitude,prev_latitude,distance,highest_height,highest_speed,longest_distance,longest_time;
    private boolean run_status;
    private boolean refresh_utils_statue;
    private int unit_flag,distance_unit_flag,time_unit_flag;
    private double tmp_speed_ms,start_time,height;
    private double time;
    private TextView txt_speed,txt_location,txt_height,txt_distance,txt_time,label_distance;
    private Button btn_start,btn_unit,bbtn_help,bbtn_high,bbtn_reset;
    private TextView label_time;
    private LocationManager myLocationManager;
    private final String[] unit_options = {"M/s", "Mile/h", "Km/h","Mile/min"};
    private final double[] unit_factors= {1,2.237,3.6,0.0373};
    private final double[] distance_factors={1,0.001,0.000621371,3.28084};
    private final double[] time_factors={1,0.0166667,0.000277778,1.157409722e-5};
    private int fontsize;
    private final String help_text="Click start to trace position and speed, if you want to change unit or font-size, just click the item you want to change.";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txt_speed = findViewById(R.id.txt_speed);
        txt_location = findViewById(R.id.txt_location);
        txt_height=findViewById(R.id.txt_height);
        txt_distance=findViewById(R.id.txt_distance);
        txt_time=findViewById(R.id.txt_time);
        btn_start = findViewById(R.id.btn_start);
        btn_unit=findViewById(R.id.btn_unit);
        bbtn_help=findViewById(R.id.btn_help);
        bbtn_reset=findViewById(R.id.btn_reset);
        bbtn_high=findViewById(R.id.bbtn_high);
        label_distance=findViewById(R.id.label_distance);
        label_time=findViewById(R.id.label_time);
        run_status = false;
        unit_flag=0;
        distance_unit_flag=0;
        time_unit_flag=0;
        tmp_speed_ms=0;
        fontsize=62;
        distance=0;
        start_time=0;
        time=0;
        prev_longitude=0;
        prev_latitude=0;
        refresh_utils_statue=false;
        highest_speed=0;
        longest_distance=0;
        longest_time=0;
        highest_height=NaN;


        myLocationManager= (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Timer utils_timer=new Timer();
        TimerTask utils_refresh=new TimerTask() {
            @Override
            public void run() {
                if(refresh_utils_statue&&start_time!=0){
                    time= (System.currentTimeMillis()-start_time);

                    switch (distance_unit_flag){
                        case 0:
                            txt_distance.setText(String.valueOf(distance*distance_factors[0]));
                            break;
                        case 1:
                            txt_distance.setText(String.valueOf(distance*distance_factors[1]));
                            break;
                        case 2:
                            txt_distance.setText(String.valueOf(distance*distance_factors[2]));
                            break;
                        case 3:
                            txt_distance.setText(String.valueOf(distance*distance_factors[3]));
                            break;
                    }
                    double time_tmp_s= Math.round(time/1000);
                    double time_tmp=0;
                    switch(time_unit_flag){
                        case 0:
                            BigDecimal b   =   new   BigDecimal(time_tmp_s*time_factors[0]);
                            time_tmp   =   b.setScale(3,   RoundingMode.HALF_UP).doubleValue();
                            break;
                        case 1:
                            BigDecimal c   =   new   BigDecimal(time_tmp_s*time_factors[1]);
                            time_tmp   =   c.setScale(3,   RoundingMode.HALF_UP).doubleValue();
                            break;
                        case 2:
                            BigDecimal d   =   new   BigDecimal(time_tmp_s*time_factors[2]);
                            time_tmp   =   d.setScale(3,   RoundingMode.HALF_UP).doubleValue();
                            break;
                        case 3:
                            BigDecimal e   =   new   BigDecimal(time_tmp_s*time_factors[3]);
                            time_tmp   =   e.setScale(5,   RoundingMode.HALF_UP).doubleValue();
                            break;
                    }
                    System.out.println(time);
                    txt_time.setText(String.valueOf(time_tmp));
                }
                else if(refresh_utils_statue==false){
                    txt_distance.setText("0.0");
                    txt_time.setText("0.00");
                }
            }
        };

        utils_timer.schedule(utils_refresh,0,1000);

        bbtn_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder help_dialog =
                        new AlertDialog.Builder(Group0.this);
                help_dialog.setTitle("High Scores");
                help_dialog.setMessage(help_text);
                help_dialog.setPositiveButton("Close",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                help_dialog.show();
            }
        });

        bbtn_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                highest_speed = 0;
                txt_speed.setText("0.00");
                txt_location.setText("00.000, 00.000");

                highest_height = 0;
                txt_height.setText("0.00");

                distance = 0;
                txt_distance.setText("0.0");

                start_time =0;
                txt_time.setText("0.00");

                btn_start.setText("START");
                run_status = false;

            }
        });

        bbtn_high.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder help_dialog =
                        new AlertDialog.Builder(Group0.this);
                help_dialog.setTitle("Help tips");
                String high_metrics="Highest Speed: "+String.format("%2f",highest_speed);
                high_metrics+="\nHighest Height: "+String.format("%.3f",highest_height);
                high_metrics+="\nLongest Distance: "+String.valueOf(longest_distance);
                help_dialog.setMessage(high_metrics);
                help_dialog.setPositiveButton("Close",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                help_dialog.show();
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

        label_distance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (distance_unit_flag){
                    case 0:
                        txt_distance.setText(String.valueOf(distance*distance_factors[1]));
                        label_distance.setText("Distance(KM)");
                        distance_unit_flag++;
                        break;
                    case 1:
                        txt_distance.setText(String.valueOf(distance*distance_factors[2]));
                        label_distance.setText("Distance(Mile)");
                        distance_unit_flag++;
                        break;
                    case 2:
                        txt_distance.setText(String.valueOf(distance*distance_factors[3]));
                        label_distance.setText("Distance(Feet)");
                        distance_unit_flag++;
                        break;
                    case 3:
                        txt_distance.setText(String.valueOf(distance*distance_factors[0]));
                        label_distance.setText("Distance(M)");
                        distance_unit_flag=0;
                        break;
                }
            }
        });

        label_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch(time_unit_flag){
                    case 0:
                        label_time.setText("Time(Min)");
                        time_unit_flag++;
                        break;
                    case 1:
                        label_time.setText("Time(Hour)");
                        time_unit_flag++;
                        break;
                    case 2:
                        label_time.setText("Time(Day)");
                        time_unit_flag++;
                        break;
                    case 3:
                        label_time.setText("Time(Sec)");
                        time_unit_flag=0;
                        break;
                }
            }
        });

        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(run_status){
                    refresh_utils_statue=false;
                    myLocationManager.removeUpdates(myLocationListener);
                    System.out.println("Location tracking stopped");
                    Toast.makeText(Group0.this,
                            "Location tracking stopped", Toast.LENGTH_SHORT).show();
                    btn_start.setText("Start");
                    //txt_location.setText("0.0000, 0.0000");
                    txt_speed.setTextColor(0xffffffff);
                    txt_speed.setText("0.00");
                    tmp_speed_ms=0;
                    run_status=false;
                }else {
                    if(start_time==0){
                        start_time=System.currentTimeMillis();
                    }
                    refresh_utils_statue=true;
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
                    double speed_tmp=tmp_speed_ms*unit_factors[1];
                    txt_speed.setText(String.valueOf(speed_tmp));
                    System.out.println("Unit changed");
                }else if(unit_flag==1){
                    btn_unit.setText(unit_options[2]);
                    unit_flag=2;
                    double speed_tmp=tmp_speed_ms*unit_factors[2];
                    txt_speed.setText(String.valueOf(speed_tmp));
                    System.out.println("Unit changed");
                }
                else if(unit_flag==2){
                    btn_unit.setText(unit_options[3]);
                    unit_flag=3;
                    double speed_tmp=tmp_speed_ms*unit_factors[3];
                    txt_speed.setText(String.valueOf(speed_tmp));
                    System.out.println("Unit changed");
                }
                else {
                    btn_unit.setText(unit_options[0]);
                    unit_flag=0;
                    double speed_tmp=tmp_speed_ms*unit_factors[0];
                    txt_speed.setText(String.valueOf(speed_tmp));
                    System.out.println("Unit changed");
                }
                btn_unit.setText(unit_options[unit_flag]);
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
                tmp_speed_ms=(double)Math.round(speed_double*100)/100;
                // Select color
                if(speed_double<10){
                    txt_speed.setTextColor(0xffffffff);
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
                    double speed_tmp=tmp_speed_ms*unit_factors[1];
                    txt_speed.setText(String.valueOf(speed_tmp));
                }else if(unit_flag==2){
                    double speed_tmp=tmp_speed_ms*unit_factors[2];
                    txt_speed.setText(String.valueOf(speed_tmp));
                }
                else if(unit_flag==3) {
                    double speed_tmp = tmp_speed_ms * unit_factors[3];
                    txt_speed.setText(String.valueOf(speed_tmp));
                }else{
                    double speed_tmp=tmp_speed_ms*unit_factors[0];
                    txt_speed.setText(String.valueOf(speed_tmp));
                }
                System.out.println("Speed refreshed");
            }
            else if(msg.what==3){
                txt_height.setText((CharSequence)msg.obj);
                System.out.println("Altitude refreshed");
            }
            else {
                System.out.println("Unknown message");
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
                //                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                ActivityResultLauncher<Intent> startActivityIntent = registerForActivityResult(
//                        new ActivityResultContracts.StartActivityForResult(),
//                        new ActivityResultCallback<ActivityResult>() {
//                            @Override
//                            public void onActivityResult(ActivityResult result) {
//                            }
//                        });
//                startActivityIntent.launch(intent);
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
        myLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0,myLocationListener);
        Location location = myLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        refreshInfo(location);
    }

    // This is used to send message to handler to refresh data on screen
    private void refreshInfo(Location location){
        if(location!=null){
            double longitude_tmp=location.getLongitude();
            double latitude_tmp=location.getLatitude();
            Message message1=handler.obtainMessage(1,1,1,String.format("%.4f", longitude_tmp) +", "+ String.format("%.4f", latitude_tmp));
            Message message2=handler.obtainMessage(2,1,1,String.valueOf(location.getSpeed()));
            Message message3=handler.obtainMessage(3,1,1,String.format("%.3f",location.getAltitude()));
            handler.sendMessage(message1);
            handler.sendMessage(message2);
            handler.sendMessage(message3);
            if(location.getSpeed()>highest_speed){
                highest_speed=location.getSpeed();
            }
            if(Double.isNaN(highest_height)){
                highest_height=location.getAltitude();
            }
            if(location.getAltitude()>highest_height){
                highest_height=location.getAltitude();
            }
            if(prev_longitude==0||prev_latitude==0){
                prev_longitude=longitude_tmp;
                prev_latitude=latitude_tmp;
            }else {
                double distance_tmp=GetDistance(prev_longitude,prev_latitude,longitude_tmp,latitude_tmp);
                System.out.println(distance_tmp);
                prev_longitude=longitude_tmp;
                prev_latitude=latitude_tmp;
                distance+=distance_tmp;
                if(distance>longest_distance){
                    longest_distance=distance;
                }
            }
            //System.out.println("Speed:"+message2.obj);
        }
        else{
            System.out.println("Location is null");
        }

    }

    private static double rad(double d)
    {
        return d * Math.PI / 180.0;
    }
    // Code from https://cloud.tencent.com/developer/article/1622606
    private static double GetDistance(double lon1,double lat1,double lon2, double lat2) {
        double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);
        double a = radLat1 - radLat2;
        double b = rad(lon1) - rad(lon2);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) + Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        s = Math.round(s * 10000) / 10000;
        return s;
    }

}

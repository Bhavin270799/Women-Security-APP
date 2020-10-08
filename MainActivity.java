package com.example.womenssec;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Locale;

public class MainActivity<isNotFirstTime> extends AppCompatActivity implements LocationListener,SensorEventListener{

    Button button_location;
    TextView textView_location;
    LocationManager locationManager;
    private SensorManager sensorManager;
    private Sensor accelerometerSensor;
    private boolean isAccelerometerSensorAvailable,isNotFirstTime= false;
    private float cx,cy,cz,lx,ly,lz,dx,dy,dz;
    private float st =9f;
    private Vibrator vibrator;
    private String msg1="CHANGES MADE msg1";
    private String msg2="CHANGES MADE msg2";
    private boolean locatefirsttime = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView_location = (TextView)findViewById(R.id.text_location);
        button_location = (Button)findViewById(R.id.button_location);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.SEND_SMS, Manifest.permission.RECEIVE_SMS} , PackageManager.PERMISSION_GRANTED);
        if(sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)!=null)
        {
            accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            isAccelerometerSensorAvailable= true;

        }
        else
        {

            isAccelerometerSensorAvailable = false;
        }
        //Runtime permission
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION
            },100);
        }
        //accelerometer code



       // button_location.setOnClickListener(new View.OnClickListener()) ;

    }

    @Override
    public void onSensorChanged(SensorEvent event)
    {

        cx = event.values[0];
        cy = event.values[1];
        cz = event.values[2];
        if(isNotFirstTime)
        {
            dx=Math.abs(lx-cx);
            dy=Math.abs(ly-cy);
            dz=Math.abs(lz-cz);
            if((dx>st && dy>st)||(dx>st && dz>st)||(dz>st && dy>st))
            {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                {
                    vibrator.vibrate(VibrationEffect.createOneShot(500,VibrationEffect.DEFAULT_AMPLITUDE));
                    if(locatefirsttime) {
                        Toast.makeText(MainActivity.this, msg1, Toast.LENGTH_LONG).show();
                        locatefirsttime =false;
                        getLocation();
                    }
                }else{vibrator.vibrate(500);
                    if(locatefirsttime) {
                        Toast.makeText(MainActivity.this, msg2, Toast.LENGTH_LONG).show();
                        locatefirsttime =false;
                        getLocation();
                    }
                    //Toast.makeText(MainActivity.this,msg2,Toast.LENGTH_LONG).show();
                    //sendSms();
                }

            }

        }


        lx=cx;
        ly=cy;
        lz=cz;
        isNotFirstTime = true;


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(isAccelerometerSensorAvailable)
            sensorManager.registerListener((SensorEventListener) this,accelerometerSensor,SensorManager.SENSOR_DELAY_NORMAL);


    }
    @Override
    protected void onPause() {
        super.onPause();
        if(isAccelerometerSensorAvailable)
            sensorManager.unregisterListener((SensorEventListener) this);


    }


    @SuppressLint("MissingPermission")
    private void getLocation() {
        try{
            locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, MainActivity.this);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Toast.makeText(this,""+location.getLatitude()+","+location.getLongitude(), Toast.LENGTH_SHORT).show();

        try{
            Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
            String address = addresses.get(0).getAddressLine(0);

            textView_location.setText(address);
            String msg ="HELLO";
            String number = "1234567890";
            String Flagmsg="Message Sent";
            SmsManager sms = SmsManager.getDefault();
            sms.sendTextMessage(number ,null,address,null,null);//yeah part mai hoga seng GPS LOCATION
            Toast.makeText(MainActivity.this,Flagmsg,Toast.LENGTH_LONG).show();
            locatefirsttime = true;


        }catch(Exception e){
            e.printStackTrace();
        }
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


    public void sendSms()
    {
        String msg ="HELLO"; //yaha par tuje GPS ka code send karna hai
        String number = "8310863570";
        String Flagmsg="Message Sent";
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(number ,null,msg,null,null);//yeah part mai hoga seng GPS LOCATION
        Toast.makeText(MainActivity.this,Flagmsg,Toast.LENGTH_LONG).show();
    }
}




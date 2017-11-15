package com.example.emily.beaconside;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;
import android.support.v4.app.ActivityCompat;
import android.Manifest;

import java.util.HashMap;

/**
 * Created by jennifer9759 on 2017/11/13.
 */

public class GPSTracker{
    private static final int REQUEST_LOCATION = 888;
    private LocationManager mLocationManager;
    public double currentLatitude = 0;
    public double currentLongitude = 0;
    private static final int LOCATION_UPDATE_MIN_DISTANCE = 1000;
    private static final int LOCATION_UPDATE_MIN_TIME = 50;
    public boolean isLocationChanged = false;
    Context mContext;


    public void getCurrentLocation(Context context) {
        mContext = context;
        Activity mActivity = (Activity) mContext;
        Location location = null;
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Check Permissions Now
            ActivityCompat.requestPermissions(mActivity,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION);

        } else {
            // permission has been granted, continue as usual
            mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
            boolean gps = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean network = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if(!gps){
                Toast.makeText(mContext,"gps未開啟",Toast.LENGTH_LONG);
                // 插入dialog，引導到開啟定位頁面
                // Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                // startActivity(intent);
            }
            try {
                mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, LOCATION_UPDATE_MIN_TIME,
                            LOCATION_UPDATE_MIN_DISTANCE, mLocationListener);
                    location = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            } catch (IllegalArgumentException e) {
                e.getStackTrace();
            }
            try {
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_UPDATE_MIN_TIME,
                            LOCATION_UPDATE_MIN_DISTANCE, mLocationListener);
                location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }  catch (IllegalArgumentException e) {
                e.getStackTrace();
            }
            if (location != null) {

                currentLatitude = location.getLatitude();
                currentLongitude = location.getLongitude();
            }
        }
    }


    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_LOCATION) {
            if(grantResults.length == 1
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // We can now safely use the API we requested access to

            } else {
                // Permission was denied or request was cancelled
            }
        }
    }


    private LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            if (location != null) {
                isLocationChanged = true;
                String msg = String.format("%f, %f", location.getLatitude(), location.getLongitude());
                currentLatitude = location.getLatitude();
                currentLongitude = location.getLongitude();
                Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();

            } else {
                // Logger.d("Location is null");
                Toast.makeText(mContext, "Location is null", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
        }

        @Override
        public void onProviderEnabled(String s) {
        }

        @Override
        public void onProviderDisabled(String s) {
        }
    };

}

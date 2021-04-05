package com.mindpass.achacha;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;


public class MyService extends Service {

    private static final String TAG = "BOOMBOOMTESTGPS";
    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 1000;
    private static final float LOCATION_DISTANCE = 10f;
    Double curlat, curlon;
    String id;
    Timer timer = new Timer();

    private DbOpenHelper mDbOpenHelper;
    String sort = "memo";

    NotificationManager manager;
    NotificationCompat.Builder builder;
    private static String CHANNEL_ID = "channel1";
    private static String CHANEL_NAME = "Channel1";
    private final static String TAGG = MyService.class.getSimpleName();

    private Context context = null;
    public int counter=0;


    private class LocationListener implements android.location.LocationListener {
        Location mLastLocation;

        public LocationListener(String provider) {
            Log.e(TAG, "LocationListener " + provider);
            mLastLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location) {
            Log.e(TAG, "onLocationChanged: " + location);
            mLastLocation.set(location);
            String markerSnippet = "위도:" + String.valueOf(mLastLocation.getLatitude())
                    + " 경도:" + String.valueOf(mLastLocation.getLongitude());
            Log.e("Hoxy?", markerSnippet);

            LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());

            curlat = location.getLatitude();
            curlon = location.getLongitude();
            id = SaveSharedPreference.getUserName(MyService.this);

            Cursor iCursor = mDbOpenHelper.sortColumn(sort);
            Log.d("showDatabase", "DB Size: " + iCursor.getCount());

            while(iCursor.moveToNext()) {
                String splace = iCursor.getString(iCursor.getColumnIndex("place"));
                String smemo = iCursor.getString(iCursor.getColumnIndex("memo"));
                Double latitude = iCursor.getDouble(iCursor.getColumnIndex("latitude"));
                Double longitude = iCursor.getDouble(iCursor.getColumnIndex("longitude"));

                Log.d("메모된 장소는 ", splace);
                Log.d("메모는", smemo);
                Log.d("메모된 경도는", Double.toString(latitude));
                Log.d("메모는 위도는", Double.toString(longitude));

                LatLng Lat = new LatLng(latitude, longitude);
                MarkerOptions makerOptions = new MarkerOptions();
                makerOptions
                        .position(Lat)
                        .title(smemo);

                if(getDistance(Lat,currentLatLng) < 500 ){
                    final Intent intent = new Intent(MyService.this.getApplicationContext(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);

                    builder = null;
                    manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        manager.createNotificationChannel(new NotificationChannel(CHANNEL_ID, CHANEL_NAME, NotificationManager.IMPORTANCE_DEFAULT));
                        builder = new NotificationCompat.Builder(MyService.this, CHANNEL_ID);
                    } else {
                        builder = new NotificationCompat.Builder(MyService.this);
                    }

                    builder.setContentTitle("아차차");
                    builder.setContentText(smemo);
                    builder.setSmallIcon(R.drawable.p3);
                    Notification notification = builder.build();

                    manager.notify(1, notification);
                }
            }

        }

        public double getDistance(LatLng LatLng1, LatLng LatLng2) {
            double distance = 0;
            Location locationA = new Location("A");
            locationA.setLatitude(LatLng1.latitude);
            locationA.setLongitude(LatLng1.longitude);
            Location locationB = new Location("B");
            locationB.setLatitude(LatLng2.latitude);
            locationB.setLongitude(LatLng2.longitude);
            distance = locationA.distanceTo(locationB);

            return distance;
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.e(TAG, "onProviderDisabled: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.e(TAG, "onProviderEnabled: " + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.e(TAG, "onStatusChanged: " + provider);
        }
    }

    LocationListener[] mLocationListeners = new LocationListener[]{
            new LocationListener(LocationManager.GPS_PROVIDER),
            new LocationListener(LocationManager.NETWORK_PROVIDER)
    };

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand");
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.d(TAG, "BackgroundService.onTaskRemoved");
        //create an intent that you want to start again.
        Intent intent = new Intent(getApplicationContext(), MyService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 1, intent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime() + 5000, pendingIntent);
        super.onTaskRemoved(rootIntent);
    }

    public MyService() {
    }

    // 생성자2
    public MyService(Context applicationContext) {
        super();
        context = applicationContext;
    }

    @Override
    public void onCreate() {
        Log.e(TAG, "onCreate");
        mDbOpenHelper = new DbOpenHelper(this);
        mDbOpenHelper.open();
        mDbOpenHelper.create();


        initializeLocationManager();

        TimerTask TT = new TimerTask() {
            @Override
            public void run() {
                // 반복실행할 구문
                Sendlocation sndloc = new Sendlocation();
                sndloc.execute();
            }

        };

        timer.schedule(TT, 5000, 10000); //Timer 실행
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[1]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "network provider does not exist, " + ex.getMessage());
        }
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[0]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "gps provider does not exist " + ex.getMessage());
        }
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy");
        super.onDestroy();
        /*if (mLocationManager != null) {
            for (int i = 0; i < mLocationListeners.length; i++) {
                try {
                    mLocationManager.removeUpdates(mLocationListeners[i]);
                } catch (Exception ex) {
                    Log.i(TAG, "fail to remove location listners, ignore", ex);
                }
            }
        }*/
        Intent broadcastIntent = new Intent("com.bluexmas.common.RestartService");
        sendBroadcast(broadcastIntent);
        stoptimertask();
    }
    public void stoptimertask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    private void initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager");
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }
    public class Sendlocation extends AsyncTask<Void, Integer, Void> {

        @Override
        protected Void doInBackground(Void... unused) {

            StringBuffer buffer = new StringBuffer();


            try {
                /* 서버연결 */
                URL url = new URL(
                        "https://mp-domain-test.kro.kr:3000/gps");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.connect();

                /* 안드로이드 -> 서버 파라메터값 전달 */
                OutputStreamWriter outStream = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
                buffer.append("id").append("=").append(id).append("&");
                buffer.append("lat").append("=").append(curlat).append("&");
                buffer.append("lon").append("=").append(curlon).append("&");
                PrintWriter writer = new PrintWriter(outStream);
                writer.write(buffer.toString());
                outStream.flush();
                outStream.close();

                /* 서버 -> 안드로이드 파라메터값 전달 */
                InputStream is = null;
                BufferedReader in = null;
                String data = "";

                is = conn.getInputStream();
                in = new BufferedReader(new InputStreamReader(is), 8 * 1024);
                String line = null;
                StringBuffer buff = new StringBuffer();
                while ((line = in.readLine()) != null) {
                    buff.append(line + "\n");
                }

                Log.e("RECV DATA", data);

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch(NullPointerException e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

        }
    }
}

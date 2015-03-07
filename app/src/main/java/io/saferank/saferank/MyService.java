package io.saferank.saferank;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;


/**
 * Created by navidhg on 04/03/15.
 */

public class MyService extends Service
        implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;


    @Override
    public void onCreate() {
        int resp = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        System.out.println("About to connect to Google Play Services in MyService");
        if (resp == ConnectionResult.SUCCESS) {
            System.out.println("Connection is available in MyService");
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(ActivityRecognition.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();

            mGoogleApiClient.connect();
        }
    }

    @Override
    @TargetApi(16)
    public int onStartCommand(Intent intent, int flags, int startID) {
        System.out.println("Service is being launched");
        Log.println(1, "Service", "Running");
        // Make sure service is never killed
        //startForeground(1, null);

        // Connect to Google Play Services
//        int resp = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
//        System.out.println("About to connect to Google Play Services in MyService");
//        if (resp == ConnectionResult.SUCCESS) {
//            System.out.println("Connection is available in MyService");
//            mGoogleApiClient = new GoogleApiClient.Builder(this)
//                    .addApi(ActivityRecognition.API)
//                    .addConnectionCallbacks(this)
//                    .addOnConnectionFailedListener(this)
//                    .build();
//
//            mGoogleApiClient.connect();
//        }
        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onConnected(Bundle bundle) {
        System.out.println("Connected to Google Play Services in MyService, about to launch intent");
        Intent i = new Intent(this, ActivityRecognitionService.class);
        PendingIntent pi = PendingIntent.getService(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
        //pi.
        ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(mGoogleApiClient, 10*60*1000, pi);
//        ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates()
//        boolean b = new Handler().postDelayed(new Runnable() {
//            public void run() {
//                pi.cancel();
//            }
//        }, 3000);
        //pi.
//        try {
//            Thread.sleep(5000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        //ActivityRecognition.ActivityRecognitionApi.removeActivityUpdates(mGoogleApiClient, pi);
//        stopSelf();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}

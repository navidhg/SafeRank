package io.saferank.saferank;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import io.saferank.saferank.MainActivity;
import io.saferank.saferank.R;

/**
 * Created by navidhg on 05/03/15.
 */
public class ActivityRecognitionService extends IntentService {

    public ActivityRecognitionService() {
        super("ActivityRecognitionService");
    }

    @Override
    @TargetApi(16)
    protected void onHandleIntent(Intent intent) {
        System.out.println("Intent was launched, going to check for data");
        if (ActivityRecognitionResult.hasResult(intent)) {
            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
            DetectedActivity act = result.getMostProbableActivity();
            if (act.getType() == DetectedActivity.ON_FOOT ||
                    act.getType() == DetectedActivity.IN_VEHICLE ||
                    act.getType() == DetectedActivity.ON_BICYCLE) {
//            if (act.getType() == DetectedActivity.STILL) {
                int confidence = act.getConfidence();
                if (confidence > 60) {
                    // Create notification
                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                            .setSmallIcon(R.drawable.mapicon)
                            .setContentTitle("SafeRank")
                            .setContentText("Rate your safety!")
                            .setVibrate(new long[] {500, 500})
                            .setColor(android.graphics.Color.argb(1, 16, 232, 203))
                            .setLights(android.graphics.Color.argb(1, 16, 232, 203), 500, 1000)
                            .setAutoCancel(true);


                    Intent resultIntent = new Intent(this, MainActivity.class);
                    resultIntent.putExtra("fromNotification", true);

                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
                    stackBuilder.addParentStack(MainActivity.class);
                    stackBuilder.addNextIntent(resultIntent);

                    PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,
                            PendingIntent.FLAG_UPDATE_CURRENT);

                    mBuilder.setContentIntent(resultPendingIntent);

                    NotificationManager mNotificationManager = (NotificationManager)
                            getSystemService(Context.NOTIFICATION_SERVICE);

                    mNotificationManager.notify(1, mBuilder.build());
                }
            }
        }
    }


}

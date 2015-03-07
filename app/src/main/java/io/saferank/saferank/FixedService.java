package io.saferank.saferank;

import android.annotation.TargetApi;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

/**
 * Created by navidhg on 07/03/15.
 */
public class FixedService extends Service {

    @Override
    @TargetApi(16)
    public int onStartCommand(Intent intent, int flags, int startID) {
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

        return Service.START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

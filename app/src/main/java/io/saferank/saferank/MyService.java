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
import android.os.IBinder;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by navidhg on 04/03/15.
 */

public class MyService extends Service {

    @Override
    @TargetApi(16)
    public int onStartCommand(Intent intent, int flags, int startID) {
        System.out.println("Service is being launched");
        Log.println(1, "Service", "Running");
        // Make sure service is never killed
        //startForeground(1, null);
//        Toast.makeText(this, "hello", Toast.LENGTH_SHORT).show();

        // Create notification
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.mapicon)
                .setContentTitle("SafeRank")
                .setContentText("Rate your safety!")
                .setVibrate(new long[] {500, 500})
                .setColor(android.graphics.Color.argb(1, 16, 232, 203))
                .setLights(android.graphics.Color.argb(1, 16, 232, 203), 500, 1000);


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


        stopSelf();
        return Service.START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

//    public MyService() { super("MyService"); }

//    @Override
//    protected void onHandleIntent(Intent intent) {
//        Toast.makeText(this, "Sample text", Toast.LENGTH_SHORT);
//    }
}

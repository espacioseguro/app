package com.espacio.seguro.Firebase;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;

import com.espacio.seguro.HomeActivity;
import com.espacio.seguro.R;

import java.util.Random;

/**
 * Created by Julian on 25/02/2018.
 */

public class MyNotificationManager {

    private Context mCtx;
    private static MyNotificationManager mInstance;

    private MyNotificationManager(Context context) {
        mCtx = context;
    }

    public static synchronized MyNotificationManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new MyNotificationManager(context);
        }
        return mInstance;
    }

    public void displayNotification(String body) {
        Random random=new Random();
        int notificationId=random.nextInt();
        Vibrator v = (Vibrator)mCtx.getSystemService(Context.VIBRATOR_SERVICE);
        // Start without a delay
        // Each element then alternates between vibrate, sleep, vibrate, sleep...
        long[] pattern = {0, 500, 250, 500, 250,500, 250};

        AudioManager am=(AudioManager)mCtx.getSystemService(Context.AUDIO_SERVICE);
        am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        am.setStreamVolume(AudioManager.STREAM_NOTIFICATION,am.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION),0);

        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);

        Intent intent = new Intent(mCtx, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(mCtx, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder;
        mBuilder = new NotificationCompat.Builder(mCtx);
        mBuilder.setSmallIcon(R.drawable.logo_positivo);
        mBuilder.setLargeIcon(BitmapFactory.decodeResource(mCtx.getResources(), R.drawable.logo_positivo));
        mBuilder.setContentTitle("Espacio Seguro");
        mBuilder.setContentText(body);
        mBuilder.setSound(notification);
        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setOnlyAlertOnce(true);
        mBuilder.setAutoCancel(true);

        NotificationManager nm=(NotificationManager)mCtx.getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder.setPriority(Notification.PRIORITY_HIGH);
        mBuilder.setVibrate(pattern);

        if (nm != null) {
            nm.notify(notificationId, mBuilder.build());
        }
    }
}

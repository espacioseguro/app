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
import com.espacio.seguro.MainActivity;
import com.espacio.seguro.R;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

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

    public void displayNotification(String body,String title) {
        Random random=new Random();
        int notificationId=random.nextInt();
        Vibrator v = (Vibrator)mCtx.getSystemService(Context.VIBRATOR_SERVICE);
        // Start without a delay
        // Each element then alternates between vibrate, sleep, vibrate, sleep...
        long[] pattern = {0, 500, 250, 500, 250,500, 250};

        final AudioManager am=(AudioManager)mCtx.getSystemService(Context.AUDIO_SERVICE);
        final int ring_mode=am.getRingerMode();
        am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        am.setStreamVolume(AudioManager.STREAM_NOTIFICATION,am.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION),0);

        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);

        Intent intent = new Intent(mCtx, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(mCtx, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder;
        mBuilder = new NotificationCompat.Builder(mCtx);
        mBuilder.setSmallIcon(R.drawable.escudo_lobo_negativo);
        mBuilder.setLargeIcon(BitmapFactory.decodeResource(mCtx.getResources(), R.drawable.logo_blue));
        mBuilder.setBadgeIconType(R.drawable.logo_blue);
        mBuilder.setContentTitle(title);
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

        Timer t=new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                am.setRingerMode(ring_mode);
            }
        },3000);
    }
}

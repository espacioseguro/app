package com.solution.tecno.seguro.Firebase;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.solution.tecno.seguro.HomeActivity;
import com.solution.tecno.seguro.R;

import static android.content.Context.NOTIFICATION_SERVICE;

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
        Vibrator v = (Vibrator)mCtx.getSystemService(Context.VIBRATOR_SERVICE);
        // Start without a delay
        // Each element then alternates between vibrate, sleep, vibrate, sleep...
        long[] pattern = {0, 500, 250, 500, 250,500, 250};

        // The '-1' here means to vibrate once, as '-1' is out of bounds in the pattern array
        v.vibrate(pattern, -1);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(mCtx, Constants.CHANNEL_ID)
                        .setSmallIcon(R.drawable.logo_positivo)
                        .setLargeIcon(BitmapFactory.decodeResource(mCtx.getResources(),R.drawable.logo))
                        .setBadgeIconType(R.drawable.logo_positivo)
                        .setContentTitle("Estado del servicio")
                        .setColor(Color.BLUE)
                        .setContentText(body)
                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(mCtx.getApplicationContext());
        Intent intent = new Intent(mCtx.getApplicationContext(), HomeActivity.class);
        taskStackBuilder.addParentStack(HomeActivity.class);
        taskStackBuilder.addNextIntent(intent);

        PendingIntent pendingIntent = taskStackBuilder.getPendingIntent(100, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pendingIntent);
        /*
        *  Clicking on the notification will take us to this intent
        *  Right now we are using the MainActivity as this is the only activity we have in our application
        *  But for your project you can customize it as you want
        * */
        Intent resultIntent = new Intent(mCtx, HomeActivity.class);
        /*
        *  Now we will create a pending intent
        *  The method getActivity is taking 4 parameters
        *  All paramters are describing themselves
        *  0 is the request code (the second parameter)
        *  We can detect this code in the activity that will open by this we can get
        *  Which notification opened the activity
        * */
//        PendingIntent pendingIntent = PendingIntent.getActivity(mCtx, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        /*
        *  Setting the pending intent to notification builder
        * */

        mBuilder.setContentIntent(pendingIntent);

        NotificationManager mNotifyMgr =
                (NotificationManager) mCtx.getSystemService(NOTIFICATION_SERVICE);

        /*
        * The first parameter is the notification id
        * better don't give a literal here (right now we are giving a int literal)
        * because using this id we can modify it later
        * */
        if (mNotifyMgr != null) {
            mNotifyMgr.notify(1, mBuilder.build());
        }
    }
}

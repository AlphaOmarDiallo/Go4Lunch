package com.alphaomardiallo.go4lunch.domain;

import static com.alphaomardiallo.go4lunch.MainApplication.CHANNEL_LUNCHTIME_REMINDER;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.alphaomardiallo.go4lunch.R;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();
        Log.e("receiver", "onReceive: " + action);

        if (action.equalsIgnoreCase("notificationString")) {

            String text = intent.getExtras().getString("notifString");

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

            Notification lunchNotification = new NotificationCompat.Builder(context, CHANNEL_LUNCHTIME_REMINDER)
                    .setSmallIcon(R.drawable.ic_baseline_ramen_dining_24)
                    .setContentTitle(context.getString(R.string.notification_title))
                    .setContentText(text)
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(text))
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setCategory(NotificationCompat.CATEGORY_ALARM)
                    .build();
        }

    }
}

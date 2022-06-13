package com.alphaomardiallo.go4lunch;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.HiltAndroidApp;

@HiltAndroidApp
public class MainApplication extends Application {

    public static final String CHANNEL_LUNCHTIME_REMINDER = "lunchTimeReminder";
    public static final String LUNCHTIME_REMINDER = "Lunch time reminder";
    public static final String CHANNEL_BOOKING_REMINDER = "makeABookingReminder";
    public static final String BOOKING_REMINDER = "Book lunch reminder";
    public final List<NotificationChannel> notificationChannelList = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannels();

    }

    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channelLunchtime = new NotificationChannel(
                    CHANNEL_LUNCHTIME_REMINDER,
                    LUNCHTIME_REMINDER,
                    NotificationManager.IMPORTANCE_HIGH
            );

            channelLunchtime.setDescription("This channel is meant to remind you of you lunch booking");
            channelLunchtime.enableVibration(true);
            channelLunchtime.enableLights(true);
            notificationChannelList.add(channelLunchtime);

            NotificationChannel channelBookingReminder = new NotificationChannel(
                    CHANNEL_BOOKING_REMINDER,
                    BOOKING_REMINDER,
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            channelBookingReminder.setDescription("This channel is meant to remind you to make a booking");
            notificationChannelList.add(channelBookingReminder);

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannels(notificationChannelList);

        }
    }
}

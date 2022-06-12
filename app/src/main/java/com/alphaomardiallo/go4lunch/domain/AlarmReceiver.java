package com.alphaomardiallo.go4lunch.domain;

import static android.content.Context.MODE_PRIVATE;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.alphaomardiallo.go4lunch.R;
import com.alphaomardiallo.go4lunch.data.dataSources.Model.Booking;
import com.alphaomardiallo.go4lunch.data.dataSources.Model.User;
import com.alphaomardiallo.go4lunch.data.repositories.BookingRepository;
import com.alphaomardiallo.go4lunch.data.repositories.BookingRepositoryImp;
import com.alphaomardiallo.go4lunch.data.repositories.UserRepository;
import com.alphaomardiallo.go4lunch.data.repositories.UserRepositoryImp;
import com.alphaomardiallo.go4lunch.ui.MainActivity;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AlarmReceiver extends BroadcastReceiver {
    private final int NOTIFICATION_ID = 007;
    private final String NOTIFICATION_TAG = "GO4LUNCH";
    public BookingRepository bookingRepository = new BookingRepositoryImp();
    public UserRepository userRepository = new UserRepositoryImp();
    String notification1 = null;
    String notification2 = null;
    String userID = null;
    String restaurantID = null;
    String restaurantName = null;
    String restaurantVicinity = null;

    @Inject
    public AlarmReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        //TODO something on receive BroadCast from Alarm - make sure to get last data possible
        setupFireBase();
        getUserDataFromSharedPreferences(context);
        setNotificationString(context);
        sendVisualNotification(notification2, context);
    }

    private void setupFireBase() {
        bookingRepository.getInstance();
        userRepository.getDataBaseInstance();
        bookingRepository.getAllBookingsFromDataBase();
        userRepository.getAllUsersFromDataBase();
    }

    private void getUserDataFromSharedPreferences(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.preferences_main_file), MODE_PRIVATE);
        userID = sharedPreferences.getString(context.getString(R.string.shared_pref_current_User_ID), null);
        restaurantID = sharedPreferences.getString(context.getString(R.string.shared_pref_restaurant_ID), null);
        restaurantName = sharedPreferences.getString(context.getString(R.string.shared_pref_restaurant_Name), null);
        restaurantVicinity = sharedPreferences.getString(context.getString(R.string.shared_pref_restaurant_Address), null);
    }

    private void setNotificationString(Context context){
        notification1 = String.format(context.getString(R.string.notification_line_1), restaurantName, restaurantVicinity);
    }

    private String setUserJoiningList() {
        List<Booking> allBookings = bookingRepository.getAllBookingsAsList().getValue();
        List<User> allUsers = userRepository.getAllUsers().getValue();
        List<User> userJoining = new ArrayList<>();
        String userJoiningString = "";

        for (User user : allUsers) {
            for (Booking booking : allBookings) {
                if (booking.getUserWhoBooked().equalsIgnoreCase(user.getUid())
                        && booking.getBookedRestaurantID().equalsIgnoreCase(restaurantID)
                        && !user.getUid().equalsIgnoreCase(userRepository.getCurrentUserID()
                )) {
                    userJoining.add(user);
                    break;
                }
            }
        }

        if (userJoining.size() > 0) {
            for (User user : userJoining) {
                userJoiningString = String.format("%s, %s", userJoiningString, user.getUsername());
            }
        }

        return userJoiningString;
    }

    private void sendVisualNotification(String notificationText, Context context) {

        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        String channelId = context.getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        // Build a AlarmReceiver object
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context, channelId)
                        .setSmallIcon(R.drawable.ic_baseline_ramen_dining_24)
                        .setSound(defaultSoundUri)
                        .setContentTitle(context.getString(R.string.notification_title))
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(String.format("%s.\n%s", notification1, notification2)))
                        .setAutoCancel(true)
                        .setCategory(NotificationCompat.CATEGORY_ALARM)
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Support Version >= Android 8
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence channelName = "Firebase Messages";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(channelId, channelName, importance);
            notificationManager.createNotificationChannel(mChannel);
        }

        // Show notification
        notificationManager.notify(NOTIFICATION_TAG, NOTIFICATION_ID, notificationBuilder.build());
    }

}

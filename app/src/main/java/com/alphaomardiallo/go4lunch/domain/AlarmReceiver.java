package com.alphaomardiallo.go4lunch.domain;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
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
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.alphaomardiallo.go4lunch.R;
import com.alphaomardiallo.go4lunch.data.dataSources.Model.Booking;
import com.alphaomardiallo.go4lunch.data.dataSources.Model.User;
import com.alphaomardiallo.go4lunch.ui.Activities.MainActivity;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AlarmReceiver extends BroadcastReceiver {
    private static final int NOTIFICATION_ID = 416;
    private static final String NOTIFICATION_TAG = "GO4LUNCH";
    public static final String COLLECTION_USERS = "users";
    public static final String COLLECTION_BOOKING = "booking";
    Context context;
    List<User> allUsers;
    List<Booking> allBookings;
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
        this.context = context;
        getUserDataFromSharedPreferences();
        getUsersFromFireStore();
        setNotificationString();
    }


    private void getUserDataFromSharedPreferences() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.preferences_main_file), MODE_PRIVATE);
        userID = sharedPreferences.getString(context.getString(R.string.shared_pref_current_User_ID), null);
        restaurantID = sharedPreferences.getString(context.getString(R.string.shared_pref_restaurant_ID), null);
        restaurantName = sharedPreferences.getString(context.getString(R.string.shared_pref_restaurant_Name), null);
        restaurantVicinity = sharedPreferences.getString(context.getString(R.string.shared_pref_restaurant_Address), null);
    }

    private void setNotificationString() {
        notification1 = String.format(context.getString(R.string.notification_line_1), restaurantName, restaurantVicinity);
    }

    private void getUsersFromFireStore() {

        FirebaseFirestore.getInstance()
                .collection(COLLECTION_USERS)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    allUsers = queryDocumentSnapshots.toObjects(User.class);
                    getBookingsFromFirestore();
                })
                .addOnFailureListener(e -> Log.e(TAG, "onFailure: Failure getting user list from Firestore", e));
    }

    private void getBookingsFromFirestore() {
        FirebaseFirestore.getInstance()
                .collection(COLLECTION_BOOKING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    allBookings = queryDocumentSnapshots.toObjects(Booking.class);
                    setUserJoiningList();
                })
                .addOnFailureListener(e -> Log.e(TAG, "onFailure: error getting booking list", e));
    }

    private void setUserJoiningList() {

        List<User> userJoining = new ArrayList<>();
        String userJoiningString = "";

        for (User user : allUsers) {
            for (Booking booking : allBookings) {
                if (!booking.getUserWhoBooked().equalsIgnoreCase(userID) && booking.getUserWhoBooked().equalsIgnoreCase(user.getUid())) {
                    userJoining.add(user);
                    break;
                }
            }
        }

        if (userJoining.size() > 0) {
            for (User user : userJoining) {
                userJoiningString = String.format(context.getString(R.string.user_joining_append), userJoiningString, user.getUsername());
            }

            notification2 = String.format(context.getString(R.string.notification_you_are_going_with), userJoiningString);
        } else {
            notification2 = context.getString(R.string.notification_you_are_eating_alone);
        }

        sendVisualNotification();

    }

    private void sendVisualNotification() {

        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        @SuppressLint("UnspecifiedImmutableFlag") PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        String channelId = context.getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        // Build a AlarmReceiver object
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context, channelId)
                        .setSmallIcon(R.drawable.ic_baseline_ramen_dining_24)
                        .setSound(defaultSoundUri)
                        .setContentTitle(context.getString(R.string.notification_title))
                        .setContentText(String.format(context.getString(R.string.final_notification), notification1, notification2))
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(String.format(context.getString(R.string.final_notification), notification1, notification2)))
                        .setAutoCancel(true)
                        .setCategory(NotificationCompat.CATEGORY_ALARM)
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Support Version >= Android 8
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence channelName = context.getString(R.string.channel_name);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(channelId, channelName, importance);
            notificationManager.createNotificationChannel(mChannel);
        }

        // Show notification
        notificationManager.notify(NOTIFICATION_TAG, NOTIFICATION_ID, notificationBuilder.build());
    }
}

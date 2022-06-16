package com.alphaomardiallo.go4lunch.data.repositories;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.alphaomardiallo.go4lunch.R;
import com.alphaomardiallo.go4lunch.data.dataSources.Model.Booking;
import com.alphaomardiallo.go4lunch.domain.AlarmReceiver;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import javax.inject.Inject;

public class BookingRepositoryImp implements BookingRepository {

    private static final int ALARM_HOUR = 0;
    private static final int ALARM_MIN_SEC = 1;
    private static final int TO_COMPARE_TO = 0;
    private static final String NOTIFICATION_TIME = "12:00";
    private static final String DATE_FORMAT = "dd MMM yyyy";
    private static final String TIME_FORMAT = "HH:mm";
    private static final String COLLECTION_NAME = "booking";
    private static final String BOOKING_ID = "bookingID";
    private static final String BOOKING_DATE = "bookingDate";
    private static final String BOOKING_RESTAURANT_ID = "bookedRestaurantID";
    private static final String BOOKING_RESTAURANT_NAME = "bookedRestaurantName";
    private static final String BOOKING_USER_ID = "userWhoBooked";
    private final MutableLiveData<List<Booking>> allBookings = new MutableLiveData<>();
    private FirebaseFirestore database;

    @Inject
    public BookingRepositoryImp() {
    }

    public void getInstance() {
        database = FirebaseFirestore.getInstance();
    }

    @Override
    public LiveData<List<Booking>> getAllBookingsAsList() {
        return allBookings;
    }

    public void getAllBookingsFromDataBase() {
        database.collection(COLLECTION_NAME)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.w(TAG, "Listen fail ", error);
                        return;
                    }

                    if (value != null) {
                        List<Booking> tempList = value.toObjects(Booking.class);
                        allBookings.setValue(tempList);
                    } else {
                        Log.i(TAG, "onEvent: all user is null");
                    }

                });
    }

    public void createBookingInDatabase(@NonNull Booking bookingToSave, Context context) {
        Map<String, Object> booking = new HashMap<>();
        booking.put(BOOKING_ID, null);
        booking.put(BOOKING_DATE, FieldValue.serverTimestamp());
        booking.put(BOOKING_RESTAURANT_ID, bookingToSave.getBookedRestaurantID());
        booking.put(BOOKING_RESTAURANT_NAME, bookingToSave.getBookedRestaurantName());
        booking.put(BOOKING_USER_ID, bookingToSave.getUserWhoBooked());

        database.collection(COLLECTION_NAME)
                .add(booking)
                .addOnSuccessListener(documentReference -> database.collection(COLLECTION_NAME)
                        .document(documentReference.getId())
                        .update(BOOKING_ID, documentReference.getId())
                        .addOnSuccessListener(unused -> {
                            setAlarmExactRTCWakeUp(context);
                            SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.preferences_main_file), Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString(context.getString(R.string.shared_pref_restaurant_ID), bookingToSave.getBookingID());
                            editor.putString(context.getString(R.string.shared_pref_restaurant_Name), bookingToSave.getBookedRestaurantName());
                            editor.apply();
                        }))
                .addOnFailureListener(e -> Log.e(TAG, "onFailure: Error adding document " + e.getMessage(), null));
    }

    public void updateBookingInDataBase(String bookingID, String restaurantID, String restaurantName, Context context) {

        Map<String, Object> updates = new HashMap<>();
        updates.put(BOOKING_RESTAURANT_ID, restaurantID);
        updates.put(BOOKING_RESTAURANT_NAME, restaurantName);

        database.collection(COLLECTION_NAME).document(bookingID)
                .update(updates)
                .addOnSuccessListener(unused -> {
                    SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.preferences_main_file), Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(context.getString(R.string.shared_pref_restaurant_ID), restaurantID);
                    editor.putString(context.getString(R.string.shared_pref_restaurant_Name), restaurantName);
                    editor.apply();
                })
                .addOnFailureListener(e -> Log.e(TAG, "onFailure: Error updating document", e));
    }


    public void deleteBookingInDatabase(String bookingID, Context context) {
        database.collection(COLLECTION_NAME).document(bookingID)
                .delete()
                .addOnSuccessListener(unused -> cancelAlarm(context))
                .addOnFailureListener(e -> Log.e(TAG, "onFailure: Error deleting document", e));

    }

    public void deleteBookingsFromPreviousDays(Context context) {
        for (Booking booking : Objects.requireNonNull(allBookings.getValue())) {
            long dateMilli = booking.getBookingDate().getTime();
            DateFormat simple = new SimpleDateFormat(DATE_FORMAT, Locale.FRANCE);
            Date result = new Date(dateMilli);
            Date today = new Date(Calendar.getInstance().getTimeInMillis());
            if (simple.format(result).compareTo(simple.format(today)) != TO_COMPARE_TO) {
                deleteBookingInDatabase(booking.getBookingID(), context);
            }
        }
    }

    /**
     * AlarmReceiver
     */

    @SuppressLint("MissingPermission")
    private void setAlarmExactRTCWakeUp(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.preferences_main_file), Context.MODE_PRIVATE);

        if (sharedPreferences.getString(context.getString(R.string.shared_pref_notifications), "false").equalsIgnoreCase("true")) {

            Calendar c = Calendar.getInstance();

            if (checkDateToSetNotification() < TO_COMPARE_TO) {
                c.set(Calendar.HOUR_OF_DAY, ALARM_HOUR);
                c.set(Calendar.MINUTE, ALARM_MIN_SEC);
                c.set(Calendar.SECOND, ALARM_MIN_SEC);

                AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                Intent alarmIntent = new Intent(context, AlarmReceiver.class);
                @SuppressLint("UnspecifiedImmutableFlag") PendingIntent pendingAlarmIntent = PendingIntent.getBroadcast(context, 1, alarmIntent, PendingIntent.FLAG_ONE_SHOT);
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingAlarmIntent);
            } else {
                Log.i(TAG, "setAlarmExactRTCWakeUp: Alarm is not set because booking is made after the notification time");
            }
        }

    }

    private void cancelAlarm(Context context) {
        @SuppressLint("ServiceCast")
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(context, AlarmReceiver.class);
        @SuppressLint("UnspecifiedImmutableFlag") PendingIntent pendingAlarmIntent = PendingIntent.getBroadcast(context, 1, alarmIntent, PendingIntent.FLAG_ONE_SHOT);
        alarmManager.cancel(pendingAlarmIntent);
    }

    private int checkDateToSetNotification() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat(TIME_FORMAT);
        Date date = new Date();
        return formatter.format(date).compareTo(NOTIFICATION_TIME);
    }
}

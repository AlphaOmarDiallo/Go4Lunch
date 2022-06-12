package com.alphaomardiallo.go4lunch.data.repositories;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

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
import java.util.Map;
import java.util.Objects;

import javax.inject.Inject;

public class BookingRepositoryImp implements BookingRepository {

    private static final String COLLECTION_NAME = "booking";
    private static final String BOOKING_ID = "bookingID";
    private static final String BOOKING_DATE = "bookingDate";
    private static final String BOOKING_RESTAURANT_ID = "bookedRestaurantID";
    private static final String BOOKING_RESTAURANT_NAME = "bookedRestaurantName";
    private static final String BOOKING_USER_ID = "userWhoBooked";
    private FirebaseFirestore database;
    private final MutableLiveData<List<Booking>> allBookings = new MutableLiveData<>();

    @Inject
    public BookingRepositoryImp() {
    }

    public void getInstance() {
        database = FirebaseFirestore.getInstance();
        Log.i(TAG, "getInstance: FireBase " + database);
    }

    public void createBookingAndAddInDatabase(@NonNull Booking bookingToSave, Context context) {
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
                        .addOnSuccessListener(unused -> setAlarmExactRTCWakeUp(context, bookingToSave)))
                .addOnFailureListener(e -> Log.e(TAG, "onFailure: Error adding document " + e.getMessage(), null));
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

    @Override
    public LiveData<List<Booking>> getAllBookings() {
        return allBookings;
    }

    public void updateBooking(String bookingID, String restaurantID, String restaurantName) {

        Map<String, Object> updates = new HashMap<>();
        updates.put(BOOKING_RESTAURANT_ID, restaurantID);
        updates.put(BOOKING_RESTAURANT_NAME, restaurantName);

        database.collection(COLLECTION_NAME).document(bookingID)
                .update(updates)
                .addOnSuccessListener(unused -> Log.d(TAG, "onSuccess: DocumentSnapshot successfully updated"))
                .addOnFailureListener(e -> Log.w(TAG, "onFailure: Error updating document", e));
    }


    public void deleteBookingInDatabase(String bookingID, Context context) {
        database.collection(COLLECTION_NAME).document(bookingID)
                .delete()
                .addOnSuccessListener(unused -> cancelAlarm(context))
                .addOnFailureListener(e -> Log.w(TAG, "onFailure: Error deleting document", e));

    }

    public void deleteBookingsFromPreviousDays(Context context) {
        for (Booking booking : Objects.requireNonNull(allBookings.getValue())) {
            long dateMilli = booking.getBookingDate().getTime();
            @SuppressLint("SimpleDateFormat") DateFormat simple = new SimpleDateFormat("dd MMM yyyy");
            Date result = new Date(dateMilli);
            Date today = new Date(Calendar.getInstance().getTimeInMillis());
            if (simple.format(result).compareTo(simple.format(today)) != 0) {
                deleteBookingInDatabase(booking.getBookingID(), context);
            }
        }
    }

    /**
     * AlarmReceiver
     */

    @SuppressLint("MissingPermission")
    private void setAlarmExactRTCWakeUp(Context context, Booking booking) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 17);
        c.set(Calendar.MINUTE, 32);
        c.set(Calendar.SECOND, 0);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(context, AlarmReceiver.class);
        @SuppressLint("UnspecifiedImmutableFlag") PendingIntent pendingAlarmIntent = PendingIntent.getBroadcast(context, 1, alarmIntent, PendingIntent.FLAG_ONE_SHOT);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingAlarmIntent);
    }

    private void cancelAlarm(Context context) {
        @SuppressLint("ServiceCast")
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(context, AlarmReceiver.class);
        @SuppressLint("UnspecifiedImmutableFlag") PendingIntent pendingAlarmIntent = PendingIntent.getBroadcast(context, 1, alarmIntent, PendingIntent.FLAG_ONE_SHOT);
        alarmManager.cancel(pendingAlarmIntent);
    }


}

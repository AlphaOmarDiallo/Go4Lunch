package com.alphaomardiallo.go4lunch.data.repositories;

import static android.content.ContentValues.TAG;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.alphaomardiallo.go4lunch.data.dataSources.Model.Booking;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

public class BookingRepositoryImp implements BookingRepository {

    private static final String COLLECTION_NAME = "booking";
    private FirebaseFirestore database;
    private MutableLiveData<List<Booking>> allBookings = new MutableLiveData<>();

    @Inject
    public BookingRepositoryImp() {
    }

    public void getInstance() {
        database = FirebaseFirestore.getInstance();
        Log.i(TAG, "getInstance: FireBase " + database);
    }

    public void createBookingAndAddInDatabase(@NonNull Booking bookingToSave) {
        //Create a booking
        Map<String, Object> booking = new HashMap<>();
        booking.put("bookingDate", FieldValue.serverTimestamp());
        booking.put("bookedRestaurantID", bookingToSave.getBookedRestaurantID());
        booking.put("userWhoBooked", bookingToSave.getUserWhoBooked());

        database.collection(COLLECTION_NAME)
                .add(booking)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "onSuccess: document added " + documentReference);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "onFailure: Error adding document " + e.getMessage(), null);
                    }
                });
    }

    public void getAllBookingsFromDataBase() {
        database.collection(COLLECTION_NAME)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.w(TAG, "Listen fail ", error);
                            return;
                        }

                        if (value != null) {
                            List<Booking> temList = value.toObjects(Booking.class);
                            allBookings.setValue(temList);
                            Log.d(TAG, "onEvent: all bookings " + allBookings.getValue());
                        } else {
                            Log.d(TAG, "onEvent: all user is null");
                        }

                    }
                });
    }

    @Override
    public LiveData<List<Booking>> getAllBookings() {
        return allBookings;
    }

    public void updateBooking(String bookingID, String restaurantID) {
        database.collection(COLLECTION_NAME).document(bookingID)
                .update("restaurantID", restaurantID)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "onSuccess: DocumentSnapshot successfully updated");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "onFailure: Error updating document", e);
                    }
                });
    }

    public void deleteBookingInDatabase(String bookingID) {
        database.collection(COLLECTION_NAME).document(bookingID)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        System.out.println("document has been deleted");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "onFailure: Error deleting document " + bookingID, e);
                    }
                });

    }


}

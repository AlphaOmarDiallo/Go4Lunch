package com.alphaomardiallo.go4lunch.data.viewModels;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.alphaomardiallo.go4lunch.data.dataSources.Model.Booking;
import com.alphaomardiallo.go4lunch.data.dataSources.Model.User;
import com.alphaomardiallo.go4lunch.data.dataSources.Model.detailsPojo.Result;
import com.alphaomardiallo.go4lunch.data.dataSources.Model.nearBySearchPojo.ResultsItem;
import com.alphaomardiallo.go4lunch.data.repositories.BookingRepository;
import com.alphaomardiallo.go4lunch.data.repositories.LocationRepository;
import com.alphaomardiallo.go4lunch.data.repositories.PermissionRepository;
import com.alphaomardiallo.go4lunch.data.repositories.PlacesAPIRepository;
import com.alphaomardiallo.go4lunch.data.repositories.UserRepository;
import com.alphaomardiallo.go4lunch.data.repositories.UserRepositoryImp;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class MainSharedViewModel extends ViewModel {

    private final UserRepositoryImp userRepositoryImp;
    private final LocationRepository locationRepository;
    private final PlacesAPIRepository placesAPIRepository;
    private final PermissionRepository permissionRepository;
    private final BookingRepository bookingRepository;
    private final MutableLiveData<List<User>> allUsers = new MutableLiveData<>();
    private final MutableLiveData<List<Booking>> allBookings = new MutableLiveData<>();
    private final MutableLiveData<String> restaurantToFocusOn = new MutableLiveData<>();
    private final LiveData<List<ResultsItem>> restaurants;
    private Location savedLocation;
    private MutableLiveData<Boolean> hasPermissions = new MutableLiveData<>();

    @Inject
    public MainSharedViewModel(UserRepositoryImp userRepositoryImp, LocationRepository locationRepository, PlacesAPIRepository placesAPIRepository, PermissionRepository permissionRepository, BookingRepository bookingRepository) {
        this.userRepositoryImp = userRepositoryImp;
        this.locationRepository = locationRepository;
        this.placesAPIRepository = placesAPIRepository;
        this.permissionRepository = permissionRepository;
        this.bookingRepository = bookingRepository;
        restaurants = placesAPIRepository.getNearBySearchRestaurantList();

    }

    /**
     * Firebase login
     */

    public UserRepository getInstance() {
        return userRepositoryImp.getInstance();
    }

    public FirebaseUser getCurrentUser() {
        return userRepositoryImp.getCurrentUser();
    }

    public Boolean isCurrentUserNotLoggedIn() {
        return (this.getCurrentUser() == null);
    }

    public Task<Void> signOut(Context context) {
        return userRepositoryImp.signOut(context);
    }

    /**
     * Location tracking
     */

    public void startTrackingLocation(Context context, Activity activity) {
        locationRepository.startLocationRequest(context, activity);
    }

    public void stopTrackingLocation() {
        locationRepository.stopLocationUpdates();
    }

    public LiveData<Location> getCurrentLocation() {
        return locationRepository.getCurrentLocation();
    }

    /**
     * Restaurant list
     */

    public LiveData<List<ResultsItem>> getRestaurants() {
        return restaurants;
    }

    public void getAllRestaurantList(Context context, Location location) {
        if (savedLocation == null) {
            if (permissionRepository.hasLocationPermissions(context)) {
                savedLocation = location;
            } else {
                savedLocation = locationRepository.getOfficeLocation();
            }

            Location currentLocation = locationRepository.getCurrentLocation().getValue();

            String locationString = Objects.requireNonNull(currentLocation).getLatitude() + "," + currentLocation.getLongitude();
            placesAPIRepository.fetchNearBySearchPlaces(locationString, locationRepository.getRadius());

        } else {
            if (savedLocation.distanceTo(location) > 51) {
                Location currentLocation = locationRepository.getCurrentLocation().getValue();

                String locationString = Objects.requireNonNull(currentLocation).getLatitude() + "," + currentLocation.getLongitude();
                placesAPIRepository.fetchNearBySearchPlaces(locationString, locationRepository.getRadius());
                Log.e(TAG, "getAllRestaurantList: distance condition met", null);
            }
        }
    }

    /**
     * Search result
     */

    public void setIdRestaurantToFocusOn(String ID) {
        restaurantToFocusOn.setValue(ID);
    }

    public LiveData<String> getRestaurantToFocusOn() {
        return restaurantToFocusOn;
    }

    /**
     * Details about restaurant
     */

    public LiveData<Result> getPartialRestaurantDetails() {
        return placesAPIRepository.getSelectedRestaurantDetails();
    }

    public void fetchPartialRestaurantDetails(String restaurantID) {
        placesAPIRepository.fetchOneNearByRestaurantDetail(restaurantID);
    }

    /**
     * Permission
     */

    public Boolean hasPermission(Context context) {
        return permissionRepository.hasLocationPermissions(context);
    }

    public void permissionSet(boolean hasPermission) {
        hasPermissions.setValue(hasPermission);
    }

    public LiveData<Boolean> observePermissionState() {
        return hasPermissions;
    }

    public LiveData<Boolean> liveDataHasPermission(Context context) {
        return permissionRepository.liveDataHasLocationPermission(context);
    }

    /**
     * UI related
     */

    public Bitmap resizeMarker(Resources resources, int drawable) {
        int height = 120;
        int width = 100;
        Bitmap icon = BitmapFactory.decodeResource(resources, drawable);
        return Bitmap.createScaledBitmap(icon, width, height, false);
    }

    /**
     * Firebase Firestore
     */

    // ============================= user ======================================
    public void getDataBaseInstanceUser() {
        userRepositoryImp.getDataBaseInstance();
    }

    public void createUserInDataBase() {
        userRepositoryImp.createUser();
    }

    public Task<User> getUserData() {
        return userRepositoryImp.getUserData().continueWith(task -> task.getResult().toObject(User.class));
    }

    public void getAllUsersFromDatabase() {
        userRepositoryImp.getAllUsersFromDataBase();
    }

    public LiveData<List<User>> observeUserList() {
        allUsers.setValue(userRepositoryImp.getAllUsers().getValue());
        return allUsers;
    }


    // ============================== booking =============================================

    public void getDatabaseInstanceBooking() {
        bookingRepository.getInstance();
    }

    public void createBooking(Booking bookingToSave) {
        bookingRepository.createBookingAndAddInDatabase(bookingToSave);
    }

    public void updateBooking(String bookingID, String restaurantID) {
        bookingRepository.updateBooking(bookingID, restaurantID);
    }

    public void deleteBooking(String bookingID) {
        bookingRepository.deleteBookingInDatabase(bookingID);
    }

    public void deleteBookingFromPreviousDays() {
        bookingRepository.deleteBookingsFromPreviousDays();
    }

    public void observeBookingsFromDataBase() {
        bookingRepository.getAllBookingsFromDataBase();
    }

    public LiveData<List<Booking>> getAllBookings() {
        return bookingRepository.getAllBookings();
    }

    private void deleteBookingsFromPreviousDays() {
        List<Booking> allBookings = bookingRepository.getAllBookings().getValue();

        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String strDate = sdf.format(c.getTime());
        Log.d("Date","DATE : " + strDate);

        for (Booking booking : allBookings) {
            System.out.println(booking.getBookingDate().toString());
        }

    }
}

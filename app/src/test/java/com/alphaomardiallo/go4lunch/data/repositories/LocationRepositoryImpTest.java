package com.alphaomardiallo.go4lunch.data.repositories;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

import android.content.Context;
import android.location.Location;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.alphaomardiallo.go4lunch.ui.activities.MainActivity;
import com.google.android.gms.location.FusedLocationProviderClient;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class LocationRepositoryImpTest {

    public LocationRepositoryImp locationRepositoryImp = new LocationRepositoryImp();
    public LocationRepositoryImp mockLocationRepositoryImp = mock(LocationRepositoryImp.class);

    public MutableLiveData<Location> locationLiveData = spy(new MutableLiveData<>());
    public Location locationMirror = mock(Location.class);
    public Location location1 = mock(Location.class);
    public FusedLocationProviderClient fusedLocationProviderClient = mock(FusedLocationProviderClient.class);

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Before
    public void init() {
        locationRepositoryImp.locationMutableLiveData.observeForever(location -> locationMirror = location);
    }

    @Test
    public void getRadius() {
        assertThat(locationRepositoryImp.getRadius()).isEqualTo(500);
    }

    @Test
    public void getCurrentLocation() {
        locationRepositoryImp.locationMutableLiveData.setValue(location1);
        assertThat(locationMirror).isEqualTo(location1);
    }
    @Test
    public void createLocationCallBack() {
        locationRepositoryImp.createLocationCallback();
        assertThat(locationRepositoryImp.locationCallback).isNotNull();
    }

    @Test
    public void create_a_location_request() {
        locationRepositoryImp.setupLocationRequest();
        assertThat(locationRepositoryImp.locationRequest).isNotNull();
    }

    @Test
    public void doS_Start_location_callBack() {
        Context context = mock(Context.class);
        MainActivity mainActivity = mock(MainActivity.class);
        LiveData<Location> currentLocation = locationLiveData;
        mockLocationRepositoryImp.startLocationRequest(context,mainActivity);

        locationRepositoryImp.getCurrentLocation();
        locationLiveData.postValue(locationMirror);

        assertThat(locationLiveData.getValue()).isEqualTo(currentLocation.getValue());
    }

    @Test
    public void get_last_known_location() {
        mockLocationRepositoryImp.instantiateFusedLocationProviderClient(mock(Context.class));
        assertThat(fusedLocationProviderClient).isNotNull();
    }

    @Test
    public void start_location_updates() {
        mockLocationRepositoryImp.startLocationUpdates();
        assertThat(locationRepositoryImp.fusedLocationProviderClient).isNull();
    }
}
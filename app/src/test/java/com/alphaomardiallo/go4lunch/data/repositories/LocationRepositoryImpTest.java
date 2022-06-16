package com.alphaomardiallo.go4lunch.data.repositories;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

import android.content.Context;
import android.location.Location;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.alphaomardiallo.go4lunch.ui.activities.MainActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class LocationRepositoryImpTest {

    public LocationRepositoryImp locationRepositoryImp = new LocationRepositoryImp();
    public LocationRepositoryImp mockLocationRepositoryImp = mock(LocationRepositoryImp.class);

    public MutableLiveData<Location> locationLiveData = spy(new MutableLiveData<>());
    public Location locationMirror = mock(Location.class);
    public Location location1 = mock(Location.class);

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Before
    public void init() {
        locationRepositoryImp.locationMutableLiveData.observeForever(new Observer<Location>() {
            @Override
            public void onChanged(Location location) {
                locationMirror = location;
            }
        });
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
        doNothing().when(mockLocationRepositoryImp).createLocationCallback();
    }

    @Test
    public void doS_Start_location_callBack() {
        Context context = mock(Context.class);
        MainActivity mainActivity = mock(MainActivity.class);
        LiveData<Location> currentLocation = locationLiveData;
        doNothing().when(mockLocationRepositoryImp).startLocationRequest(context,mainActivity);

        doReturn(currentLocation).when(mockLocationRepositoryImp).getCurrentLocation();
        locationLiveData.postValue(locationMirror);

        assertThat(locationLiveData.getValue()).isEqualTo(currentLocation.getValue());
    }




}
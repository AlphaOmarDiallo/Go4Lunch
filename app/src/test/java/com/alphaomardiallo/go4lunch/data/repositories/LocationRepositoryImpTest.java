package com.alphaomardiallo.go4lunch.data.repositories;

import static com.google.common.truth.Truth.assertThat;

import static org.mockito.Mockito.mock;

import android.location.Location;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class LocationRepositoryImpTest {

    public LocationRepositoryImp locationRepositoryImp = new LocationRepositoryImp();
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


}
package com.alphaomardiallo.go4lunch.data.repositories;

import static com.google.common.truth.Truth.assertThat;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;

import com.alphaomardiallo.go4lunch.data.dataSources.Model.Booking;
import com.alphaomardiallo.go4lunch.ui.activities.MainActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import javax.inject.Inject;

import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;

@HiltAndroidTest
public class BookingRepositoryImpTest {

    @Inject BookingRepositoryImp bookingRepository;

    Booking booking = new Booking("restaurantID", "testRestaurant", "mNHHVbsTDkNTYBs6AQ97O8Wi1so1");

    @Rule
    public HiltAndroidRule hiltAndroidRule = new HiltAndroidRule(this);

    @Rule
    public ActivityScenarioRule<MainActivity> activityScenarioRule = new ActivityScenarioRule(MainActivity.class);

    @Before
    public void setUp() throws Exception {
        hiltAndroidRule.inject();
        bookingRepository.getInstance();
    }

    @Test
    public void bookingRepository_is_not_null(){
        assertThat(bookingRepository).isNotNull();
        assertThat(hiltAndroidRule).isNotNull();
    }

    @Test
    public void get_all_bookings_from_database_correctly() {
        bookingRepository.getAllBookingsFromDataBase();
        assertThat(bookingRepository.getAllBookingsAsList()).isNotNull();
    }

    @Test
    public void set_Alarm() {
        if (bookingRepository.checkDateToSetNotification() > 0) {

        }
        bookingRepository.setAlarmExactRTCWakeUp(InstrumentationRegistry.getInstrumentation().getContext());
    }
}
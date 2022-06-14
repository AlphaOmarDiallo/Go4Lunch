package com.alphaomardiallo.go4lunch.ui.activities;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.LooperMode;

import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;
import dagger.hilt.android.testing.HiltTestApplication;

@HiltAndroidTest
@Config(application = HiltTestApplication.class)
@RunWith(RobolectricTestRunner.class)
@LooperMode(LooperMode.Mode.PAUSED)
public class RoboMainActivityTest {

    @Rule public HiltAndroidRule hiltRule = new HiltAndroidRule(this);

    @Before
    public void setup(){
        hiltRule.inject();
    }

    @Test
    public void fakeTest() {
        assert true;
    }

}
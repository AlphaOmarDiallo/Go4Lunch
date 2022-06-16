package com.alphaomardiallo.go4lunch.ui.activities;

import static com.google.common.truth.Truth.assertThat;

import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;

@HiltAndroidTest
public class MainActivityTest {

    @Rule
    public HiltAndroidRule hiltAndroidRule = new HiltAndroidRule(this);

    @Rule
    public ActivityScenarioRule<MainActivity> activityScenarioRule = new ActivityScenarioRule<>(MainActivity.class);

    @Before
    public void init() {
        hiltAndroidRule.inject();
    }

    @Test
    public void test() {
        assertThat(1).isEqualTo(1);
    }
}
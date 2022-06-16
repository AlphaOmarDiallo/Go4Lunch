package com.alphaomardiallo.go4lunch.data.repositories;


import static com.google.common.truth.Truth.assertThat;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import javax.inject.Inject;

import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;

@HiltAndroidTest
public class AutocompleteRepositoryImpTest {

    @Inject
    public AutocompleteRepository autocompleteRepository;

    @Rule
    public HiltAndroidRule hiltAndroidRule = new HiltAndroidRule(this);

    @Before
    public void setUp() throws Exception {
        hiltAndroidRule.inject();
    }

    @Test
    public void updatePredictionList() {
        assertThat(hiltAndroidRule).isNotNull();
    }

    @Test
    public void searchPredictionResults() {
    }

    @Test
    public void setPlaceToFocusOn() {
    }
}
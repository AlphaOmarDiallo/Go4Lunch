package com.alphaomardiallo.go4lunch.data.repositories;


import static com.google.common.truth.Truth.assertThat;

import com.alphaomardiallo.go4lunch.data.dataSources.Model.autocompletePojo.PredictionsItem;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;

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

    List<PredictionsItem> predictionItemLiveDataList;
    PredictionsItem restaurant = new PredictionsItem();

    @Test
    public void setAutocompleteRepository_is_not_null() {
        assertThat(hiltAndroidRule).isNotNull();
        assertThat(autocompleteRepository).isNotNull();
    }

    @Test
    public void value_is_updated_and_liveData_is_updated() {
        autocompleteRepository.updatePredictionList(predictionItemLiveDataList);
        assertThat(autocompleteRepository.searchPredictionResults().getValue()).isEqualTo(predictionItemLiveDataList);
    }

    @Test
    public void setPlaceToFocusOn() {
        autocompleteRepository.setPlaceToFocusOn(restaurant);
    }
}
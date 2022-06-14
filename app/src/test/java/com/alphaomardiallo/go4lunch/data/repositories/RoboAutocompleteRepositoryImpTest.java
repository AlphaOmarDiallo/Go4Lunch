package com.alphaomardiallo.go4lunch.data.repositories;

import static com.google.common.truth.Truth.assertThat;

import static org.mockito.Mockito.mock;

import com.alphaomardiallo.go4lunch.data.dataSources.Model.autocompletePojo.PredictionsItem;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.LooperMode;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;
import dagger.hilt.android.testing.HiltTestApplication;

@HiltAndroidTest
@Config(application = HiltTestApplication.class)
@RunWith(RobolectricTestRunner.class)
@LooperMode(LooperMode.Mode.PAUSED)
public class RoboAutocompleteRepositoryImpTest {

    @Inject
    AutocompleteRepositoryImp autocompleteRepository;
    @Inject
    PlacesAPIRepositoryImp placesAPIRepository;
    @Inject
    LocationRepositoryImp locationRepositoryImp;

    List<PredictionsItem> list = new ArrayList<>();
    PredictionsItem predictionsItem = mock(PredictionsItem.class);

    @Rule
    public HiltAndroidRule hiltRule = new HiltAndroidRule(this);

    @Before
    public void setUp() {
        hiltRule.inject();
    }

    @Test
    public void assert_that_autocomplete_repository_is_not_null (){
        assertThat(autocompleteRepository).isNotNull();
    }

    @Test
    public void updatePredictionList() {
        list.add(predictionsItem);
        autocompleteRepository.updatePredictionList(list);
        assertThat(autocompleteRepository.predictionsList).isNotNull();
        assertThat(autocompleteRepository.predictionsList.getValue()).isNotEmpty();
    }

    @Test
    public void searchPredictionResults() {
        assertThat(list).isNotNull();
    }

    @Test
    public void setPlaceToFocusOn() {
        autocompleteRepository.setPlaceToFocusOn(predictionsItem);
        assertThat(autocompleteRepository.selectedRestaurant.getValue()).isEqualTo(predictionsItem);
    }

}
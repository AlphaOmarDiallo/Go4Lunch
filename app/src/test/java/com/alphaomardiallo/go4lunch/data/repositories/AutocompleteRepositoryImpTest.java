package com.alphaomardiallo.go4lunch.data.repositories;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.mock;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;

import com.alphaomardiallo.go4lunch.data.dataSources.Model.autocompletePojo.PredictionsItem;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.List;

@RunWith(JUnit4.class)
public class AutocompleteRepositoryImpTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    public AutocompleteRepositoryImp autocompleteRepositoryImp = new AutocompleteRepositoryImp();

    public List<PredictionsItem> list = new ArrayList<>();
    public List<PredictionsItem> listMirroring = new ArrayList<>();
    public PredictionsItem selectedRestaurant = mock(PredictionsItem.class);
    public PredictionsItem restaurant1 = mock(PredictionsItem.class);

    @Before
    public void init() {
        autocompleteRepositoryImp.selectedRestaurant.observeForever(new Observer<PredictionsItem>() {
            @Override
            public void onChanged(PredictionsItem predictionsItem) {
                selectedRestaurant = predictionsItem;
                System.out.println(predictionsItem);
            }
        });

        autocompleteRepositoryImp.predictionsList.observeForever(new Observer<List<PredictionsItem>>() {
            @Override
            public void onChanged(List<PredictionsItem> predictionsItems) {
                listMirroring = predictionsItems;
                System.out.println(predictionsItems);
            }
        });
    }

    @Test
    public void updatePredictionList() {
        assertThat(list).isNotNull();
        assertThat(list).isEmpty();
        list.add(restaurant1);
        autocompleteRepositoryImp.updatePredictionList(list);
        assertThat(listMirroring.size()).isEqualTo(list.size());
        assertThat(listMirroring).isEqualTo(list);
    }

    @Test
    public void searchPredictionResults() {
        assertThat(restaurant1).isNotNull();
        autocompleteRepositoryImp.setPlaceToFocusOn(restaurant1);
        autocompleteRepositoryImp.searchPredictionResults();
        assertThat(selectedRestaurant).isEqualTo(restaurant1);
    }

    @Test
    public void setPlaceToFocusOn() {
        assertThat(restaurant1).isNotNull();
        autocompleteRepositoryImp.setPlaceToFocusOn(restaurant1);
        assertThat(selectedRestaurant).isEqualTo(restaurant1);
    }
}
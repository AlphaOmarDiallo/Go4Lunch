package com.alphaomardiallo.go4lunch.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.alphaomardiallo.go4lunch.R;
import com.alphaomardiallo.go4lunch.data.dataSources.Model.autocompletePojo.PredictionsItem;
import com.alphaomardiallo.go4lunch.data.viewModels.SearchViewModel;
import com.alphaomardiallo.go4lunch.databinding.ActivitySearchBinding;
import com.alphaomardiallo.go4lunch.domain.OnClickItemListener;
import com.alphaomardiallo.go4lunch.ui.adapters.SearchAdapter;
import com.bumptech.glide.Glide;

import java.util.List;
import java.util.Objects;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SearchActivity extends AppCompatActivity implements OnClickItemListener {

    private static final String KEY_SELECTED_RESTAURANT_ID = "placeID";
    private static final String KEY_SELECTED_RESTAURANT_NAME = "placeName";
    private final SearchAdapter adapter = new SearchAdapter(new SearchAdapter.ListSearchDiff(), this);
    private SearchViewModel viewModel;
    private ActivitySearchBinding binding;
    private String query;
    private String location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        viewModel = new ViewModelProvider(this).get(SearchViewModel.class);
        setContentView(view);

        getIntentAndData();

        setupSearch();

        showSoftKeyboard(binding.searchViewSA);

        setupResultsInRecyclerView();
    }

    public void getIntentAndData() {
        Intent intent = getIntent();
        query = intent.getStringExtra("Query");
        location = intent.getStringExtra("Location");
    }

    private void setupSearch() {
        binding.searchViewSA.setIconifiedByDefault(false);
        binding.searchViewSA.setQuery(query, true);
        binding.searchViewSA.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                viewModel.searchAutoComplete(s, location);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                viewModel.searchAutoComplete(s, location);
                return true;
            }
        });
    }

    public void showSoftKeyboard(View view) {
        if (view.requestFocus()) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    private void setupResultsInRecyclerView() {
        binding.recyclerViewSA.setHasFixedSize(true);
        binding.recyclerViewSA.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        binding.recyclerViewSA.setAdapter(adapter);

        Glide.with(binding.iVLoaderSA)
                .load(getString(R.string.no_results_gif))
                .into(binding.iVLoaderSA);

        viewModel.getPredictionList().observe(this, adapter::submitList);
        viewModel.getPredictionList().observe(this, this::removeImageIfListNotEmpty);
    }

    private void removeImageIfListNotEmpty(List<PredictionsItem> predictionsItems) {
        if (predictionsItems != null) {
            if (predictionsItems.size() > 0) {
                binding.iVLoaderSA.setVisibility(View.INVISIBLE);
            } else {
                binding.iVLoaderSA.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onClickItem(int position) {
        PredictionsItem restaurant = Objects.requireNonNull(viewModel.getPredictionList().getValue()).get(position);
        viewModel.setRestaurantToFocusOn(restaurant);
        Intent returnIntent = new Intent();
        returnIntent.putExtra(KEY_SELECTED_RESTAURANT_ID, restaurant.getPlaceId());
        returnIntent.putExtra(KEY_SELECTED_RESTAURANT_NAME, restaurant.getStructuredFormatting().getMainText());
        setResult(RESULT_OK, returnIntent);
        finish();
    }

}
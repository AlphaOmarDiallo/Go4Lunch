package com.alphaomardiallo.go4lunch.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.alphaomardiallo.go4lunch.data.dataSources.Model.autocompletePojo.PredictionsItem;
import com.alphaomardiallo.go4lunch.data.viewModels.SearchViewModel;

import com.alphaomardiallo.go4lunch.databinding.ActivitySearchBinding;
import com.alphaomardiallo.go4lunch.domain.OnClickItemListener;
import com.alphaomardiallo.go4lunch.ui.adapters.SearchAdapter;

import java.util.Objects;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SearchActivity extends AppCompatActivity implements OnClickItemListener {

    private SearchViewModel viewModel;
    private ActivitySearchBinding binding;
    private final SearchAdapter adapter = new SearchAdapter(new SearchAdapter.ListSearchDiff(), this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        viewModel = new ViewModelProvider(this).get(SearchViewModel.class);
        setContentView(view);

        Intent intent = getIntent();
        String query = intent.getStringExtra("Query");
        String location = intent.getStringExtra("Location");
        Log.e("Search", "onCreate: " + query);

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

        binding.recyclerViewSA.setHasFixedSize(true);
        binding.recyclerViewSA.setLayoutManager(new LinearLayoutManager(view.getContext(), LinearLayoutManager.VERTICAL, false));
        binding.recyclerViewSA.setAdapter(adapter);

        viewModel.getPredictionList().observe(this, adapter::submitList);
    }

    @Override
    public void onClickItem(int position) {
        PredictionsItem restaurant = Objects.requireNonNull(viewModel.getPredictionList().getValue()).get(position);
        viewModel.setRestaurantToFocusOn(restaurant);
        finish();
    }

}
package com.alphaomardiallo.go4lunch.ui.adapters;


import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.alphaomardiallo.go4lunch.R;
import com.alphaomardiallo.go4lunch.data.dataSources.Model.nearBySearchPojo.ResultsItem;
import com.alphaomardiallo.go4lunch.databinding.ItemRestaurantBinding;

import java.util.List;

public class ListViewAdapter extends ListAdapter <ResultsItem, ListViewAdapter.ListViewHolder> {

    ItemRestaurantBinding binding;
    private List<ResultsItem> resultsItemList;

    public ListViewAdapter(@NonNull DiffUtil.ItemCallback<ResultsItem> diffCallback, List<ResultsItem> resultsItemList) {
        super(diffCallback);
        this.resultsItemList = resultsItemList;
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = ItemRestaurantBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ListViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull ListViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    public static class ListDiff extends DiffUtil.ItemCallback<ResultsItem> {

        @Override
        public boolean areItemsTheSame(@NonNull ResultsItem oldItem, @NonNull ResultsItem newItem) {
            return oldItem.getPlaceId() == newItem.getPlaceId();
        }

        @SuppressLint("DiffUtilEquals")
        @Override
        public boolean areContentsTheSame(@NonNull ResultsItem oldItem, @NonNull ResultsItem newItem) {
            return oldItem.getPlaceId() == newItem.getPlaceId() &&
                    oldItem.getName() == newItem.getName() &&
                    oldItem.getGeometry().getLocation() == newItem.getGeometry().getLocation();

        }
    }

    public static class ListViewHolder extends RecyclerView.ViewHolder {
        TextView restaurantName;

        public ListViewHolder(@NonNull View itemView) {
            super(itemView);
            restaurantName = itemView.findViewById(R.id.tvRestaurantName);
        }

        public void bind(ResultsItem restaurant) {
            restaurantName.setText(restaurant.getName());
        }

        static ListViewHolder create(ViewGroup parent) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_restaurant, parent, false);
            return new ListViewHolder(view);
        }
    }
}

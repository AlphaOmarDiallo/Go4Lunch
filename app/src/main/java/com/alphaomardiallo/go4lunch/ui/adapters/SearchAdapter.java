package com.alphaomardiallo.go4lunch.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.alphaomardiallo.go4lunch.R;
import com.alphaomardiallo.go4lunch.data.dataSources.Model.autocompletePojo.PredictionsItem;
import com.alphaomardiallo.go4lunch.databinding.ItemSearchBinding;
import com.alphaomardiallo.go4lunch.domain.OnClickRestaurantListener;

public class SearchAdapter extends ListAdapter<PredictionsItem, SearchAdapter.SearchViewHolder> {

    public ItemSearchBinding binding;
    final OnClickRestaurantListener onClickRestaurantListener;

    public SearchAdapter(@NonNull DiffUtil.ItemCallback<PredictionsItem> diffCallback, OnClickRestaurantListener onClickRestaurantListener) {
        super(diffCallback);
        this.onClickRestaurantListener = onClickRestaurantListener;
    }

    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = ItemSearchBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new SearchViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {
        holder.bind(getItem(position), onClickRestaurantListener);
    }

    public static class ListSearchDiff extends DiffUtil.ItemCallback<PredictionsItem> {

        @Override
        public boolean areItemsTheSame(@NonNull PredictionsItem oldItem, @NonNull PredictionsItem newItem) {
            return oldItem.getPlaceId().equalsIgnoreCase(newItem.getPlaceId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull PredictionsItem oldItem, @NonNull PredictionsItem newItem) {
            return oldItem.getDescription().equalsIgnoreCase(newItem.getDescription());
        }
    }

    public static class SearchViewHolder extends RecyclerView.ViewHolder {

        TextView tvName;
        TextView tvAddress;
        ConstraintLayout card;

        public SearchViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tvNameIS);
            tvAddress = itemView.findViewById(R.id.tvAddressIS);
            card = itemView.findViewById(R.id.search_item);
        }

        public void bind(PredictionsItem restaurant, OnClickRestaurantListener onClickRestaurantListener) {
            tvName.setText(restaurant.getStructuredFormatting().getMainText());
            tvAddress.setText(restaurant.getStructuredFormatting().getSecondaryText());
            card.setOnClickListener(view -> onClickRestaurantListener.onClickRestaurant(getAbsoluteAdapterPosition()));
        }
    }
}

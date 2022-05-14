package com.alphaomardiallo.go4lunch.ui.adapters;


import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.alphaomardiallo.go4lunch.BuildConfig;
import com.alphaomardiallo.go4lunch.R;
import com.alphaomardiallo.go4lunch.data.dataSources.Model.nearBySearchPojo.ResultsItem;
import com.alphaomardiallo.go4lunch.databinding.ItemRestaurantBinding;
import com.bumptech.glide.Glide;

import java.util.List;

public class ListViewAdapter extends ListAdapter<ResultsItem, ListViewAdapter.ListViewHolder> {

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
            return oldItem.getPlaceId().equals(newItem.getPlaceId());
        }

        @SuppressLint("DiffUtilEquals")
        @Override
        public boolean areContentsTheSame(@NonNull ResultsItem oldItem, @NonNull ResultsItem newItem) {
            return oldItem.getPlaceId().equals(newItem.getPlaceId()) &&
                    oldItem.getName().equalsIgnoreCase(newItem.getName()) &&
                    oldItem.getGeometry().getLocation() == newItem.getGeometry().getLocation();

        }
    }

    public static class ListViewHolder extends RecyclerView.ViewHolder {
        TextView restaurantName;
        TextView restaurantStyleAndAddress;
        TextView restaurantOpeningTime;
        TextView restaurantDistance;
        ImageView restaurantPersonOutlined;
        TextView restaurantNumberOfWorkmates;
        RatingBar restaurantRating;
        ImageView restaurantPhoto;

        public ListViewHolder(@NonNull View itemView) {
            super(itemView);
            restaurantName = itemView.findViewById(R.id.tvRestaurantName);
            restaurantStyleAndAddress = itemView.findViewById(R.id.tvStyleAndAddress);
            restaurantOpeningTime = itemView.findViewById(R.id.tvOpeningHours);
            restaurantDistance = itemView.findViewById(R.id.tvDistance);
            restaurantPersonOutlined = itemView.findViewById(R.id.iVOutlinePerson);
            restaurantNumberOfWorkmates = itemView.findViewById(R.id.tvNumberOfGuests);
            restaurantRating = itemView.findViewById(R.id.ratingBar);
            restaurantPhoto = itemView.findViewById(R.id.ivPhotoRestaurant);
        }

        public void bind(ResultsItem restaurant) {
            restaurantName.setText(restaurant.getName());
            restaurantStyleAndAddress.setText(restaurant.getVicinity());
            restaurantOpeningTime.setText(getOpeningTime(restaurant.getOpeningHours().isOpenNow() ));
            restaurantDistance.setText(restaurant.getGeometry().getLocation().toString());
            restaurantNumberOfWorkmates.setText("2");
            setupRatingBar();
            restaurantRating.setRating(getRating(restaurant.getRating()));
            Glide.with(restaurantPhoto)
                    .load("https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photo_reference="+ restaurant.getPhotos().get(0).getPhotoReference() +"&key="+ BuildConfig.PLACES_API_KEY)
                    .into(restaurantPhoto);
        }

        static ListViewHolder create(ViewGroup parent) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_restaurant, parent, false);
            return new ListViewHolder(view);
        }

        private String getOpeningTime(Boolean isOpenNow) {
            if (!isOpenNow) {
                return "Closed";
            } else {
                return "Open now";
            }
        }

        private void setupRatingBar() {
            restaurantRating.setIsIndicator(true);
            restaurantRating.setMax(3);
            restaurantRating.setNumStars(3);
            restaurantRating.setStepSize(0.1f);

        }

        private float getRating(Double rating) {
            return (float) ((rating / 5) * 3);
        }
    }
}

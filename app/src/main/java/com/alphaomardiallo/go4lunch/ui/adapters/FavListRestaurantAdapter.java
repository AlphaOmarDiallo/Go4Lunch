package com.alphaomardiallo.go4lunch.ui.adapters;


import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.alphaomardiallo.go4lunch.BuildConfig;
import com.alphaomardiallo.go4lunch.R;
import com.alphaomardiallo.go4lunch.data.dataSources.Model.detailsPojo.Result;
import com.alphaomardiallo.go4lunch.databinding.ItemRestaurantBinding;
import com.alphaomardiallo.go4lunch.domain.OnClickRestaurantListener;
import com.bumptech.glide.Glide;

public class FavListRestaurantAdapter extends ListAdapter<Result, FavListRestaurantAdapter.ListViewHolder> {

    private final OnClickRestaurantListener onClickRestaurantListener;
    private static final String OPEN = "Open now";
    public static final String CLOSED = "Closed";

    public FavListRestaurantAdapter(@NonNull DiffUtil.ItemCallback<Result> diffCallback, OnClickRestaurantListener onClickRestaurantListener) {
        super(diffCallback);
        this.onClickRestaurantListener = onClickRestaurantListener;
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        com.alphaomardiallo.go4lunch.databinding.ItemRestaurantBinding binding = ItemRestaurantBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ListViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull ListViewHolder holder, int position) {
        holder.bind(getItem(position), onClickRestaurantListener);
    }

    public static class ListDiff extends DiffUtil.ItemCallback<Result> {

        @Override
        public boolean areItemsTheSame(@NonNull Result oldItem, @NonNull Result newItem) {
            return oldItem.getPlaceId().equals(newItem.getPlaceId());
        }

        @SuppressLint("DiffUtilEquals")
        @Override
        public boolean areContentsTheSame(@NonNull Result oldItem, @NonNull Result newItem) {
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
        ConstraintLayout card;

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
            card = itemView.findViewById(R.id.restaurant_item);

            setupRatingBar();
        }

        public void bind(Result restaurant, OnClickRestaurantListener onClickRestaurantListener) {
            restaurantName.setText(restaurant.getName());
            restaurantStyleAndAddress.setText(restaurant.getVicinity());
            restaurantOpeningTime.setText(getOpeningTime(restaurant.getOpeningHours().isOpenNow()));
            restaurantRating.setRating(getRating(restaurant.getRating()));

            if (restaurantPhoto != null) {
                Glide.with(restaurantPhoto)
                        .load("https://maps.googleapis.com/maps/api/place/photo?maxwidth=100&photo_reference=" + restaurant.getPhotos().get(0).getPhotoReference() + "&key=" + BuildConfig.PLACES_API_KEY)
                        .placeholder(R.drawable.hungry_droid)
                        .into(restaurantPhoto);
            } else {
                assert false;
                restaurantPhoto.setImageResource(R.drawable.hungry_droid);
            }

            card.setOnClickListener(view -> onClickRestaurantListener.onClickRestaurant(getAbsoluteAdapterPosition()));
        }

        private String getOpeningTime(Boolean isOpenNow) {
            if (!isOpenNow) {
                restaurantOpeningTime.setTextColor(Color.RED);
                return CLOSED;
            } else {
                restaurantOpeningTime.setTextColor(Color.BLUE);
                return OPEN;
            }
        }

        private void setupRatingBar() {
            restaurantRating.setIsIndicator(true);
            restaurantRating.setMax(3);
            restaurantRating.setNumStars(3);
            restaurantRating.setStepSize(0.1f);
            restaurantRating.setScaleX(-1f);
        }

        private float getRating(Double rating) {
            return (float) ((rating / 5) * 3);
        }

    }
}

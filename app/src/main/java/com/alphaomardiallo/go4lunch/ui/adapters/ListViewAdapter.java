package com.alphaomardiallo.go4lunch.ui.adapters;


import android.annotation.SuppressLint;
import android.graphics.Color;
import android.location.Location;
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
import com.alphaomardiallo.go4lunch.data.dataSources.Model.Booking;
import com.alphaomardiallo.go4lunch.data.dataSources.Model.nearBySearchPojo.ResultsItem;
import com.alphaomardiallo.go4lunch.databinding.ItemRestaurantBinding;
import com.alphaomardiallo.go4lunch.domain.OnClickRestaurantListener;
import com.bumptech.glide.Glide;

import java.util.List;
import java.util.Locale;

public class ListViewAdapter extends ListAdapter<ResultsItem, ListViewAdapter.ListViewHolder> {

    private final OnClickRestaurantListener onClickRestaurantListener;
    private final Location location;
    private final List<Booking> bookingList;

    public ListViewAdapter(@NonNull DiffUtil.ItemCallback<ResultsItem> diffCallback, OnClickRestaurantListener onClickRestaurantListener, Location location, List<Booking> bookingList) {
        super(diffCallback);
        this.onClickRestaurantListener = onClickRestaurantListener;
        this.location = location;
        this.bookingList = bookingList;
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        com.alphaomardiallo.go4lunch.databinding.ItemRestaurantBinding binding = ItemRestaurantBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ListViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull ListViewHolder holder, int position) {
        holder.bind(getItem(position), onClickRestaurantListener, location, bookingList);
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

        public void bind(ResultsItem restaurant, OnClickRestaurantListener onClickRestaurantListener, Location location, List<Booking> bookingList) {
            restaurantName.setText(restaurant.getName());
            restaurantStyleAndAddress.setText(restaurant.getVicinity());
            restaurantOpeningTime.setText(getOpeningTime(restaurant.getOpeningHours().isOpenNow()));
            restaurantDistance.setText(restaurant.getGeometry().getLocation().toString());
            restaurantNumberOfWorkmates.setText(getNumberOfWorkmates(bookingList, restaurant.getPlaceId()));
            restaurantRating.setRating(getRating(restaurant.getRating()));
            Location restaurantLocation = new Location("restaurant");
            restaurantLocation.setLatitude(restaurant.getGeometry().getLocation().getLat());
            restaurantLocation.setLongitude(restaurant.getGeometry().getLocation().getLng());
            restaurantDistance.setText(String.format("%sm", Math.round(location.distanceTo(restaurantLocation))));

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
                return "Closed";
            } else {
                restaurantOpeningTime.setTextColor(Color.BLUE);
                return "Open now";
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

        private String getNumberOfWorkmates(List<Booking> bookingList, String restaurantID) {
            int numberOfBookings = 0;
            if (bookingList != null && bookingList.size() > 0) {
                for (Booking booking : bookingList) {
                    if (booking.getBookedRestaurantID().equalsIgnoreCase(restaurantID)) {
                        numberOfBookings++;
                    }
                }
            }

            return String.format(Locale.getDefault(), "(%d)", numberOfBookings);
        }
    }
}

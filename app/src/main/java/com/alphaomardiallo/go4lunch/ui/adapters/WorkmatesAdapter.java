package com.alphaomardiallo.go4lunch.ui.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.alphaomardiallo.go4lunch.R;
import com.alphaomardiallo.go4lunch.data.dataSources.Model.Booking;
import com.alphaomardiallo.go4lunch.data.dataSources.Model.User;
import com.alphaomardiallo.go4lunch.databinding.ItemWorkmatesBinding;
import com.alphaomardiallo.go4lunch.domain.OnClickWorkmateListener;
import com.bumptech.glide.Glide;

import java.util.List;

public class WorkmatesAdapter extends ListAdapter<User, WorkmatesAdapter.WorkmatesViewHolder> {

    private final OnClickWorkmateListener onClickItemListener;

    @SuppressLint("StaticFieldLeak")
    private static Context context;
    private final List<Booking> bookingList;

    public WorkmatesAdapter(@NonNull DiffUtil.ItemCallback<User> diffCallback, OnClickWorkmateListener onClickItemListener, List<Booking> bookingList, Context context) {
        super(diffCallback);
        this.onClickItemListener = onClickItemListener;
        this.bookingList = bookingList;
        WorkmatesAdapter.context = context;
    }

    @NonNull
    @Override
    public WorkmatesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        com.alphaomardiallo.go4lunch.databinding.ItemWorkmatesBinding binding = ItemWorkmatesBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new WorkmatesViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull WorkmatesViewHolder holder, int position) {
        holder.bind(getItem(position), onClickItemListener, bookingList);
    }

    public static class ListDiff extends DiffUtil.ItemCallback<User> {

        @Override
        public boolean areItemsTheSame(@NonNull User oldItem, @NonNull User newItem) {
            return oldItem.getUid().equalsIgnoreCase(newItem.getUid());
        }

        @Override
        public boolean areContentsTheSame(@NonNull User oldItem, @NonNull User newItem) {
            return oldItem.getUsername().equalsIgnoreCase(newItem.getUsername()) &&
                    oldItem.getUserEmail().equalsIgnoreCase(newItem.getUserEmail());
        }
    }

    public static class WorkmatesViewHolder extends RecyclerView.ViewHolder {

        ConstraintLayout item;
        ImageView workmateAvatar;
        TextView workmateLunchStatus;

        public WorkmatesViewHolder(@NonNull View itemView) {
            super(itemView);

            item = itemView.findViewById(R.id.itemWorkmate);
            workmateAvatar = itemView.findViewById(R.id.ivWorkmateAvatar);
            workmateLunchStatus = itemView.findViewById(R.id.tvWorkmateRestaurantChoice);
        }


        @SuppressLint("StringFormatMatches")
        public void bind(User user, OnClickWorkmateListener onClickItemListener, List<Booking> bookingList) {
            // Checking for a booking

            boolean hasABooking = false;
            Booking userBooking = null;

            for (Booking booking : bookingList) {
                if (booking.getUserWhoBooked().equalsIgnoreCase(user.getUid())) {
                    hasABooking = true;
                    userBooking = booking;
                    break;
                }
            }

            //ImageView
            if (user.getUrlPicture() != null) {
                Glide.with(workmateAvatar)
                        .load(user.getUrlPicture())
                        .circleCrop()
                        .into(workmateAvatar);
            } else {
                Glide.with(workmateAvatar)
                        .load(context.getString(R.string.fake_avatar))
                        .circleCrop()
                        .into(workmateAvatar);
            }

            //TextView

            if (!hasABooking) {
                workmateLunchStatus.setText(String.format(context.getString(R.string.workmate_has_not_selected_a_restaurant), user.getUsername()));
            } else {
                workmateLunchStatus.setText(String.format(context.getString(R.string.workmate_has_selected_a_restaurant), user.getUsername(), userBooking.getBookedRestaurantName()));
                workmateLunchStatus.setTextColor(Color.BLACK);
            }

            if (hasABooking) {
                Booking finalUserBooking = userBooking;
                item.setOnClickListener(view -> onClickItemListener.onClickWorkmate(finalUserBooking.getBookedRestaurantID()));
            }
        }
    }

}

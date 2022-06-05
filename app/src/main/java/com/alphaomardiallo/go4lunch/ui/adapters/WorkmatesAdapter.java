package com.alphaomardiallo.go4lunch.ui.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.alphaomardiallo.go4lunch.R;
import com.alphaomardiallo.go4lunch.data.dataSources.Model.Booking;
import com.alphaomardiallo.go4lunch.data.dataSources.Model.User;
import com.alphaomardiallo.go4lunch.databinding.ItemWorkmatesBinding;
import com.alphaomardiallo.go4lunch.domain.OnClickItemListener;
import com.bumptech.glide.Glide;

import java.util.List;

public class WorkmatesAdapter extends ListAdapter<User, WorkmatesAdapter.WorkmatesViewHolder> {

    private ItemWorkmatesBinding binding;
    private OnClickItemListener onClickItemListener;
    private static Context context;
    private List<Booking> bookingList;

    public WorkmatesAdapter(@NonNull DiffUtil.ItemCallback<User> diffCallback, OnClickItemListener onClickItemListener, List<Booking> bookingList, Context context) {
        super(diffCallback);
        this.onClickItemListener = onClickItemListener;
        this.bookingList = bookingList;
        this.context = context;
    }


    @NonNull
    @Override
    public WorkmatesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = ItemWorkmatesBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
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
            return oldItem.getUrlPicture().equalsIgnoreCase(newItem.getUrlPicture()) &&
                    oldItem.getUsername().equalsIgnoreCase(newItem.getUsername()) &&
                    oldItem.getUserEmail().equalsIgnoreCase(newItem.getUserEmail()) &&
                    oldItem.getBookingOfTheDay().equals(newItem.getBookingOfTheDay());
        }
    }


    public static class WorkmatesViewHolder extends RecyclerView.ViewHolder {
        ImageView workmateAvatar;
        TextView workmateLunchStatus;

        public WorkmatesViewHolder(@NonNull View itemView) {
            super(itemView);

            workmateAvatar = itemView.findViewById(R.id.ivWorkmateAvatar);
            workmateLunchStatus = itemView.findViewById(R.id.tvWorkmateRestaurantChoice);
        }

        @SuppressLint("StringFormatMatches")
        public void bind(User user, OnClickItemListener onClickItemListener, List<Booking> bookingList) {
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
            String hasNotDecided = context.getString(R.string.workmate_has_not_selected_a_restaurant);
            workmateLunchStatus.setText(String.format(hasNotDecided, user.getUsername()));

        }
    }
}

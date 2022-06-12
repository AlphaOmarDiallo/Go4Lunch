package com.alphaomardiallo.go4lunch.ui.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
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
import com.alphaomardiallo.go4lunch.data.dataSources.Model.User;
import com.alphaomardiallo.go4lunch.databinding.ItemWorkmatesBinding;
import com.bumptech.glide.Glide;

public class WorkmatesJoiningAdapter extends ListAdapter<User, WorkmatesJoiningAdapter.WorkmatesJoiningViewHolder> {


    @SuppressLint("StaticFieldLeak")
    private static Context context;

    public WorkmatesJoiningAdapter(@NonNull DiffUtil.ItemCallback<User> diffCallback, Context context) {
        super(diffCallback);
        WorkmatesJoiningAdapter.context = context;
    }


    @NonNull
    @Override
    public WorkmatesJoiningViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        com.alphaomardiallo.go4lunch.databinding.ItemWorkmatesBinding binding = ItemWorkmatesBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new WorkmatesJoiningViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull WorkmatesJoiningViewHolder holder, int position) {
        holder.bind(getItem(position));
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

    public static class WorkmatesJoiningViewHolder extends RecyclerView.ViewHolder {

        ConstraintLayout item;
        ImageView workmateAvatar;
        TextView workmateLunchStatus;

        public WorkmatesJoiningViewHolder(@NonNull View itemView) {
            super(itemView);

            item = itemView.findViewById(R.id.itemWorkmate);
            workmateAvatar = itemView.findViewById(R.id.ivWorkmateAvatar);
            workmateLunchStatus = itemView.findViewById(R.id.tvWorkmateRestaurantChoice);
        }

        @SuppressLint("StringFormatMatches")
        public void bind(User user) {
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

            workmateLunchStatus.setText(String.format(context.getString(R.string.is_joining), user.getUsername()));

        }
    }

}


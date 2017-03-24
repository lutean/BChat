package com.prepod.bchat.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.prepod.bchat.R;
import com.prepod.bchat.activities.MainActivity;
import com.prepod.bchat.containers.User;

import java.util.List;

/**
 * Created by Антон on 28.02.2017.
 */

public class UsersListAdapter extends RecyclerView.Adapter<UsersListAdapter.UsersViewHolder> {

    private Context context;
    private List<User> usersList;

    public UsersListAdapter(Context context, List<User> usersList) {
        this.context = context;
        this.usersList = usersList;
    }

    @Override
    public UsersListAdapter.UsersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_users_list, null);
        return new UsersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(UsersListAdapter.UsersViewHolder holder, int position) {
        holder.userName.setText(usersList.get(position).getName());
        if (usersList.get(position).getUserAvatar() != null)
        Glide.with(context)
                .load(usersList.get(position).getUserAvatar())
                .into(holder.userAvater);
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    class UsersViewHolder extends RecyclerView.ViewHolder{

        private ImageView userAvater;
        private TextView userName;

        public UsersViewHolder(View itemView) {
            super(itemView);

            userAvater = (ImageView) itemView.findViewById(R.id.userAvatar);
            userName = (TextView) itemView.findViewById(R.id.roomTitleTextView);
        }
    }
}

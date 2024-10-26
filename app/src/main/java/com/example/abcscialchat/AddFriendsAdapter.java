package com.example.abcscialchat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AddFriendsAdapter extends RecyclerView.Adapter<AddFriendsAdapter.AddFriendViewHolder> {

    private List<String> friendNames;
    private OnAddFriendClickListener listener;

    public AddFriendsAdapter(List<String> friendNames, OnAddFriendClickListener listener) {
        this.friendNames = friendNames;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AddFriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_add_friend, parent, false);
        return new AddFriendViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddFriendViewHolder holder, int position) {
        String friendName = friendNames.get(position);
        holder.friendNameTextView.setText(friendName);
        holder.addFriendButton.setOnClickListener(v -> listener.onAddFriend(friendName));
    }

    @Override
    public int getItemCount() {
        return friendNames.size();
    }

    public static class AddFriendViewHolder extends RecyclerView.ViewHolder {
        TextView friendNameTextView;
        Button addFriendButton;

        public AddFriendViewHolder(@NonNull View itemView) {
            super(itemView);
            friendNameTextView = itemView.findViewById(R.id.friend_name);
            addFriendButton = itemView.findViewById(R.id.add_friend_button);
        }
    }

    public interface OnAddFriendClickListener {
        void onAddFriend(String friendName);
    }
}


package com.example.abcscialchat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FriendListAdapter extends RecyclerView.Adapter<FriendListAdapter.FriendViewHolder> {

    private List<String> friendsList;
    private OnFriendClickListener clickListener;

    public FriendListAdapter(List<String> friendsList, OnFriendClickListener clickListener) {
        this.friendsList = friendsList;

    }

    @NonNull
    @Override
    public FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend, parent, false);
        return new FriendViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendViewHolder holder, int position) {
        String friendName = friendsList.get(position);
        holder.friendNameTextView.setText(friendName);
        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onFriendClick(friendName);
            }
        });
    }

    @Override
    public int getItemCount() {
        return friendsList.size();
    }

    public static class FriendViewHolder extends RecyclerView.ViewHolder {
        TextView friendNameTextView;

        public FriendViewHolder(@NonNull View itemView) {
            super(itemView);
            friendNameTextView = itemView.findViewById(R.id.friend_name);
        }
    }

    public void updateFriendsList(List<String> newFriendsList) {
        friendsList.clear();
        friendsList.addAll(newFriendsList);
        notifyDataSetChanged();
    }

    // Define an interface for friend click events
    public interface OnFriendClickListener {
        void onFriendClick(String friendName);
    }
}

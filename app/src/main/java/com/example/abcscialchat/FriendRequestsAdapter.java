package com.example.abcscialchat;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FriendRequestsAdapter extends RecyclerView.Adapter<FriendRequestsAdapter.FriendRequestViewHolder> {

    private static List<String> requestsList;
    private OnFriendRequestActionListener actionListener;

    public FriendRequestsAdapter(List<String> requestsList, OnFriendRequestActionListener actionListener) {
        this.requestsList = requestsList;
        this.actionListener = actionListener;
    }

    @NonNull
    @Override
    public FriendRequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend_request, parent, false);
        return new FriendRequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendRequestViewHolder holder, int position) {
        String friendName = requestsList.get(position);
        holder.friendNameTextView.setText(friendName);

        holder.acceptButton.setOnClickListener(v -> actionListener.onAcceptRequest(friendName));
        holder.declineButton.setOnClickListener(v -> actionListener.onDeclineRequest(friendName));
    }

    @Override
    public int getItemCount() {
        return requestsList.size();
    }

    public static class FriendRequestViewHolder extends RecyclerView.ViewHolder {
        TextView friendNameTextView;
        Button acceptButton, declineButton;

        public FriendRequestViewHolder(@NonNull View itemView) {
            super(itemView);
            friendNameTextView = itemView.findViewById(R.id.friend_name);
            acceptButton = itemView.findViewById(R.id.accept_button);
            declineButton = itemView.findViewById(R.id.decline_button);
        }
    }

    public interface OnFriendRequestActionListener {
        void onAcceptRequest(String friendName);
        void onDeclineRequest(String friendName);
    }
    public void updateRequestsList(List<String> newRequestsList) {
        requestsList.clear();
        requestsList.addAll(newRequestsList);
        notifyDataSetChanged();
    }
    public void removeRequest(String friendName) {
        requestsList.remove(friendName);
        notifyDataSetChanged();
    }

}


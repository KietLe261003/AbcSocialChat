package com.example.abcscialchat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class FriendRequestAdapter extends ArrayAdapter<String> {
    private Context context;
    private List<String> requestsList;
    private FriendRequestActionListener actionListener;

    public FriendRequestAdapter(Context context, List<String> requestsList, FriendRequestActionListener actionListener) {
        super(context, R.layout.friend_request_item, requestsList);
        this.context = context;
        this.requestsList = requestsList;
        this.actionListener = actionListener;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.friend_request_item, parent, false);
        }

        String requesterName = requestsList.get(position);
        TextView requesterNameView = convertView.findViewById(R.id.requester_name);
        Button acceptButton = convertView.findViewById(R.id.accept_button);
        Button rejectButton = convertView.findViewById(R.id.reject_button);

        requesterNameView.setText(requesterName);

        acceptButton.setOnClickListener(v -> actionListener.onAcceptRequest(requesterName));
        rejectButton.setOnClickListener(v -> actionListener.onRejectRequest(requesterName));

        return convertView;
    }

    public interface FriendRequestActionListener {
        void onAcceptRequest(String requesterName);
        void onRejectRequest(String requesterName);
    }
}

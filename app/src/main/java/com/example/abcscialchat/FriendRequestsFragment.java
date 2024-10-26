package com.example.abcscialchat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FriendRequestsFragment extends Fragment {

    private ListView friendRequestsListView;
    private ArrayList<String> friendRequests = new ArrayList<>();
    private FriendRequestAdapter requestsAdapter;
    private DatabaseReference friendRequestsRef, usersRef;
    private String currentUserId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friend_requests, container, false);

        friendRequestsListView = view.findViewById(R.id.friend_requests_list);
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        friendRequestsRef = FirebaseDatabase.getInstance().getReference("friendRequests").child(currentUserId);
        usersRef = FirebaseDatabase.getInstance().getReference("user");

        requestsAdapter = new FriendRequestAdapter(getActivity(), friendRequests, new FriendRequestAdapter.FriendRequestActionListener() {
            @Override
            public void onAcceptRequest(String requesterName) {
                confirmFriendRequest(requesterName);
            }

            @Override
            public void onRejectRequest(String requesterName) {
                rejectFriendRequest(requesterName);
            }
        });
        friendRequestsListView.setAdapter(requestsAdapter);

        loadFriendRequests();

        return view;
    }

    private void loadFriendRequests() {
        friendRequestsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                friendRequests.clear();
                for (DataSnapshot requestSnapshot : snapshot.getChildren()) {
                    String requesterId = requestSnapshot.getKey();
                    usersRef.child(requesterId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                            String requesterName = userSnapshot.child("userName").getValue(String.class);
                            if (requesterName != null) {
                                friendRequests.add(requesterName);
                                requestsAdapter.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(getActivity(), "Error loading requester data", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Error loading friend requests", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void confirmFriendRequest(String requesterName) {
        // Logic xác nhận yêu cầu kết bạn
    }

    private void rejectFriendRequest(String requesterName) {
        // Logic từ chối yêu cầu kết bạn
    }
}

package com.example.abcscialchat;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

//public class FriendsActivity extends AppCompatActivity {
//
//    private EditText searchBar;
//    private ListView friendsListView, friendRequestsListView, addFriendsListView;
//    private ArrayList<String> friendsList = new ArrayList<>();
//    private ArrayAdapter<String> friendsAdapter;
//
//    private ArrayList<String> searchResults = new ArrayList<>();
//    private ArrayAdapter<String> searchAdapter;
//
//    private ArrayList<String> friendRequests = new ArrayList<>();
//    private FriendRequestAdapter requestsAdapter;
//
//    private DatabaseReference usersRef, friendsRef, friendRequestsRef;
//    private String currentUserId;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_friends);
//
//        searchBar = findViewById(R.id.search_bar);
//        friendsListView = findViewById(R.id.friends_list);
//        friendRequestsListView = findViewById(R.id.friend_requests_list);
//        addFriendsListView = findViewById(R.id.add_friends_list);
//
//        // Firebase references
//        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
//        usersRef = FirebaseDatabase.getInstance().getReference("user");
//        friendsRef = FirebaseDatabase.getInstance().getReference("friends").child(currentUserId);
//        friendRequestsRef = FirebaseDatabase.getInstance().getReference("friendRequests").child(currentUserId);
//
//        // Initialize adapters
//        friendsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, friendsList);
//        friendsListView.setAdapter(friendsAdapter);
//
//        searchAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, searchResults);
//        addFriendsListView.setAdapter(searchAdapter);
//
//        requestsAdapter = new FriendRequestAdapter(this, friendRequests, new FriendRequestAdapter.FriendRequestActionListener() {
//            @Override
//            public void onAcceptRequest(String requesterName) {
//                confirmFriendRequest(requesterName);
//            }
//
//            @Override
//            public void onRejectRequest(String requesterName) {
//                rejectFriendRequest(requesterName);
//            }
//        });
//        friendRequestsListView.setAdapter(requestsAdapter);
//
//        // Load existing friends
//        loadFriendsList();
//
//        // Load friend requests
//        loadFriendRequests();
//
//        // Handle search for friends
//        searchBar.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                searchForFriends(s.toString());
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {}
//        });
//
//        // Add friend on search result click
//        addFriendsListView.setOnItemClickListener((parent, view, position, id) -> {
//            String selectedFriend = searchResults.get(position);
//            addFriend(selectedFriend);
//        });
//    }
//
//    // Load friends from Firebase
//    private void loadFriendsList() {
//        friendsRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                friendsList.clear();
//                for (DataSnapshot friendSnapshot : snapshot.getChildren()) {
//                    String friendName = friendSnapshot.getValue(String.class);
//                    friendsList.add(friendName);
//                }
//                friendsAdapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Toast.makeText(FriendsActivity.this, "Error loading friends list", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//    // Load friend requests
//    private void loadFriendRequests() {
//        friendRequestsRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                friendRequests.clear();
//                for (DataSnapshot requestSnapshot : snapshot.getChildren()) {
//                    String requesterId = requestSnapshot.getKey();
//                    usersRef.child(requesterId).addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot userSnapshot) {
//                            String requesterName = userSnapshot.child("userName").getValue(String.class);
//                            if (requesterName != null) {
//                                friendRequests.add(requesterName);
//                                requestsAdapter.notifyDataSetChanged();
//                            }
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError error) {
//                            Toast.makeText(FriendsActivity.this, "Error loading requester data", Toast.LENGTH_SHORT).show();
//                        }
//                    });
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Toast.makeText(FriendsActivity.this, "Error loading friend requests", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//    // Search for friends in the database
//    private void searchForFriends(String query) {
//        Query searchQuery = usersRef.orderByChild("userName").startAt(query).endAt(query + "\uf8ff");
//        searchQuery.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                searchResults.clear();
//                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
//                    String userName = userSnapshot.child("userName").getValue(String.class);
//                    searchResults.add(userName);
//                }
//                searchAdapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Toast.makeText(FriendsActivity.this, "Error searching friends", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//    // Add friend
//    private void addFriend(String friendName) {
//        Query query = usersRef.orderByChild("userName").equalTo(friendName);
//        query.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (snapshot.exists()) {
//                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
//                        String friendId = userSnapshot.getKey();
//                        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
//
//                        // Check if trying to send request to self
//                        if (friendId.equals(currentUserId)) {
//                            Toast.makeText(FriendsActivity.this, "Bạn không thể gửi yêu cầu kết bạn cho chính mình", Toast.LENGTH_SHORT).show();
//                            return;
//                        }
//
//                        Map<String, Object> friendData = new HashMap<>();
//                        friendData.put("name", FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
//                        friendData.put("status", false);
//
//                        DatabaseReference friendRequestRef = FirebaseDatabase.getInstance().getReference("friendRequests").child(friendId);
//                        friendRequestRef.child(currentUserId).setValue(friendData).addOnCompleteListener(task -> {
//                            if (task.isSuccessful()) {
//                                Toast.makeText(FriendsActivity.this, "Đã gửi lời mời kết bạn", Toast.LENGTH_SHORT).show();
//                            }
//                        });
//                    }
//                } else {
//                    Toast.makeText(FriendsActivity.this, "Người dùng không tìm thấy", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Toast.makeText(FriendsActivity.this, "Lỗi khi thêm bạn", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//    // Confirm friend request
//    private void confirmFriendRequest(String friendName) {
//        Query query = usersRef.orderByChild("userName").equalTo(friendName);
//        query.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (snapshot.exists()) {
//                    String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
//                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
//                        String friendId = userSnapshot.getKey();
//                        String friendUserName = userSnapshot.child("userName").getValue(String.class);
//
//                        // Save friendship information for both the sender and receiver
//                        DatabaseReference currentUserFriendsRef = FirebaseDatabase.getInstance().getReference("friends").child(currentUserId);
//                        DatabaseReference friendFriendsRef = FirebaseDatabase.getInstance().getReference("friends").child(friendId);
//
//                        // Add friend to the sender's friends list
//                        currentUserFriendsRef.child(friendId).setValue(friendUserName); // Add friend's name to sender's list
//
//                        // Add sender to the friend's friends list
//                        friendFriendsRef.child(currentUserId).setValue(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
//
//                        // Remove the friend request from the friendRequests
//                        friendRequestsRef.child(friendId).child(currentUserId).removeValue();
//
//                        // Update friend lists for both parties
//                        loadFriendsList(); // Update for receiver
//                        loadFriendsListForSender(currentUserId); // Update for sender
//
//                        Toast.makeText(FriendsActivity.this, "Yêu cầu kết bạn đã được chấp nhận", Toast.LENGTH_SHORT).show();
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Toast.makeText(FriendsActivity.this, "Lỗi khi xác nhận yêu cầu kết bạn", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//    // Load friends list for the sender
//    private void loadFriendsListForSender(String userId) {
//        DatabaseReference friendsRef = FirebaseDatabase.getInstance().getReference("friends").child(userId);
//        friendsRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                friendsList.clear();
//                for (DataSnapshot friendSnapshot : snapshot.getChildren()) {
//                    String friendUserName = friendSnapshot.getValue(String.class);
//                    friendsList.add(friendUserName); // Add friend's name to the list
//                }
//                friendsAdapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Toast.makeText(FriendsActivity.this, "Error loading friends list", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//    // Reject friend request
//    private void rejectFriendRequest(String requesterName) {
//        Query query = usersRef.orderByChild("userName").equalTo(requesterName);
//        query.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (snapshot.exists()) {
//                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
//                        String requesterId = userSnapshot.getKey();
//                        friendRequestsRef.child(requesterId).child(currentUserId).removeValue().addOnCompleteListener(task -> {
//                            if (task.isSuccessful()) {
//                                Toast.makeText(FriendsActivity.this, "Đã từ chối yêu cầu kết bạn", Toast.LENGTH_SHORT).show();
//                            }
//                        });
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Toast.makeText(FriendsActivity.this, "Lỗi khi từ chối yêu cầu", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//}
public class FriendsActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private FriendsPagerAdapter friendsPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

        // Set up the ViewPager with the FriendsPagerAdapter
        friendsPagerAdapter = new FriendsPagerAdapter(this);
        viewPager.setAdapter(friendsPagerAdapter);

        // Link TabLayout with ViewPager2
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Friends");
                    break;
                case 1:
                    tab.setText("Friend Requests");
                    break;
                case 2:
                    tab.setText("Add Friends");
                    break;
            }
        }).attach();
    }
}

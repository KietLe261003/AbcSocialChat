package com.example.abcscialchat;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class FriendsActivity extends AppCompatActivity {

    private EditText searchBar;
    private RecyclerView friendsListView, friendRequestsListView, addFriendsListView;
    private FriendListAdapter friendsAdapter;
    private FriendRequestsAdapter requestsAdapter;
    private AddFriendsAdapter addFriendsAdapter;
    private ArrayList<String> searchResults = new ArrayList<>();

    private DatabaseReference usersRef, friendsRef, friendRequestsRef;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        // Initialize UI components
        initializeUI();

        // Firebase references
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        usersRef = FirebaseDatabase.getInstance().getReference("user");
        friendsRef = FirebaseDatabase.getInstance().getReference("friends").child(currentUserId);
        friendRequestsRef = FirebaseDatabase.getInstance().getReference("friendRequests").child(currentUserId);

        // Initialize adapters and set them to RecyclerViews
        setupAdapters();

        // Register context menu for addFriendsListView
        registerForContextMenu(addFriendsListView);

        // Load friends and friend requests
        loadFriendsList();
        loadFriendRequests();

        // Setup search listener
        setupSearchListener();
    }

    private void initializeUI() {
        searchBar = findViewById(R.id.search_bar);
        friendsListView = findViewById(R.id.friends_list);
        friendRequestsListView = findViewById(R.id.friend_requests_list);
        addFriendsListView = findViewById(R.id.add_friends_list);

        friendsListView.setLayoutManager(new LinearLayoutManager(this));
        friendRequestsListView.setLayoutManager(new LinearLayoutManager(this));
        addFriendsListView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setupAdapters() {
        // Initialize and set FriendListAdapter
        friendsAdapter = new FriendListAdapter(new ArrayList<>(), friendName -> {
            // Define behavior when a friend is clicked
        });
        friendsListView.setAdapter(friendsAdapter);

        // Initialize and set FriendRequestsAdapter
        requestsAdapter = new FriendRequestsAdapter(new ArrayList<>(), new FriendRequestsAdapter.OnFriendRequestActionListener() {
            @Override
            public void onAcceptRequest(String friendName) {
                acceptFriendRequest(friendName);
            }

            @Override
            public void onDeclineRequest(String friendName) {
                declineFriendRequest(friendName);
            }
        });
        friendRequestsListView.setAdapter(requestsAdapter);

        // Initialize and set AddFriendsAdapter
        addFriendsAdapter = new AddFriendsAdapter(searchResults, this::addFriend);
        addFriendsListView.setAdapter(addFriendsAdapter);
    }

    private void setupSearchListener() {
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchForFriends(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void loadFriendsList() {
        friendsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<String> friendsList = new ArrayList<>();
                for (DataSnapshot friendSnapshot : snapshot.getChildren()) {
                    Boolean status = friendSnapshot.child("status").getValue(Boolean.class);
                    if (status != null && status) {
                        String friendName = friendSnapshot.getKey();
                        friendsList.add(friendName);
                    }
                }
                friendsAdapter.updateFriendsList(friendsList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(FriendsActivity.this, "Error loading friends list", Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void loadFriendRequests() {
        friendRequestsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<String> requestsList = new ArrayList<>();
                for (DataSnapshot requestSnapshot : snapshot.getChildren()) {
                    String requesterId = requestSnapshot.getKey();
                    usersRef.child(requesterId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                            String requesterName = userSnapshot.child("userName").getValue(String.class);
                            if (requesterName != null) {
                                requestsList.add(requesterName);
                                requestsAdapter.updateRequestsList(requestsList);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(FriendsActivity.this, "Error loading requester data", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(FriendsActivity.this, "Error loading friend requests", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void searchForFriends(String query) {
        Query searchQuery = usersRef.orderByChild("userName").startAt(query).endAt(query + "\uf8ff");
        searchQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                searchResults.clear();
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    String userName = userSnapshot.child("userName").getValue(String.class);
                    if (userName != null && !userName.equals(FirebaseAuth.getInstance().getCurrentUser().getDisplayName())) {
                        searchResults.add(userName);
                    }
                }
                addFriendsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(FriendsActivity.this, "Error searching friends", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addFriend(String friendName) {
        Query query = usersRef.orderByChild("userName").equalTo(friendName);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                        String friendId = userSnapshot.getKey();
                        if (friendId.equals(currentUserId)) {
                            Toast.makeText(FriendsActivity.this, "Cannot add yourself as a friend", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Map<String, Object> friendData = new HashMap<>();
                        friendData.put("name", FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                        friendData.put("status", false);

                        DatabaseReference friendRequestRef = FirebaseDatabase.getInstance().getReference("friendRequests").child(friendId);
                        friendRequestRef.child(currentUserId).setValue(friendData).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(FriendsActivity.this, "Friend request sent", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } else {
                    Toast.makeText(FriendsActivity.this, "User not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(FriendsActivity.this, "Error adding friend", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void acceptFriendRequest(String friendName) {
        Query query = usersRef.orderByChild("userName").equalTo(friendName);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                        String friendId = userSnapshot.getKey();

                        // Xóa lời mời kết bạn sau khi chấp nhận
                        friendRequestsRef.child(friendId).removeValue();

                        // Thêm bạn bè vào bảng "friends"
                        Map<String, Object> friendData = new HashMap<>();
                        friendData.put("status", true);

                        friendsRef.child(friendId).setValue(friendData);
                        DatabaseReference friendRef = FirebaseDatabase.getInstance().getReference("friends").child(friendId);
                        friendRef.child(currentUserId).setValue(friendData);

                        Toast.makeText(FriendsActivity.this, "Bạn đã chấp nhận kết bạn với " + friendName, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(FriendsActivity.this, "Người dùng không tồn tại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(FriendsActivity.this, "Lỗi chấp nhận kết bạn", Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void declineFriendRequest(String friendName) {
        // Implement the decline friend request logic
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId() == R.id.add_friends_list) {
            menu.setHeaderTitle("Actions");
            menu.add(0, v.getId(), 0, "Add Friend");
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle().equals("Add Friend")) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            String selectedFriend = searchResults.get(info.position);
            addFriend(selectedFriend);
        }
        return super.onContextItemSelected(item);
    }
}

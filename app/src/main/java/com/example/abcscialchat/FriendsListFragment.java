package com.example.abcscialchat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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

public class FriendsListFragment extends Fragment {

    private ListView friendsListView;
    private ArrayList<String> friendsList = new ArrayList<>();
    private ArrayAdapter<String> friendsAdapter;
    private DatabaseReference friendsRef;
    private String currentUserId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends_list, container, false);

        friendsListView = view.findViewById(R.id.friends_list);
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        friendsRef = FirebaseDatabase.getInstance().getReference("friends").child(currentUserId);

        friendsAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, friendsList);
        friendsListView.setAdapter(friendsAdapter);

        loadFriendsList();

        return view;
    }

    private void loadFriendsList() {
        friendsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                friendsList.clear();
                for (DataSnapshot friendSnapshot : snapshot.getChildren()) {
                    String friendName = friendSnapshot.getValue(String.class);
                    friendsList.add(friendName);
                }
                friendsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Error loading friends list", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

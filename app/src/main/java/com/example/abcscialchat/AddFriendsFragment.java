package com.example.abcscialchat;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AddFriendsFragment extends Fragment {

    private EditText searchBar;
    private ListView addFriendsListView;
    private ArrayList<String> searchResults = new ArrayList<>();
    private ArrayAdapter<String> searchAdapter;
    private DatabaseReference usersRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_friends, container, false);

        // Khởi tạo các view
        searchBar = view.findViewById(R.id.search_bar);
        addFriendsListView = view.findViewById(R.id.add_friends_list);

        // Khởi tạo Firebase reference
        usersRef = FirebaseDatabase.getInstance().getReference("user");
        Log.d("Firebase", "Firebase reference initialized.");

        // Khởi tạo adapter cho ListView
        searchAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, searchResults);
        addFriendsListView.setAdapter(searchAdapter);

        // Listener cho EditText (search bar)
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Kiểm tra xem query không trống
                if (s.toString().trim().isEmpty()) {
                    Log.d("Search", "Empty query.");
                    searchResults.clear();  // Xóa kết quả cũ khi không có query
                    searchAdapter.notifyDataSetChanged();
                } else {
                    Log.d("Search", "Query: " + s.toString());
                    searchForFriends(s.toString().trim());  // Bỏ khoảng trắng thừa
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Xử lý sự kiện khi click vào 1 item trong danh sách
        addFriendsListView.setOnItemClickListener((parent, view1, position, id) -> {
            String selectedFriend = searchResults.get(position);
            addFriend(selectedFriend);
        });

        return view;
    }

    // Hàm tìm kiếm bạn bè trong Firebase
    private void searchForFriends(String query) {
        Log.d("Firebase", "Searching for friends with query: " + query);

        Query searchQuery = usersRef.orderByChild("userName").startAt(query).endAt(query + "\uf8ff");

        searchQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Kiểm tra dữ liệu nhận về từ Firebase
                Log.d("Firebase", "Snapshot received: " + snapshot.toString());

                searchResults.clear(); // Xóa dữ liệu cũ

                // Kiểm tra snapshot có tồn tại
                if (snapshot.exists()) {
                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                        // Nhận username từ Firebase
                        String userName = userSnapshot.child("userName").getValue(String.class);
                        if (userName != null) {
                            searchResults.add(userName); // Thêm vào danh sách
                        }
                    }

                    if (searchResults.isEmpty()) {
                        Log.d("Firebase", "Không có kết quả phù hợp.");
                        Toast.makeText(getActivity(), "Không tìm thấy bạn bè.", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.d("Firebase", "Tìm thấy: " + searchResults.size() + " bạn bè.");
                    }

                } else {
                    Log.d("Firebase", "Không có dữ liệu.");
                    Toast.makeText(getActivity(), "Không tìm thấy bạn bè.", Toast.LENGTH_SHORT).show();
                }

                searchAdapter.notifyDataSetChanged(); // Cập nhật adapter
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Error: " + error.getMessage());
                Toast.makeText(getActivity(), "Lỗi khi tìm kiếm", Toast.LENGTH_SHORT).show();
            }
        });
    }

        // Hàm thêm bạn bè khi chọn 1 người
    private void addFriend(String friendName) {
        Log.d("AddFriend", "Friend added: " + friendName);
        // Logic thêm bạn bè
    }
}

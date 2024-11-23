package com.example.abcscialchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth auth;
    RecyclerView mainUserRecyclerView;
    UserAdapter adapter;
    FirebaseDatabase database;
    ArrayList<User> userArrayList;
    ImageView logoutimg,cameraImageView,chatImageView,profileImageView,homeImageView;
    private ImageView addFriend;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cameraImageView=findViewById(R.id.ic_cam);
        addFriend = findViewById(R.id.addFriend);
        chatImageView=findViewById(R.id.ic_chat);
        profileImageView=findViewById(R.id.ic_profile);
        homeImageView=findViewById(R.id.ic_home);
        auth=FirebaseAuth.getInstance();
        if(auth.getCurrentUser()==null)
        {
            Intent it = new Intent(MainActivity.this,login.class);
            startActivity(it);
        }
        initNavigate();

        //Cấu hình biến sử dụng bình thường
        userArrayList=new ArrayList<>();
        mainUserRecyclerView=findViewById(R.id.mainUserRecyclerView);
        mainUserRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter= new UserAdapter(MainActivity.this,userArrayList);
        mainUserRecyclerView.setAdapter(adapter);
        //Cấu hình biến Database
        database=FirebaseDatabase.getInstance();
        DatabaseReference reference= database.getInstance().getReference("friends").child(auth.getCurrentUser().getUid());// trỏ tới bảng user trong database
        User AIChat = new User("https://firebasestorage.googleapis.com/v0/b/socialchat-9ff4d.appspot.com/o/AI.png?alt=media&token=7898f80b-c87a-4585-b802-e48523d95504","ChatAIBox","1","1","1","1");
        userArrayList.add(AIChat);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot:snapshot.getChildren())
                {
                    String friendId = dataSnapshot.getKey();
                    DatabaseReference friendFriendsRef = FirebaseDatabase.getInstance().getReference("user")
                            .child(friendId);
                    friendFriendsRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            User user = snapshot.getValue(User.class);
                            userArrayList.add(user);
                            adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                Log.d("Data", "onDataChange: "+userArrayList.size());
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        logoutimg=findViewById(R.id.logoutimg);

        logoutimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog = new Dialog(MainActivity.this,R.style.dialoge);
                dialog.setContentView(R.layout.dialog_layout);
                Button btnYes,btnNo;
                btnYes=dialog.findViewById(R.id.btnYes);
                btnNo=dialog.findViewById(R.id.btnNo);
                btnYes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        FirebaseAuth.getInstance().signOut();
                        Intent it = new Intent(MainActivity.this,login.class);
                        startActivity(it);
                    }
                });
                btnNo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });
    }
    public void initNavigate(){
        if(auth.getCurrentUser()==null)
        {
            Intent it = new Intent(MainActivity.this,login.class);
            startActivity(it);
        }
        homeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(MainActivity.this,HomeActivity.class);
                startActivity(it);
            }
        });
        cameraImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(MainActivity.this,CreateBlog.class);
                startActivity(it);
            }
        });
        addFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(MainActivity.this, FriendsActivity.class);
                startActivity(it);
            }
        });
        chatImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(MainActivity.this,MainActivity.class);
                startActivity(it);
                finish();
            }
        });
        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(MainActivity.this, Profile.class);
                it.putExtra("idUser",auth.getCurrentUser().getUid());
                startActivity(it);
            }
        });
    }
}
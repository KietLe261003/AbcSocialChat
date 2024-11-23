package com.example.abcscialchat;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.abcscialchat.entity.blog;
import com.example.abcscialchat.entity.comment;
import com.example.abcscialchat.entity.share;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class HomeActivity extends AppCompatActivity {
    FirebaseAuth auth;
    private TextView appNameTextView;
    private ImageView logoutimg;
    private RecyclerView mainBlogRecyclerView;
    private ImageView cameraImageView,addFriend,chatImageView,profileImageView,homeImageView;
    blogAdapter adapter;
    ArrayList<blog> blogArrayList;
    FirebaseDatabase database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        cameraImageView=findViewById(R.id.ic_cam);
        addFriend = findViewById(R.id.addFriend);
        chatImageView=findViewById(R.id.ic_chat);
        profileImageView=findViewById(R.id.ic_profile);
        homeImageView=findViewById(R.id.ic_home);
        auth= FirebaseAuth.getInstance();
        if(auth.getCurrentUser()==null)
        {
            Intent it = new Intent(HomeActivity.this,login.class);
            startActivity(it);
        }
        initNavigate();
        logoutimg=findViewById(R.id.logoutimg);

        logoutimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog = new Dialog(HomeActivity.this,R.style.dialoge);
                dialog.setContentView(R.layout.dialog_layout);
                Button btnYes,btnNo;
                btnYes=dialog.findViewById(R.id.btnYes);
                btnNo=dialog.findViewById(R.id.btnNo);
                btnYes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        FirebaseAuth.getInstance().signOut();
                        Intent it = new Intent(HomeActivity.this,login.class);
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

        //Cấu hình biến Database
        database=FirebaseDatabase.getInstance();
        blogArrayList=new ArrayList<>();
        mainBlogRecyclerView=findViewById(R.id.mainBlogRecyclerView);
        mainBlogRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new blogAdapter(HomeActivity.this,blogArrayList);
        mainBlogRecyclerView.setAdapter(adapter);

        DatabaseReference reference= database.getReference().child("blogs");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                blogArrayList.clear();
                for (DataSnapshot dataSnapshot:snapshot.getChildren())
                {
                    for (DataSnapshot blogSnapshot : dataSnapshot.getChildren()) {
                        blog dtblog = blogSnapshot.getValue(blog.class);
                        // Kiểm tra nếu comments và shares không tồn tại thì khởi tạo mảng rỗng
                        if (dtblog.getComments() == null) {
                            dtblog.setComments(new ArrayList<comment>());
                        }
                        if (dtblog.getShares() == null) {
                            dtblog.setShares(new ArrayList<share>());
                        }
                        blogArrayList.add(dtblog);
                    }

                }
                Collections.sort(blogArrayList, new Comparator<blog>() {
                    @Override
                    public int compare(blog b1, blog b2) {
                        return Long.compare(b2.getTimeCreate(), b1.getTimeCreate()); 
                    }
                });
                Log.d("Data", "onDataChange: "+blogArrayList.size());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    public void initNavigate(){
        if(auth.getCurrentUser()==null)
        {
            Intent it = new Intent(HomeActivity.this,login.class);
            startActivity(it);
        }

        cameraImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(HomeActivity.this,CreateBlog.class);
                startActivity(it);
            }
        });
        addFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(HomeActivity.this, FriendsActivity.class);
                startActivity(it);
            }
        });
        chatImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(HomeActivity.this,MainActivity.class);
                startActivity(it);
            }
        });
        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(HomeActivity.this, Profile.class);
                it.putExtra("idUser",auth.getCurrentUser().getUid());
                startActivity(it);
            }
        });
    }

}
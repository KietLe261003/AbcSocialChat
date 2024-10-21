package com.example.abcscialchat;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.abcscialchat.entity.blog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {
    FirebaseAuth auth;
    private TextView appNameTextView;
    private ImageView logoutImageView;
    private RecyclerView mainBlogRecyclerView;
    private ImageView cameraImageView;
    private ImageView chatImageView;
    private ImageView settingsImageView;
    ArrayList<blog> blogArrayList;
    FirebaseDatabase database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        auth= FirebaseAuth.getInstance();
        if(auth.getCurrentUser()==null)
        {
            Intent it = new Intent(HomeActivity.this,login.class);
            startActivity(it);
        }
        blogArrayList=new ArrayList<>();


        //Cấu hình biến Database
        database=FirebaseDatabase.getInstance();
        DatabaseReference reference= database.getReference().child("user").child(auth.getUid()).child("blog");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
}
package com.example.abcscialchat;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.abcscialchat.entity.blog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class blogAdapter extends RecyclerView.Adapter {
    Context context;
    ArrayList<blog> blogAdapterArratList;
    FirebaseAuth auth;
    FirebaseDatabase database;
    public blogAdapter(Context context, ArrayList<blog> blogAdapterArratList) {
        this.context = context;
        this.blogAdapterArratList = blogAdapterArratList;
        this.database = FirebaseDatabase.getInstance();  // Khởi tạo FirebaseDatabase
        this.auth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.blog_item,parent,false);
        return new blogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        blog blogUser= blogAdapterArratList.get(position);
        blogViewHolder viewHoler=(blogViewHolder) holder;
        DatabaseReference reference = database.getReference().child("user").child(blogUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("DataUser",""+snapshot.getValue().toString());
                String userName=snapshot.child("userName").getValue().toString();
                String profilePic=snapshot.child("profilePic").getValue().toString();
                Picasso.get().load(profilePic).into(viewHoler.circleImageView);
                Picasso.get().load(blogUser.getImage()).into(viewHoler.imgPost);
                viewHoler.nameUser.setText(snapshot.child("userName").getValue().toString());
                viewHoler.timeCreate.setText(formatMillisToDate(blogUser.getTimeCreate())+"");
                viewHoler.contentPost.setText(blogUser.getContent());
                viewHoler.shareCount.setText(blogUser.getShares().size()+"");
                viewHoler.likeCount.setText(blogUser.getLike()+"");
                viewHoler.commentCount.setText(blogUser.getComments().size()+"");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    public static String formatMillisToDate(long millis) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date(millis);
        return sdf.format(date);
    }

    @Override
    public int getItemCount() {
        return blogAdapterArratList.size();
    }
    class blogViewHolder extends RecyclerView.ViewHolder {
        CircleImageView circleImageView;
        TextView nameUser,timeCreate,contentPost,shareCount,likeCount,commentCount;
        ImageView imgPost;
        public blogViewHolder(@NonNull View itemView) {
            super(itemView);
            circleImageView=itemView.findViewById(R.id.userimg);
            nameUser=itemView.findViewById(R.id.nameuser);
            timeCreate=itemView.findViewById(R.id.time);
            imgPost=itemView.findViewById(R.id.imgPost);
            contentPost=itemView.findViewById(R.id.contentPost);
            shareCount=itemView.findViewById(R.id.shareCount);
            likeCount=itemView.findViewById(R.id.likeCount);
            commentCount=itemView.findViewById(R.id.commentCount);

        }
    }
}

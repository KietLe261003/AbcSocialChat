package com.example.abcscialchat;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.abcscialchat.entity.blog;
import com.example.abcscialchat.entity.comment;
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
        DatabaseReference blogsRef = database.getReference().child("blogs").child(blogUser.getUid());
        // Tạo đường dẫn tới bài viết trong Firebase
        String idUser = blogUser.getUid(); // ID người dùng
        String idBlog = blogUser.getId(); // ID bài viết
        DatabaseReference blogRef = database.getReference()
                .child("blogs")
                .child(idUser)
                .child(idBlog);
        String currentUserId = auth.getCurrentUser().getUid();
        // Kiểm tra trạng thái "đã like" ngay khi load
        blogRef.child("likes").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean isLiked = snapshot.hasChild(currentUserId);
                viewHoler.ic_like.setSelected(isLiked);

                viewHoler.ic_like.setOnClickListener(v -> {
                    v.setSelected(!isLiked); // Thay đổi trạng thái tạm thời
                    if (isLiked) {
                        blogRef.child("likes").child(currentUserId).removeValue();
                        blogRef.child("like").setValue(blogUser.getLike() - 1);
                    } else {
                        blogRef.child("likes").child(currentUserId).setValue(true);
                        blogRef.child("like").setValue(blogUser.getLike() + 1);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "Lỗi khi kiểm tra trạng thái like: " + error.getMessage());
            }
        });
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
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
        viewHoler.ic_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, CommentBlog.class);
                intent.putExtra("blogId", blogUser.getId());
                intent.putExtra("userId", blogUser.getUid());
                context.startActivity(intent);
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
        ImageView imgPost,ic_like,ic_comment;
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
            ic_like=itemView.findViewById(R.id.ic_like);
            ic_comment=itemView.findViewById(R.id.ic_comment);
        }
    }
}

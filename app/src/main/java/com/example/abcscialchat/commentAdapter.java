package com.example.abcscialchat;

import static com.example.abcscialchat.blogAdapter.formatMillisToDate;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.abcscialchat.entity.comment;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class commentAdapter extends RecyclerView.Adapter<commentAdapter.CommentViewHolder> {

    private Context context;
    private ArrayList<comment> commentList;
    private DatabaseReference userDatabase;

    // Constructor
    public commentAdapter(Context context, ArrayList<comment> commentList) {
        this.context = context;
        this.commentList = commentList;
        this.userDatabase = FirebaseDatabase.getInstance().getReference("user");
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        comment currentComment = commentList.get(position);

        // Set dữ liệu comment
        holder.commentContentTextView.setText(currentComment.getComment());
        long timeMillis = Long.parseLong(currentComment.getTime());
        holder.timeTextView.setText(formatMillisToDate(timeMillis));

        // Lấy thông tin người dùng (từ uidSender) từ Firebase Database
        String uidSender = currentComment.getUidSender();
        userDatabase.child(uidSender).get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                String userName = snapshot.child("userName").getValue(String.class);
                String profilePicUrl = snapshot.child("profilePic").getValue(String.class);

                holder.userNameTextView.setText(userName);

                // Load avatar bằng Picasso
                if (profilePicUrl != null && !profilePicUrl.isEmpty()) {
                    Picasso.get().load(profilePicUrl).into(holder.avatarImageView);
                } else {
                    holder.avatarImageView.setImageResource(R.drawable.ic_profile); // Ảnh mặc định
                }
            }
        }).addOnFailureListener(e -> {
            holder.userNameTextView.setText("Unknown User");
            holder.avatarImageView.setImageResource(R.drawable.ic_profile);
        });
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    // ViewHolder class
    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        CircleImageView avatarImageView;
        TextView userNameTextView, commentContentTextView, timeTextView;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);

            avatarImageView = itemView.findViewById(R.id.avatar);
            userNameTextView = itemView.findViewById(R.id.userNameTextView);
            commentContentTextView = itemView.findViewById(R.id.commentContentTextView);
            timeTextView = itemView.findViewById(R.id.timeTextView);
        }
    }
}

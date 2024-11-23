package com.example.abcscialchat;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.abcscialchat.entity.comment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CommentBlog extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseDatabase database;
    EditText edtComment;
    ImageView btnSend;
    ArrayList<comment> listComment;
    RecyclerView commentRecyclerView;
    commentAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_comment_blog);
        //Cau hinh cac bien khoi tao
        database=FirebaseDatabase.getInstance();
        auth=FirebaseAuth.getInstance();
        edtComment=findViewById(R.id.commentInput);
        btnSend=findViewById(R.id.sendCommentButton);
        listComment= new ArrayList<>();
        commentRecyclerView=findViewById(R.id.commentsRecyclerView);
        commentRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter=new commentAdapter(this,listComment);
        commentRecyclerView.setAdapter(adapter);
        // Lay thong tin tu intent qua
        String idOwner=getIntent().getStringExtra("userId");
        String idBlog=getIntent().getStringExtra("blogId");
        DatabaseReference blogsRef = database.getReference().child("blogs").child(idOwner).child(idBlog);
        blogsRef.child("comments").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listComment.clear(); // Xóa dữ liệu cũ
                for (DataSnapshot commentSnapshot : snapshot.getChildren()) {
                    comment itemComment = commentSnapshot.getValue(comment.class);
                    Log.d("Comment", "onDataChange: "+itemComment.getComment());
                    listComment.add(itemComment);
                }
                adapter.notifyDataSetChanged(); // Cập nhật RecyclerView
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CommentBlog.this, "Failed to load comments!", Toast.LENGTH_SHORT).show();
            }
        });
        btnSend.setOnClickListener(v -> sendComment(blogsRef));
    }

    private void sendComment(DatabaseReference blogsRef) {
        String commentText = edtComment.getText().toString().trim();
        // Kiểm tra nội dung bình luận
        if (TextUtils.isEmpty(commentText)) {
            Toast.makeText(this, "Please write a comment!", Toast.LENGTH_SHORT).show();
            return;
        }
        // Lấy thông tin người dùng hiện tại
        String currentUserId = auth.getCurrentUser().getUid();
        String commentId = blogsRef.child("comments").push().getKey(); // Tạo ID bình luận duy nhất
        long timestamp = System.currentTimeMillis();

        // Tạo đối tượng bình luận
        comment Comment = new comment(commentText, timestamp + "", currentUserId, commentId);

        // Gửi bình luận lên Firebase
        blogsRef.child("comments").child(commentId).setValue(Comment)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Comment added!", Toast.LENGTH_SHORT).show();
                        edtComment.setText(""); // Xóa nội dung trong ô nhập sau khi gửi
                    } else {
                        Toast.makeText(this, "Failed to add comment!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
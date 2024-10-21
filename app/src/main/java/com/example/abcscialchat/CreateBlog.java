package com.example.abcscialchat;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.net.Uri;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.abcscialchat.entity.blog;
import com.example.abcscialchat.entity.comment;
import com.example.abcscialchat.entity.share;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class CreateBlog extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private LinearLayout toolBar;
    private ImageView closePost,imageUserChoose;
    private EditText postContent;
    private Button chooseImageBtn, postButton;
    ProgressDialog progressDialog;
    FirebaseDatabase database;
    FirebaseAuth auth;
    String authUid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_blog);
        // Khởi tạo Firebase Storage
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference("images");
        progressDialog= new ProgressDialog(this);
        progressDialog.setMessage("Create blog");
        progressDialog.setCancelable(false);
        // Ánh xạ các thành phần giao diện
        chooseImageBtn = findViewById(R.id.chooseImageBtn);
        postButton = findViewById(R.id.postButton);
        postContent = findViewById(R.id.postContent);
        closePost = findViewById(R.id.closePost);
        imageUserChoose=findViewById(R.id.imageUserChoose);
        chooseImageBtn.setOnClickListener(v -> openFileChooser());

        //Database
        auth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();
        authUid=auth.getUid();

        // Nút đăng bài và tải ảnh lên Firebase
        postButton.setOnClickListener(v -> {
            if (imageUri != null) {
                uploadImageToFirebase();
            } else {
                Toast.makeText(CreateBlog.this, "Chưa chọn hình ảnh", Toast.LENGTH_SHORT).show();
            }
        });
        closePost.setOnClickListener(v -> finish());
    }

    // Hàm mở thư viện ảnh
    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            imageUserChoose.setImageURI(imageUri);  // Hiển thị ảnh lên ImageView
            Toast.makeText(this, "Đã chọn hình ảnh", Toast.LENGTH_SHORT).show();
        }
    }

    // Hàm upload hình ảnh lên Firebase
    private void uploadImageToFirebase() {
        if (imageUri != null) {
            StorageReference fileReference = storageReference.child(System.currentTimeMillis()
                    + ".jpg");  // Tạo tên file ngẫu nhiên dựa trên thời gian hiện tại
            fileReference.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> fileReference.getDownloadUrl()
                            .addOnSuccessListener(uri -> {
                                String imageUrl = uri.toString();
                                String content=postContent.getText().toString();
                                String idUser=auth.getUid();
                                ArrayList<comment> comments = new ArrayList<>();
                                ArrayList<share> shares = new ArrayList<>();
                                blog blog = new blog(idUser,content,imageUrl,comments,shares,0,System.currentTimeMillis(),System.currentTimeMillis());
                                progressDialog.dismiss();
                                database=FirebaseDatabase.getInstance();
                                database.getReference().child("blogs").child(idUser).push().setValue(blog).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isComplete())
                                        {
                                            Toast.makeText(CreateBlog.this, "Đăng bài thành công", Toast.LENGTH_SHORT).show();
                                            Intent it = new Intent(CreateBlog.this,HomeActivity.class);
                                            startActivity(it);
                                            finish();
                                        }
                                        else {
                                            Toast.makeText(CreateBlog.this, "Đăng bài khong thành công", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }))
                    .addOnFailureListener(e -> Toast.makeText(CreateBlog.this, "Lỗi upload ảnh: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }
}
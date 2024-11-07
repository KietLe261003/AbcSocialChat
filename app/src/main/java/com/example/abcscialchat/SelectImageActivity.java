package com.example.abcscialchat;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class SelectImageActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private ArrayList<Uri> imageList;
    private ImageAdapter imageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_image);

        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);

        // Thiết lập RecyclerView với lưới 3 cột
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        imageList = new ArrayList<>();

        // Khởi tạo adapter cho RecyclerView
        imageAdapter = new ImageAdapter(imageList, new ImageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Uri uri) {
                // Gửi hình ảnh được chọn về lại chatWin
                Intent resultIntent = new Intent();
                resultIntent.setData(uri);
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });

        recyclerView.setAdapter(imageAdapter);

        // Tải các hình ảnh từ Firebase Storage
        fetchImagesFromFirebase();
    }

    private void fetchImagesFromFirebase() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference imagesRef = storage.getReference().child("images");

        // Hiển thị ProgressBar trong khi tải ảnh
        progressBar.setVisibility(View.VISIBLE);

        // Lấy danh sách tất cả ảnh trong thư mục "images"
        imagesRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                for (StorageReference item : listResult.getItems()) {
                    item.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            imageList.add(uri);
                            imageAdapter.notifyDataSetChanged();
                        }
                    });
                }
                progressBar.setVisibility(View.GONE); // Ẩn ProgressBar sau khi tải xong
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(SelectImageActivity.this, "Không thể tải danh sách hình ảnh.", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
        });
    }
}

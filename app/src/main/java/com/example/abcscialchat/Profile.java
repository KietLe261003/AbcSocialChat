package com.example.abcscialchat;

import static com.example.abcscialchat.chatWin.senderImg;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class Profile extends AppCompatActivity {
    private CircleImageView profileImg;
    private EditText displayName,emailAddress, address, phoneNumber;
    private TextView txtName, txtHandle, mediaShared, viewAll;
    private ImageView imgIconChat, imgIconVideo, imgIconCall, imgIconMore;
    private RecyclerView mediaRecyclerView;
    Button btnSaveName,btnSaveEmailAddress,btnSaveAddress,btnSavePhoneNumber,btnChangeAvatar;
    FirebaseDatabase database;
    FirebaseAuth auth;
    private static final int SELECT_IMAGE_REQUEST_CODE = 2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
        String idProfile=getIntent().getStringExtra("idUser");
        auth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();
        // Mapping the views
        profileImg = findViewById(R.id.profileImg);
        txtName = findViewById(R.id.txt_name);
        txtHandle = findViewById(R.id.txt_handle);
        displayName = findViewById(R.id.display_name);
        emailAddress = findViewById(R.id.email_address);
        address = findViewById(R.id.address);
        phoneNumber = findViewById(R.id.phone_number);
        btnChangeAvatar=findViewById(R.id.changeAvatar);
        viewAll = findViewById(R.id.view_all);

        // Mapping action icons
        imgIconChat = findViewById(R.id.img_icon_chat);
        imgIconVideo = findViewById(R.id.img_icon_video);
        imgIconCall = findViewById(R.id.img_icon_call);
        imgIconMore = findViewById(R.id.img_icon_more);

        //Mapping button
        btnSaveName=findViewById(R.id.btn_saveName);
        btnSaveEmailAddress=findViewById(R.id.btn_saveEmail);
        btnSaveAddress=findViewById(R.id.btn_saveAddress);
        btnSavePhoneNumber=findViewById(R.id.btn_savePhoneNumber);

        // RecyclerView for media
        mediaRecyclerView = findViewById(R.id.media_recycler_view);
        if (auth.getCurrentUser() != null && auth.getCurrentUser().getUid().equals(idProfile)) {
            // Show save buttons if the profile belongs to the current user
            btnSaveName.setVisibility(View.VISIBLE);
            btnSaveEmailAddress.setVisibility(View.VISIBLE);
            btnSaveAddress.setVisibility(View.VISIBLE);
            btnSavePhoneNumber.setVisibility(View.VISIBLE);
            btnChangeAvatar.setVisibility(View.VISIBLE);
        } else {
            // Hide save buttons if it's a different user's profile
            btnSaveName.setVisibility(View.GONE);
            btnSaveEmailAddress.setVisibility(View.GONE);
            btnSaveAddress.setVisibility(View.GONE);
            btnSavePhoneNumber.setVisibility(View.GONE);
            btnChangeAvatar.setVisibility(View.GONE);
            displayName.setEnabled(false);
            emailAddress.setEnabled(false);
            address.setEnabled(false);
            phoneNumber.setEnabled(false);
        }
        getUserInfo(idProfile);
        btnSaveName.setOnClickListener(view -> saveField(displayName, "userName", idProfile));
        btnSaveEmailAddress.setOnClickListener(view -> saveField(emailAddress, "email", idProfile));
        btnSaveAddress.setOnClickListener(view -> saveField(address, "address", idProfile));
        btnSavePhoneNumber.setOnClickListener(view -> saveField(phoneNumber, "phone", idProfile));
        btnChangeAvatar.setOnClickListener(view -> {
            Intent intent = new Intent(Profile.this, SelectImageActivity.class);
            startActivityForResult(intent, SELECT_IMAGE_REQUEST_CODE);
        });
    }
    private void saveField(EditText editText, String field, String idProfile) {
        if (editText.isEnabled()) {
            // Save the data to Firebase
            String newValue = editText.getText().toString().trim();
            if (!newValue.isEmpty()) {
                database.getReference("user").child(idProfile).child(field).setValue(newValue)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(this, "Saved successfully", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(this, "Failed to save", Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                Toast.makeText(this, "Field cannot be empty", Toast.LENGTH_SHORT).show();
            }
            editText.setEnabled(false);
        } else {
            // Enable the EditText for editing
            editText.setEnabled(true);
            editText.requestFocus();
            Toast.makeText(this, "Edit mode enabled", Toast.LENGTH_SHORT).show();
        }
    }
    private void getUserInfo(String idProfile) {
        database.getReference("user").child(idProfile)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        // Lấy dữ liệu người dùng từ Firebase
                        String name = task.getResult().child("userName").getValue(String.class);
                        String password=task.getResult().child("password").getValue(String.class);
                        String email = task.getResult().child("email").getValue(String.class);
                        String status=task.getResult().child("status").getValue(String.class);
                        String addressText = task.getResult().child("address").getValue(String.class);
                        String phone = task.getResult().child("phone").getValue(String.class);
                        String profileImageUrl = task.getResult().child("profilePic").getValue(String.class);
                        String bio=task.getResult().child("bio").getValue(String.class);
                        // Gán dữ liệu vào giao diện
                        txtName.setText(name != null ? name : "N/A");
                        displayName.setText(name != null ? name : "N/A");
                        emailAddress.setText(email != null ? email : "N/A");
                        address.setText(addressText != null ? addressText : "N/A");
                        phoneNumber.setText(phone != null ? phone : "N/A");
                        txtHandle.setText(bio!=null?bio:"Bio");
                        Picasso.get().load(profileImageUrl).into(profileImg);

                    } else {
                        // Xử lý lỗi nếu không lấy được dữ liệu
                        Toast.makeText(this, "Failed to load user info", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                // Gọi phương thức uploadAvatar để tải ảnh lên Firebase và cập nhật avatar
                //uploadAvatar(selectedImageUri.toString(), auth.getCurrentUser().getUid());
                // Cập nhật URL avatar vào Firebase Realtime Database
                database.getReference("user").child(auth.getCurrentUser().getUid()).child("profilePic").setValue(selectedImageUri.toString())
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(Profile.this, "Avatar updated successfully!", Toast.LENGTH_SHORT).show();
                                // Cập nhật ảnh hiển thị trong giao diện
                                Picasso.get().load(selectedImageUri.toString()).into(profileImg);
                            } else {
                                Toast.makeText(Profile.this, "Failed to update avatar in database", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        }
    }
}
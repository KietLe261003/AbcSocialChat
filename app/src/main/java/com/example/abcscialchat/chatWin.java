package com.example.abcscialchat;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class chatWin extends AppCompatActivity {
    private String reciverImg, reciverUid, reciverName, senderUid;
    private CircleImageView profileImg;
    private TextView rcName;
    private CardView btnSend, btnSendImage;
    private EditText edtMsg;
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    public static String senderImg;
    public static String reciverIImg;
    private String senderRoom, reciverRoom;
    private RecyclerView msgadapter;
    private ArrayList<msgModelClass> messagesList;
    private messageAdapter messageAdapter;

    private static final int SELECT_IMAGE_REQUEST_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_win);

        // Initialize Firebase database and authentication
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();

        // Retrieve data from the intent
        reciverName = getIntent().getStringExtra("name");
        reciverImg = getIntent().getStringExtra("reciverImg");
        reciverUid = getIntent().getStringExtra("uid");

        // Initialize views
        profileImg = findViewById(R.id.profileImg);
        rcName = findViewById(R.id.reciverName);
        btnSend = findViewById(R.id.btnSend);
        edtMsg = findViewById(R.id.edtWrite);
        btnSendImage = findViewById(R.id.btnSendImage);
        msgadapter = findViewById(R.id.msgadapter);

        // Set up RecyclerView
        messagesList = new ArrayList<>();
        messageAdapter = new messageAdapter(chatWin.this, messagesList);
        msgadapter.setLayoutManager(new LinearLayoutManager(this));
        msgadapter.setAdapter(messageAdapter);

        // Load receiver's profile image and name
        Picasso.get().load(reciverImg).into(profileImg);
        rcName.setText(reciverName);
        senderUid = auth.getUid();

        senderRoom = senderUid + reciverUid;
        reciverRoom = reciverUid + senderUid;

        // Load messages from Firebase
        DatabaseReference chatReference = database.getReference().child("chats").child(senderRoom).child("messages");
        chatReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messagesList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    msgModelClass message = dataSnapshot.getValue(msgModelClass.class);
                    messagesList.add(message);
                }
                messageAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(chatWin.this, "Failed to load messages.", Toast.LENGTH_SHORT).show();
            }
        });

        // Send text message
        btnSend.setOnClickListener(view -> {
            String msg = edtMsg.getText().toString();
            if (msg.isEmpty()) {
                Toast.makeText(chatWin.this, "Enter the first message", Toast.LENGTH_SHORT).show();
                return;
            }
            edtMsg.setText("");
            Date date = new Date();
            msgModelClass msgModelClass = new msgModelClass(msg, senderUid, date.getTime());
            sendMessage(msgModelClass);
        });

        // Open SelectImageActivity to choose an image
        btnSendImage.setOnClickListener(view -> {
            Intent intent = new Intent(chatWin.this, SelectImageActivity.class);
            startActivityForResult(intent, SELECT_IMAGE_REQUEST_CODE);
        });
    }

    // Handle the selected image result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                sendImageMessage(selectedImageUri.toString());
            }
        }
    }

    // Method to send a text message
    private void sendMessage(msgModelClass message) {
        DatabaseReference messageRef = database.getReference().child("chats").child(senderRoom).child("messages");
        messageRef.push().setValue(message).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                database.getReference().child("chats").child(reciverRoom).child("messages").push().setValue(message);
            }
        });
    }

    // Method to send an image message
    private void sendImageMessage(String imageUrl) {
        Date date = new Date();
        msgModelClass imageMessage = new msgModelClass(imageUrl, senderUid, date.getTime(), true); // `true` could indicate it's an image message
        sendMessage(imageMessage);
    }
}

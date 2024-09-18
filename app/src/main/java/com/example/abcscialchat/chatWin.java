package com.example.abcscialchat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class chatWin extends AppCompatActivity {
    String reciverImg,reciverUid,reciverName,senderUid;
    CircleImageView profileImg;
    TextView rcName;
    CardView btnSend;
    EditText edtMsg;
    FirebaseAuth auth;
    FirebaseDatabase database;
    public static String senderImg;
    public static String reciverIImg;
    String senderRoom, reciverRoom;
    RecyclerView msgadapter;
    ArrayList<msgModelClass> messagesList;
    messageAdapter messageAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat_win);
        //Cấu hình biến cơ sở dữ liệu
        database=FirebaseDatabase.getInstance();
        auth=FirebaseAuth.getInstance();

        //Cấu hình dữ liệu gửi từ mainactivity
        reciverName= getIntent().getStringExtra("name");
        reciverImg=getIntent().getStringExtra("reciverImg");
        reciverUid=getIntent().getStringExtra("uid");

        messagesList= new ArrayList<>();

        // Cấu hình adapter
        msgadapter=findViewById(R.id.msgadapter);
        LinearLayoutManager linearLayoutManager= new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        msgadapter.setLayoutManager(linearLayoutManager);
        messageAdapter= new messageAdapter(chatWin.this,messagesList);
        msgadapter.setAdapter(messageAdapter);

        //Cấu hình biến sử dụng
        profileImg=findViewById(R.id.profileImg);
        rcName=findViewById(R.id.reciverName);
        btnSend=findViewById(R.id.btnSend);
        edtMsg=findViewById(R.id.edtWrite);
        Picasso.get().load(reciverImg).into(profileImg);
        rcName.setText(""+reciverName);
        senderUid=auth.getUid();


        senderRoom=senderUid+reciverUid; //lấy id phòng của người gửi
        reciverRoom=reciverUid+senderUid; //lấy id phòng của người được gửi
        DatabaseReference reference= database.getReference().child("user").child(auth.getUid());
        DatabaseReference chatReference = database.getReference().child("chats").child(senderRoom).child("messages"); //lấy tin nhắn
        chatReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messagesList.clear();
                for (DataSnapshot dataSnapshot:snapshot.getChildren())
                {
                    msgModelClass message=dataSnapshot.getValue(msgModelClass.class);
                    messagesList.add(message);
                }
                messageAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        // lấy hình ảnh của người gửi và hình ảnh của người được gửi
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                senderImg=snapshot.child("profilePic").getValue().toString();
                reciverIImg=reciverImg;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Xử lý nút ấn gửi
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg=edtMsg.getText().toString();
                if (msg.isEmpty())
                {
                    Toast.makeText(chatWin.this, "Enter the first message", Toast.LENGTH_SHORT).show();
                }
                edtMsg.setText("");
                Date date= new Date();
                msgModelClass msgModelClass= new msgModelClass(msg,senderUid,date.getTime());
                database=FirebaseDatabase.getInstance();
                database.getReference().child("chats").child(senderRoom).child("messages").push()
                        .setValue(msgModelClass).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                database.getReference().child("chats").child(reciverRoom).child("messages").push()
                                        .setValue(msgModelClass).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                            }
                                        });
                            }
                        });
            }
        });

    }
}
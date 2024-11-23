package com.example.abcscialchat;

import android.os.Bundle;
import android.util.Log;
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

import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatAI extends AppCompatActivity {
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

    //Cấu hình biến gửi dữ liệu
    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");
    OkHttpClient client = new OkHttpClient();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat_ai);
        //Cấu hình biến cơ sở dữ liệu
        database=FirebaseDatabase.getInstance();
        auth=FirebaseAuth.getInstance();

        //Khởi tạo list dữ liệu chat
        messagesList= new ArrayList<>();

        // Cấu hình adapter
        msgadapter=findViewById(R.id.msgadapter);
        LinearLayoutManager linearLayoutManager= new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        msgadapter.setLayoutManager(linearLayoutManager);
        messageAdapter= new messageAdapter(ChatAI.this,messagesList);
        msgadapter.setAdapter(messageAdapter);

        //Cấu hình biến sử dụng
        profileImg=findViewById(R.id.profileImg);
        rcName=findViewById(R.id.reciverName);
        btnSend=findViewById(R.id.btnSend);
        edtMsg=findViewById(R.id.edtWrite);
        Picasso.get()
                .load("https://firebasestorage.googleapis.com/v0/b/socialchat-9ff4d.appspot.com/o/AI.png?alt=media&token=7898f80b-c87a-4585-b802-e48523d95504")
                .into(profileImg);
        rcName.setText("Chat AI");
        senderUid=auth.getUid();

        //Lấy dữ liệu
        DatabaseReference reference= database.getReference().child("user").child(auth.getUid());
        DatabaseReference chatReference = database.getReference().child("AIChat").child(auth.getUid()).child("messages");
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
                msgadapter.scrollToPosition(messagesList.size() - 1);
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
                    Toast.makeText(ChatAI.this, "Enter the first message", Toast.LENGTH_SHORT).show();
                }
                edtMsg.setText("");
                Date date= new Date();
                msgModelClass msgModelClass= new msgModelClass(msg,senderUid,date.getTime());
                database=FirebaseDatabase.getInstance();
                database.getReference().child("AIChat").child(senderUid).child("messages").push()
                        .setValue(msgModelClass).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                callApi(msg,senderUid);
                                msgadapter.scrollToPosition(messagesList.size() - 1);
                            }
                        });
            }
        });
    }
    void callApi(String question,String senderUid){
        // Specify a Gemini model appropriate for your use case
        GenerativeModel gm =
                new GenerativeModel("gemini-1.5-flash","");
        GenerativeModelFutures model = GenerativeModelFutures.from(gm);

        Content content =
                new Content.Builder().addText(question).build();
        Executor executor = Executors.newSingleThreadExecutor();

        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);
        Futures.addCallback(
                response,
                new FutureCallback<GenerateContentResponse>() {
                    @Override
                    public void onSuccess(GenerateContentResponse result) {
                        String resultText = result.getText();
                        Date date= new Date();
                        msgModelClass msgAI= new msgModelClass(resultText,"AI CHAT",date.getTime());
                        database.getReference().child("AIChat").child(senderUid).child("messages").push()
                                .setValue(msgAI).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                    }
                                });
                        Log.d("Res",resultText);
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        t.printStackTrace();
                    }
                },
                executor);
    }

}
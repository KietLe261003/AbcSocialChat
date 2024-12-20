package com.example.abcscialchat;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.viewholder> {
    MainActivity mainActivity;
    ArrayList<User> userArrayList;
    public UserAdapter(MainActivity mainActivity, ArrayList<User> userArrayList) {
        this.mainActivity=mainActivity;
        this.userArrayList=userArrayList;
    }

    @NonNull
    @Override
    public UserAdapter.viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mainActivity).inflate(R.layout.user_item,parent,false);
        return new viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.viewholder holder, int position) {
        //gán dữ liệu cho user item
        User user = userArrayList.get(position);
        holder.username.setText(user.userName);
        holder.userstatus.setText(user.status);
        Picasso.get().load(user.profilePic).into(holder.userimg);

        //Xử lý sư kiện khi nhấn vào item
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(user.getProfilePic()=="https://firebasestorage.googleapis.com/v0/b/socialchat-9ff4d.appspot.com/o/AI.png?alt=media&token=7898f80b-c87a-4585-b802-e48523d95504")
                {
                    Intent it = new Intent(mainActivity, ChatAI.class);
                    mainActivity.startActivity(it);
                }
                else
                {
                    Intent it = new Intent(mainActivity, chatWin.class);
                    it.putExtra("name",user.getUserName());
                    it.putExtra("reciverImg",user.getProfilePic());
                    it.putExtra("uid",user.getUserId());
                    mainActivity.startActivity(it);
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return userArrayList.size();
    }

    public class viewholder extends RecyclerView.ViewHolder {
        CircleImageView userimg;
        TextView username,userstatus;

        public viewholder(@NonNull View itemView) {
            super(itemView);
            userimg=itemView.findViewById(R.id.userimg);
            username=itemView.findViewById(R.id.username);
            userstatus=itemView.findViewById(R.id.userstatus);

        }
    }
}

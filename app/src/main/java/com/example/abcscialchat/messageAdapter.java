package com.example.abcscialchat;

import static com.example.abcscialchat.chatWin.reciverIImg;
import static com.example.abcscialchat.chatWin.senderImg;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class messageAdapter extends RecyclerView.Adapter {
    Context context;
    ArrayList<msgModelClass> messageAdapterArratList;
    int ITEM_SEND=1;
    int ITEM_RECIVER=2;

    public messageAdapter(Context context, ArrayList<msgModelClass> messageAdapterArratList) {
        this.context = context;
        this.messageAdapterArratList = messageAdapterArratList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType==ITEM_SEND)
        {
            View view = LayoutInflater.from(context).inflate(R.layout.sender_layout,parent,false);
            return new senderViewHoler(view);
        }
        else
        {
            View view = LayoutInflater.from(context).inflate(R.layout.reciver_layout,parent,false);
            return new reciverViewHoler(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        msgModelClass message = messageAdapterArratList.get(position);
        if(holder.getClass()==senderViewHoler.class)
        {
            senderViewHoler viewHoler=(senderViewHoler) holder;
            viewHoler.msg.setText(message.getMessage());
            Picasso.get().load(senderImg).into(viewHoler.circleImageView);
        }
        else
        {
            reciverViewHoler viewHoler=(reciverViewHoler) holder;
            viewHoler.msg.setText(message.getMessage());
            Picasso.get().load(reciverIImg).into(viewHoler.circleImageView);
        }
    }

    @Override
    public int getItemCount() {
        return messageAdapterArratList.size();
    }

    @Override
    public int getItemViewType(int position) {
        msgModelClass message = messageAdapterArratList.get(position);
        if(FirebaseAuth.getInstance().getCurrentUser().getUid().equals(message.senderid))
        {
            return ITEM_SEND;
        }
        else
        {
            return ITEM_RECIVER;
        }
    }

    class senderViewHoler extends RecyclerView.ViewHolder {
        CircleImageView circleImageView;
        TextView msg;
        public senderViewHoler(@NonNull View itemView) {
            super(itemView);
            circleImageView=itemView.findViewById(R.id.profilerggg);
            msg=itemView.findViewById(R.id.msgsendertyp);
        }
    }
    class reciverViewHoler extends RecyclerView.ViewHolder {
        CircleImageView circleImageView;
        TextView msg;
        public reciverViewHoler(@NonNull View itemView) {
            super(itemView);
            circleImageView=itemView.findViewById(R.id.pro);
            msg=itemView.findViewById(R.id.recivertextset);
        }
    }

}

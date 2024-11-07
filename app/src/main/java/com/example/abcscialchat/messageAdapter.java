package com.example.abcscialchat;

import static com.example.abcscialchat.chatWin.reciverIImg;
import static com.example.abcscialchat.chatWin.senderImg;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class messageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private ArrayList<msgModelClass> messageAdapterArratList;
    private static final int ITEM_SEND = 1;
    private static final int ITEM_RECEIVER = 2;

    public messageAdapter(Context context, ArrayList<msgModelClass> messageAdapterArratList) {
        this.context = context;
        this.messageAdapterArratList = messageAdapterArratList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_SEND) {
            View view = LayoutInflater.from(context).inflate(R.layout.sender_layout, parent, false);
            return new SenderViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.reciver_layout, parent, false);
            return new ReceiverViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        msgModelClass message = messageAdapterArratList.get(position);

        if (holder instanceof SenderViewHolder) {
            SenderViewHolder senderHolder = (SenderViewHolder) holder;
            if (message.isImage()) {
                senderHolder.msgText.setVisibility(View.GONE);
                senderHolder.msgImage.setVisibility(View.VISIBLE);
                Picasso.get().load(message.getMessage()).into(senderHolder.msgImage);
            } else {
                senderHolder.msgText.setVisibility(View.VISIBLE);
                senderHolder.msgImage.setVisibility(View.GONE);
                senderHolder.msgText.setText(message.getMessage());
            }

            // Kiểm tra nếu `senderImg` không null, tải vào `profileImage`
            if (senderImg != null && !senderImg.isEmpty()) {
                Picasso.get().load(senderImg).into(senderHolder.profileImage);
            }
        } else {
            ReceiverViewHolder receiverHolder = (ReceiverViewHolder) holder;
            if (message.isImage()) {
                receiverHolder.msgText.setVisibility(View.GONE);
                receiverHolder.msgImage.setVisibility(View.VISIBLE);
                Picasso.get().load(message.getMessage()).into(receiverHolder.msgImage);
            } else {
                receiverHolder.msgText.setVisibility(View.VISIBLE);
                receiverHolder.msgImage.setVisibility(View.GONE);
                receiverHolder.msgText.setText(message.getMessage());
            }

            // Kiểm tra nếu `reciverIImg` không null, tải vào `profileImage`
            if (reciverIImg != null && !reciverIImg.isEmpty()) {
                Picasso.get().load(reciverIImg).into(receiverHolder.profileImage);
            }
        }
    }

    @Override
    public int getItemCount() {
        return messageAdapterArratList.size();
    }

    @Override
    public int getItemViewType(int position) {
        msgModelClass message = messageAdapterArratList.get(position);
        if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(message.getSenderId())) {
            return ITEM_SEND;
        } else {
            return ITEM_RECEIVER;
        }
    }

    static class SenderViewHolder extends RecyclerView.ViewHolder {
        CircleImageView profileImage;
        TextView msgText;
        ImageView msgImage;

        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.profilerggg);
            msgText = itemView.findViewById(R.id.msgsendertyp);
            msgImage = itemView.findViewById(R.id.senderImageView);
        }
    }

    static class ReceiverViewHolder extends RecyclerView.ViewHolder {
        CircleImageView profileImage;
        TextView msgText;
        ImageView msgImage;

        public ReceiverViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.pro);
            msgText = itemView.findViewById(R.id.recivertextset);
            msgImage = itemView.findViewById(R.id.receiverImageView);
        }
    }
}

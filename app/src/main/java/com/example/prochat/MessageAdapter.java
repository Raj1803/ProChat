package com.example.prochat;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>
{
    private List<Messages> usermessagelist;

    private FirebaseAuth mAuth;
    private DatabaseReference userref;


    public MessageAdapter(List<Messages> usermessagelist)
    {
        this.usermessagelist = usermessagelist;
    }


    public class MessageViewHolder extends RecyclerView.ViewHolder
    {

        public TextView sendermessagetext, receivermessagetext;
        public CircleImageView receiverprofileimage;


        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);

            sendermessagetext = itemView.findViewById(R.id.sender_message_text);
            receivermessagetext = itemView.findViewById(R.id.receiver_message_text);
            receiverprofileimage = itemView.findViewById(R.id.message_profile_image);


        }
    }



    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_messages_layout, parent,false);


        mAuth = FirebaseAuth.getInstance();


        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MessageViewHolder holder, int position) {

        String messagesenderid = mAuth.getCurrentUser().getUid();

        Messages messages = usermessagelist.get(position);

        String fromuserid = messages.getFrom();
        String frommessagetype = messages.getType();

        userref = FirebaseDatabase.getInstance().getReference().child("Users").child(fromuserid);

        userref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.hasChild("image"))
                {
                    String receiverimage = dataSnapshot.child("image").getValue().toString();

                    Picasso.get().load(receiverimage).placeholder(R.drawable.profile_image).into(holder.receiverprofileimage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        if(frommessagetype.equals("text"))
        {
            holder.receivermessagetext.setVisibility(View.INVISIBLE);
            holder.receiverprofileimage.setVisibility(View.INVISIBLE);
            holder.sendermessagetext.setVisibility(View.INVISIBLE);

            if(fromuserid.equals(messagesenderid))
            {
                holder.sendermessagetext.setVisibility(View.VISIBLE);

                holder.sendermessagetext.setBackgroundResource(R.drawable.sender_messages_layout);
                holder.sendermessagetext.setText(messages.getMessage());
            }
            else
            {


                holder.receiverprofileimage.setVisibility(View.VISIBLE);
                holder.receivermessagetext.setVisibility(View.VISIBLE);

                holder.receivermessagetext.setBackgroundResource(R.drawable.receiver_messages_layout);
                holder.receivermessagetext.setText(messages.getMessage());



            }
        }
    }

    @Override
    public int getItemCount() {
        return usermessagelist.size();
    }


}

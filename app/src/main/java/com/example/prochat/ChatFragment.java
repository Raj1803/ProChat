package com.example.prochat;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment
{

    private View privatechatview;
    private RecyclerView chatList;

    private DatabaseReference chatsref, userref;
    private FirebaseAuth mAuth;
    private String currentuserid;



    public ChatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        privatechatview = inflater.inflate(R.layout.fragment_chat, container, false);

        mAuth = FirebaseAuth.getInstance();
        currentuserid = mAuth.getCurrentUser().getUid();

        chatsref = FirebaseDatabase.getInstance().getReference().child("Contacts").child(currentuserid);
        userref = FirebaseDatabase.getInstance().getReference().child("Users");

        chatList = privatechatview.findViewById(R.id.chat_list);
        chatList.setLayoutManager(new LinearLayoutManager(getContext()));


        return privatechatview;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Contacts> options =
                new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(chatsref, Contacts.class)
                .build();

        FirebaseRecyclerAdapter<Contacts, ChatViewHolder> adapter =
                new FirebaseRecyclerAdapter<Contacts, ChatViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final ChatViewHolder chatViewHolder, int i, @NonNull Contacts contacts) {

                        final String userid = getRef(i).getKey();
                        final String[] retImage = {"defualt_image"};


                        userref.child(userid).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                if(dataSnapshot.exists())
                                {
                                    if(dataSnapshot.hasChild("image"))
                                    {
                                        retImage[0] = dataSnapshot.child("image").getValue().toString();

                                        Picasso.get().load(retImage[0]).into(chatViewHolder.profileimage);


                                    }

                                    final String retName = dataSnapshot.child("name").getValue().toString();
                                    final String retStatus = dataSnapshot.child("status").getValue().toString();

                                    chatViewHolder.userName.setText(retName);
                                    chatViewHolder.userstatus.setText("Last Seen: " + "\n" + "Date" + " Time" );


                                    chatViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent chatintent = new Intent(getContext(),Chat_activity.class);
                                            chatintent.putExtra("visit_user_id" , userid);
                                            chatintent.putExtra("visit_user_name",retName);
                                            chatintent.putExtra("visit_user_image", retImage[0]);
                                            startActivity(chatintent);
                                        }
                                    });
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }

                    @NonNull
                    @Override
                    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {

                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_display_layout,parent,false);
                        return new ChatViewHolder(view);
                    }
                };


        chatList.setAdapter(adapter);
        adapter.startListening();

    }


    public static class ChatViewHolder extends RecyclerView.ViewHolder
    {

        CircleImageView profileimage;
        TextView userName,userstatus;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);

            profileimage = itemView.findViewById(R.id.users_profile_image);
            userName = itemView.findViewById(R.id.user_name);
            userstatus = itemView.findViewById(R.id.user_status);



        }


    }
}

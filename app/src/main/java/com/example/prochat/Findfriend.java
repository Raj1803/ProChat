package com.example.prochat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;


import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;


import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import de.hdodenhof.circleimageview.CircleImageView;

public class Findfriend extends AppCompatActivity {

    private Toolbar mtoolbar;
    private RecyclerView Findfriendrecyle;

    private DatabaseReference userroof;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_findfriend);

        userroof = FirebaseDatabase.getInstance().getReference().child("Users");

        Findfriendrecyle = findViewById(R.id.find_friend_recycle);
        Findfriendrecyle.setLayoutManager(new LinearLayoutManager(this));


        mtoolbar = findViewById(R.id.find_friend_toolbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Find Friend");

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Contacts> options =
                new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(userroof,Contacts.class)
                .build();

        FirebaseRecyclerAdapter<Contacts, FindFriendViewHolder> adapter =
                new FirebaseRecyclerAdapter<Contacts, FindFriendViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull FindFriendViewHolder holder, final int position, @NonNull Contacts model) {

                        holder.username.setText(model.getName());
                        holder.userstatus.setText(model.getStatus());
                        Picasso.get().load(model.getImage()).placeholder(R.drawable.profile_image).into(holder.profilepic);

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                String visit_user_id = getRef(position).getKey();

                                Intent Profileintent = new Intent(Findfriend.this,Profile.class);
                                Profileintent.putExtra("visit_user_id",visit_user_id);
                                startActivity(Profileintent);
                            }
                        });

                    }

                    @NonNull
                    @Override
                    public FindFriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_display_layout,parent,false);
                        FindFriendViewHolder viewHolder = new FindFriendViewHolder(view);
                        return viewHolder;
                    }
                };

        Findfriendrecyle.setAdapter(adapter);


        adapter.startListening();
    }




    public static class FindFriendViewHolder extends RecyclerView.ViewHolder
    {
        TextView username, userstatus;
        CircleImageView profilepic;

        public FindFriendViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.user_name);
            userstatus = itemView.findViewById(R.id.user_status);
            profilepic = itemView.findViewById(R.id.users_profile_image);
        }
    }
}

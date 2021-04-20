package com.example.prochat;


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
public class ContactsFragment extends Fragment {

    private View contactview;
    private RecyclerView mcontactlist;

    private DatabaseReference contactref,userref;
    private FirebaseAuth mAuth;
    private String currentsuerid;

    public ContactsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        contactview = inflater.inflate(R.layout.fragment_contacts, container, false);

        mcontactlist = contactview.findViewById(R.id.contact_list);

        mcontactlist.setLayoutManager(new LinearLayoutManager(getContext()));


        mAuth = FirebaseAuth.getInstance();
        currentsuerid = mAuth.getCurrentUser().getUid();

        contactref = FirebaseDatabase.getInstance().getReference().child("Contacts").child(currentsuerid);
        userref = FirebaseDatabase.getInstance().getReference().child("Users");


        return contactview;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(contactref,Contacts.class)
                .build();

        FirebaseRecyclerAdapter<Contacts,ContactsViewHolder> adapter
                = new FirebaseRecyclerAdapter<Contacts, ContactsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final ContactsViewHolder holder, int position, @NonNull Contacts contacts) {

                final String userIds = getRef(position).getKey();
                userref.child(userIds).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if(dataSnapshot.hasChild("image")){
                            String userimage = dataSnapshot.child("image").getValue().toString();
                            String profilename = dataSnapshot.child("name").getValue().toString();
                            String profilestatus = dataSnapshot.child("status").getValue().toString();

                            holder.username.setText(profilename);
                            holder.userstatus.setText(profilestatus);

                            Picasso.get().load(userimage).placeholder(R.drawable.profile_image).into(holder.profileimage);
                        }
                        else
                        {

                            String profilename = dataSnapshot.child("name").getValue().toString();
                            String profilestatus = dataSnapshot.child("status").getValue().toString();

                            holder.username.setText(profilename);
                            holder.userstatus.setText(profilestatus);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });



            }

            @NonNull
            @Override
            public ContactsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_display_layout, parent,false);
                ContactsViewHolder viewHolder = new ContactsViewHolder(view);
                return viewHolder;
            }
        };

        mcontactlist.setAdapter(adapter);
        adapter.startListening();

    }


    public static class ContactsViewHolder extends RecyclerView.ViewHolder
    {

        TextView username,userstatus;
        CircleImageView profileimage;


        public ContactsViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.user_name);
            userstatus = itemView.findViewById(R.id.user_status);
            profileimage = itemView.findViewById(R.id.users_profile_image);
        }
    }
}

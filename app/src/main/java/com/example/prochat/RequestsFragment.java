package com.example.prochat;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class RequestsFragment extends Fragment {

    private View Requestsfragmentview;
    private RecyclerView mrequestlist;

    private DatabaseReference chatreqref,userref,contactref;
    private FirebaseAuth mAuth;
    private String currentuserid;

    public RequestsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Requestsfragmentview = inflater.inflate(R.layout.fragment_requests, container, false);

        mAuth = FirebaseAuth.getInstance();
        currentuserid = mAuth.getCurrentUser().getUid();

        chatreqref = FirebaseDatabase.getInstance().getReference().child("Chat requests");
        userref = FirebaseDatabase.getInstance().getReference().child("Users");
        contactref = FirebaseDatabase.getInstance().getReference().child("Contacts");

        mrequestlist = Requestsfragmentview.findViewById(R.id.chat_request_list);
        mrequestlist.setLayoutManager(new LinearLayoutManager(getContext()));




        return Requestsfragmentview;
    }

    @Override
    public void onStart() {
        super.onStart();


        FirebaseRecyclerOptions<Contacts> options =
                new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(chatreqref.child(currentuserid),Contacts.class)
                .build();

        FirebaseRecyclerAdapter<Contacts,RequestsViewHolder> adapter =
                new FirebaseRecyclerAdapter<Contacts, RequestsViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final RequestsViewHolder holder, int position, @NonNull Contacts contacts) {

                        holder.itemView.findViewById(R.id.request_accept_button).setVisibility(View.VISIBLE);
                        holder.itemView.findViewById(R.id.request_cancel_button).setVisibility(View.VISIBLE);

                        final String list_user_id = getRef(position).getKey();

                        DatabaseReference gettype = getRef(position).child("request_type").getRef();

                        gettype.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                if(dataSnapshot.exists())
                                {
                                    String type = dataSnapshot.getValue().toString();

                                    if(type.equals("received"))
                                    {

                                        userref.child(list_user_id).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                if(dataSnapshot.hasChild("image"))
                                                {

                                                    final String requestprofileimage = dataSnapshot.child("image").getValue().toString();

                                                    Picasso.get().load(requestprofileimage).placeholder(R.drawable.profile_image).into(holder.profileimege);

                                                }
                                                    final String requestusername = dataSnapshot.child("name").getValue().toString();
                                                    final String requestuserstatus = dataSnapshot.child("status").getValue().toString();

                                                    holder.username.setText(requestusername);
                                                    holder.userstatus.setText("want to connect with you");



                                                holder.itemView.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {

                                                        CharSequence options[] = new CharSequence[]
                                                                {
                                                                        "Accept",
                                                                        "Cancel"
                                                                };

                                                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                                                        builder.setTitle( requestusername + "  Chat Request");

                                                        builder.setItems(options, new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {

                                                                if(which == 0)
                                                                {
                                                                    contactref.child(currentuserid).child(list_user_id).child("Contact")
                                                                            .setValue("saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {

                                                                            if(task.isSuccessful())
                                                                            {
                                                                                contactref.child(list_user_id).child(currentuserid).child("Contact")
                                                                                        .setValue("saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<Void> task) {

                                                                                        if(task.isSuccessful())
                                                                                        {
                                                                                            chatreqref.child(currentuserid).child(list_user_id)
                                                                                                    .removeValue()
                                                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                        @Override
                                                                                                        public void onComplete(@NonNull Task<Void> task) {

                                                                                                            if (task.isSuccessful()) {
                                                                                                                chatreqref.child(list_user_id).child(currentuserid)
                                                                                                                        .removeValue()
                                                                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                            @Override
                                                                                                                            public void onComplete(@NonNull Task<Void> task) {

                                                                                                                                if (task.isSuccessful()) {

                                                                                                                                    Toast.makeText(getContext(), "New Contact Saved", Toast.LENGTH_SHORT).show();

                                                                                                                                }

                                                                                                                            }
                                                                                                                        });

                                                                                                            }

                                                                                                        }
                                                                                                    });

                                                                                        }
                                                                                    }
                                                                                });

                                                                            }
                                                                        }
                                                                    });

                                                                }


                                                                if(which == 1)
                                                                {

                                                                    chatreqref.child(currentuserid).child(list_user_id)
                                                                            .removeValue()
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {

                                                                                    if (task.isSuccessful()) {
                                                                                        chatreqref.child(list_user_id).child(currentuserid)
                                                                                                .removeValue()
                                                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                    @Override
                                                                                                    public void onComplete(@NonNull Task<Void> task) {

                                                                                                        if (task.isSuccessful()) {

                                                                                                            Toast.makeText(getContext(), "Request Canceled", Toast.LENGTH_SHORT).show();

                                                                                                        }

                                                                                                    }
                                                                                                });

                                                                                    }

                                                                                }
                                                                            });

                                                                }
                                                            }
                                                        });


                                                        builder.show();

                                                    }
                                                });

                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });

                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }

                    @NonNull
                    @Override
                    public RequestsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_display_layout,parent,false);
                        RequestsViewHolder holder = new RequestsViewHolder(view);
                        return holder;

                    }
                };

        mrequestlist.setAdapter(adapter);
        adapter.startListening();


    }


    public static class RequestsViewHolder extends RecyclerView.ViewHolder
    {

        TextView username,userstatus;
        CircleImageView profileimege;
        Button acceptbutton, cancelbutton;

        public RequestsViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.user_name);
            userstatus = itemView.findViewById(R.id.user_status);
            profileimege = itemView.findViewById(R.id.users_profile_image);
            acceptbutton = itemView.findViewById(R.id.request_accept_button);
            cancelbutton = itemView.findViewById(R.id.request_cancel_button);
        }
    }
}

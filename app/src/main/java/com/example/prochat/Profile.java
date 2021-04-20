package com.example.prochat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.UserHandle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class Profile extends AppCompatActivity {

    private String receiverUID,currentuserid,currentstate,senderuserid;

    private CircleImageView userprofileimage;
    private TextView userprofilename, userprofilestatus;
    private Button sendmessagereqbutton, declinemessagereqbutton;

    private DatabaseReference userref,chatrequestref,contectref, notificationref;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        mAuth = FirebaseAuth.getInstance();
        userref = FirebaseDatabase.getInstance().getReference().child("Users");
        chatrequestref = FirebaseDatabase.getInstance().getReference().child("Chat requests");
        contectref = FirebaseDatabase.getInstance().getReference().child("Contacts");
        notificationref = FirebaseDatabase.getInstance().getReference("Notifications");


        receiverUID = getIntent().getExtras().get("visit_user_id").toString();
        senderuserid = mAuth.getCurrentUser().getUid();

        Toast.makeText(this, "User ID: " +receiverUID, Toast.LENGTH_SHORT).show();

        userprofileimage = findViewById(R.id.visit_profile_image);
        userprofilename = findViewById(R.id.visit_user_name);
        userprofilestatus = findViewById(R.id.visit_user_status);
        sendmessagereqbutton = findViewById(R.id.send_message_button);
        declinemessagereqbutton = findViewById(R.id.decline_message_button);

        currentstate = "new";

        Retriveuserinfo();


    }

    private void Retriveuserinfo()
    {
        userref.child(receiverUID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if((dataSnapshot.exists()) && (dataSnapshot.hasChild("image")))
                {
                    String userimage = dataSnapshot.child("image").getValue().toString();
                    String username = dataSnapshot.child("name").getValue().toString();
                    String userstatus = dataSnapshot.child("status").getValue().toString();

                    Picasso.get().load(userimage).placeholder(R.drawable.profile_image).into(userprofileimage);
                    userprofilename.setText(username);
                    userprofilestatus.setText(userstatus);

                    managechatrequest();
                }

                else
                {
                    String username = dataSnapshot.child("name").getValue().toString();
                    String userstatus = dataSnapshot.child("status").getValue().toString();

                    userprofilename.setText(username);
                    userprofilestatus.setText(userstatus);

                    managechatrequest();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



    private void managechatrequest()
    {

        chatrequestref.child(senderuserid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild(receiverUID))
                        {
                            String request_type = dataSnapshot.child(receiverUID).child("request_type").getValue().toString();

                            if(request_type.equals("sent")){
                                currentstate = "request_sent";
                                sendmessagereqbutton.setText("Cancel Chat Request");
                            }

                            else if(request_type.equals("received"))
                            {
                                currentstate = "request_received";
                                sendmessagereqbutton.setText("Accept Chat Request");

                                declinemessagereqbutton.setVisibility(View.VISIBLE);

                                declinemessagereqbutton.setEnabled(true);

                                declinemessagereqbutton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        cancelchatrequest();
                                    }
                                });
                            }

                        }

                        else
                        {
                            contectref.child(senderuserid).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.hasChild(receiverUID))
                                    {
                                        currentstate = "friends";
                                        sendmessagereqbutton.setText("Remove This Contact");
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


        if(!senderuserid.equals(receiverUID)){

            sendmessagereqbutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendmessagereqbutton.setEnabled(false);

                    if(currentstate.equals("new"))
                    {
                        sendchatrequest();

                    }

                    if(currentstate.equals("request_sent"))
                    {
                        cancelchatrequest();
                    }
                    
                    if(currentstate.equals("request_received")){
                        
                        acceptmshrequest();
                        
                    }
                    if(currentstate.equals("friends"))
                    {
                        removespecificcontact();
                    }
                }
            });
        }
        else
        {
            sendmessagereqbutton.setVisibility(View.INVISIBLE);
        }
    }




    private void removespecificcontact() {

        contectref.child(senderuserid).child(receiverUID)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful())
                        {
                            contectref.child(receiverUID).child(senderuserid)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if(task.isSuccessful())
                                            {
                                                sendmessagereqbutton.setEnabled(true);
                                                currentstate = "new";
                                                sendmessagereqbutton.setText("Send Message");

                                                declinemessagereqbutton.setVisibility(View.INVISIBLE);
                                                declinemessagereqbutton.setEnabled(false);
                                            }

                                        }
                                    });
                        }
                    }
                });

    }





    private void acceptmshrequest() {

        contectref.child(senderuserid).child(receiverUID)
                .child("Contacts").setValue("Saved")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful())
                        {
                            contectref.child(receiverUID).child(senderuserid)
                                    .child("Contact").setValue("Saved")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if(task.isSuccessful())
                                            {
                                                chatrequestref.child(senderuserid).child(receiverUID).removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {

                                                                if(task.isSuccessful()){
                                                                    chatrequestref.child(receiverUID).child(senderuserid).removeValue()
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {

                                                                                    sendmessagereqbutton.setEnabled(true);
                                                                                    currentstate = "friends";
                                                                                    sendmessagereqbutton.setText("Remove This Contact");

                                                                                    declinemessagereqbutton.setVisibility(View.INVISIBLE);
                                                                                    declinemessagereqbutton.setEnabled(false);

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






    private void cancelchatrequest() {

        chatrequestref.child(senderuserid).child(receiverUID)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful())
                        {
                            chatrequestref.child(receiverUID).child(senderuserid)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if(task.isSuccessful())
                                            {
                                                sendmessagereqbutton.setEnabled(true);
                                                currentstate = "new";
                                                sendmessagereqbutton.setText("Send Message");

                                                declinemessagereqbutton.setVisibility(View.INVISIBLE);
                                                declinemessagereqbutton.setEnabled(false);
                                            }

                                        }
                                    });
                        }
                    }
                });

    }




    private void sendchatrequest()
    {
        chatrequestref.child(senderuserid).child(receiverUID)
                .child("request_type").setValue("sent")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            chatrequestref.child(receiverUID).child(senderuserid)
                                    .child("request_type").setValue("received")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful())
                                            {
                                                HashMap<String, String> chatnotificatoinmap = new HashMap<>();
                                                chatnotificatoinmap.put("from" , senderuserid);
                                                chatnotificatoinmap.put("type", "request");


                                                notificationref.child(receiverUID).push()
                                                        .setValue(chatnotificatoinmap)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {

                                                                if(task.isSuccessful())
                                                                {
                                                                    sendmessagereqbutton.setEnabled(true);
                                                                    currentstate = "request_sent";
                                                                    sendmessagereqbutton.setText("Cancel Chat Request");

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

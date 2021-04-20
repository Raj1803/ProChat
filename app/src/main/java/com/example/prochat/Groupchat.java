package com.example.prochat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.telephony.mbms.MbmsErrors;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

public class Groupchat extends AppCompatActivity {

    private Toolbar mToolbar;
    private ImageButton sendmsgbutton;
    private EditText usermsginput;
    private ScrollView mscrollview;
    private TextView displaytextmsg;

    FirebaseAuth mAuth;
    DatabaseReference userref,groupnameref,groupmsgkeyref;


    private String currentgroupname,currentuserID,currentuserName,currentdate,currenttime;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groupchat);


        currentgroupname = getIntent().getExtras().get("groupname").toString();


        mAuth = FirebaseAuth.getInstance();
        currentuserID = mAuth.getCurrentUser().getUid();
        userref = FirebaseDatabase.getInstance().getReference().child("Users");
        groupnameref = FirebaseDatabase.getInstance().getReference().child("Groups").child(currentgroupname);



        InitializeFields();

        getuserinfo();


        sendmsgbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savemsgtodatabase();

                usermsginput.setText("");

                mscrollview.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });

    }


    @Override
    protected void onStart() {
        super.onStart();

        groupnameref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                if(dataSnapshot.exists())
                {
                    Displaymsg(dataSnapshot);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.exists())
                {
                    Displaymsg(dataSnapshot);
                }

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



    private void InitializeFields()
    {
        mToolbar = findViewById(R.id.group_chat_bar_layout);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(currentgroupname);

        sendmsgbutton = findViewById(R.id.send_message_button);
        usermsginput = findViewById(R.id.input_group_msg);
        displaytextmsg = findViewById(R.id.group_chat_text);
        mscrollview = findViewById(R.id.scroll_view);
    }




    private void getuserinfo(){

        userref.child(currentuserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    currentuserName = dataSnapshot.child("name").getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }



    private void savemsgtodatabase()
    {
        String msg = usermsginput.getText().toString();
        String msgKey = groupnameref.push().getKey();

        if(TextUtils.isEmpty(msg))
        {
            Toast.makeText(this, "Empty msg cannot send !", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Calendar calfordate = Calendar.getInstance();
            SimpleDateFormat currentdateformate = new SimpleDateFormat("dd MMM, yyyy");
            currentdate = currentdateformate.format(calfordate.getTime());



            Calendar calfortime = Calendar.getInstance();
            SimpleDateFormat currenttimeformate = new SimpleDateFormat("hh:mm a");
            currenttime = currenttimeformate.format(calfortime.getTime());


            HashMap<String, Object> groupmsgkey = new HashMap<>();
            groupnameref.updateChildren(groupmsgkey);

            groupmsgkeyref = groupnameref.child(msgKey);

            HashMap<String, Object> msginfomap = new HashMap<>();
                msginfomap.put("name",currentuserName);
                msginfomap.put("message",msg);
                msginfomap.put("date",currentdate);
                msginfomap.put("time",currenttime);
             groupmsgkeyref.updateChildren(msginfomap);

        }
    }



    private void Displaymsg(DataSnapshot dataSnapshot){

        Iterator iterator = dataSnapshot.getChildren().iterator();

        while (iterator.hasNext())
        {
            String chatdate = (String) ((DataSnapshot)iterator.next()).getValue();
            String chatmsg = (String) ((DataSnapshot)iterator.next()).getValue();
            String chatname = (String) ((DataSnapshot)iterator.next()).getValue();
            String chattime = (String) ((DataSnapshot)iterator.next()).getValue();


            displaytextmsg.append(chatname + " : \n" + chatmsg + "  \n" + chattime + "     " + chatdate + "\n\n\n");

            mscrollview.fullScroll(ScrollView.FOCUS_DOWN);

        }
    }
}

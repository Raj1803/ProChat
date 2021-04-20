package com.example.prochat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class chat extends AppCompatActivity {

    private Toolbar mtoolbar;
    private ViewPager myviewPager;
    private TabLayout mytablayout;
    TabsAccessorAdaptor myTabsAccessorAdaptor;

    private FirebaseAuth auth;
    private DatabaseReference Rootref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        auth = FirebaseAuth.getInstance();
        Rootref = FirebaseDatabase.getInstance().getReference();

        mtoolbar = findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("ProChat");

        myviewPager = findViewById(R.id.main_tab_pager);
        myTabsAccessorAdaptor = new TabsAccessorAdaptor(getSupportFragmentManager());
        myviewPager.setAdapter(myTabsAccessorAdaptor);

        mytablayout = findViewById(R.id.main_tabs);
        mytablayout.setupWithViewPager(myviewPager);

    }



    @Override
    protected void onStart() {
        super.onStart();

        verifyuserexistance();

    }



    private void verifyuserexistance()
    {
        String currentUserID = auth.getCurrentUser().getUid();

        Rootref.child("Users").child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if((dataSnapshot.child("name").exists())){
                    Toast.makeText(chat.this, "Welcome! ", Toast.LENGTH_SHORT).show();
                }
                else{
                    SendusertosettingActivity();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



    private void SendusertosettingActivity() {
        Intent set = new Intent(chat.this,settings.class);
        startActivity(set);
    }

    private void sendtofindfriend() {
        Intent find = new Intent(chat.this,Findfriend.class);
        startActivity(find);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        if(item.getItemId() == R.id.main_find_friends_option)
        {
            sendtofindfriend();

        }

        if(item.getItemId() == R.id.main_create_group_option)
        {
            RequestNewgroup();

        }
        if(item.getItemId() == R.id.main_logout_option)
        {
            auth.signOut();
            Intent in = new Intent(chat.this , login.class);
            finish();
        }
        if(item.getItemId() == R.id.main_settings_option)
        {
            SendusertosettingActivity();
        }

        return true;
    }




    private void RequestNewgroup() {
        AlertDialog.Builder builder =  new AlertDialog.Builder(chat.this,R.style.AlertDialog);
        builder.setTitle("Enter Group Name");

        final EditText groupnamefield = new EditText(chat.this);
        groupnamefield.setHint("e.g. Prochat Project");
        builder.setView(groupnamefield);

        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String groupname = groupnamefield.getText().toString();

                if(TextUtils.isEmpty(groupname)){
                    Toast.makeText(chat.this, "Please give Group Name !", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Creategroup(groupname);
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();

    }



    private void Creategroup(final String groupname) {

        Rootref.child("Groups").child(groupname).setValue("")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(chat.this, groupname + " group is created successfully !", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}

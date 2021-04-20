package com.example.prochat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class settings extends AppCompatActivity {

    private Button updateaccountsetting;
    private EditText username,userstatus;
    private CircleImageView userprofile;
  //  private ImageView userprofile1;

    private String currentUserID;
    private FirebaseAuth mAuth;
    private DatabaseReference Roofref;

    private static final int galarypic = 1;

    private StorageReference UserProfileImagesRef;

    private ProgressDialog loadingbar;

    private Toolbar settingtoolbar;

    private String downloadurl;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        Roofref = FirebaseDatabase.getInstance().getReference();
        UserProfileImagesRef = FirebaseStorage.getInstance().getReference().child("Profile Images");

        loadingbar = new ProgressDialog(this);



        updateaccountsetting = findViewById(R.id.up_set_button);
        username = findViewById(R.id.set_user_name);
        userstatus = findViewById(R.id.set_profile_status);
       // userprofile = findViewById(R.id.set_profile_image);
        userprofile = findViewById(R.id.set_profile_image);


        settingtoolbar = findViewById(R.id.setting_toolbar);
        setSupportActionBar(settingtoolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Account Settings");

        updateaccountsetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatesettings();
            }
        });

        Retriveuserinfo();

        userprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryintent = new Intent();
                galleryintent.setAction(Intent.ACTION_GET_CONTENT);
                galleryintent.setType("image/*");
                startActivityForResult(galleryintent, galarypic);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == galarypic && resultCode == RESULT_OK && data != null){

            Uri Imageuri = data.getData();

            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if(resultCode == RESULT_OK)
            {

                loadingbar.setTitle("Set Profile image");
                loadingbar.setMessage("Please wait your profile image is uploading");
                loadingbar.setCanceledOnTouchOutside(false);
                loadingbar.show();


                Uri resulturi = result.getUri();


                final StorageReference filepath = UserProfileImagesRef.child(currentUserID + ".jpg" );

                filepath.putFile(resulturi).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                        if (task.isSuccessful()) {

                            Toast.makeText(settings.this, "Profile Image upload successfully !", Toast.LENGTH_SHORT).show();

                            // final String downloadurl = task.getResult().getMetadata().getReference().getDownloadUrl().toString();

                            UserProfileImagesRef.child(currentUserID + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    downloadurl = uri.toString();

                                }
                            });



                            Roofref.child("Users").child(currentUserID).child("image")
                                    .setValue(downloadurl)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if(task.isSuccessful())
                                            {

                                                Toast.makeText(settings.this, "Image stored in database is successfully..!", Toast.LENGTH_SHORT).show();

                                                loadingbar.dismiss();
                                            }
                                            else
                                            {
                                                String msg = task.getException().toString();
                                                Toast.makeText(settings.this, "Error : " + msg, Toast.LENGTH_SHORT).show();
                                                loadingbar.dismiss();
                                            }
                                        }
                                    });


                        }
                        else
                        {
                            String msg = task.getException().toString();
                            Toast.makeText(settings.this, "Error : " + msg, Toast.LENGTH_SHORT).show();
                            loadingbar.dismiss();
                        }
                    }
                });
            }

        }


    }

    private void updatesettings() {
        String setUsername = username.getText().toString();
        String setStatus = userstatus.getText().toString();

        if(TextUtils.isEmpty(setUsername)){
            Toast.makeText(this, "Please write Username !", Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(setStatus)) {
            Toast.makeText(this, "Please write Designation !", Toast.LENGTH_SHORT).show();
        }
        else
        {
            HashMap<String, Object> Profilemap = new HashMap<>();
                Profilemap.put("uid",currentUserID);
                Profilemap.put("name",setUsername);
                Profilemap.put("status",setStatus);
             Roofref.child("Users").child(currentUserID).updateChildren(Profilemap)
                     .addOnCompleteListener(new OnCompleteListener<Void>() {
                         @Override
                         public void onComplete(@NonNull Task<Void> task) {
                            if( task.isSuccessful())
                            {
                                sendtomainactivity();
                                Toast.makeText(settings.this, "Profile Updated Successfully !", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                String msg = task.getException().toString();
                                Toast.makeText(settings.this, "Error" + msg, Toast.LENGTH_SHORT).show();
                            }
                         }
                     });
        }


        }




    private void Retriveuserinfo() {
        Roofref .child("Users").child(currentUserID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if((dataSnapshot.exists()) && (dataSnapshot.hasChild("name"))&& (dataSnapshot.hasChild("image")))
                        {
                            String Retrivievename = dataSnapshot.child("name").getValue().toString();
                            String Retrivievestatus = dataSnapshot.child("status").getValue().toString();
                            String Retrivieveimage = dataSnapshot.child("image").getValue().toString();

                            username.setText(Retrivievename);
                            userstatus.setText(Retrivievestatus);

                            System.out.println("hello method run");
                            Picasso.get().load(Retrivieveimage).into(userprofile);

                        }
                        else if((dataSnapshot.exists()) && (dataSnapshot.hasChild("name")))
                        {
                            String Retrivievename = dataSnapshot.child("name").getValue().toString();
                            String Retrivievestatus = dataSnapshot.child("status").getValue().toString();

                            username.setText(Retrivievename);
                            userstatus.setText(Retrivievestatus);

                            System.out.println("this is 2nd method" );


                        }
                        else
                        {
                            Toast.makeText(settings.this, "Please set & update your profile !", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }



    private void sendtomainactivity() {
        Intent in = new Intent(settings.this,chat.class);
        startActivity(in);
        finish();
    }


}

package com.example.prochat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class Signin extends AppCompatActivity {

    private static final String TAG = "EmailPassword";

    EditText emailfield;
    EditText passfield;
    Button si1;
    public FirebaseAuth mAuth;
    public FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference RootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        si1 = findViewById(R.id.button3);
        emailfield = findViewById(R.id.editText6);
        passfield = findViewById(R.id.editText7);

        mAuth = FirebaseAuth.getInstance();
        RootRef = FirebaseDatabase.getInstance().getReference();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if(firebaseAuth.getCurrentUser()!=null){

                    Toast.makeText(Signin.this,"you Already Signin.",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(Signin.this,login.class));

                }

            }
        };


        si1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startsignIn();

            }
        });

        mAuth.addAuthStateListener(mAuthListener);
    }

    public void SigninActivity1()
    {
        Intent i1 = new Intent(Signin.this, login.class);
        startActivity(i1);
    }

    private void startsignIn(){
        String email = emailfield.getText().toString().trim();
        String password = passfield.getText().toString().trim();

        if(TextUtils.isEmpty(email)|| TextUtils.isEmpty(password)){

            Toast.makeText(Signin.this,"email or password are empty.",Toast.LENGTH_SHORT).show();

        }

        else if (password.length() < 6)

        {
            Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();

            return;

        }



        else{

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                String devicetoken = FirebaseInstanceId.getInstance().getToken();

                                String currentUserID = mAuth.getCurrentUser().getUid();
                                RootRef.child("Users").child(currentUserID).setValue("");

                                RootRef.child("Users").child(currentUserID).child("device_token")
                                        .setValue(devicetoken);

                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "createUserWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                Toast.makeText(Signin.this, "Authentication Success.",
                                        Toast.LENGTH_SHORT).show();
                                SigninActivity1();

                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                Toast.makeText(Signin.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();

                            }

                            // ...
                        }
                    });

        }
    }
}


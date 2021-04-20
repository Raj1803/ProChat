package com.example.prochat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

import static com.google.firebase.auth.PhoneAuthProvider.*;

public class Phonelogin extends AppCompatActivity {

    private Button sendvericationcodebutton,verifyuser;
    private EditText phone_number,verificationcode;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    private FirebaseAuth mAuth;

    private ProgressDialog loadingbar;

    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phonelogin);

        mAuth = FirebaseAuth.getInstance();

        sendvericationcodebutton = findViewById(R.id.send_verification_code);
        verifyuser = findViewById(R.id.verify_button);
        phone_number = findViewById(R.id.phone_number_input);
        verificationcode = findViewById(R.id.phone_verification_code_input);
        loadingbar = new ProgressDialog(this);


        sendvericationcodebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                String phonenumber = phone_number.getText().toString();

                if(TextUtils.isEmpty(phonenumber))
                {
                    Toast.makeText(Phonelogin.this, "PhoneNumber is required", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    loadingbar.setTitle("Phone Verification");
                    loadingbar.setMessage("Please Wait, we are authenticate your phone number.!");
                    loadingbar.setCanceledOnTouchOutside(false);
                    loadingbar.show();


                    getInstance().verifyPhoneNumber(
                            phonenumber,        // Phone number to verify
                            60,                 // Timeout duration
                            TimeUnit.SECONDS,   // Unit of timeout
                            Phonelogin.this,               // Activity (for callback binding)
                            callbacks);        // OnVerificationStateChangedCallbacks
                }

            }
        });



        verifyuser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendvericationcodebutton.setVisibility(View.INVISIBLE);
                phone_number.setVisibility(View.INVISIBLE);

                String verification_code = verificationcode.getText().toString();

                if(TextUtils.isEmpty(verification_code)){
                    Toast.makeText(Phonelogin.this, "Please Enter Verification Code !", Toast.LENGTH_SHORT).show();
                }

                else
                {
                    loadingbar.setTitle("Verification Code");
                    loadingbar.setMessage("Please Wait, we are authenticate your verification code.!");
                    loadingbar.setCanceledOnTouchOutside(false);
                    loadingbar.show();

                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, verification_code);
                    signInWithPhoneAuthCredential(credential);
                }



            }
        });


        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks(){

            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {

                loadingbar.dismiss();

                Toast.makeText(Phonelogin.this, "Invalid, PhoneNumber, Please Enter PhoneNumber with your country code..", Toast.LENGTH_SHORT).show();


                sendvericationcodebutton.setVisibility(View.VISIBLE);
                phone_number.setVisibility(View.VISIBLE);

                verifyuser.setVisibility(View.INVISIBLE);
                verificationcode.setVisibility(View.INVISIBLE);


            }

            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;

                loadingbar.dismiss();

                Toast.makeText(Phonelogin.this, "Code has sent, Please check and verify..!", Toast.LENGTH_SHORT).show();


                sendvericationcodebutton.setVisibility(View.INVISIBLE);
                phone_number.setVisibility(View.INVISIBLE);

                verifyuser.setVisibility(View.VISIBLE);
                verificationcode.setVisibility(View.VISIBLE);


                // ...
            }
        };
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            loadingbar.dismiss();
                            Toast.makeText(Phonelogin.this, "Log in Successfully", Toast.LENGTH_SHORT).show();
                            sendusertochatactivity();



                        } else {

                            String msg = task.getException().toString();
                            Toast.makeText(Phonelogin.this, "Error : " + msg, Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }

    private void sendusertochatactivity()
    {
        Intent chatintent = new Intent(Phonelogin.this,chat.class);
        startActivity(chatintent);
        finish();
    }

}

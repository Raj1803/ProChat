package com.example.prochat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    Timer t1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        t1 = new Timer();
        t1.schedule(new TimerTask() {
            @Override
            public void run() {
                Intent i1;
                i1 = new Intent(MainActivity.this,login.class);
                startActivity(i1);
                finish();

            }
        },3000);
    }
}

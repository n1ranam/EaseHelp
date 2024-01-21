package com.example.easehelp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.content.Intent;
import android.os.Handler;

public class first extends AppCompatActivity {
    private static int LOADING_TIME = 2000; // Adjust the loading time as needed (in milliseconds)


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(first.this, first.class);
                startActivity(intent);
                finish(); // Close the loading page
            }
        }, LOADING_TIME);

    }
}
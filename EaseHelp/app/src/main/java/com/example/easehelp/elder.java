package com.example.easehelp;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class elder extends AppCompatActivity {

    private Button selectServicesButton;
    private Button button2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_elder); // Corrected layout file name

        // Initialize buttons
        selectServicesButton = findViewById(R.id.ssbtn);
        button2 = findViewById(R.id.sbtn);

        // Set click listeners for buttons
        selectServicesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(elder.this, eld_services.class);
                startActivity(intent);
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Code to handle click on button2
            }
        });
    }
}

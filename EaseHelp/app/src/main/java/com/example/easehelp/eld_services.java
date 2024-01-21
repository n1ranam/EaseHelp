package com.example.easehelp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class eld_services extends AppCompatActivity {

    LinearLayout s1,s2,s3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eld_services);

        Intent intent = getIntent();
        String userphone = intent.getStringExtra("phonenum");
        String ulocation = intent.getStringExtra("location");

        FirebaseDatabase.getInstance().getReference().child("ActiveRequests").child(userphone).addValueEventListener(new ValueEventListener() {
            //for any snapshot if the acceptedVolunteer is equal to the current volunteer id then intent to activity
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Intent i=new Intent(eld_services.this,ElderServiceWaiting.class);
                    i.putExtra("userid",userphone);
                    startActivity(i);
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(eld_services.this, "Error Fetch", Toast.LENGTH_SHORT).show();
            }
        });
        s1=findViewById(R.id.service1);
        s2=findViewById(R.id.service2);
        s3=findViewById(R.id.service3);



        s1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(eld_services.this, elder_req.class);
                i.putExtra("serviceType", "Bill Payments");
                i.putExtra("userphone", userphone);
                i.putExtra("ulocation", ulocation);
                startActivity(i);
            }
        });

        s2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(eld_services.this, elder_req.class);
                i.putExtra("serviceType", "Medical Service");
                i.putExtra("userphone", userphone);
                i.putExtra("ulocation", ulocation);
                startActivity(i);
            }
        });

        s3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(eld_services.this, elder_req.class);
                i.putExtra("serviceType", "Akshaya Services");
                i.putExtra("userphone", userphone);
                i.putExtra("ulocation", ulocation);
                startActivity(i);
            }
        });
    }
}

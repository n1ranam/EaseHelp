package com.example.easehelp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.easehelp.model.ActiveRequests;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;

public class ElderServiceWaiting extends AppCompatActivity {
    ActiveRequests req =null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_elder_service_waiting);
        getReqObj();
        TextView signout=(TextView) findViewById(R.id.signoutBtn);
        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ElderServiceWaiting.this,MainActivity.class));
                finish();
            }
        });
    }

    public void playrec(View view) {
        MediaPlayer mediaPlayer = new MediaPlayer();

        try {
            mediaPlayer.setDataSource(req.getRecordingUrl());
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                if (mediaPlayer != null) {
                    mediaPlayer.release();
                    mediaPlayer = null;
                }
            }
        });
    }

    public void getReqObj() {
        String userid = getIntent().getStringExtra("userid");
        FirebaseDatabase.getInstance().getReference().child("ActiveRequests").child(userid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    req = dataSnapshot.getValue(ActiveRequests.class);

                    if(!req.getAcceptedVolunteer().equals("none"))
                    {
                        LinearLayout ll=findViewById(R.id.volunteer_details);
                        ll.setVisibility(View.VISIBLE);
                        vol_fill(req.getAcceptedVolunteer());

                    }
                    TextView otp=findViewById(R.id.otp);
                    TextView sts=findViewById(R.id.status);
                    TextView stype=findViewById(R.id.servicetype);
                    otp.setText(req.getOTP());
                    sts.setText(req.getRequestStatus());
                    stype.setText(req.getRequestType());
                }
                    else
                    {
                    }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
    public void vol_fill(String id){

                    //get value of a volunteer from the database
                    FirebaseDatabase.getInstance().getReference().child("users").child("Volunteers").child(id).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists())
                            {
                                String volname=dataSnapshot.child("name").getValue().toString();
                                String volphone=dataSnapshot.child("phone").getValue().toString();
                                TextView vol_name=findViewById(R.id.vol_name);
                                TextView vol_phone=findViewById(R.id.vol_phone);
                                vol_name.setText(volname);
                                vol_phone.setText(volphone);
                                Button callbtn=findViewById(R.id.call_volunteer_btn);
                                callbtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        //call a number
                                        Intent intent = new Intent(Intent.ACTION_DIAL);
                                        intent.setData(Uri.parse("tel:"+volphone));
                                        startActivity(intent);
                                    }
                                });
                            }
                            else
                            {
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
//

    }
}
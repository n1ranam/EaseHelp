package com.example.easehelp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.easehelp.model.ElderModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;

public class VolunteerAcceptance extends AppCompatActivity {
    TextView uname;
    TextView uloc;
    TextView uphone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volunteer_acceptance);
        String userid = getIntent().getStringExtra("userid");
        String serviceType = getIntent().getStringExtra("serviceType");
        String serviceLoc = getIntent().getStringExtra("location");
        String recordingUrl = getIntent().getStringExtra("recordingUrl");
        String volid = getIntent().getStringExtra("vol_id");
        String mywork = getIntent().getStringExtra("mywork");
        TextView serviceTypeView = (TextView) findViewById(R.id.stype);
        TextView serviceLocView = (TextView) findViewById(R.id.sloc);
        serviceTypeView.setText(serviceType);
        serviceLocView.setText(serviceLoc);
        Button uaudio = (Button) findViewById(R.id.s_audio);
        uaudio.setOnClickListener(view -> {
            MediaPlayer mediaPlayer = new MediaPlayer();

            try {
                mediaPlayer.setDataSource(recordingUrl);
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
        });
        FirebaseDatabase.getInstance().getReference().child("users").child("Elders").child(userid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    ElderModel elder = dataSnapshot.getValue(ElderModel.class);
                    uname = (TextView) findViewById(R.id.uname);
                    uloc = (TextView) findViewById(R.id.uloc);
                    uphone = (TextView) findViewById(R.id.uphone);
                    uname.setText(elder.getName());
                    uloc.setText(elder.getAddress());
                    uphone.setText(elder.getPhone());
                    Button call_user = (Button) findViewById(R.id.callu_btn);
                    call_user.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //dial the user number
                            Intent intent = new Intent(Intent.ACTION_DIAL);
                            intent.setData(Uri.parse("tel:"+elder.getPhone()));
                            startActivity(intent);
                        }
                    });
                } else {
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        Button acceptbtn = (Button) findViewById(R.id.acceptbtn);
        acceptbtn.setOnClickListener(view -> {
//            FirebaseDatabase.getInstance().getReference().child("ActiveRequests").child(userid).removeValue();
            String vol_id = getIntent().getStringExtra("vol_id");
            FirebaseDatabase.getInstance().getReference().child("ActiveRequests").child(userid).child("acceptedVolunteer").setValue(vol_id);
            FirebaseDatabase.getInstance().getReference().child("ActiveRequests").child(userid).child("requestStatus").setValue("in progress");
            LinearLayout udetails=(LinearLayout) findViewById(R.id.userdetails);
            udetails.setVisibility(View.VISIBLE);
            TextView signoutBtn=(TextView) findViewById(R.id.signoutBtn);
            signoutBtn.setVisibility(View.VISIBLE);
            signoutBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(VolunteerAcceptance.this, MainActivity.class);
                    startActivity(intent);
                }
            });
            acceptbtn.setText("Accepted");


        });
        
        if(mywork!=null)
        {
            acceptbtn.performClick();
        }

        Button otpfinish=(Button) findViewById(R.id.taskfinish);
        otpfinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DatabaseReference activeRequestsRef=FirebaseDatabase.getInstance().getReference().child("ActiveRequests").child(userid);
                activeRequestsRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            String otp = dataSnapshot.child("otp").getValue().toString();
                            EditText otpinput = (EditText) findViewById(R.id.otp_val);
                            if (otp.equals(otpinput.getText().toString())) {
                                FirebaseDatabase.getInstance().getReference().child("ActiveRequests").child(userid).removeValue();
                                activeRequestsRef.removeEventListener(this);
                                startActivity(new Intent(VolunteerAcceptance.this, MainActivity.class));
                                finish();
                                Toast.makeText(VolunteerAcceptance.this, "Request Completed", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(VolunteerAcceptance.this, "Wrong OTP", Toast.LENGTH_SHORT).show();
                            }

                        } else {
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }

                });



            }
        });
    }
}
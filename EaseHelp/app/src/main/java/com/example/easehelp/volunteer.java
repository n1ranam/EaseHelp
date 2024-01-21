package com.example.easehelp;

import static android.content.Intent.getIntent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.easehelp.model.ActiveRequests;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;

class CustomListAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<ActiveRequests> data; // Replace YourDataModel with your actual data model class
    String vol_id;
    public CustomListAdapter(Context context, ArrayList<ActiveRequests> data,String vol_id){
        this.context = context;
        this.data = data;
        this.vol_id=vol_id;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_layout, parent, false);

        }

        TextView serviceType = convertView.findViewById(R.id.serviceType);
        TextView serviceLoc = convertView.findViewById(R.id.serviceLocation);
        Button actionButton = convertView.findViewById(R.id.actionButton);
        actionButton.setText("Play Audio");
        ActiveRequests item = (ActiveRequests) getItem(position);
        serviceType.setText(item.getRequestType());
        serviceLoc.setText(item.getLocation());

        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MediaPlayer mediaPlayer = new MediaPlayer();

                try {
                    mediaPlayer.setDataSource(data.get(position).getRecordingUrl());
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
        });

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //get the value from the context intent
                Intent i=new Intent(context,VolunteerAcceptance.class);
                i.putExtra("userid",data.get(position).getUserid());
                i.putExtra("serviceType",data.get(position).getRequestType());
                i.putExtra("location",data.get(position).getLocation());
                i.putExtra("recordingUrl",data.get(position).getRecordingUrl());
                i.putExtra("vol_id",vol_id);
                context.startActivity(i);
            }
        });

        return convertView;
    }
}

public class volunteer extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volunteer);
        ListView listView = findViewById(R.id.vol_list); // Replace with your ListView's ID
        String vol_id = getIntent().getStringExtra("phonenum");
        FirebaseDatabase.getInstance().getReference().child("ActiveRequests").addValueEventListener(new ValueEventListener() {
            //for any snapshot if the acceptedVolunteer is equal to the current volunteer id then intent to activity

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        ActiveRequests req = snapshot.getValue(ActiveRequests.class);
                        if(req.getAcceptedVolunteer().equals(vol_id))
                        {
                            Intent i=new Intent(volunteer.this,VolunteerAcceptance.class);
                            i.putExtra("userid",req.getUserid());
                            i.putExtra("serviceType",req.getRequestType());
                            i.putExtra("location",req.getLocation());
                            i.putExtra("recordingUrl",req.getRecordingUrl());
                            i.putExtra("vol_id",vol_id);
                            i.putExtra("mywork","True");
                            startActivity(i);
                            finish();
                        }
                    }

                    ArrayList<ActiveRequests> h1 = new ArrayList<>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        ActiveRequests req = snapshot.getValue(ActiveRequests.class);
                        h1.add(req);
                    }
                    CustomListAdapter adapter = new CustomListAdapter(volunteer.this,h1,vol_id);
                    listView.setAdapter(adapter);
                }
                else
                {
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }

        });
        //get activerequests from firebase
        FirebaseDatabase.getInstance().getReference().child("ActiveRequests").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {

                    ArrayList<ActiveRequests> h1 = new ArrayList<>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        ActiveRequests req = snapshot.getValue(ActiveRequests.class);
                        h1.add(req);
                    }
                    CustomListAdapter adapter = new CustomListAdapter(volunteer.this,h1,vol_id);
                    listView.setAdapter(adapter);
                }
                else
                {
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }

        });

    }
}
package com.example.easehelp;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import android.os.Bundle;

import com.example.easehelp.model.ActiveRequests;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class elder_req extends AppCompatActivity {


    private static final int REQUEST_PERMISSION = 200;
    String outputFile;
    String recordingUrl;
    MediaRecorder mediaRecorder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_elder_req);

        Button recButton = findViewById(R.id.rec_btn); // Add this line
        Button finButton = findViewById(R.id.fin_btn);
        finButton.setEnabled(false);

        outputFile = getExternalCacheDir().getAbsolutePath() + "/recording.3gp";
        recButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isPermissionGranted()) {
                    startRecording();
                    recButton.setEnabled(false);
                    finButton.setEnabled(true);
                }
                else {
                    requestPermission();
                }
            }
        });

        finButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopRecording();
            }
        });
    }

    private void startRecording() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mediaRecorder.setOutputFile(outputFile);
        try {
            mediaRecorder.prepare();
            mediaRecorder.start(); // Start the recording
            Toast.makeText(this, "Recording started...", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            System.out.print(e);
            e.printStackTrace();
        }
    }

    private void stopRecording() {
        if (mediaRecorder != null) {
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
            Toast.makeText(this, "Recording stopped.", Toast.LENGTH_SHORT).show();

            //upload the recording to firebase
            Uri uri = Uri.fromFile(new File(outputFile));
            String filePath = "files/" + UUID.randomUUID().toString();
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();
            StorageReference riversRef = storageRef.child(filePath);

            riversRef.putFile(uri)
                    .addOnSuccessListener(taskSnapshot -> {
                        riversRef.getDownloadUrl().addOnSuccessListener(uri1 -> {
                            String downloadUrl = uri1.toString();
                            recordingUrl= downloadUrl;
                            Log.d("TAG", "onActivityResult: " + downloadUrl);
                            Toast.makeText(this, downloadUrl, Toast.LENGTH_SHORT).show();
                            String phone=getIntent().getStringExtra("userphone");
                            String serviceType=getIntent().getStringExtra("serviceType");
                            String ulocation=getIntent().getStringExtra("ulocation");

                            int randomPIN = (int)(Math.random()*9000)+1000;
                            String pin = String.valueOf(randomPIN);

                            ActiveRequests activeRequest=new ActiveRequests(phone,serviceType,recordingUrl,ulocation,pin);

                            FirebaseDatabase.getInstance().getReference("ActiveRequests").child(phone).setValue(activeRequest);

                            Intent intent=new Intent(elder_req.this,eld_services.class);
                            intent.putExtra("userid",phone);
                            startActivity(intent);
                            finish();
                        });
                    })
                    .addOnFailureListener(exception -> {
                        // Handle unsuccessful uploads
                        Log.e("TAG", "Upload failed: " + exception.getMessage(), exception);
                        Toast.makeText(this, "Upload failed: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                    });



        }
    }

    private boolean isPermissionGranted() {
        return (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                startRecording();
            } else {
                Toast.makeText(this, "Permission denied. Cannot record audio.", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
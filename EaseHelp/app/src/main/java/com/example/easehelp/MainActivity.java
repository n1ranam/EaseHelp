package com.example.easehelp;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    EditText usn, pswd;
    Button btn;
    TextView tv;
    RadioGroup rg;
    RadioButton rb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        usn=findViewById(R.id.phone);
        pswd=findViewById(R.id.password);
        btn=findViewById(R.id.lginbtn);
        tv=findViewById(R.id.tvu);
        rg=findViewById(R.id.usertype);
        tv.setPaintFlags(tv.getPaintFlags() |Paint.UNDERLINE_TEXT_FLAG);

        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, registration.class));
            }
        });
    }

    public void login_click(View view) {
        String phone=usn.getText().toString();
        String pass=pswd.getText().toString();
        int selectedId = rg.getCheckedRadioButtonId();
        if(selectedId==-1)
        {
            Toast.makeText(this, "Please select a user type", Toast.LENGTH_SHORT).show();
            return;
        }
        if(phone.isEmpty() || pass.isEmpty())
        {
            Toast.makeText(this, "Please enter all the fields", Toast.LENGTH_SHORT).show();
            return;
        }
        rb = (RadioButton) findViewById(selectedId);
        String usertype=rb.getText().toString();

        FirebaseDatabase.getInstance().getReference().child("users").child(usertype).child(phone).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    String password=dataSnapshot.child("password").getValue().toString();
                    if(password.equals(pass))
                    {
                        Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                        if(usertype.equals("Elders"))
                        {
                            Intent intent=new Intent(MainActivity.this,eld_services.class);
                            intent.putExtra("phonenum",phone);
                            intent.putExtra("location",dataSnapshot.child("address").getValue().toString());
                            startActivity(intent);
                            finish();
                        }
                        else
                        {
                            Intent intent=new Intent(MainActivity.this,volunteer.class);
                            intent.putExtra("phonenum",phone);
                            startActivity(intent);
                            finish();
                        }
                    }
                    else
                    {
                        Toast.makeText(MainActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(MainActivity.this, "User does not exist", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
}
package com.example.easehelp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.easehelp.model.ElderModel;
import com.example.easehelp.model.VolunteerModel;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class registration extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    public static final int CAMERA_REQUEST = 9999;
    private Spinner spinner;
    private Button submitButton;
    private ArrayAdapter<String> adapter;
    private Button ib;
    private String[] userTypes = {"Select UserType", "Elders", "Volunteers"};

    private EditText usernameEditText;
    private EditText emailEditText;
    private EditText dobEditText;
    private EditText addressEditText;
    private EditText phoneEditText;
    private EditText passwordEditText;

    String idImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        spinner = findViewById(R.id.spnr);
        submitButton = findViewById(R.id.sbtn);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, userTypes);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        ib = findViewById(R.id.Ibutton);

        // Initialize EditText fields
        usernameEditText = findViewById(R.id.rname);
        emailEditText = findViewById(R.id.remail);
        dobEditText = findViewById(R.id.rdob);
        addressEditText = findViewById(R.id.raddress);
        phoneEditText = findViewById(R.id.rphone);
        passwordEditText = findViewById(R.id.rpassword);

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String userType = parent.getItemAtPosition(position).toString();
        if ("Volunteers".equals(userType)) {
            ib.setVisibility(View.VISIBLE);
        } else {
            ib.setVisibility(View.GONE);
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    public void OpenCamera(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK && data != null) {
            //save image to gallery in full resolution
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            String imagePath = saveImageToGallery(bitmap, this);

            //uri from imagepath

            Uri uri = Uri.fromFile(new File(imagePath));
            String filePath = "files/" + UUID.randomUUID().toString();
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();
            StorageReference riversRef = storageRef.child(filePath);

            riversRef.putFile(uri)
                    .addOnSuccessListener(taskSnapshot -> {
                        // Upload successful
                        Toast.makeText(this, "File Uploaded", Toast.LENGTH_SHORT).show();
                        riversRef.getDownloadUrl().addOnSuccessListener(uri1 -> {
                            String downloadUrl = uri1.toString();
                            idImage= downloadUrl;
                            Log.d("TAG", "onActivityResult: " + downloadUrl);
                        });
                    })
                    .addOnFailureListener(exception -> {
                        // Handle unsuccessful uploads
                        Log.e("TAG", "Upload failed: " + exception.getMessage(), exception);
                        Toast.makeText(this, "Upload failed: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                    });

        }
    }

    private String saveImageToGallery(Bitmap bitmap, Context context) {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + ".jpg";
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imageFile = new File(storageDir, imageFileName);

        try (FileOutputStream out = new FileOutputStream(imageFile)) {
            // Compress the bitmap and save in jpg format max quality
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Return the absolute path of the saved image
        return imageFile.getAbsolutePath();
    }

    // Validation method for Date of Birth
    private boolean validateDateOfBirth(String dob) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        dateFormat.setLenient(false); // Disable leniency to ensure strict date validation

        Date currentDate = new Date(); // Get the current date

        try {
            // Parse the entered date of birth string to a Date object
            Date enteredDate = dateFormat.parse(dob);

            // Check if the entered date is in the past
            if (enteredDate.after(currentDate)) {
                return false; // Date of birth is in the future
            }

            // Calculate age based on the entered date of birth
            Calendar dobCal = Calendar.getInstance();
            dobCal.setTime(enteredDate);
            Calendar currentCal = Calendar.getInstance();
            currentCal.setTime(currentDate);

            int age = currentCal.get(Calendar.YEAR) - dobCal.get(Calendar.YEAR);
            if (currentCal.get(Calendar.DAY_OF_YEAR) < dobCal.get(Calendar.DAY_OF_YEAR)) {
                age--;
            }

            // Check if age is within a specific range (e.g., 18 to 100 years old)
            if (age < 18 || age > 100) {
                return false; // Age is not within the specified range
            }

            return true; // Date of birth is valid
        } catch (ParseException e) {
            e.printStackTrace();
            return false; // Error occurred while parsing date
        }
    }

    // Validation method for all fields
    private boolean validateForm() {
        boolean valid = true;
        
        String username = usernameEditText.getText().toString();
        String email = emailEditText.getText().toString();
        String dob = dobEditText.getText().toString();
        String address = addressEditText.getText().toString();
        String phone = phoneEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if(spinner.getSelectedItem().toString()=="Select UserType"){
            Toast.makeText(this, "Please select a user type", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (username.isEmpty() || TextUtils.isDigitsOnly(username)) {
            usernameEditText.setError("Username is required");
            valid = false;
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Enter a valid email address");
            valid = false;
        }

        if (dob.isEmpty()) {
            dobEditText.setError("Date of Birth is required");
            valid = false;
        } else if (!validateDateOfBirth(dob)) {
            dobEditText.setError("Invalid Date of Birth or age is not within the allowed range");
            valid = false;
        } else {
            dobEditText.setError(null); // Clear error when the date of birth is valid
        }

        if (address.isEmpty()) {
            addressEditText.setError("Address is required");
            valid = false;
        }

        if (phone.isEmpty()) {
            phoneEditText.setError("Phone number is required");
            valid = false;
        } else if (phone.length() < 10) {
            phoneEditText.setError("Enter a valid phone number");
            valid = false;
        }

        // Advanced Password Validation
        if (password.isEmpty() || password.length() < 6 || !isValidPassword(password)) {
            passwordEditText.setError("Password must be at least 6 characters containing at least one symbol, one number, and start with a capital letter");
            valid = false;
        }

        return valid;
    }

    private boolean isValidPassword(String password) {
        // Password should contain at least 6 characters, one symbol, one number, and start with a capital letter
//        String regex = "^(?=.*[0-9])(?=.*[!@#$%^&*()-_=+{}|\\[\\]:;\"'<>,.?])(?=.*[A-Z]).{6,}$";
//        password should contain at least 6 characters ,one symbol,one number
        String regex = "^(?=.*[0-9])(?=.*[!@#$%^&*()-_=+{}|\\[\\]:;\"'<>,.?]).{6,}$";
        return password.matches(regex);
    }

    public void registeruser(View view) {
        if (validateForm()) {

            String spinneritem= spinner.getSelectedItem().toString();
            if(spinneritem=="Elders"){
                String name = usernameEditText.getText().toString();
                String address = addressEditText.getText().toString();
                String phone = phoneEditText.getText().toString();
                String email = emailEditText.getText().toString();
                String dob = dobEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                ElderModel dataModel = new ElderModel(name, address, dob, phone, email, password);
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("users");
                myRef.child(spinneritem).child(phone).setValue(dataModel);

                Intent intent=new Intent(this,MainActivity.class);
                startActivity(intent);
                finish();
            }
            else if(spinneritem=="Volunteers"){
                String name = usernameEditText.getText().toString();
                String address = addressEditText.getText().toString();
                String phone = phoneEditText.getText().toString();
                String email = emailEditText.getText().toString();
                String dob = dobEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                VolunteerModel vol = new VolunteerModel(name, address, dob, phone, email, password, idImage);
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("users");
                myRef.child(spinneritem).child(phone).setValue(vol);
                Intent intent=new Intent(this,MainActivity.class);
                startActivity(intent);
                finish();

            }
            else{
                Toast.makeText(this, "Please select a user type", Toast.LENGTH_SHORT).show();
            }

        }
    }

}

package com.example.teproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;

public class Register extends AppCompatActivity {

    EditText mName, mEmail, mPassword, mBranch, mRegistrationID, mRollno;
    Button mSignUpBtn, mLoginBtn;
    FirebaseAuth fAuth;
    ProgressBar progressBar;
    RadioGroup radioGroup;
    RadioButton radioButton;

    FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);

        mName = findViewById(R.id.name);
        mEmail = findViewById(R.id.email);
        mBranch = findViewById(R.id.branch);
        mPassword = findViewById(R.id.password);
        mLoginBtn = findViewById(R.id.rloginbutn);
        mSignUpBtn = findViewById(R.id.rsignupbtn);
        mRegistrationID = findViewById(R.id.registrationID);
        mRollno = findViewById(R.id.rollnostudent);
        radioGroup = findViewById(R.id.rdgroup);
        progressBar = findViewById(R.id.progressBar);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();


        if(fAuth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }

        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Login.class));
                finish();
            }
        });

        mSignUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmail.getText().toString().trim();
                String pass = mPassword.getText().toString().trim();
                String name = mName.getText().toString();
                String regID = mRegistrationID.getText().toString();
                String branch = mBranch.getText().toString();
                int radio = radioGroup.getCheckedRadioButtonId();

                radioButton = findViewById(radio);

                Log.d("radio", "radio button checked is: "+ radio);


                if(TextUtils.isEmpty(email)){
                    mEmail.setError("Email is required.");
                    return;
                }
                if(TextUtils.isEmpty(pass)){
                    mPassword.setError("Password is required.");
                    return;
                }
                if(pass.length() < 5){
                    mPassword.setError("Password must be >= 5 characters");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                // register user in firebase
                fAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(Register.this, "Account created", Toast.LENGTH_SHORT).show();
                            DocumentReference documentReference = fStore.document("year/"+year+"- "+(year+1)+"/Users/Users");
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        }else{
                            Toast.makeText(Register.this, "Error..! " + task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }
                });



            }
        });



    }


}
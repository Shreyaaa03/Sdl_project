package com.example.teproject;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {

    EditText mName, mEmail, mPassword, mBranch,mRegistrationID, mRollno;
    Button mSignUpBtn, mLoginBtn;
    FirebaseAuth fAuth;
    ProgressBar progressBar;
    RadioGroup radioGroup;
    RadioButton radioButton;
    //  Spinner mBranch;
    String rollno;
    Boolean role;
    FirebaseFirestore fStore;
    private static final String TAG = "Register";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);


        mName = findViewById(R.id.name);
        mEmail = findViewById(R.id.email);
        mPassword = findViewById(R.id.password);
        mLoginBtn = findViewById(R.id.rloginbutn);
        mSignUpBtn = findViewById(R.id.rsignupbtn);
        mRegistrationID = findViewById(R.id.registrationID);
        mRollno = findViewById(R.id.rollnostudent);
        radioGroup = findViewById(R.id.rdgroup);
        progressBar = findViewById(R.id.progressBar);
        mBranch = findViewById(R.id.branchspinner);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        if(fAuth.getCurrentUser() != null){
            // user already loggen in...Directly send to main screen
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }

//        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.Branches, android.R.layout.simple_spinner_item);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        mBranch.setAdapter(adapter);
//        mBranch.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) this);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                radioButton = findViewById(checkedId);
                String roletext = radioButton.getText().toString();
                if (roletext.equals("Student")) {
                    mRollno.setVisibility(View.VISIBLE);
                    role = true;


                } else if(roletext.equals("Teacher")){
                    mRollno.setVisibility(View.INVISIBLE);
                    role = false;
                }
            }
        });
        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // user has already an account...Start login activity
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
                String regID = mRegistrationID.getText().toString().toUpperCase();
                String branch = mBranch.getText().toString();

                if(role){
                    rollno = mRollno.getText().toString();
                }
                //     String branch = mBranch.getItemAtPosition(i).toString();

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
                if(regID.length() != 11){
                    mRegistrationID.setError("Wrong format");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                // register user in firebase
                fAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            // AUTHENTICATION SUCCESSFUL
                            Toast.makeText(Register.this, "Account created", Toast.LENGTH_SHORT).show();
                            // new account created!
                            Intent intent = new Intent(getApplicationContext(), MyDomain.class);
                            intent.putExtra("activity", TAG);
                            startActivity(intent);

                            DocumentReference docR_c2k = fStore.document("year/"+year+"- "+(year+1)+"/IDS/"+fAuth.getCurrentUser().getUid());
                            Map<String, Object> idandc2k = new HashMap<>();
                            idandc2k.put("RegID",regID);
                            docR_c2k.set(idandc2k);

                            // ADD DATA TO FIRESTORE
                            DocumentReference docR = fStore.document("year/"+year+"- "+(year+1)+"/Users/"+regID);

                            //     DocumentReference docref = fStore.collection("Year").document(year+"-"+(year+1)).collection("Users").document(regID);
                            //     DocumentReference docr = fStore.collection("Users").document(regID);
                            Map<String, Object> datatosave = new HashMap<>();

                            datatosave.put("Name", name);
                            if(role){
                                datatosave.put("Role", true);
                                datatosave.put("RollNo", rollno);
                            } else{
                                datatosave.put("Role", false);
                                datatosave.put("RollNo", "N.A.");
                            }
                            datatosave.put("Email", email);
                            datatosave.put("Branch", branch);
                            datatosave.put("Password", pass);
                            datatosave.put("GroupID", "N.A.");
                            datatosave.put("PhoneNo", "N.A.");
                            datatosave.put("Linkedin", "N.A.");
                            datatosave.put("Github", "N.A.");
                            datatosave.put("Resume", "N.A.");

                            datatosave.put("RegistrationID", regID);
                            docR.set(datatosave).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Log.d("Roll", "rollno: "+ rollno);
                                        Log.d("Hooray", "Task successful");
                                    } else{
                                        Log.d("Error", "There was an error!");
                                    }
                                }
                            });
                        }else{
                            // AUTHENTICATION UNSUCCESSFUL
                            Toast.makeText(Register.this, "Error..! " + task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

    }


    @Override
    public void onBackPressed() {

    }




}

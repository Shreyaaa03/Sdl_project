package com.example.teproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static com.google.firebase.firestore.FieldValue.arrayUnion;

public class joingroup extends AppCompatActivity {

    private TextView mCreateGroupText;
    private Button mCreateGrpBtn, mJoinGrpBtn, mSkipBtn;
    private Random rand;
    private EditText mJoinGroupTxt;
    private int year, codeint;
    final ArrayList<Integer> codeArr = new ArrayList<>();
    private String fireRegID, userUid, code;
    FirebaseFirestore fStore;
    FirebaseAuth fAuth;
    private DocumentReference docRef, docRef2;
    private boolean status, exist, fireRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joingroup);

        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);

        mCreateGrpBtn = findViewById(R.id.creategroupbtn);
        mJoinGrpBtn = findViewById(R.id.joingroupbtn);
        mCreateGroupText = findViewById(R.id.groupCodetext);
        mJoinGroupTxt = findViewById(R.id.joingrptext);
        mSkipBtn = findViewById(R.id.skipbtn);
        rand = new Random();

        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        FirebaseUser user = fAuth.getCurrentUser();
        userUid =  user.getUid();

        String userEmail = user.getEmail();

        getIDS();

        if(true){
            mCreateGrpBtn.setEnabled(true);
        } else{
            mCreateGrpBtn.setEnabled(false);
        }

        mCreateGrpBtn. setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                codeint = rand.nextInt((9999 - 100) + 1) + 10;
                code = Integer.toString(codeint);
                docRef = fStore.document("year/"+year+"- "+(year+1)+"/Groups/"+code);
                docRef2 = fStore.document("year/"+year+"- "+(year+1)+"/Users/"+fireRegID);

                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        while(documentSnapshot.exists()){
                            codeint = rand.nextInt((9999 - 100) + 1) + 10;
                            code = Integer.toString(codeint);
                        }
                        if(documentSnapshot.exists() == false){
                            mCreateGrpBtn.setEnabled(false);
                            mJoinGrpBtn.setEnabled(false);
                            Log.d("TAG", "Reg is: "+ fireRegID);

                            mCreateGroupText.setVisibility(View.VISIBLE);
                            mCreateGroupText.setText("Group code: "+code + "\n" +
                                    "Share it with group members only!!");

                            addGroupID();

                        }
                    }
                });
            }
        });

        mSkipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });

        mJoinGrpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                code = mJoinGroupTxt.getText().toString().trim();
                docRef = fStore.document("year/"+year+"- "+(year+1)+"/Groups/"+code);
                docRef2 = fStore.document("year/"+year+"- "+(year+1)+"/Users/"+fireRegID);
                Log.d("TAG", "code is: "+ code);
                checkIfgroupExists();
                if(exist){
                    addGroupID();
                }

            }
        });

    }

    void addMemberToGrp(){

        Map<String, Object> datatosave2 = new HashMap<>();
        if(fireRole){
            datatosave2.put("Members", arrayUnion(fireRegID));
        } else {
            datatosave2.put("MentorID", fireRegID);
        }

        docRef.set(datatosave2, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Log.d("TAG", "Member successfully added");
                    Toast.makeText(joingroup.this, "You're a member of team: "+code, Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), joingroup.class));
                } else{
                    Log.d("TAG", "Member not added!");
                    Toast.makeText(joingroup.this, "You're in another team", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    void addGroupID(){

        docRef2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                if(document.exists()){
                    String groupid = document.getString("GroupID");
                    if(groupid.equals("N.A.")){
                        status = true;
                        Map<String, Object> datatosave = new HashMap<>();
                        datatosave.put("GroupID", code);

                        docRef2.update(datatosave).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Log.d("Hooray", "GroupID added successfully");
                                }else{
                                    Log.d("Error", "There was an error!");
                                }
                            }
                        });
                        addMemberToGrp();
                    } else{
                        status = false;
                        Log.d("TAG", "Already a group member!");
                    }
                }
            }
        });

    }
    void checkIfgroupExists(){
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                if(document.exists()){
                    exist = true;
                } else{
                    exist = false;
                    Toast.makeText(joingroup.this, "Invalid group code", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    void getRegID(){
        DocumentReference dRef = fStore.document("year/"+year+"- "+(year+1)+"/Users/"+fireRegID);

        dRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    fireRegID = document.getString("RegistrationID");
                    fireRole = document.getBoolean("Role");
                    Log.d("TAG", "data => " + document.getData());
                    Log.d("TAG", "ROle is: "+ fireRole);
                } else {
                    Log.d("TAG", "Error getting documents: ", task.getException());
                }

            }
        });
    }
    void getIDS(){
        DocumentReference dRef_1 = fStore.document("year/"+year+"- "+(year+1)+"/IDS/"+userUid);
        dRef_1.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                if(document.exists()){
                    fireRegID = document.getString("RegID");
                    Log.d("TAG", "RegID: "+ fireRegID);
                    getRegID();

                } else{
                    Log.d("TAG", "Error fetching the document");
                }
            }
        });
    }
}



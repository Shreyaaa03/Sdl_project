package com.example.teproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.Map;


public class GroupProfile extends AppCompatActivity {

    FirebaseFirestore fStore;
    FirebaseAuth fAuth;
    String RegID, GroupID = "";
    TextView group_id, problem_statement, mentor_id, member1, techStack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_profile);

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        group_id = findViewById(R.id.groupid);
        problem_statement = findViewById(R.id.problem_statement_id);
        mentor_id = findViewById(R.id.mentor_id);
        member1 = findViewById(R.id.member1);
        techStack = findViewById(R.id.tech_stack_id);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        DocumentReference docR = fStore.document("year/"+year+"- "+(year+1)+"/IDS/"+fAuth.getCurrentUser().getUid());

        docR.get().addOnSuccessListener(this, new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    RegID = documentSnapshot.getString("RegID");
                    tp(year);
                }

            }
        });

    }

    void tp(int year){
        DocumentReference docR_2 = fStore.document("year/"+year+"- "+(year+1)+"/Users/"+RegID);
        Log.d("check1", "check1 - doc");
        docR_2.get().addOnSuccessListener(this, new OnSuccessListener<DocumentSnapshot>() {

            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()) {
                    Log.d("check2", "check2 - exists");
                    GroupID = documentSnapshot.getString("GroupID");
                    group(year, GroupID);

                }
            }
        });
    }


    void group(int year, String GroupID){
        DocumentReference docR_3 = fStore.document("year/"+year+"- "+(year+1)+"/Groups/"+GroupID);
        Log.d("check1", "check1 - doc");
        docR_3.get().addOnSuccessListener(this, new OnSuccessListener<DocumentSnapshot>() {

            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()) {
                    Log.d("check2", "check2 - exists");
                    group_id.setText(GroupID+"\n");
                    mentor_id.setText(documentSnapshot.getString("MentorID")+"\n");
                    problem_statement.setText(documentSnapshot.getString("ProblemStatement")+"\n");

                    Map<String, Object> map = documentSnapshot.getData();
                    for (Map.Entry<String, Object> entry : map.entrySet()) {
                        if (entry.getKey().equals("Members")) {
                            String mem = (entry.getValue().toString());
                            mem = mem.substring(1, mem.length()-1);
                            String[] members = mem.split(", ", -2);

                            for (String a : members){
                                 member1.append(a+"\n");
                            }

                        }
                    }

                    Map<String, Object> map1 = documentSnapshot.getData();
                    for (Map.Entry<String, Object> entry : map1.entrySet()) {
                        if (entry.getKey().equals("TechStack")) {
                            String tech = (entry.getValue().toString());
                            tech = tech.substring(1, tech.length()-1);
                            String[] members = tech.split(", ", -2);

                            for (String a : members){
                                techStack.append(a+"\n");
                            }

                        }
                    }


                }
            }
        });
    }





}
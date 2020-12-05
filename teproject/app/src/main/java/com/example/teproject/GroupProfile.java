package com.example.teproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.gson.Gson;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static com.google.firebase.firestore.FieldValue.arrayRemove;


public class GroupProfile extends AppCompatActivity {

    FirebaseFirestore fStore;
    FirebaseAuth fAuth;
    String RegID, GroupID = "";
    TextView group_id, problem_statement, mentor_id, member1, techStack;
    Button mLeaveGrpbtn, edit_group;
    DocumentReference docR, docR_2,docR_3;
    private boolean mRole;
    private int year;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_profile);

        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);

        group_id = findViewById(R.id.groupid);
        problem_statement = findViewById(R.id.problem_statement_id);
        mentor_id = findViewById(R.id.mentor_id);
        member1 = findViewById(R.id.member1);
        techStack = findViewById(R.id.tech_stack_id);
   //     mLeaveGrpbtn = findViewById(R.id.leavegrpBtn);
        edit_group = findViewById(R.id.edit_group);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        // getting the caller activity/fragment
        Intent groupIntent = getIntent();
        String caller = groupIntent.getStringExtra("activity");
        Log.d("Caller", caller);

        if (caller.equals("MainActivity")) {
            docR = fStore.document("year/"+year+"- "+(year+1)+"/IDS/"+fAuth.getCurrentUser().getUid());

            docR.get().addOnSuccessListener(this, new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if(documentSnapshot.exists()){
                        RegID = documentSnapshot.getString("RegID");
                        tp(year);
                    }
                }
            });
        } else{
            // extract other details from intent
            Gson gson = new Gson();
            GroupsOverview groupsOverview = gson.fromJson(groupIntent.getStringExtra("groupsOverView"), GroupsOverview.class);
            String groupId = groupIntent.getStringExtra("groupId");

            // hide the edit button
            edit_group.setVisibility(View.GONE);
            // now setting the values of components:
            group_id.setText(groupId);
            if(groupsOverview.getMentorID() != null)
                mentor_id.setText(groupsOverview.getMentorID());
            if(groupsOverview.getProblemStatement() != null)
                problem_statement.setText(groupsOverview.getProblemStatement());
            if(groupsOverview.getMembers() != null) {
                String mem = (groupsOverview.getMembers().toString());
                mem = mem.substring(1, mem.length()-1);
                String[] members = mem.split(", ", -2);

                for (String a : members){
                    member1.append(a+"\n");
                }
            }
            if(groupsOverview.getTechStack() != null) {
                String tech = (groupsOverview.getTechStack().toString());
                tech = tech.substring(1, tech.length()-1);
                String[] members = tech.split(", ", -2);

                for (String a : members){
                    if (!a.equals("null")){
                        techStack.append(a+"\n");
                    }

                }
            }
        }


//        mLeaveGrpbtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Log.d("TAG", "RegID: "+ RegID);
//                Log.d("TAG", "YEar: "+year);
//                Log.d("TAG", "GroupID: "+GroupID);
//             //   removeFromGrp();
//            }
//        });


        edit_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), EditGroup.class));
            }
        });


    }
//
//    void removeFromGrp(){
//        Map<String, Object>datatosave = new HashMap<>();
//
//        if(mRole){
//            datatosave.put("Members", arrayRemove(RegID));
//        } else {
//            datatosave.put("MentorID", "");
//        }
//
//        docR_3 = fStore.document("year/"+year+"- "+(year+1)+"/Groups/"+GroupID);
//        docR_3.set(datatosave, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//                if(task.isSuccessful()){
//                    Toast.makeText(GroupProfile.this, "You're no longer a part of the group", Toast.LENGTH_SHORT).show();
//                    removeGrpIDfromUser();
//                } else {
//                    Toast.makeText(GroupProfile.this, "Task failed", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//    }
//
//    void removeGrpIDfromUser(){
//        docR_2.update("GroupID", "N.A.").addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//                if(task.isSuccessful()){
//                    Log.d("TAG", "Removed group id from profile");
//                } else{
//                    Log.d("TAG", "Failed to remove group id from profile");
//                }
//            }
//        });
//    }


    void tp(int year){
        docR_2 = fStore.document("year/"+year+"- "+(year+1)+"/Users/"+RegID);
        Log.d("check1", " in tp check1 - doc");
        docR_2.get().addOnSuccessListener(this, new OnSuccessListener<DocumentSnapshot>() {

            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()) {
                    Log.d("check2", " in tp check2 - exists");
                    GroupID = documentSnapshot.getString("GroupID");
                    mRole  = documentSnapshot.getBoolean("Role");
                    Log.d("TAG", "GroupID"+GroupID);

                    if(!GroupID.isEmpty()){
                        group(year, GroupID);
                    }
                }
            }
        });
    }

    void members(String mem){
        docR_2 = fStore.document("year/"+year+"- "+(year+1)+"/Users/"+mem);
        docR_2.get().addOnSuccessListener(this, new OnSuccessListener<DocumentSnapshot>() {

            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String name = documentSnapshot.getString("Name").toString();
                member1.append(mem+ " - "+ name+ "\n");
            }
        });

    }

    void getMentor(String mem){
        docR_2 = fStore.document("year/"+year+"- "+(year+1)+"/Users/"+mem);
        docR_2.get().addOnSuccessListener(this, new OnSuccessListener<DocumentSnapshot>() {

            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String name = documentSnapshot.getString("Name").toString();
                mentor_id.setText(mem+ " - "+ name+ "\n");
            }
        });

    }

    void group(int year, String GroupID){
        docR_3 = fStore.document("year/"+year+"- "+(year+1)+"/Groups/"+GroupID);
        Log.d("check1", "in group check1 - doc");
        docR_3.get().addOnSuccessListener(this, new OnSuccessListener<DocumentSnapshot>() {

            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()) {
                    Log.d("check2", " in group check2 - exists");
                    group_id.setText(GroupID);
            //        mLeaveGrpbtn.setVisibility(View.VISIBLE);
                    String mentor = (documentSnapshot.getString("MentorID")).toString();
                    getMentor(mentor);
                    problem_statement.setText(documentSnapshot.getString("ProblemStatement"));

                    Map<String, Object> map = documentSnapshot.getData();
                    for (Map.Entry<String, Object> entry : map.entrySet()) {
                        if (entry.getKey().equals("Members")) {
                            String mem = (entry.getValue().toString());
                            mem = mem.substring(1, mem.length()-1);
                            String[] members = mem.split(", ", -2);

                            for (String a : members){
                               //  member1.append(a+"\n");
                                a.trim();
                                members(a);
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
                                if (!a.equals("null")){
                                    techStack.append(a+"\n");
                                }

                            }

                        }
                    }


                }
            }
        });
    }





}
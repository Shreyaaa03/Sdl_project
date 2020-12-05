package com.example.teproject;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.Map;


public class HomeFragment extends Fragment {
    FirebaseFirestore fStore;
    FirebaseAuth fAuth;
    String RegID, GroupID = "";
    TextView group_id, problem_statement, mentor_id, member1, techStack;
    Button mLeaveGrpbtn, edit_group;
    DocumentReference docR, docR_2,docR_3;
    private boolean mRole;
    private int year;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);




        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);

        group_id = view.findViewById(R.id.groupid);
        problem_statement = view.findViewById(R.id.problem_statement_id);
        mentor_id = view.findViewById(R.id.mentor_id);
        member1 = view.findViewById(R.id.member1);
        techStack = view.findViewById(R.id.tech_stack_id);
        edit_group = view.findViewById(R.id.edit_group);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        edit_group.setVisibility(View.GONE);
        docR = fStore.document("year/"+year+"- "+(year+1)+"/IDS/"+fAuth.getCurrentUser().getUid());

        docR.get().addOnSuccessListener(getActivity(), new OnSuccessListener<DocumentSnapshot>() {
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
        docR_2 = fStore.document("year/"+year+"- "+(year+1)+"/Users/"+RegID);
        Log.d("check1", " in tp check1 - doc");
        docR_2.get().addOnSuccessListener(getActivity(), new OnSuccessListener<DocumentSnapshot>() {

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
        docR_2.get().addOnSuccessListener(getActivity(), new OnSuccessListener<DocumentSnapshot>() {

            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String name = documentSnapshot.getString("Name").toString();
                member1.append(mem+ " - "+ name+ "\n");
            }
        });

    }

    void getMentor(String mem){
        docR_2 = fStore.document("year/"+year+"- "+(year+1)+"/Users/"+mem);
        docR_2.get().addOnSuccessListener(getActivity(), new OnSuccessListener<DocumentSnapshot>() {

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
        docR_3.get().addOnSuccessListener(getActivity(), new OnSuccessListener<DocumentSnapshot>() {

            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()) {
                    Log.d("check2", " in group check2 - exists");
                    group_id.setText(GroupID);
                    //        mLeaveGrpbtn.setVisibility(View.VISIBLE);
                    String mentor = (documentSnapshot.getString("MentorID")).toString();
                    if (mentor != null)
                         getMentor(mentor);
                    problem_statement.setText(documentSnapshot.getString("ProblemStatement"));

                    Map<String, Object> map = documentSnapshot.getData();
                    for (Map.Entry<String, Object> entry : map.entrySet()) {
                        if (entry.getKey().equals("Members")) {
                            String mem = (entry.getValue().toString());
                            mem = mem.substring(1, mem.length()-1);
                            String[] members = mem.split(", ", -2);

                            for (String a : members){
                               // member1.append(a+"\n");
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





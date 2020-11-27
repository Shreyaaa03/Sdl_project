package com.example.teproject;

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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class HomeFragment extends Fragment {

    private TextView mCreateGroupText;
    private Button mCreateGrpBtn, mJoinGrpBtn;
    private Random rand;
    private int code, year;
    final ArrayList<Integer> codeArr = new ArrayList<>();
    private String fireRegID, userUid;
    FirebaseFirestore fStore;
    FirebaseAuth fAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);

        mCreateGrpBtn = v.findViewById(R.id.creategroupbtn);
        mJoinGrpBtn = v.findViewById(R.id.joingroupbtn);
        mCreateGroupText = v.findViewById(R.id.groupCodetext);
        rand = new Random();

        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        FirebaseUser user = fAuth.getCurrentUser();
        userUid =  user.getUid();

        String userEmail = user.getEmail();

        getIDS();

        mCreateGrpBtn. setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    code = rand.nextInt((9999 - 100) + 1) + 10;
                    DocumentReference docRef = fStore.document("year/"+year+"- "+(year+1)+"/Groups/"+code);
                    docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            while(documentSnapshot.exists()){
                                code = rand.nextInt((9999 - 100) + 1) + 10;
                            }
                            if(documentSnapshot.exists() == false){
                                mCreateGrpBtn.setEnabled(false);
                                mJoinGrpBtn.setEnabled(false);
                                Map<String, Object>datatosave2 = new HashMap<>();
                                datatosave2.put("Members", Arrays.asList(fireRegID));

                                docRef.set(datatosave2, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Log.d("TAG", "Member successfully added");
                                        } else{
                                            Log.d("TAG", "Member not added!");
                                        }
                                    }
                                });
                            }
                        }
                    });

                Log.d("TAG", "Reg is: "+ fireRegID);

                mCreateGroupText.setVisibility(View.VISIBLE);
                mCreateGroupText.setText("Group code: "+code + "\n" +
                        "Share it with group members only!!");

             //   DocumentReference docRef = fStore.document("year/"+year+"- "+(year+1)+"/Groups/"+code);
                DocumentReference docRef2 = fStore.document("year/"+year+"- "+(year+1)+"/Users/"+fireRegID);

                Map<String, Object> datatosave = new HashMap<>();
                datatosave.put("GroupID", code);

                docRef2.update(datatosave).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Log.d("Hooray", "Task successful");
                        }else{
                            Log.d("Error", "There was an error!");
                        }
                    }
                });



            }
        });

        return v;

    }

    void getRegID(){
        DocumentReference dRef = fStore.document("year/"+year+"- "+(year+1)+"/Users/"+fireRegID);

        dRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    fireRegID = document.getString("RegistrationID");
                    Log.d("TAG", "data => " + document.getData());
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

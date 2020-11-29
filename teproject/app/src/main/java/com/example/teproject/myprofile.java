package com.example.teproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class myprofile extends AppCompatActivity {

    FirebaseFirestore fStore;
    FirebaseAuth fAuth;
    String RegID;
    TextView fullname, emailid, phone, branch, rollno, role, groupid, linkedin, github, resume;
    Button editProfBtn;
    TextView domains;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myprofile);

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        fullname = findViewById(R.id.fullname);
        emailid = findViewById(R.id.emailid);
        phone = findViewById(R.id.phone);
        branch = findViewById(R.id.branch);
        rollno = findViewById(R.id.rollno);
        role = findViewById(R.id.role);
        groupid = findViewById(R.id.groupid);
        linkedin = findViewById(R.id.linkedin);
        github = findViewById(R.id.github);
        resume = findViewById(R.id.resume);
        domains = findViewById(R.id.domaintext);

        editProfBtn = findViewById(R.id.editprofile);

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

        editProfBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), editprofile.class));
                finish();
            }
        });

    }
    void tp(int year){
        DocumentReference docR_2 = fStore.document("year/"+year+"- "+(year+1)+"/Users/"+RegID);
        Log.d("helllllllllllllll", "gg");
        docR_2.get().addOnSuccessListener(this, new OnSuccessListener<DocumentSnapshot>() {

            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()) {
                    Log.d("taggggggggggggg", "gg");
                    fullname.setText(documentSnapshot.getString("Name"));
                    Log.d("taggggggggggggg", "gg");
                    emailid.setText(documentSnapshot.getString("Email"));
                    branch.setText(documentSnapshot.getString("Branch"));
                    rollno.setText(documentSnapshot.getString("RollNo"));
                    if(documentSnapshot.getBoolean("Role") == true){
                        role.setText("Student");
                    }
                    else{
                        role.setText("Teacher");
                    }
                    groupid.setText(documentSnapshot.getString("GroupID"));
                    phone.setText(documentSnapshot.getString("PhoneNo"));
                    linkedin.setText(documentSnapshot.getString("Linkedin"));
                    github.setText(documentSnapshot.getString("Github"));
                    resume.setText(documentSnapshot.getString("Resume"));

                    Map<String , ArrayList<String>> subDomains = new HashMap<>();
                    subDomains = (Map<String , ArrayList<String>>) documentSnapshot.get("Domains");
                    String s = "";
                    if(subDomains==null){
                        Log.d("hiiiiiii","hi");
                    }
                    else {
                        for (Map.Entry m: subDomains.entrySet()){
                            String dname = m.getKey().toString();
                            ArrayList<String> snames = (ArrayList<String>) m.getValue();
                            s += dname + " : ";
                            for(int i=0; i< snames.size(); i++ ){
                                s += snames.get(i)+"   ";
                            }
                            s += "\n\n";
                        }
                        Log.d("hii", s);
                        domains.setText(s);
                    }
                }
            }
        });
    }
}


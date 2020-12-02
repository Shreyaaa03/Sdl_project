package com.example.teproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class editprofile extends AppCompatActivity {

    TextView email, phone, linkedin, github, resume;
    Button savechanges;

    FirebaseFirestore fStore;
    FirebaseAuth fAuth;

    String RegID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editprofile);

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);

        email = findViewById(R.id.edittext1);
        phone = findViewById(R.id.edittext2);
        linkedin = findViewById(R.id.edittext3);
        github = findViewById(R.id.edittext4);
        resume = findViewById(R.id.edittext5);

        savechanges = findViewById(R.id.savechanges);

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

        savechanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email_temp = email.getText().toString().trim();
                String phone_temp = phone.getText().toString();
                String github_temp = github.getText().toString();
                String linkedin_temp = linkedin.getText().toString();
                String resume_temp = resume.getText().toString();
                Log.d("lalalala",email_temp);
                DocumentReference docR_2 = fStore.document("year/"+year+"- "+(year+1)+"/Users/"+RegID);

                Map<String, Object> datatosave = new HashMap<>();
                datatosave.put("Email", email_temp);
                datatosave.put("PhoneNo", phone_temp);
                datatosave.put("Github", github_temp);
                datatosave.put("Linkedin", linkedin_temp);
                datatosave.put("Resume", resume_temp);

                docR_2.set(datatosave, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Intent profileIntent = new Intent(getApplicationContext(), myprofile.class);
                        profileIntent.putExtra("caller", "MainActivity");
                        startActivity(profileIntent);
                    }
                });




            }
        });
    }

    void tp(int year){
        DocumentReference docR_2 = fStore.document("year/"+year+"- "+(year+1)+"/Users/"+RegID);
        docR_2.get().addOnSuccessListener(this, new OnSuccessListener<DocumentSnapshot>() {

            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()) {
                    email.setText(documentSnapshot.getString("Email"));
                    phone.setText(documentSnapshot.getString("PhoneNo"));
                    linkedin.setText(documentSnapshot.getString("Linkedin"));
                    github.setText(documentSnapshot.getString("Github"));
                    resume.setText(documentSnapshot.getString("Resume"));
                }
            }
        });
    }

}

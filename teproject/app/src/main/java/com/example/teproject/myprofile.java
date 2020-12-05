package com.example.teproject;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class myprofile extends AppCompatActivity {
    private static final String TAG = "myprofile";

    StorageReference mStorageReference;
    FirebaseFirestore fStore;
    FirebaseAuth fAuth;
    String RegID;
    TextView fullname, emailid, phone, branch, rollno, role, groupid, linkedin, github, resume, upload, regid;
    Button editProfBtn;
    TextView domains;
    ImageView profile_pic;

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
        regid = findViewById(R.id.RegID);

        upload = findViewById(R.id.upload);
        profile_pic = findViewById(R.id.profilepicture);

        editProfBtn = findViewById(R.id.editprofile);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        mStorageReference = FirebaseStorage.getInstance().getReference();

        // checking who has called this activity
        Intent intent = getIntent();
        String caller = intent.getStringExtra("caller");
        if (caller.equals("MainActivity")) {
            Log.d(TAG, "Main activity called me!");
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

            StorageReference fileref = mStorageReference.child("users/"+fAuth.getCurrentUser().getUid()+"/profile.jpg");
            fileref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.get().load(uri).into(profile_pic);
                }
            });

            upload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent openGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(openGallery,1000);
                }
            });

        } else if(caller.equals("ListUserFragment")) {
            Log.d(TAG, "Fragment called me");
            editProfBtn.setVisibility(View.GONE);
            upload.setVisibility(View.GONE);

            // extracting the gson object
            Gson gson = new Gson();
            UserOverview userOverview = gson.fromJson(getIntent().getStringExtra("userOverview"), UserOverview.class);
            Log.d(TAG, userOverview.getName());

            fullname.setText(userOverview.getName());
            emailid.setText(userOverview.getEmail());
            branch.setText(userOverview.getBranch());
            rollno.setText(userOverview.getRollNo());
            if(userOverview.getRole() == true){
                role.setText("Student");
            }
            else{
                role.setText("Teacher");
            }
            groupid.setText(userOverview.getGroupID());
            phone.setText(userOverview.getPhoneNo());
            linkedin.setText(userOverview.getLinkedin());
            github.setText(userOverview.getGithub());
            resume.setText(userOverview.getResume());
            regid.setText(userOverview.getRegistrationID());

            Map<String , ArrayList<String>> subDomains = new HashMap<>();
            subDomains = userOverview.getDomains();
            String s = "";
            if(subDomains==null){
                Log.d("hiiiiiii","hi");
            }
            else {
                int ct = 0;
                for (Map.Entry m: subDomains.entrySet()){
                    if(ct!=0){
                        s += "\n\n";
                    }
                    ct++;
                    String dname = m.getKey().toString();
                    ArrayList<String> snames = (ArrayList<String>) m.getValue();
                    s += dname + " : ";
                    int i=0;
                    for(i=0; i< snames.size(); i++ ){
                        s += snames.get(i)+"   ";
                    }
                }
                Log.d("hii", s);
                domains.setText(s);
            }

        }

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1000){
            if(resultCode == Activity.RESULT_OK){
                Uri imageuri = data.getData();
                profile_pic.setImageURI(imageuri);

                uploadimagetofirebase(imageuri);
            }
        }
    }

    private void uploadimagetofirebase(Uri imageuri) {
        final StorageReference fileref = mStorageReference.child("users/"+fAuth.getCurrentUser().getUid()+"/profile.jpg");
        fileref.putFile(imageuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(myprofile.this,"Image uploaded",Toast.LENGTH_SHORT).show();
                fileref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(profile_pic);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(myprofile.this,"Image could not be uploaded",Toast.LENGTH_SHORT).show();
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
                    regid.setText(documentSnapshot.getString("RegistrationID"));

                    Map<String , ArrayList<String>> subDomains = new HashMap<>();
                    subDomains = (Map<String , ArrayList<String>>) documentSnapshot.get("Domains");
                    String s = "";
                    if(subDomains==null){
                        Log.d("hiiiiiii","hi");
                    }
                    else {
                        int ct=0;
                        for (Map.Entry m: subDomains.entrySet()){
                            if(ct!=0){
                                s += "\n\n";
                            }
                            ct++;
                            String dname = m.getKey().toString();
                            ArrayList<String> snames = (ArrayList<String>) m.getValue();
                            s += dname + " : ";
                            for(int i=0; i< snames.size(); i++ ){
                                s += snames.get(i)+"   ";
                            }
                        }
                        Log.d("hii", s);
                        domains.setText(s);
                    }
                }
            }
        });
    }
}


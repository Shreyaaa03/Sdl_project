package com.example.teproject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static com.google.firebase.firestore.FieldValue.arrayUnion;


public class EditGroup extends AppCompatActivity {

    Calendar calendar = Calendar.getInstance();
    int year = calendar.get(Calendar.YEAR);

    private static final String TAG = "MainActivity";
    private LinearLayout parentLinearLayout;
    EditText text;
    FirebaseFirestore fStore;
    FirebaseAuth fAuth;
    DocumentReference docR, docR2;
    String RegID, GroupID;
    Button add,submit;
    ImageView delete;

    String problem_statement="";
    private Toolbar mToolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_group);

        mToolbar = findViewById(R.id.back_toolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                // Your code
                Intent groupIntent = new Intent(getApplicationContext(), GroupProfile.class);
                groupIntent.putExtra("activity", TAG);
                startActivity(groupIntent);
                finish();
            }
        });


        parentLinearLayout=(LinearLayout) findViewById(R.id.parent_linear_layout);
        text = findViewById(R.id.problem_statement_text);
        add = findViewById(R.id.button_add);
        delete = findViewById(R.id.delete_button);
        submit = findViewById(R.id.button_submit);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        docR = fStore.document("year/"+year+"- "+(year+1)+"/IDS/"+fAuth.getCurrentUser().getUid());

        docR.get().addOnSuccessListener(this, new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    RegID = documentSnapshot.getString("RegID");
                    Log.d("ch", RegID);
                    getGroupID();
                }

            }
        });



    }



    public void getDetails(){
        docR = fStore.document("year/"+year+"- "+(year+1)+"/Groups/"+GroupID);
        docR.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    problem_statement = document.getString("ProblemStatement");
                    if(problem_statement != null){
                        text.setText(problem_statement);
                    }
                    Map<String, Object> map1 = document.getData();
                    for (Map.Entry<String, Object> entry : map1.entrySet()) {
                        if (entry.getKey().equals("TechStack")) {
                            String tech = (entry.getValue().toString());
                            tech = tech.substring(1, tech.length()-1);
                            String[] techh = tech.split(", ", -2);

                            for (String a : techh){
                                if (!a.equals("null")){
                                    addTech(a);
                                }
                            }

                        }
                    }

                }
            }
        });
    }

    public void addTech(String tech){
        LayoutInflater inflater=(LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView=inflater.inflate(R.layout.field, null);
        EditText text = rowView.findViewById(R.id.tech_edit_text);
        text.setText(tech);
        // Add the new row before the add field button.
        parentLinearLayout.addView(rowView, parentLinearLayout.getChildCount() - 1);

    }





    public void getGroupID(){
        docR2 = fStore.document("year/" + year + "- " + (year + 1) + "/Users/" + RegID);
        docR2.get().addOnSuccessListener(this, new OnSuccessListener<DocumentSnapshot>() {

            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    GroupID = documentSnapshot.getString("GroupID");
                    Log.d("c", GroupID);
                    getDetails();

                }
            }
        });
    }


    public void onAddField(View v) {
        LayoutInflater inflater=(LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView=inflater.inflate(R.layout.field, null);
        // Add the new row before the add field button.
        parentLinearLayout.addView(rowView, parentLinearLayout.getChildCount() - 1);
    }



    public void onDelete(View v) {
        parentLinearLayout.removeView((View) v.getParent());

    }


    public void onSubmit(View v){

        docR = fStore.document("year/"+year+"- "+(year+1)+"/Groups/"+GroupID);

        Map<String, Object> datatosave = new HashMap<>();
        String[] techStack = new String[20];

        int childcount = parentLinearLayout.getChildCount();

        String problem_statement = text.getText().toString().trim();
        if (!problem_statement.isEmpty())
            datatosave.put("ProblemStatement", problem_statement);


        for (int i=0; i < childcount; i++){
            View view = parentLinearLayout.getChildAt(i);
            // do whatever you would want to do with this View
            EditText text = view.findViewById(R.id.tech_edit_text);
            String tech= (text.getText().toString().trim());
            if (!(tech.isEmpty())){
                techStack[i] = tech;
            }


        }

        datatosave.put("TechStack", arrayUnion(techStack));


        docR.set(datatosave, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(EditGroup.this, "Saved", Toast.LENGTH_SHORT).show();
                } else{
                    Toast.makeText(EditGroup.this, "Try Again", Toast.LENGTH_SHORT).show();
                }
            }
        });



    }


}

package com.example.teproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyDomain extends AppCompatActivity {

    ExpandableListView domainsList;
    DomainsAdapter adapter;
    Button btnSave, btnNext;
    String RegID;

    private int year;
    FirebaseAuth fAuth;
    private FirebaseFirestore db;
    private CollectionReference domainsRef;
    private CollectionReference userdomainsRef;

    String activity;



    // We first get the complete list of domains and subdomains
    public List<String> groups = new ArrayList<>();
    public ArrayList<ArrayList<String>> children = new ArrayList<ArrayList<String>>();

    // These will contain the actual user data
    public List<String> usergroups = new ArrayList<>();
    public ArrayList<ArrayList<String>> userchildren = new ArrayList<ArrayList<String>>();

    public ArrayList<ArrayList<Integer>> check_states = new ArrayList<>();
    public HashMap<String, Integer> check_groups = new HashMap<>();

    private static final String TAG = "MyDomain";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_domain);

        btnNext = findViewById(R.id.btn_next);
        Intent intent = getIntent();
        activity = intent.getStringExtra("activity");
        if(activity.equals("Register")) {
            btnNext.setVisibility(View.VISIBLE);

        } else if(activity.equals("MainActivity")) {
            btnNext.setVisibility(View.GONE);
        } else {
            btnNext.setVisibility(View.GONE);
        }


        // setting up connection...
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        db = FirebaseFirestore.getInstance();
        domainsRef = db.collection("year/"+year+"- "+(year+1)+"/Domains");
        userdomainsRef = db.collection("year/"+year+"- "+(year+1)+"/Users");
        fAuth = FirebaseAuth.getInstance();


        // first get the list of all domains and subdomains
        loadDomains(new FirestoretoreCallback() {
            @Override
            public void callback(List<String> list1, ArrayList<ArrayList<String>> list2) {
                groups = list1;
                children = list2;
                Log.d(TAG, groups.toString());
                Log.d(TAG, children.toString());
            }
        });

    }

    private void loadDomains(FirestoretoreCallback firestoretoreCallback) {
        domainsRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    for(DocumentSnapshot documentSnapshot: task.getResult()) {
                        String name = (String) documentSnapshot.get("name");
                        ArrayList<String> subNames = (ArrayList<String>) documentSnapshot.get("subdomains");
                        groups.add(name);
                        children.add(subNames);
                    }
                    firestoretoreCallback.callback(groups, children);
                    Log.d(TAG, "Task 1 Complete");


                    DocumentReference docR = db.document("year/"+year+"- "+(year+1)+"/IDS/"+fAuth.getCurrentUser().getUid());
                    docR.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            String tempId;
                            if(task.isSuccessful()) {
                                tempId = (String) task.getResult().get("RegID");
                                RegID = tempId;
                            }
                            else {
                                Log.d(TAG, "Could not find RegID");
                            }
                            Log.d(TAG, "Registration ID: "+RegID);
                            Log.d(TAG, "Task 2 completed!");

                            // moving on to find user's domains and subdomains
                            userdomainsRef.whereEqualTo("RegistrationID", RegID)
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if(task.isSuccessful()) {
                                                QuerySnapshot querySnapshot = task.getResult();
                                                for(DocumentSnapshot documentSnapshot: querySnapshot) {
                                                    Map<String, ArrayList<String>> subDomains = new HashMap<>();
                                                    subDomains = (Map<String, ArrayList<String>>) documentSnapshot.get("Domains");

                                                    if(activity.equals("Register")) {
                                                        //
                                                    }
                                                    else {
                                                        for(Map.Entry m: subDomains.entrySet()) {
                                                            String dname = m.getKey().toString();
                                                            usergroups.add(dname);
                                                            ArrayList<String> snames = (ArrayList<String>) m.getValue();
                                                            userchildren.add(snames);
                                                        }
                                                    }

                                                }
                                                Log.d(TAG, "Completed Task 3");
                                                Log.d(TAG, usergroups.toString());
                                                Log.d(TAG, userchildren.toString());

                                                // moving to next task ie initialising boolean array to 0

                                                check_states = new ArrayList<>();
                                                for (int i = 0; i < groups.size(); i++) {
                                                    ArrayList<Integer> tmp = new ArrayList<Integer>();
                                                    for (int j = 0; j <children.get(i).size(); j++) {
                                                        tmp.add(0);
                                                    }
                                                    check_states.add(tmp);
                                                }
                                                Log.d(TAG, "Completed Task 4");
                                                Log.d(TAG, check_states.toString());

                                                // moving to next!
                                                for(int i=0;i<usergroups.size();i++) {
                                                    String currGrp = usergroups.get(i);
                                                    int idx1 = groups.indexOf(currGrp);
                                                    ArrayList<String> target = children.get(idx1);
                                                    ArrayList<String> src = userchildren.get(i);
                                                    for(int j=0;j<src.size();j++) {
                                                        int idx2 = target.indexOf(src.get(j));
                                                        check_states.get(idx1).set(idx2, 1);
                                                    }
                                                }
                                                Log.d(TAG, "Modified boolean array");
                                                Log.d(TAG, check_states.toString());

                                                check_groups = new HashMap<>();
                                                for (int i=0;i<groups.size();i++) {
                                                    check_groups.put(groups.get(i), 0);
                                                }

                                                for(int i=0;i<usergroups.size();i++) {
                                                    check_groups.put(usergroups.get(i), 1);
                                                }

                                                Log.d(TAG, "Check Groups done!");
                                                Log.d(TAG, check_groups.toString());

                                                // we are here now!

                                                domainsList = findViewById(R.id.domains_list);
                                                adapter = new DomainsAdapter(getApplicationContext(), groups,
                                                        children, usergroups, userchildren, check_states, check_groups);
                                                domainsList.setAdapter(adapter);

                                                //        for(int i=0; i < adapter.getGroupCount(); i++)
                                                //            domainsList.expandGroup(i);

                                                btnSave = findViewById(R.id.btn_save);
                                                btnSave.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        HashMap<String, Integer> selectedDomains = new HashMap<>();
                                                        ArrayList<String> allDomains = new ArrayList<>();
                                                        ArrayList<ArrayList<Integer>> allSubDomains = new ArrayList<ArrayList<Integer>>();

                                                        selectedDomains = adapter.check_groups;
                                                        allDomains = (ArrayList<String>) adapter.groups;
                                                        allSubDomains = adapter.check_states;

                                                        HashMap<String, ArrayList<String>> updatedMap = new HashMap<>();
                                                        String msg = "";
                                                        for(int i=0;i<allDomains.size();i++) {
                                                            String currDomain = allDomains.get(i);
                                                            ArrayList<String> temp2 = new ArrayList<>();
                                                            if(selectedDomains.get(currDomain) == 1) {
                                                                // domain is selected
                                                                // Now, check for subdomains
                                                                msg += currDomain;
                                                                msg += " ";
                                                                ArrayList<Integer> temp = allSubDomains.get(i);
                                                                for(int j=0;j<temp.size();j++) {
                                                                    String currSubdomain = adapter.children.get(i).get(j);
                                                                    if (temp.get(j) == 1) {
                                                                        // subdomain is also selected
                                                                        msg += currSubdomain;
                                                                        temp2.add(currSubdomain);
                                                                        msg += " ";
                                                                    }
                                                                }
                                                                updatedMap.put(currDomain, temp2);
                                                            }
                                                            msg += "\n";
                                                        }
                                                        Toast.makeText(MyDomain.this, msg, Toast.LENGTH_SHORT).show();
                                                        Log.d(TAG, "Lastly reg Id is "+RegID);

                                                        // now, update the document ib the database
                                                        DocumentReference docR_2 = db.document("year/"+year+"- "+(year+1)+"/Users/"+RegID);
                                                        Map<String, Object> userObj = new HashMap<>();
                                                        userObj.put("Domains", null);
                                                        docR_2.set(userObj, SetOptions.merge());
                                                        userObj.put("Domains", updatedMap);
                                                        docR_2.set(userObj, SetOptions.merge());
                                                        Log.d(TAG, updatedMap.toString());


                                                    }
                                                });
                                                btnNext.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                                    }
                                                });

                                            }
                                            else {
                                                Toast.makeText(MyDomain.this, "Could not find User details!", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                    });
                }
                else {
                    Log.d(TAG, "Error getting messages");
                }
            }
        });
        Log.d(TAG, groups.toString());
        Log.d(TAG, "Last");
    }

    private interface FirestoretoreCallback {
        void callback(List<String> list1, ArrayList<ArrayList<String>> list2);

    }

    private void test() {

    }


}
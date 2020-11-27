package com.example.teproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class MyDomain extends AppCompatActivity {

    ExpandableListView domainsList;
    DomainsAdapter adapter;
    Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_domain);


        domainsList = findViewById(R.id.domains_list);
        adapter = new DomainsAdapter(getApplicationContext());
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

                String msg = "";
                for(int i=0;i<allDomains.size();i++) {
                    String currDomain = allDomains.get(i);
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
                                msg += " ";
                            }
                        }
                    }
                    msg += "\n";
                }
                Toast.makeText(MyDomain.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
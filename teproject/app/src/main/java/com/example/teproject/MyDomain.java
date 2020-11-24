package com.example.teproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MyDomain extends AppCompatActivity {

    ExpandableListView domainsList;
    List<String> domains;
    HashMap<String, List<String>> subdomains;
    HashMap<String, List<String>> tempMap;
    DomainsAdapter adapter;
    Spinner spinner;
    List<String> domainsOptions;
    String selectedString;
    Button btnAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_domain);

        domainsList = findViewById(R.id.domains_list);
        domains = new ArrayList<>();
        subdomains = new HashMap<>();
        adapter = new DomainsAdapter(this, domains, subdomains);
        domainsList.setAdapter(adapter);
        spinner = findViewById(R.id.spinner);
        selectedString = "";

        // When user clicks on this button, it should add domain to the list
        btnAdd = findViewById(R.id.btn_add_domain);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<String> tempList = tempMap.get(selectedString);
                updateListData(adapter.getGroupCount(), selectedString, tempList);
            }
        });



        initListData();
        setTheSpinner();
    }



    private void initListData() {
        // This method will populate the list with the domains and
        // subdomains selected by the user at the time of registration
        // Mentioned just 2 sample domains here

        domains.add("AI");
        domains.add("Cloud Computing");

        List<String> list1 = new ArrayList<>();
        list1.add("AI SD 1");
        list1.add("AI SD 2");
        list1.add("AI SD 3");

        List<String> list2 = new ArrayList<>();
        list2.add("CC SD 1");
        list2.add("CC SD 2");
        list2.add("CC SD 3");
        list2.add("CC SD 4");

        subdomains.put(domains.get(0), list1);
        subdomains.put(domains.get(1), list2);
        adapter.notifyDataSetChanged();
    }

    public void updateListData(int idx, String domainName, List<String> subDomainNames) {
        // This method will add the domain in the list
        domains.add(domainName);
        subdomains.put(domainName, subDomainNames);
        adapter.notifyDataSetChanged();
    }

    private void setTheSpinner() {
        // This method will display the remaining options to the user which he can further select
        // Here we will populate domainsOptions array

        // Let the below 2 domains be the remaining domains
        tempMap = new HashMap<>();
        String domain1 = "Cyber Security";
        List<String> list1 = new ArrayList<>();
        list1.add("SS SD 1");
        list1.add("SS SD 2");
        list1.add("SS SD 3");

        String domain2 = "Big Data";
        List<String> list2 = new ArrayList<>();
        list2.add("BD SD 1");
        list2.add("BD SD 2");
        list2.add("BD SD 3");
        list2.add("BD SD 4");

        tempMap.put(domain1, list1);
        tempMap.put(domain2, list2);


        // Now, populate the spinner with these 2 domains
        domainsOptions = new ArrayList<>();
        domainsOptions.add(domain1);
        domainsOptions.add(domain2);


        // creating array adapter instance having list of options
        ArrayAdapter<String> aa = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, domainsOptions);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // setting the adapter on the spinner
        spinner.setAdapter(aa);

        // spinner item click handling
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                // get the text from the spinner and add save it
                selectedString = adapterView.getItemAtPosition(i).toString();


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
}
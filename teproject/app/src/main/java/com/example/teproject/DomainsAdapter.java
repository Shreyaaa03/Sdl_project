package com.example.teproject;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DomainsAdapter extends BaseExpandableListAdapter {

    private int year;
    private FirebaseFirestore db;
    FirebaseAuth fAuth;
    private CollectionReference domainsRef;
    private CollectionReference userdomainsRef;

    private static final String TAG = "DomainsAdapter";

    // Sample data set.  children[i] contains the children (String[]) for groups[i].

    // We first get the complete list of domains and subdomains
    public List<String> groups = new ArrayList<>();
    public ArrayList<ArrayList<String>> children = new ArrayList<ArrayList<String>>();

    // There will contain the actual user data
    public List<String> usergroups = new ArrayList<>();
    public ArrayList<ArrayList<String>> userchildren = new ArrayList<ArrayList<String>>();

    private Context context;

    public ArrayList<ArrayList<Integer>> check_states;
    public HashMap<String, Integer> check_groups;

    static boolean flag = false;

    //Constructors
    public DomainsAdapter() {
    }

    public DomainsAdapter(Context c, List<String> groups, ArrayList<ArrayList<String>> children,
                          List<String> usergroups, ArrayList<ArrayList<String>> userchildren,
                          ArrayList<ArrayList<Integer>> check_states, HashMap<String, Integer> check_groups) {
        // set the context
        this.context = c;
        this.groups = groups;
        this.children = children;
        this.usergroups = usergroups;
        this.userchildren = userchildren;
        this.check_states = check_states;
        this.check_groups = check_groups;

//        // setting original data
//        groups.add("AI");
//        groups.add("Cloud Computing");
//        groups.add("CyberSecurity");
//        groups.add("Big Data");
//
//        Toast.makeText(context, groups.toString(), Toast.LENGTH_SHORT).show();
//
//
//
//        ArrayList<String> l1 = new ArrayList<>();
//        l1.add("AI SD 1");
//        l1.add("AI SD 2");
//        l1.add("AI SD 3");
//        l1.add("AI SD 4");
//        children.add(l1);
//
//        ArrayList<String> l2 = new ArrayList<>();
//        l2.add("CC SD 1");
//        l2.add("CC SD 2");
//        l2.add("CC SD 3");
//        l2.add("CC SD 4");
//        children.add(l2);
//
//        ArrayList<String> l3 = new ArrayList<>();
//        l3.add("CS SD 1");
//        l3.add("CS SD 2");
//        l3.add("CS SD 3");
//        children.add(l3);
//
//        ArrayList<String> l4 = new ArrayList<>();
//        l4.add("BD SD 1");
//        l4.add("BD SD 2");
//        children.add(l4);
////        // ----------------------------
//
//        // user's data
//        usergroups.add("AI");
//        usergroups.add("Cloud Computing");
//        // ---------------------------------------
//
//        ArrayList<String> s1 = new ArrayList<>();
//        s1.add("AI SD 1");
//        s1.add("AI SD 2");
//
//        ArrayList<String> s2 = new ArrayList<>();
//        s2.add("CC SD 3");
//        s2.add("CC SD 4");
//
//
//
//        // check_states is an array 1-> subdomain selected 0-> not selected
//        check_states = new ArrayList<>();
////        initialize the states to all 0;
//        for (int i = 0; i < this.groups.size(); i++) {
//            ArrayList<Integer> tmp = new ArrayList<Integer>();
//            for (int j = 0; j < this.children.get(i).size(); j++) {
//                tmp.add(0);
//            }
//            check_states.add(tmp);
//        }
//
//
//
//        // Now, set the values in check_status depending upon user's preselected values
//        for(int i=0;i<s1.size();i++)
//            setStates(i, s1);
//
//        for(int i=0;i<s2.size();i++)
//            setStates(i, s2);
//
//        check_groups = new HashMap<>();
//        for (int i=0;i<groups.size();i++) {
//            check_groups.put(groups.get(i), 0);
//        }
//
//        for(int i=0;i<usergroups.size();i++) {
//            check_groups.put(usergroups.get(i), 1);
//        }

    }


    private void setStates(int i, ArrayList<String> s1) {
        for(int j=0;j<this.groups.size();j++) {
            for(int k=0;k<this.children.get(j).size();k++) {
                String temp = this.children.get(j).get(k);
                if(temp.equals(s1.get(i))) {
                    check_states.get(j).set(k, 1);
                }
            }
        }
    }



    @Override
    public int getGroupCount() {
        return groups.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return children.get(groupPosition).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groups.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return children.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        View grid;

        if (convertView == null) {
            grid = new View(context);
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            grid = inflater.inflate(R.layout.specialty_header, parent, false);
        } else {
            grid = (View) convertView;
        }

        TextView header = (TextView) grid.findViewById(R.id.specialty_header);
        String heading = getGroup(groupPosition).toString();
        header.setText(heading);
        int color;
        if(check_groups.get(heading) == 0)
            color = ContextCompat.getColor(context,R.color.faded_gray);
        else {
            color = ContextCompat.getColor(context, R.color.black);
        }
        header.setTextColor(color);
        return grid;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        View grid;


        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        grid = inflater.inflate(R.layout.specialty_list_item, parent, false);

        final int grpPos = groupPosition;
        final int childPos = childPosition;
        TextView header = (TextView) grid.findViewById(R.id.title);
        header.setText(getChild(groupPosition, childPosition).toString());
        final ImageView tick = grid.findViewById(R.id.image_check);
        int color;
        if (check_states.get(grpPos).get(childPos) == 1) {
            tick.setImageResource(R.drawable.ic_checkbox_filled_foreground);
            color = ContextCompat.getColor(context, R.color.black);
        }
        else {
            tick.setImageResource(R.drawable.ic_checkbox_empty_foreground);
            color = ContextCompat.getColor(context,R.color.faded_gray);
        }
        header.setTextColor(color);
        grid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int x = check_states.get(grpPos).get(childPos);
                int color;
                if(x == 0) {
                    check_states.get(grpPos).set(childPos, 1);
                    tick.setImageResource(R.drawable.ic_checkbox_filled_foreground);
                    color = ContextCompat.getColor(context, R.color.black);
                }
                else {
                    check_states.get(grpPos).set(childPos, 0);
                    tick.setImageResource(R.drawable.ic_checkbox_empty_foreground);
                    color = ContextCompat.getColor(context,R.color.faded_gray);
                }
                header.setTextColor(color);

                // now, here check if all the children of parent are off or not. If off dim the parent else make it bold

                ArrayList<Integer> temp = new ArrayList<>();
                temp = check_states.get(groupPosition);
                for(int p=0;p<temp.size();p++) {
                    if(temp.get(p) == 1) {
                        flag = true;
                        break;
                    }
                }
                String message = "";
                if(flag == false) {
                    // toggle parent
                    String heading = (String) getGroup(groupPosition);
                    check_groups.put(heading, 0);
                    for(Map.Entry m: check_groups.entrySet()) {
                        message += m.getKey().toString() + " " + m.getValue().toString() + "\n";
                    }
//                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                }
                else {
                    // keep the parent highlighted
                    String heading = (String) getGroup(groupPosition);
                    check_groups.put(heading, 1);
                    for(Map.Entry m: check_groups.entrySet()) {
                        message += m.getKey().toString() + " " + m.getValue().toString() + "\n";
                    }
//                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                    flag = false;
                }

            }
        });


        return grid;
    }

    public void toggleParent() {

    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}


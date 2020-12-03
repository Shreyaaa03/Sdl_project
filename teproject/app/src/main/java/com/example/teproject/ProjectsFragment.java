package com.example.teproject;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.gson.Gson;

import java.util.Calendar;

public class ProjectsFragment extends Fragment {

    private View thisView;
    private int year;

    private RecyclerView recyclerView;

    private FirebaseFirestore db;
    private CollectionReference groupsRef;
    private ListGroupsAdapter adapter;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // Defines the xml file for the fragment
        thisView = inflater.inflate(R.layout.fragment_projects, container, false);

        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        db = FirebaseFirestore.getInstance();
        groupsRef = db.collection("year/"+year+"- "+(year+1)+"/Groups");
        recyclerView = thisView.findViewById(R.id.groups_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        return thisView;
    }

    @Override
    public void onStart() {
        super.onStart();
        Query query = groupsRef;
        FirestoreRecyclerOptions<GroupsOverview> options = new FirestoreRecyclerOptions.Builder<GroupsOverview>()
                .setQuery(query, GroupsOverview.class)
                .build();
        Log.d("Options: ", options.toString());
        adapter = new ListGroupsAdapter(options);
        recyclerView.setAdapter(adapter);
        adapter.setOnGroupItemClickListener(new ListGroupsAdapter.OnGroupItemClickListener() {
            @Override
            public void onGrpItemClick(DocumentSnapshot documentSnapshot, int position) {
                GroupsOverview groupsOverview = documentSnapshot.toObject(GroupsOverview.class);
//                Toast.makeText(getContext(), "Position: "+position+" ID: "+documentSnapshot.getId(), Toast.LENGTH_SHORT).show();
                // start new activity
                Intent groupIntent = new Intent(getContext(), GroupProfile.class);
                groupIntent.putExtra("activity", "ProjectsFragment");

                // passing the clicked object's data...
                Gson gson = new Gson();
                String currUser = gson.toJson(groupsOverview);
                groupIntent.putExtra("groupsOverView", currUser);
                groupIntent.putExtra("groupId", documentSnapshot.getId());
                startActivity(groupIntent);


            }
        });
        adapter.startListening();

    }

//    // This event is triggered soon after onCreateView().
//    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
//
//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        // Setup any handles to view objects here
//        // EditText etFoo = (EditText) view.findViewById(R.id.etFoo);
//    }
}

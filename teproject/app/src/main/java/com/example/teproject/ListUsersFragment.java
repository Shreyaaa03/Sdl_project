package com.example.teproject;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Calendar;

public class ListUsersFragment extends Fragment {
    private static final String TAG = "ListUsersFragment";

    private View studentsFragmentsView;
    private RecyclerView recyclerView;

    private int year;
    private FirebaseFirestore db;
    private CollectionReference userdomainsRef;

    private ListUsersAdapter adapter;

    String tabName;
    public ListUsersFragment(String tabName) {
        this.tabName = tabName;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        studentsFragmentsView = inflater.inflate(R.layout.fragment_list_users, container, false);

        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        db = FirebaseFirestore.getInstance();
        userdomainsRef = userdomainsRef = db.collection("year/"+year+"- "+(year+1)+"/Users");


        recyclerView = studentsFragmentsView.findViewById(R.id.students_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        return studentsFragmentsView;
    }

    @Override
    public void onStart() {
        super.onStart();
        Query query;
        if(tabName.equals("Students")) {
            query = userdomainsRef.whereEqualTo("Role", true);
        } else
            query = userdomainsRef.whereEqualTo("Role", false);
        FirestoreRecyclerOptions<UserOverview> options = new FirestoreRecyclerOptions.Builder<UserOverview>()
                .setQuery(query, UserOverview.class)
                .build();

        adapter = new ListUsersAdapter(options);

        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new ListUsersAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                UserOverview userOverview = documentSnapshot.toObject(UserOverview.class);
                String name = userOverview.getName();
                Toast.makeText(getContext(), "Position: "+position+" Name: "+name, Toast.LENGTH_SHORT).show();
            }
        });
        adapter.startListening();
    }

}

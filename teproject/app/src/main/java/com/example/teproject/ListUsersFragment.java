package com.example.teproject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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
import com.google.gson.Gson;

import org.w3c.dom.Text;

import java.util.Calendar;

public class ListUsersFragment extends Fragment {
    private static final String TAG = "ListUsersFragment";
    private static final String CALLING_TAG = "ListUserFragment";

    private View studentsFragmentsView;
    private RecyclerView recyclerView;
    private TextView txt_title;

    private int year;
    private FirebaseFirestore db;
    private CollectionReference userdomainsRef;
    private EditText txtSearch;

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

        txtSearch = studentsFragmentsView.findViewById(R.id.txt_search_users);

        recyclerView = studentsFragmentsView.findViewById(R.id.students_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        return studentsFragmentsView;
    }

    @Override
    public void onStart() {
        super.onStart();
        Query query;
        txt_title = studentsFragmentsView.findViewById(R.id.list_heading);
        if(tabName.equals("Students")) {
            query = userdomainsRef.whereEqualTo("Role", true);
        } else {
            query = userdomainsRef.whereEqualTo("Role", false);
            txt_title.setText("Find your mentors");
        }
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
//                Toast.makeText(getContext(), "Position: "+position+" Name: "+name, Toast.LENGTH_SHORT).show();
                Intent profileIntent = new Intent(getContext(), myprofile.class);

                // passing this to identify caller activity/fragment
                profileIntent.putExtra("caller", CALLING_TAG);

                // serialising the data because custom objects cannot be directly passed!
                Gson gson = new Gson();
                String currUser = gson.toJson(userOverview);
                profileIntent.putExtra("userOverview", currUser);
                startActivity(profileIntent);

            }
        });
        adapter.startListening();

        txtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                filter(editable.toString());
            }
        });
    }
    private void filter(String toString) {
        Query query = userdomainsRef;
        if(tabName.equals("Students")) {
            query = userdomainsRef.whereEqualTo("Role", true);
        } else {
            query = userdomainsRef.whereEqualTo("Role", false);
            txt_title.setText("Find your mentors");
        }
        FirestoreRecyclerOptions<UserOverview> options = new FirestoreRecyclerOptions.Builder<UserOverview>()
                .setQuery(query, UserOverview.class)
                .build();

        Query q2;
        if(tabName.equals("Students")) {
            q2 = userdomainsRef.whereEqualTo("Role", true)
                    .whereGreaterThanOrEqualTo("Name", toString)
                    .whereLessThanOrEqualTo("Name", toString+"\uf8ff");
        } else {
            q2 = userdomainsRef.whereEqualTo("Role", false)
                    .whereGreaterThanOrEqualTo("Name", toString)
                    .whereLessThanOrEqualTo("Name", toString+"\uf8ff");
            txt_title.setText("Find your mentors");
        }
        FirestoreRecyclerOptions<UserOverview> options2 = new FirestoreRecyclerOptions.Builder<UserOverview>()
                .setQuery(q2, UserOverview.class)
                .build();
        if(toString.equals("")) {
            adapter.updateOptions(options);
        } else
            adapter.updateOptions(options2);
    }

}

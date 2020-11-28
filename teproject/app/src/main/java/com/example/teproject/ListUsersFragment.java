package com.example.teproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Calendar;

public class ListUsersFragment extends Fragment {

    private View studentsFragmentsView;
    private RecyclerView recyclerView;

    private int year;
    private FirebaseFirestore db;
    private CollectionReference userdomainsRef;

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

        FirestoreRecyclerAdapter<UserOverview, UserHolder> adapter = new FirestoreRecyclerAdapter<UserOverview, UserHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull UserHolder holder, int position, @NonNull UserOverview model) {
                // holder.itemView.component for accessing
                // what we have to put in each layout in our CardView

                holder.txtName.setText(model.getName());
                holder.txtBranch.setText(model.getBranch());
                holder.txtEmail.setText(model.getEmail());

            }

            @NonNull
            @Override
            public UserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.person_list_item, parent, false);
                UserHolder holder = new UserHolder(view);
                return holder;
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    public static class UserHolder extends RecyclerView.ViewHolder {

        TextView txtName, txtBranch, txtEmail;
        public UserHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.usr_full_name);
            txtBranch = itemView.findViewById(R.id.usr_branch_name);
            txtEmail = itemView.findViewById(R.id.usr_email);
        }
    }

}

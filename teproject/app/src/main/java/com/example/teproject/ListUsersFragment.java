package com.example.teproject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ListUsersFragment extends Fragment {
    private static final String TAG = "ListUsersFragment";
    private static final String CALLING_TAG = "ListUserFragment";

    private View studentsFragmentsView;
    private RecyclerView recyclerView;
    private TextView txt_title;

    private int year;
    private FirebaseFirestore db;
    private CollectionReference userdomainsRef;
    private CollectionReference domainsRef;
    private EditText txtSearch;

    private ListUsersAdapter adapter;

    private ArrayList<String> listDomains;
    private Button btnFilter;
    private int checkedInitialItems;
    private int backUp;
    private String arrDomains[];

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
        userdomainsRef = db.collection("year/"+year+"- "+(year+1)+"/Users");
        domainsRef = db.collection("year/"+year+"- "+(year+1)+"/Domains");

        btnFilter = studentsFragmentsView.findViewById(R.id.btn_filter);
        listDomains = new ArrayList<>();
        readData(new FirestoreCallback() {
            @Override
            public void onCallBack(List<String> list) {
                listDomains = new ArrayList<>();
                listDomains.add("All");
                for (int x=0;x<list.size();x++) {
                    listDomains.add(list.get(x));
                }

                arrDomains = new String[listDomains.size()];
                for (int x=0;x<listDomains.size();x++) {
                    arrDomains[x] = listDomains.get(x);
                }
                checkedInitialItems = 0;
                backUp = 0;

//                Toast.makeText(getContext(), listDomains.toString(), Toast.LENGTH_SHORT).show();
                Toast.makeText(getContext(), "Data Loaded!", Toast.LENGTH_SHORT).show();
                ArrayList<String> selectedItems = new ArrayList<>();

                btnFilter.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setTitle("Select Domains")
                                .setSingleChoiceItems(arrDomains, checkedInitialItems, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        checkedInitialItems = i;
                                    }
                                });
                        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int which) {

                                backUp = checkedInitialItems;

                                String finalSelected = listDomains.get(checkedInitialItems);
                                Log.d("Finally... ", finalSelected);

                                boolean tflag = false;
                                if (checkedInitialItems == 0) {
                                    tflag = false;
                                } else
                                    tflag = true;

                                if (tflag == false) {
                                    // all entries are false....do not do anything
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
                                    adapter.updateOptions(options);

                                }
                                else {
                                    Query q2;
                                    if(tabName.equals("Students")) {
                                        q2 = userdomainsRef.whereEqualTo("Role", true);
                                        String str = "Domains.";
                                        str += finalSelected;
                                        q2 = q2.whereNotEqualTo(str, "");
                                    } else {
                                        q2 = userdomainsRef.whereEqualTo("Role", false);
                                        String str = "Domains.";
                                        str += finalSelected;
                                        q2 = q2.whereNotEqualTo(str, "");
                                        txt_title.setText("Find your mentors");
                                    }
                                    FirestoreRecyclerOptions<UserOverview> options2 = new FirestoreRecyclerOptions.Builder<UserOverview>()
                                            .setQuery(q2, UserOverview.class)
                                            .build();
                                    adapter.updateOptions(options2);
                                }
                                // ---------------------------------------

                            }
                        });
                        builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // do something
                                // revert the checked array back to backup array
                                checkedInitialItems = backUp;
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                });

            }
        });


        txtSearch = studentsFragmentsView.findViewById(R.id.txt_search_users);
        recyclerView = studentsFragmentsView.findViewById(R.id.students_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        return studentsFragmentsView;
    }

    private void readData(FirestoreCallback firestoreCallback) {
        domainsRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot documentSnapshot: task.getResult()) {
                        String currDomain = (String) documentSnapshot.get("name");
                        Log.d("Current: ", currDomain);
                        listDomains.add(currDomain);
                    }
                    firestoreCallback.onCallBack(listDomains);
                } else
                    Log.d(TAG, "Error fetching data!");
            }
        });
    }
    private interface FirestoreCallback {
        void onCallBack(List<String> list);
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

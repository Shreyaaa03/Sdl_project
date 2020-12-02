package com.example.teproject;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

public class ListGroupsAdapter extends FirestoreRecyclerAdapter<GroupsOverview, ListGroupsAdapter.GroupHolder> {

    private OnGroupItemClickListener listener;
    public ListGroupsAdapter(@NonNull FirestoreRecyclerOptions<GroupsOverview> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull GroupHolder holder, int position, @NonNull GroupsOverview model) {
        if(model == null) {
            Log.d("lOGGING!!", "Model is null");
        }
        DocumentSnapshot documentSnapshot = getSnapshots().getSnapshot(position);
        String currGroupId = documentSnapshot.getId();

        holder.groupId.setText("Group Id: "+currGroupId);
        if (model.getProblemStatement() != null) {
            String probStatement = model.getProblemStatement();
            if(probStatement.length() > 22) {
                probStatement = probStatement.substring(0, 21);
                probStatement += "...";
            }
            holder.problemStatement.setText("Problem Statement: "+probStatement);
        }
    }

    @NonNull
    @Override
    public GroupHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_list_item, parent, false);
        return new GroupHolder(v);
    }

    class GroupHolder extends RecyclerView.ViewHolder {
        TextView problemStatement, groupId;
        public GroupHolder(@NonNull View itemView) {
            super(itemView);
            groupId = itemView.findViewById(R.id.group_name);
            problemStatement = itemView.findViewById(R.id.problem_statement);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        listener.onGrpItemClick(getSnapshots().getSnapshot(position), position);
                    }
                }
            });
        }
    }

    public interface OnGroupItemClickListener {
        void onGrpItemClick(DocumentSnapshot documentSnapshot, int position);
    }
    public void setOnGroupItemClickListener(OnGroupItemClickListener listener) {
        this.listener = listener;
    }
}

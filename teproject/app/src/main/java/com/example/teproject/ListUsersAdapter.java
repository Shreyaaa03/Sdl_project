package com.example.teproject;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

public class ListUsersAdapter extends FirestoreRecyclerAdapter<UserOverview, ListUsersAdapter.UserHolder> {

    private OnItemClickListener listener;

    public ListUsersAdapter(@NonNull FirestoreRecyclerOptions<UserOverview> options) {
        super(options);
    }

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
        return new UserHolder(view);
    }

    class UserHolder extends RecyclerView.ViewHolder {

        TextView txtName, txtBranch, txtEmail;
        public UserHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.usr_full_name);
            txtBranch = itemView.findViewById(R.id.usr_branch_name);
            txtEmail = itemView.findViewById(R.id.usr_email);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if(position != RecyclerView.NO_POSITION && listener != null) {
                        listener.onItemClick(getSnapshots().getSnapshot(position), position);
                    }
                }
            });

        }
    }

    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;

    }
}

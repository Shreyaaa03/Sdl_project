package com.example.teproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;


public class EditGroup extends AppCompatActivity {

    private LinearLayout parentLinearLayout;
    EditText text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_group);
        parentLinearLayout=(LinearLayout) findViewById(R.id.parent_linear_layout);
        text = findViewById(R.id.problem_statement_text);

    }

    public void onAddField(View v) {
        LayoutInflater inflater=(LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView=inflater.inflate(R.layout.field, null);
        // Add the new row before the add field button.
        parentLinearLayout.addView(rowView, parentLinearLayout.getChildCount() - 1);
    }

    public void onDelete(View v) {
        parentLinearLayout.removeView((View) v.getParent());
    }

    public void onSubmit(View v){
        int childcount = parentLinearLayout.getChildCount();

        String check = text.getText().toString();
        for (int i=0; i < childcount; i++){
            //View v = parentLinearLayout.getChildAt(i);
            // do whatever you would want to do with this View
            //trying
            Toast.makeText(EditGroup.this, "Child count - " + childcount + "\nProblem Statement - " +check, Toast.LENGTH_LONG).show();
        }
    }


}
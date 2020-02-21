package com.stued.StuEd.Tutor_ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.stued.StuEd.R;
import com.stued.StuEd.Model_Classes.TinyDBorderID;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class StudentRegisteredAdapter extends RecyclerView.Adapter<StudentRegisteredAdapter.programmingViewHolder> {

    private List<String> data;
    private LayoutInflater layoutInflater;
    private Context context;

    public StudentRegisteredAdapter(Context context, List<String> data)
    {
        this.data=data;
        this.layoutInflater= LayoutInflater.from(context);
        this.context=context;
    }

    @NonNull
    @Override
    public programmingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view=layoutInflater.inflate(R.layout.lists,parent,false);

        return new programmingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final programmingViewHolder holder, int position) {
        String student_uid=data.get(position);
        FirebaseDatabase.getInstance().getReference((new TinyDBorderID(context)).getString("collegeName")).child("Users").child(student_uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                holder.subjectName.setText(dataSnapshot.child("Username").getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        //holder.subjectName.setText(subjectName);
    }

    @Override
    public int getItemCount()
    {
        return data.size();
    }

    public class programmingViewHolder extends RecyclerView.ViewHolder{
        TextView subjectName;
        LinearLayout linearLayout;
        public programmingViewHolder(@NonNull View itemView) {
            super(itemView);
            subjectName=(TextView)itemView.findViewById(R.id.subjectName);
            linearLayout=(LinearLayout)itemView.findViewById(R.id.pant);
        }
    }
}

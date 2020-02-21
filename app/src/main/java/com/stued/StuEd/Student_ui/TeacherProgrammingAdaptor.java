package com.stued.StuEd.Student_ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.stued.StuEd.Model_Classes.TinyDBorderID;
import com.stued.StuEd.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class TeacherProgrammingAdaptor extends RecyclerView.Adapter<TeacherProgrammingAdaptor.programmingViewHolder> {

    private List<String> data;
    private LayoutInflater layoutInflater;
    final private DatabaseReference databaseReference;
    private Context context;
    public TeacherProgrammingAdaptor(Context context, List<String> data, DatabaseReference databaseReference)
    {
        this.data=data;
        this.layoutInflater=LayoutInflater.from(context);
        this.databaseReference = databaseReference;
        this.context=context;
    }

    @NonNull
    @Override
    public programmingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=layoutInflater.inflate(R.layout.list1,parent,false);
        return new programmingViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final programmingViewHolder holder, int position) {
        final String subjectName=data.get(position);
        final DatabaseReference userReference = FirebaseDatabase.getInstance().getReference((new TinyDBorderID(context)).getString("collegeName")).child("Users").child(subjectName);
        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                holder.subjectName.setText(dataSnapshot.child("Username").getValue(String.class));
                float rating=0;
                if(dataSnapshot.child("noOfRating").getValue(Integer.class)!=0)rating=dataSnapshot.child("rating").getValue(Float.class)/dataSnapshot.child("noOfRating").getValue(Integer.class);
                holder.currentRating.setText(rating+"/5");
                holder.linearLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AppCompatActivity activity = (AppCompatActivity) view.getContext();
                        Fragment myFragment = new TeacherDescription(layoutInflater, databaseReference.child(subjectName));
                        activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, myFragment,"FRAGMENT").addToBackStack("FRAGMENT_OTHER").commit();

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    @Override
    public int getItemCount() {
        return data.size();
    }
    public class programmingViewHolder extends RecyclerView.ViewHolder{
        TextView subjectName,currentRating;
        LinearLayout linearLayout;
        public programmingViewHolder(@NonNull View itemView) {
            super(itemView);
            subjectName=itemView.findViewById(R.id.subjectName);
            linearLayout=itemView.findViewById(R.id.pantx);
            currentRating=itemView.findViewById(R.id.currentRating);
            subjectName.setSelected(true);
        }
    }
}

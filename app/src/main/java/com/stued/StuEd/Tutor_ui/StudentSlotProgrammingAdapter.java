package com.stued.StuEd.Tutor_ui;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.stued.StuEd.R;
import com.stued.StuEd.Model_Classes.TinyDBorderID;
import com.stued.StuEd.Student_ui.TutorSlotBooked;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class StudentSlotProgrammingAdapter extends RecyclerView.Adapter<StudentSlotProgrammingAdapter.programmingViewHolder> {

    private List<String> data;
    private LayoutInflater layoutInflater;
    private DatabaseReference databaseReference;
    public StudentSlotProgrammingAdapter(Context context, List<String> data)
    {
        this.data=data;
        this.layoutInflater=LayoutInflater.from(context);
        this.databaseReference= FirebaseDatabase.getInstance().getReference((new TinyDBorderID(context)).getString("collegeName"));
    }

    @NonNull
    @Override
    public  programmingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=layoutInflater.inflate(R.layout.lists,parent,false);
        return new  programmingViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final programmingViewHolder holder, int position) {
        final String slotPath=data.get(position);
        databaseReference.child(slotPath).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    //SlotsClass slot = dataSnapshot.getValue(SlotsClass.class);
                    holder.subjectName.setText(dataSnapshot.child("time").getValue(String.class) + " @ " + dataSnapshot.child("date").getValue(String.class));
                    holder.linearLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            AppCompatActivity activity = (AppCompatActivity) view.getContext();
                            Fragment myFragment = new TutorSlotBooked(layoutInflater,databaseReference.child(slotPath));
                            activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, myFragment,"FRAGMENT").addToBackStack("FRAGMENT_OTHER").commit();

                        }
                    });
                }
                Log.i("as", "onslotaddChan" +
                        "ge: added");
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
        TextView subjectName;
        LinearLayout linearLayout;
        public programmingViewHolder(@NonNull View itemView) {
            super(itemView);
            subjectName=(TextView)itemView.findViewById(R.id.subjectName);
            linearLayout=(LinearLayout)itemView.findViewById(R.id.pant);
        }
    }
}

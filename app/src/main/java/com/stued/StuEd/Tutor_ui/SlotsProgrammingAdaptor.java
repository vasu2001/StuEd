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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SlotsProgrammingAdaptor extends RecyclerView.Adapter<SlotsProgrammingAdaptor.programmingViewHolder> {

    private List<String> data;
    private LayoutInflater layoutInflater;
    private DatabaseReference databaseReference;
    private ArrayList<Integer> attended;

    public SlotsProgrammingAdaptor(Context context, List<String> data,ArrayList<Integer> attended) {
        this.data = data;
        this.layoutInflater = LayoutInflater.from(context);
        this.databaseReference = FirebaseDatabase.getInstance().getReference();
        this.attended=attended;

        //attended is the no of students attended
        //-1 for iupcoming slot
        //0 for ongoing slot
        //finite no for past slot


    }

    @Override
    @NonNull
    public int getItemViewType(int position) {
        final int currentAttended=attended.get(position);
        return ((currentAttended==-1) ? R.layout.upcoming_slots : R.layout.past_slot);
    }

    @NonNull
    @Override
    public programmingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;

        view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);

        return new programmingViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final programmingViewHolder holder, final int position) {

        final String slotPath = data.get(position);
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppCompatActivity activity = (AppCompatActivity) view.getContext();
                Fragment myFragment = new TutorSlotDetails(databaseReference.child(slotPath),attended.get(position));
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, myFragment, "FRAGMENT").addToBackStack("FRAGMENT_OTHER").commit();

            }
        });

        databaseReference.child(slotPath).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    //SlotsClass slot = dataSnapshot.getValue(SlotsClass.class);
                    //holder.subjectName.setText(dataSnapshot.child("time").getValue(String.class) + " @ " + dataSnapshot.child("date").getValue(String.class));
                    holder.date.setText(dataSnapshot.child("date").getValue(String.class));
                    holder.time.setText(dataSnapshot.child("time").getValue(String.class));
                    holder.subject.setText(databaseReference.child(slotPath).getParent().getParent().getParent().getKey());
                }
                Log.i("as", "onslotaddChange: added");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        //holder.subjectName.setText(subjectName);

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class programmingViewHolder extends RecyclerView.ViewHolder{
        TextView date,time,subject;
        LinearLayout linearLayout;
        public programmingViewHolder(@NonNull View itemView) {
            super(itemView);
            //subjectName=(TextView)itemView.findViewById(R.id.subjectName);
            date=itemView.findViewById(R.id.datePast);
            time=itemView.findViewById(R.id.timePast);
            subject=itemView.findViewById(R.id.subjectPast);
            linearLayout=(LinearLayout)itemView.findViewById(R.id.pant);
        }
    }
}

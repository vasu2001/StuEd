package com.stued.StuEd.Student_ui;

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

import com.stued.StuEd.Model_Classes.SlotBookingClass;
import com.stued.StuEd.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class BookedSlotProgrammingAdapter extends RecyclerView.Adapter<BookedSlotProgrammingAdapter.programmingViewHolder> {

    private List<SlotBookingClass> data;
    private LayoutInflater layoutInflater;
    private DatabaseReference databaseReference;
    public BookedSlotProgrammingAdapter(Context context, List<SlotBookingClass> data)
    {
        this.data=data;
        this.layoutInflater=LayoutInflater.from(context);
        this.databaseReference= FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public int getItemViewType(int position) {
        final int currentRating=data.get(position).rating;
        return ((currentRating==-1) ? R.layout.upcoming_slots : R.layout.past_slot);
    }

    @NonNull
    @Override
    public  programmingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=layoutInflater.inflate(viewType,parent,false);
        return new  programmingViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final programmingViewHolder holder, int position) {
        final String slotPath=data.get(position).slotPath;
        final int currentRating=data.get(position).rating;
        //if rating=-1 slot has not been done yet
        //if rating=0 slot has been completed but rating has not been given by this user
        //if rating bw 1-5 rating has been given

        databaseReference.child(slotPath).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    //SlotsClass slot = dataSnapshot.getValue(SlotsClass.class);
                    //holder.subjectName.setText(dataSnapshot.child("time").getValue(String.class) + " @ " + dataSnapshot.child("date").getValue(String.class));
                    holder.date.setText(dataSnapshot.child("date").getValue(String.class));
                    holder.time.setText(dataSnapshot.child("time").getValue(String.class));
                    holder.subject.setText(databaseReference.child(slotPath).getParent().getParent().getParent().getKey());
                    holder.linearLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            AppCompatActivity activity = (AppCompatActivity) view.getContext();
                            Fragment myFragment;
                            if(currentRating==-1)
                                myFragment = new TutorSlotBooked(layoutInflater,databaseReference.child(slotPath));
                            else
                                myFragment=new PastSlotFragment(layoutInflater,databaseReference.child(slotPath),currentRating);
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

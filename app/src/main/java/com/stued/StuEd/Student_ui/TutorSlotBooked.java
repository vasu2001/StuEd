package com.stued.StuEd.Student_ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.stued.StuEd.Model_Classes.TinyDBorderID;
import com.stued.StuEd.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class TutorSlotBooked extends Fragment
{
    View view;
    private LayoutInflater layoutInflater;
    private DatabaseReference databaseReference;

    public TutorSlotBooked(LayoutInflater layoutInflater, DatabaseReference databaseReference) {
        this.layoutInflater = layoutInflater;
        this.databaseReference=databaseReference;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = layoutInflater.inflate(R.layout.activity_tutor_slot_booked , container, false);

        final TextView topicName = view.findViewById(R.id.mtopic);
        TextView subjectName = view.findViewById(R.id.msubject);
        final TextView teacherName = view.findViewById(R.id.mteacher);
        final TextView date = view.findViewById(R.id.mdate);
        final TextView time = view.findViewById(R.id.mtime);
        final TextView fees = view.findViewById(R.id.mfees);
        final TextView teacherPhone = view.findViewById(R.id.mphone);
        final TextView teacherEmail = view.findViewById(R.id.memail);
        final TextView estimatedMarks = view.findViewById(R.id.estmarks);
        final TextView venue1 = view.findViewById(R.id.venue1);
        final TextView venue2 = view.findViewById(R.id.venue2);
        final TextView otp=view.findViewById(R.id.motp);

        final String uid=databaseReference.getKey();
        databaseReference.getParent().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                topicName.setText(dataSnapshot.child("topicName").getValue(String.class));
                estimatedMarks.setText(dataSnapshot.child("estimatedMarks").getValue(String.class));
                DataSnapshot slotSnapshot = dataSnapshot.child(uid);
                date.setText(slotSnapshot.child("date").getValue(String.class));
                time.setText(slotSnapshot.child("time").getValue(String.class));
                fees.setText("Rs. "+slotSnapshot.child("fees").getValue(String.class));
                venue1.setText(slotSnapshot.child("venue1").getValue(String.class));
                venue2.setText(slotSnapshot.child("venue2").getValue(String.class));
                otp.setText(slotSnapshot.child("students").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("otp").getValue(Integer.class)+"");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        subjectName.setText(databaseReference.getParent().getParent().getParent().getKey());
        DatabaseReference teacehrReference = FirebaseDatabase.getInstance().getReference((new TinyDBorderID(getActivity())).getString("collegeName")).child("Users").child(databaseReference.getParent().getKey());
        teacehrReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                teacherName.setText(dataSnapshot.child("Username").getValue(String.class));
                teacherEmail.setText(dataSnapshot.child("Email").getValue(String.class));
                teacherPhone.setText(dataSnapshot.child("PhoneNo").getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return view;
    }

}

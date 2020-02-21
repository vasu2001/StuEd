package com.stued.StuEd.Tutor_ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.stued.StuEd.Model_Classes.TinyDBorderID;
import com.stued.StuEd.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class attendance extends Fragment {

    private DatabaseReference databaseReference;
    final ArrayList<String> student,student1;
    final ArrayList<Integer> otp;


    public attendance(DatabaseReference databaseReference,ArrayList<String> student,final ArrayList<Integer> otp) {
        this.databaseReference = databaseReference;
        this.student=student;
        this.otp=otp;
        student1=new ArrayList<>(student);
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View view=inflater.inflate(R.layout.attendance_otp,container,false);

        RecyclerView recyclerView=view.findViewById(R.id.studentOTPlist);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        //read  data here

        attendanceAdapter programmingAdapter=new attendanceAdapter(student1,otp,databaseReference);
        recyclerView.setAdapter(programmingAdapter);


        Button endslot=view.findViewById(R.id.endSlot);
        endslot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String slotUID=databaseReference.getKey();
                DatabaseReference studentReference = FirebaseDatabase.getInstance().getReference((new TinyDBorderID(getActivity())).getString("collegeName")).child("Users");
                for(String studentUID: student){
                    studentReference.child(studentUID).child("slots").child(slotUID).child("rating").setValue(0);
                }
                studentReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("slots").child(slotUID).child("attended").setValue(student.size()-student1.size());

                //call cloud funcn for broadcast topic message using fsm
            }
        });
        return view;
    }
}

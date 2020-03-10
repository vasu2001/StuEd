package com.stued.StuEd.Tutor_ui;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.ImageView;

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
    private Dialog dialog;
    private LayoutAnimationController animation;


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
        int resId = R.anim.layout_animation_fall_down;
        animation = AnimationUtils.loadLayoutAnimation(getContext(), resId);

        if(view.getContext()!=null) {
            RecyclerView recyclerView = view.findViewById(R.id.studentOTPlist);
            recyclerView.setLayoutAnimation(animation);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            attendanceAdapter programmingAdapter = new attendanceAdapter(student1, otp, databaseReference);
            recyclerView.setAdapter(programmingAdapter);
        }

        //read  data here


        dialog=new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_payment);
        dialog.setCanceledOnTouchOutside(false);
        dialog.dismiss();
        final ImageView saveButton = dialog.findViewById(R.id.save22);
        final ImageView cancelButton = dialog.findViewById(R.id.cancel22);

        Button endslot=view.findViewById(R.id.endSlot);
        endslot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
                saveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String slotUID=databaseReference.getKey();
                        DatabaseReference studentReference = FirebaseDatabase.getInstance().getReference((new TinyDBorderID(getActivity())).getString("collegeName")).child("Users");
                        for(String studentUID: student){
                            studentReference.child(studentUID).child("slots").child(slotUID).child("rating").setValue(0);
                        }
                        studentReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("slots").child(slotUID).child("attended").setValue(student.size()-student1.size());

                        //call cloud funcn for broadcast topic message using fsm

                    }
                });
                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

            }
        });
        return view;
    }
}

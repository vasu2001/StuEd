package com.stued.StuEd.Tutor_ui;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.stued.StuEd.Model_Classes.SlotsClass;
import com.stued.StuEd.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class TutorSlotDetails extends Fragment{


    Button StudentsRegistered,startSlotButton;
    //private LayoutInflater layoutInflater;
    private DatabaseReference databaseReference;
    private int attended;
    private Dialog dialog;

    public TutorSlotDetails(DatabaseReference databaseReference,int attended) {
        this.databaseReference = databaseReference;
        this.attended=attended;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view=inflater.inflate(R.layout.activity_tutor_slot_details,container,false);

        final TextView
                subjectName = view.findViewById(R.id.msubject),
                topicName = view.findViewById(R.id.mtopic),
                date = view.findViewById(R.id.mdate),
                time = view.findViewById(R.id.mtime),
                fees = view.findViewById(R.id.mfees),
                venue1 = view.findViewById(R.id.venue1),
                venue2 = view.findViewById(R.id.venue2),
                maxStudents1 = view.findViewById(R.id.max_students),
                estimatedMarks = view.findViewById(R.id.estmarks);
        final String uid = databaseReference.getKey();
        //final ArrayList<String> studentUIDs=new ArrayList<>();
        StudentsRegistered= (Button) view.findViewById(R.id.stuRegistered);
        startSlotButton=(Button)view.findViewById(R.id.startSlotButton);
        ImageView image = (ImageView) view.findViewById(R.id.edit);

        if(attended!=-1) {
            startSlotButton.setVisibility(View.INVISIBLE);
            startSlotButton.setClickable(false);
            image.setVisibility(View.INVISIBLE);
            image.setClickable(false);

        }
        dialog=new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.startslotdialog);
        dialog.setCanceledOnTouchOutside(false);
        dialog.dismiss();
        final ImageView yes = dialog.findViewById(R.id.save223);
        final ImageView no = dialog.findViewById(R.id.cancel23);

        subjectName.setText(databaseReference.getParent().getParent().getParent().getKey());
        final ArrayList<String> student=new ArrayList<>();
        final ArrayList<Integer> otp=new ArrayList<>();

        databaseReference.getParent().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                topicName.setText(dataSnapshot.child("topicName").getValue(String.class));
                estimatedMarks.setText(dataSnapshot.child("estimatedMarks").getValue(String.class));

                SlotsClass slotdata = dataSnapshot.child(uid).getValue(SlotsClass.class);

                fees.setText("Rs. "+slotdata.fees);
                date.setText(slotdata.date);
                time.setText(slotdata.time);
                venue1.setText(slotdata.venue1);
                venue2.setText(slotdata.venue2);
                maxStudents1.setText((slotdata.maxStudents)+"");

                if(slotdata.currentStudents==0)
                {
                    //student.add("No student added");
                }
                else{
                    for(DataSnapshot studentSnapshot : dataSnapshot.child(uid).child("students").getChildren()){
                        student.add(studentSnapshot.getKey());
                        if(!studentSnapshot.child("validated").getValue(Boolean.class))
                            otp.add(studentSnapshot.child("otp").getValue(Integer.class));
                        else
                            otp.add(-1);
                    }
                }


                StudentsRegistered.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AppCompatActivity activity = (AppCompatActivity) view.getContext();
                        Fragment myFragment = new ListOfStudents(student,maxStudents1.getText().toString());
                        activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, myFragment,"FRAGMENT").addToBackStack("FRAGMENT_OTHER").commit();
                    }
                });

                        startSlotButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.show();
                                yes.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        databaseReference.child("slotStatus").setValue(false);
                                        AppCompatActivity activity = (AppCompatActivity) view.getContext();
                                        Fragment myFragment = new attendance(databaseReference,student,otp);
                                        activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, myFragment,"FRAGMENT").addToBackStack("FRAGMENT_OTHER").commit();
                                dialog.dismiss();
                                    }
                                });

                            }
                        });


                no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        image.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick( View view)
            {

                final Dialog dialog=new Dialog(getView().getContext());

                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialogslot);
                dialog.show();
                final EditText newVenue1 = dialog.findViewById(R.id.newvenue);
                final EditText newMaxStudents = dialog.findViewById(R.id.newMaxStudent);
                final ImageView editButton = dialog.findViewById(R.id.editSlotbutton);

                newVenue1.setHint(venue1.getText().toString());

                databaseReference.child("maxStudents").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        newMaxStudents.setHint(dataSnapshot.getValue(Long.class).toString());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                editButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String
                                updateMaxStudents = newMaxStudents.getText().toString(),
                                updateVenue = newVenue1.getText().toString();

                        if(!updateMaxStudents.isEmpty()) databaseReference.child("maxStudents").setValue(Integer.parseInt(updateMaxStudents));
                        if(!updateVenue.isEmpty()) databaseReference.child("venue1").setValue(updateVenue);

                        dialog.cancel();
                    }
                });
            }
        });


        return view;
    }


}

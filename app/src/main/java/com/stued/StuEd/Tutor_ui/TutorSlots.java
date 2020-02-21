package com.stued.StuEd.Tutor_ui;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.stued.StuEd.Model_Classes.SlotsClass;
import com.stued.StuEd.Model_Classes.TinyDBorderID;
import com.stued.StuEd.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.net.URLDecoder;
import java.util.Calendar;

public class TutorSlots extends Fragment
{
    private DatabaseReference reference;
    private Dialog dp,tp;
    private String amPm;
    private RadioGroup preferences;
    private EditText maxstudents,feesD,venue1,venue2;
    private TextView dateD,timeD;
    private DatabaseReference userReference;

    public TutorSlots(DatabaseReference reference) {
        this.reference = reference;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //very imp
        final View view = inflater.inflate(R.layout.activity_tutor_slots, container, false);

        Button datePicker = view.findViewById(R.id.datePicker);
        Button timePicker = view.findViewById(R.id.timepicker);
        Button addSlot = view.findViewById(R.id.AddSlot);
        //Done=view.findViewById(R.id.Done);
        maxstudents=view.findViewById(R.id.Students);
        dateD=view.findViewById(R.id.mdate);
        timeD=view.findViewById(R.id.mtime);
        feesD=view.findViewById(R.id.mFees);
        venue1=view.findViewById(R.id.venue1);
        venue2=view.findViewById(R.id.venue2);
        preferences=view.findViewById(R.id.preference);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();




        userReference = FirebaseDatabase.getInstance().getReference((new TinyDBorderID(getActivity())).getString("collegeName")).child("Users").child(mAuth.getCurrentUser().getUid());


        datePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c= Calendar.getInstance();
                int  mDay=c.get(Calendar.DAY_OF_MONTH);
                int mMonth=c.get(Calendar.MONTH);
                int mYear=c.get(Calendar.YEAR);
                // Log.i("todaydate", " "+day+" "+month+" "+year);

                dp=new DatePickerDialog(view.getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        if(day<10 && month+1<10)
                            dateD.setText("0"+day+"/0"+(month+1)+"/"+year);
                        else if (day<10)
                            dateD.setText("0"+day+"/"+(month+1)+"/"+year);
                        else if(month+1<10)
                            dateD.setText(day+"/0"+(month+1)+"/"+year);
                        else
                            dateD.setText(day+"/"+(month+1)+"/"+year);
                    }
                },mYear,mMonth,mDay);
                dp.show();
            }
        });

        timePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tp= new TimePickerDialog(view.getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hoursOfDay, int minutes) {
                        if(hoursOfDay>=12)
                        {
                            amPm="PM";
                            hoursOfDay=hoursOfDay-12;
                        }
                        else
                            amPm="AM";
                        if(hoursOfDay<10 && minutes<10){
                            timeD.setText("0"+hoursOfDay+":0"+minutes+" "+amPm);}
                        else if(hoursOfDay<10){
                            timeD.setText("0"+hoursOfDay+":"+minutes+" "+amPm);}
                        else if(minutes<10){
                            timeD.setText(hoursOfDay+":0"+minutes+" "+amPm);}
                        else {
                            timeD.setText(hoursOfDay+":"+minutes+" "+amPm);}
                    }
                },0,0,false);
                tp.show();
            }
        });

        addSlot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String date=dateD.getText().toString();
                final String time=timeD.getText().toString();
                final String fees=feesD.getText().toString();
                final String maxStudents = maxstudents.getText().toString();
                final String venueName1 = venue1.getText().toString();
                final String venueName2 = venue2.getText().toString();

                String genderPreference=null;

                switch (preferences.getCheckedRadioButtonId()){
                    case R.id.checkBox: genderPreference="Boys only";
                        break;
                    case R.id.checkBox2: genderPreference="Girls only";
                        break;
                    case R.id.checkBox3: genderPreference="Both";
                        break;

                }

                if(date.isEmpty() || time.isEmpty() || fees.isEmpty() ||  maxStudents.isEmpty() || venueName1.isEmpty() || venueName2.isEmpty() || genderPreference.isEmpty()){
                    Toast.makeText(getActivity(),"Enter all the details",Toast.LENGTH_SHORT).show();
                    return;
                }

                SlotsClass hola=new SlotsClass(
                        date,
                        time,
                        fees,
                        Integer.parseInt(maxStudents),
                        venueName1,
                        venueName2,
                        genderPreference
                );
                String uid= reference.push().getKey();
                reference.child(uid).setValue(hola)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(view.getContext(),"Slot Added", Toast.LENGTH_LONG).show();
                                dateD.setText(null);
                                timeD.setText(null);
                                feesD.setText(null);
                                maxstudents.setText(null);
                                venue1.setText(null);
                                venue2.setText(null);
                            }
                        });

                DatabaseReference tempReference = userReference.child("slots").child(uid);
                tempReference.child("slotPath").setValue(URLDecoder.decode(reference.child(uid).toString().substring(reference.getRoot().toString().length())),"UTF-8");
                tempReference.child("attended").setValue(-1);

            }
        });


        return view;
    }
}

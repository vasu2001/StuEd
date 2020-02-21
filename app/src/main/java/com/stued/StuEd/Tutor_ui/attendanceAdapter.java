package com.stued.StuEd.Tutor_ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.stued.StuEd.Model_Classes.TinyDBorderID;
import com.stued.StuEd.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class attendanceAdapter extends RecyclerView.Adapter<attendanceAdapter.programmingViewHolder> {

    private ArrayList<String> data;
    private ArrayList<Integer> otp;
    private DatabaseReference databaseReference;
    View view;

    public attendanceAdapter(ArrayList<String> data, ArrayList<Integer> otp, DatabaseReference databaseReference){

        this.data=data;
        this.otp=otp;
        this.databaseReference=databaseReference;

        for(int i=otp.size()-1;i>=0;i--){
            if(otp.get(i)==-1) {
                otp.remove(i);
                data.remove(i);
            }
        }
    }

    @NonNull
    @Override
    public programmingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
         view=inflater.inflate(R.layout.otp,parent,false);
        return new programmingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final programmingViewHolder holder, final int position) {

       /* String studentName = data[position];
            holder.name.setText(studentName);*/

        final String student_uid=data.get(position);
        FirebaseDatabase.getInstance().getReference((new TinyDBorderID(view.getContext())).getString("collegeName")).child("Users").child(student_uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                holder.name.setText(dataSnapshot.child("Username").getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

       final Integer currentOTP = otp.get(position);

       holder.validate.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
                String userInput = holder.otp.getText().toString();
                if(userInput.isEmpty()){
                    Toast.makeText(view.getContext(),"Enter OTP",Toast.LENGTH_SHORT).show();
                }
                else if(Integer.parseInt(userInput)!=currentOTP){
                    Toast.makeText(view.getContext(),"Incorrect OTP",Toast.LENGTH_SHORT).show();
                    holder.otp.setText("");
                }
                else{
                    Toast.makeText(view.getContext(),"OTP validated successfully",Toast.LENGTH_SHORT).show();
                    databaseReference.child("students").child(student_uid).child("validated").setValue(true);
                    otp.remove(position);
                    data.remove(position);
                    notifyDataSetChanged();
                }
           }
       });


    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class programmingViewHolder extends RecyclerView.ViewHolder{

        private TextView name;
        private EditText otp;
        private Button validate;
        public programmingViewHolder(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.studenname);
            otp=itemView.findViewById(R.id.otp);
            validate=itemView.findViewById(R.id.validate);
            validate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
        }
    }
}

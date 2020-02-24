package com.stued.StuEd.Tutor_ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.stued.StuEd.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

public class TutorTopicDescription extends Fragment {

    //Calendar c;
    private Button slotButton;
    private TextView topicName;
    private EditText estmarks,topicdescription,estTime;
    private FirebaseAuth mAuth;
    View view;
    private LayoutInflater layoutInflater;
    private DatabaseReference databaseReference,userReference;

    public TutorTopicDescription(LayoutInflater layoutInflater, DatabaseReference databaseReference) {
        this.layoutInflater = layoutInflater;
        this.databaseReference=databaseReference;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = layoutInflater.inflate(R.layout.activity_tutor_topic_description, container, false);

        topicName=view.findViewById(R.id.topic);
        estTime=view.findViewById(R.id.Estimated_Time);
        topicdescription=view.findViewById(R.id.topicdescription);
        estmarks=view.findViewById(R.id.Marks);
        slotButton=view.findViewById(R.id.slotButton);
        mAuth=FirebaseAuth.getInstance();


        topicName.setText(databaseReference.getKey());
        final DatabaseReference reference = databaseReference.child(mAuth.getCurrentUser().getUid());

        slotButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String estimatedMarks= estmarks.getText().toString();
                final String estimatedTime= estTime.getText().toString();
                final String topicDescription = topicdescription.getText().toString();

                reference.child("topicDescription").setValue(topicDescription);
                reference.child("topicName").setValue(topicName.getText());
                reference.child("estimatedMarks").setValue(estimatedMarks);
                reference.child("estimatedTime").setValue(estimatedTime);

                if(TextUtils.isEmpty(estimatedMarks) || TextUtils.isEmpty(estimatedTime) || TextUtils.isEmpty(topicDescription))
                {
                    Toast.makeText(getActivity(), "Please fill all the fields", Toast.LENGTH_SHORT).show();
                }
                else {

                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    TutorSlots slotFragment = new TutorSlots(reference);
                    transaction.replace(R.id.fragmentContainer, slotFragment).commit();
                }

            }
        });


        return view;
    }

}

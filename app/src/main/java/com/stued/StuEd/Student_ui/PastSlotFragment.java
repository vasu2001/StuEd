package com.stued.StuEd.Student_ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.stued.StuEd.Model_Classes.TinyDBorderID;
import com.stued.StuEd.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hsalf.smilerating.SmileRating;

public class PastSlotFragment extends Fragment {
    View view;
    private LayoutInflater layoutInflater;
    private DatabaseReference databaseReference;
    private int currentRating;

    public PastSlotFragment(LayoutInflater layoutInflater, DatabaseReference databaseReference, int currentRating) {
        this.layoutInflater = layoutInflater;
        this.databaseReference = databaseReference;
        this.currentRating = currentRating;
    }

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.review, container, false);
        final SmileRating smileRating = (SmileRating) view.findViewById(R.id.smile_rating);

        final TextView
                subjectTextView = view.findViewById(R.id.subjectTextVIew),
                topicTextView = view.findViewById(R.id.topicTextView),
                teacherTextView = view.findViewById(R.id.teacherTextView),
                dateTextView = view.findViewById(R.id.dateTextView),
                timeTextView = view.findViewById(R.id.timeTextView),
                feesTextView = view.findViewById(R.id.feesTextView);
        final Button ratingButton = view.findViewById(R.id.submit);

        subjectTextView.setText(databaseReference.getParent().getParent().getParent().getKey());
        topicTextView.setText(databaseReference.getParent().getParent().getKey());

        final String teacherUID = databaseReference.getParent().getKey();
        final DatabaseReference teacherReference = FirebaseDatabase.getInstance().getReference((new TinyDBorderID(getActivity())).getString("collegeName")).child("Users").child(teacherUID);

        teacherReference.child("Username").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                teacherTextView.setText(dataSnapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dateTextView.setText(dataSnapshot.child("date").getValue(String.class));
                timeTextView.setText(dataSnapshot.child("time").getValue(String.class));
                feesTextView.setText(dataSnapshot.child("fees").getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        if (currentRating != 0) {

            ratingButton.setClickable(false);
            ratingButton.setVisibility(View.INVISIBLE);
            smileRating.setSelectedSmile(currentRating - 1);
            smileRating.setIndicator(true);

        } else {
            final String userUID = FirebaseAuth.getInstance().getUid();
            final DatabaseReference userReference = FirebaseDatabase.getInstance().getReference((new TinyDBorderID(getActivity())).getString("collegeName")).child("Users").child(userUID).child("slots").child(databaseReference.getKey());

            ratingButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final int rating = smileRating.getRating();
                    if (rating == 0) {
                        Toast.makeText(getActivity(), "Tell us how was the class!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    userReference.child("rating").setValue(rating);
                    teacherReference.child("rating").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            int newRating = dataSnapshot.getValue(Integer.class)+rating;
                            teacherReference.child("rating").setValue(newRating);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    teacherReference.child("noOfRating").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            int newNoOfRating = dataSnapshot.getValue(Integer.class) + 1;
                            teacherReference.child("noOfRating").setValue(newNoOfRating);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            });


        }


        return view;
    }
}

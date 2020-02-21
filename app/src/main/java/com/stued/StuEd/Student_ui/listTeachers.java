package com.stued.StuEd.Student_ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.stued.StuEd.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class listTeachers extends Fragment {

    private DatabaseReference databaseReference;

    public listTeachers(DatabaseReference databaseReference) {
        this.databaseReference = databaseReference;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        if (container != null) {
            getActivity().getFragmentManager().popBackStack();
            container.removeAllViews();
        }

        final View view=inflater.inflate(R.layout.list_teacher_fragment,container,false);

        final ArrayList<String> teachersUID=new ArrayList<>();

        //list add
        //
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                teachersUID.clear();
                Log.i("teacher", "teacher count: "+dataSnapshot.getChildrenCount());
                for(DataSnapshot teacherSnapshot : dataSnapshot.getChildren()){
                    String userUID = teacherSnapshot.getKey();
                    teachersUID.add(userUID);

                    /*userReference.child(userUID).child("Username").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String username = dataSnapshot.getValue(String.class);
                            teachers.add(username);
                            Log.i("teacher", "teacher name: "+username);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });*/
                }
                RecyclerView recyclerView=view.findViewById(R.id.subjectList3);
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                TeacherProgrammingAdaptor programmingAdapter=new TeacherProgrammingAdaptor(getActivity(),teachersUID, databaseReference);
                recyclerView.setAdapter(programmingAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return view;
    }

}

package com.stued.StuEd.Tutor_ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.stued.StuEd.R;

import java.util.ArrayList;

public class ListOfStudents extends Fragment {

    private ArrayList<String> studentUID;
    String currentStudents,maxStudents;

    public ListOfStudents(ArrayList<String> student, String maxStudents) {
        this.studentUID = student;
        this.maxStudents = maxStudents;
        this.currentStudents = student.size()+"";
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view=inflater.inflate(R.layout.activity_list_of_students,container,false);

        TextView studentCount = view.findViewById(R.id.numstudents);
        studentCount.setText(currentStudents+"/"+maxStudents);

        if(view.getContext()!=null) {
            RecyclerView recyclerView = view.findViewById(R.id.subjectList);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            StudentRegisteredAdapter programmingAdapter = new StudentRegisteredAdapter(view.getContext(), studentUID);
            TextView emptyView = view.findViewById(R.id.empty_view1);

            if (studentUID.isEmpty()) {
                recyclerView.setVisibility(View.GONE);
                emptyView.setText("No Student Registered!!");
                emptyView.setVisibility(View.VISIBLE);

            } else {
                recyclerView.setVisibility(View.VISIBLE);
                emptyView.setVisibility(View.GONE);

            }
            recyclerView.setAdapter(programmingAdapter);
        }

        return view;
    }
}

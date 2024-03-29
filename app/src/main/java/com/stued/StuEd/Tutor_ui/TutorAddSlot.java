package com.stued.StuEd.Tutor_ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.stued.StuEd.R;
import com.stued.StuEd.Model_Classes.TinyDBorderID;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class TutorAddSlot extends Fragment {
  private   RecyclerView recyclerView;
    Spinner branch, semester;
    private TextView emptyView;
    View Home;
    private String semesterValue, branchValue;
    private List<String> subjects;
    private LayoutAnimationController animation;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //very imp
        View view = inflater.inflate(R.layout.home_fragment, container, false);
        this.Home = view;
        init();
        //list
        listFetcher();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.Home = view;
        final TinyDBorderID tinyDBorderID = new TinyDBorderID(getActivity());
        int resId = R.anim.layout_animation_fall_down;
        animation = AnimationUtils.loadLayoutAnimation(getContext(), resId);
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(getActivity(), R.array.Branch, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        branch.setAdapter(adapter1);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(getActivity(), R.array.Sem, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        semester.setAdapter(adapter2);

        int initialSem=0,initialYear=0;
        String temp;

        temp=tinyDBorderID.getString("initialSem");
        if(temp!=null&&!temp.isEmpty())initialSem=Integer.parseInt(temp);

        temp=tinyDBorderID.getString("initialYear");
        if(temp!=null&&!temp.isEmpty())initialYear=Integer.parseInt(temp);

        branch.setSelection(initialSem);
        semester.setSelection(initialYear);

        branch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                branchValue = branch.getItemAtPosition(position).toString();
                tinyDBorderID.putString("initialSem",position+"");
                listFetcher();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        semester.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                semesterValue = semester.getItemAtPosition(position).toString();
                tinyDBorderID.putString("initialYear",position+"");
                listFetcher();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    public void init() {
        semester = Home.findViewById(R.id.semester);
        branch = Home.findViewById(R.id.branch);
        recyclerView = Home.findViewById(R.id.subjectList);
        semesterValue = "1";
        branchValue = "ECE";
        subjects = new ArrayList<>();
        emptyView=Home.findViewById(R.id.empty_view6);
    }

    private void listFetcher() {

        recyclerView.setVisibility(View.INVISIBLE);
        emptyView.setVisibility(View.VISIBLE);
        String collegeName = (new TinyDBorderID(getActivity())).getString("collegeName");
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(collegeName).child("Slots").child(branchValue.trim()).child(semesterValue.trim());
        final DatabaseReference subjectReference = FirebaseDatabase.getInstance().getReference(collegeName).child("Subjects").child(branchValue.trim()+"_"+semesterValue.trim());

        subjectReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                subjects.clear();

                Log.i(TAG, branchValue + semesterValue + dataSnapshot.getChildrenCount());
                for (DataSnapshot subjectSnapshot : dataSnapshot.getChildren()) {
                    subjects.add(subjectSnapshot.getKey());
                    Log.i(TAG, "onDataChange1: " + subjectSnapshot.getKey());
                }

                if(Home.getContext()!=null) {
                    if(subjects.isEmpty())
                    {
                        emptyView.setText("No Subjects");
                        emptyView.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.INVISIBLE);
                    }
                    else
                    {
                        emptyView.setText("Loading...");
                        emptyView.setVisibility(View.INVISIBLE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                    recyclerView.setLayoutAnimation(animation);
                    recyclerView.setLayoutManager(new LinearLayoutManager(Home.getContext()));
                    tprogAdapter listAdapter = new tprogAdapter(Home.getContext(), subjects, databaseReference);
                    recyclerView.setAdapter(listAdapter);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


}



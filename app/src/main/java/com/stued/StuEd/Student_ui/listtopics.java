 package com.stued.StuEd.Student_ui;

 import android.os.Bundle;
 import android.text.Editable;
 import android.text.TextWatcher;
 import android.util.Log;
 import android.view.LayoutInflater;
 import android.view.View;
 import android.view.ViewGroup;
 import android.widget.EditText;
 import android.widget.TextView;

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


public class listtopics extends Fragment
{

    EditText editTextSearch;
    private DatabaseReference databaseReference;
    private ArrayList<String> topics=new ArrayList<>();
    listtopicadapter adapter;

    public listtopics(DatabaseReference databaseReference) {
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


        //very imp
        final View view=inflater.inflate(R.layout.listtopics_fragment,container,false);
        //list
        final TextView emptyview=view.findViewById(R.id.empty_view4);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
         @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.i("topic", "listcount: "+dataSnapshot.getChildrenCount());
                topics.clear();
                for(DataSnapshot topicSnapshot : dataSnapshot.getChildren()){
                    topics.add(topicSnapshot.getKey());
                    Log.i("topic", "onTopicAdd: "+topicSnapshot.child("topicName").getValue(String.class));
                }
                Log.i("topic", "loop ended");

                //here
                editTextSearch=(EditText) view.findViewById(R.id.editText4);
                editTextSearch.setSelection(editTextSearch.getText().length());
                final listtopicadapter programmingAdapter=new listtopicadapter(getActivity(),databaseReference,topics);

                editTextSearch.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        filter(editable.toString(),programmingAdapter);

                    }
                });

                RecyclerView recyclerView=view.findViewById(R.id.subjectList2);

             if(topics.isEmpty())
             {
                 emptyview.setText("No Topic Available!!");
                 emptyview.setVisibility(View.VISIBLE);
                 recyclerView.setVisibility(View.INVISIBLE);
             }
             else
             {
                 emptyview.setVisibility(View.INVISIBLE);
                 recyclerView.setVisibility(View.VISIBLE);
             }
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                recyclerView.setAdapter(programmingAdapter);
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        Log.i("topic", "listTopic "+topics.toString());





        return view;
    }

    private void filter(String text,listtopicadapter programmingAdapter) {
        ArrayList<String> filterd= new ArrayList<>();
        for (String s: topics){
            if (s.toLowerCase().contains(text.toLowerCase())){
                filterd.add(s);
            }
        }
        programmingAdapter.filterList(filterd);
    }
    }



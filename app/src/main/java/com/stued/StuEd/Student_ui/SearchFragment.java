package com.stued.StuEd.Student_ui;

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

import com.stued.StuEd.Model_Classes.SlotBookingClass;
import com.stued.StuEd.Model_Classes.TinyDBorderID;
import com.stued.StuEd.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class SearchFragment extends Fragment {
    View v;

    int partition(ArrayList<SlotBookingClass> arr, int low, int high)
    {
        int pivot = arr.get(high).rating;
        int i = (low-1); // index of smaller element
        for (int j=low; j<high; j++)
        {
            // If current element is smaller than the pivot
            if (arr.get(j).rating < pivot)
            {
                i++;

                // swap arr[i] and arr[j]
                Collections.swap(arr,i,j);
            }
        }

        // swap arr[i+1] and arr[high] (or pivot)
        Collections.swap(arr,i+1,high);

        return i+1;
    }


    /* The main function that implements QuickSort()
      arr[] --> Array to be sorted,
      low  --> Starting index,
      high  --> Ending index */
    void sort(ArrayList<SlotBookingClass> arr, int low, int high)
    {
        if (low < high)
        {
            /* pi is partitioning index, arr[pi] is
              now at right place */
            int pi = partition(arr, low, high);

            // Recursively sort elements before
            // partition and after partition
            sort(arr, low, pi-1);
            sort(arr, pi+1, high);
        }
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v= inflater.inflate(R.layout.activity_tutor_home,container,false );
        DatabaseReference userReference= FirebaseDatabase.getInstance().getReference((new TinyDBorderID(getActivity())).getString("collegeName")).child("Users").child(FirebaseAuth.getInstance().getUid()).child("slots");
        final RecyclerView recyclerView=v.findViewById(R.id.subjectList2);
        final TextView emptyview=v.findViewById(R.id.empty_view3);

        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<SlotBookingClass> slots=new ArrayList<>();
                if(dataSnapshot.exists()){
                    for(DataSnapshot slotSnapshot: dataSnapshot.getChildren()){
                        slots.add(slotSnapshot.getValue(SlotBookingClass.class));
                        //Log.i("directory",slotSnapshot.getValue(String.class));
                    }
                    sort(slots,0,slots.size()-1);
                    if(slots.isEmpty())
                    {
                        emptyview.setText("No Slots!!");
                        recyclerView.setVisibility(View.INVISIBLE);
                        emptyview.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        recyclerView.setVisibility(View.VISIBLE);
                        emptyview.setVisibility(View.INVISIBLE);
                    }
                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    BookedSlotProgrammingAdapter programmingAdapter=new BookedSlotProgrammingAdapter(v.getContext(),slots);
                    recyclerView.setAdapter(programmingAdapter);
                }
                else ;//showDialog();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return v;
    }
}

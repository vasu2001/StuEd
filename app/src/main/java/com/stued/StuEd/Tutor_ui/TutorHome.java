package com.stued.StuEd.Tutor_ui;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.stued.StuEd.Model_Classes.TinyDBorderID;
import com.stued.StuEd.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class TutorHome extends Fragment {
    View v;
    Button btn;

    Integer partition(@NonNull ArrayList<String> arr1,@NonNull ArrayList<Integer> arr2, Integer low, Integer high)
    {
        Integer pivot = arr2.get(high);
        Integer i = (low-1); // index of smaller element
        for (Integer j=low; j<high; j++)
        {
            // If current element is smaller than the pivot
            if (arr2.get(j)< pivot)
            {
                i++;

                // swap arr[i] and arr[j]
                Collections.swap(arr1,i,j);
                Collections.swap(arr2,i,j);

            }
        }

        // swap arr[i+1] and arr[high] (or pivot)
        Collections.swap(arr1,i+1,high);
        Collections.swap(arr2,i+1,high);


        return i+1;
    }


    /* The main function that implements QuickSort()
      arr[] --> Array to be sorted,
      low  --> Starting index,
      high  --> Ending index */
    void sort(@NonNull ArrayList<String> arr1,@NonNull ArrayList<Integer> arr2, Integer low, Integer high)
    {
        if(arr1==null || arr2==null)
            return;
        if (low < high)
        {
            /* pi is partitioning index, arr[pi] is
              now at right place */
            Integer pi = partition(arr1,arr2, low, high);

            // Recursively sort elements before
            // partition and after partition
            sort(arr1,arr2, low, pi-1);
            sort(arr1, arr2,pi+1, high);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        v= inflater.inflate(R.layout.activity_tutor_home,container,false );
        DatabaseReference userReference= FirebaseDatabase.getInstance().getReference((new TinyDBorderID(getActivity())).getString("collegeName")).child("Users").child(FirebaseAuth.getInstance().getUid()).child("slots");
        final RecyclerView recyclerView=v.findViewById(R.id.subjectList2);
        final TextView emptyview=v.findViewById(R.id.empty_view3);
        emptyview.setVisibility(View.INVISIBLE);
        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> slots=new ArrayList<>();
                ArrayList<Integer> attended= new ArrayList<>();
                if(dataSnapshot.exists()){
                    for(DataSnapshot slotSnapshot: dataSnapshot.getChildren()){
                        slots.add(slotSnapshot.child("slotPath").getValue(String.class));
                        attended.add(slotSnapshot.child("attended").getValue(Integer.class));
                        Log.i("attended", "onDataChange: "+slotSnapshot.child("attended").getValue(Integer.class));
                    }
                    sort(slots,attended,0,slots.size()-1);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    SlotsProgrammingAdaptor programmingAdapter=new SlotsProgrammingAdaptor(v.getContext(),slots,attended);
                    recyclerView.setAdapter(programmingAdapter);
                }
                else showDialog();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return v;
    }

    public void showDialog() {
        if (getActivity() != null) {

            final Dialog dialog = new Dialog(getActivity());
            final BottomNavigationView bottomNavigationView = (BottomNavigationView) getActivity().findViewById(R.id.bottomNavigationView);

            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.setContentView(R.layout.dialog);
            //TextView dialog_btn = dialog.findViewById(R.id.button);
            ImageView dialog_btn = dialog.findViewById(R.id.button);
            dialog_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    TutorAddSlot slotFragment = new TutorAddSlot();
                    transaction.replace(R.id.fragmentContainer, slotFragment).commit();

                    bottomNavigationView.setSelectedItemId(R.id.nav_add);
                    dialog.cancel();
                }
            });

            dialog.show();
        }
    }
}

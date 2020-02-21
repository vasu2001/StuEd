package com.stued.StuEd.Tutor_ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.stued.StuEd.R;
import com.google.firebase.database.DatabaseReference;

import java.util.List;

public class tprogAdapter extends RecyclerView.Adapter<tprogAdapter.programmingViewHolder> {

    private List<String> data;
    private LayoutInflater layoutInflater;
    DatabaseReference databaseReference;


    public tprogAdapter(Context context, List<String> data, DatabaseReference databaseReference)
    {
        this.data=data;
        this.layoutInflater=LayoutInflater.from(context);
        this.databaseReference=databaseReference;
    }
    @NonNull
    @Override
    public programmingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view=layoutInflater.inflate(R.layout.lists,parent,false);
        return new programmingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull programmingViewHolder holder, int position) {


        final String subjectName=data.get(position);
        holder.subjectName.setText(subjectName);
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppCompatActivity activity = (AppCompatActivity) view.getContext();
                Fragment myFragment = new TutorTopic (databaseReference.child(subjectName));
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, myFragment,"FRAGMENT").addToBackStack("FRAGMENT_OTHER").commit();

            }
        });

    }



    @Override
    public int getItemCount()
    {
        return data.size();
    }

    public class programmingViewHolder extends RecyclerView.ViewHolder{
        TextView subjectName;
        LinearLayout linearLayout;
        public programmingViewHolder(@NonNull View itemView) {
            super(itemView);
            subjectName=(TextView)itemView.findViewById(R.id.subjectName);
            subjectName.setSelected(true);
            linearLayout=(LinearLayout)itemView.findViewById(R.id.pant);
        }
    }

}

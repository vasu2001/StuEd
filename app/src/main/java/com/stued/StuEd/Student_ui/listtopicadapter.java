package com.stued.StuEd.Student_ui;

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

import java.util.ArrayList;
import java.util.List;

public class listtopicadapter extends RecyclerView.Adapter <listtopicadapter.programmingViewHolder> {

    private List<String> data;
    private LayoutInflater layoutInflater;
    private DatabaseReference databaseReference;

    public listtopicadapter(Context context, DatabaseReference databaseReference, List<String> data) {
        this.layoutInflater = LayoutInflater.from(context);
        this.databaseReference = databaseReference;
        this.data = data;

    }


    @NonNull
    @Override
    public programmingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.lists, parent, false);

        return new programmingViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull programmingViewHolder holder, final int position) {
        final String subjectName = data.get(position);
        holder.subjectName.setText(subjectName);
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppCompatActivity activity = (AppCompatActivity) view.getContext();
                Fragment myFragment = new listTeachers(databaseReference.child(subjectName));
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, myFragment, "FRAGMENT").addToBackStack("FRAGMENT_OTHER").commit();

            }
        });
    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    public class programmingViewHolder extends RecyclerView.ViewHolder {
        TextView subjectName;
        LinearLayout linearLayout;

        public programmingViewHolder(@NonNull View itemView) {
            super(itemView);
            subjectName = (TextView) itemView.findViewById(R.id.subjectName);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.pant);
            subjectName.setSelected(true);
        }


    }
    public void filterList(ArrayList<String> filterList)
    {
        this.data = filterList;
        notifyDataSetChanged();

    }
}
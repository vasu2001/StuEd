package com.stued.StuEd.Tutor_ui;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.stued.StuEd.R;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

public class tlistprogAdaptor extends RecyclerView.Adapter<tlistprogAdaptor.programmingViewHolder> {

    private List<String> data;
    private LayoutInflater layoutInflater;
    private DatabaseReference databaseReference;
    public tlistprogAdaptor(Context context, List<String> data, DatabaseReference databaseReference)
    {
        this.data=data;
        this.layoutInflater=LayoutInflater.from(context);
        this.databaseReference=databaseReference;
    }

    @Override
    public int getItemViewType(int position) {
        int l =data.size();
        return ((position == l) ? R.layout.addnewtopic : R.layout.lists);
    }
    @NonNull
    @Override
    public programmingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view;
        if(viewType==R.layout.lists)
        {
            view=LayoutInflater.from(parent.getContext()).inflate(R.layout.lists,parent,false);
        }
        else
        {
            view=LayoutInflater.from(parent.getContext()).inflate(R.layout.addnewtopic,parent,false);
        }
        return new programmingViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull programmingViewHolder holder, int position) {

        if(!(position==data.size())) {
            final String subjectName = data.get(position);
            holder.subjectName.setText(subjectName);
            holder.linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AppCompatActivity activity = (AppCompatActivity) view.getContext();
                    Fragment myFragment = new TutorTopicDescription(layoutInflater, databaseReference.child(subjectName));
                    activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, myFragment, "FRAGMENT").addToBackStack("FRAGMENT_OTHER").commit();

                }
            });
        }
        else{
            //Button button = (Button) view.findViewById(R.id.addslotbutton);
            holder.AddNewTopic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    final Dialog dialog=new Dialog(v.getContext());
                    //final BottomNavigationView bottomNavigationView = (BottomNavigationView) v.getRootView().findViewById(R.id.bottomNavigationView);

                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.dialog_topic);
                    ImageView dialog_btn=dialog.findViewById(R.id.buttonNewTopic);
                    final EditText subjectName = dialog.findViewById(R.id.newSubjectName);

                    dialog_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //bottomNavigationView.setSelectedItemId(R.id.nav_add);

                            String newSubjectNAme = subjectName.getText().toString();
                            if(newSubjectNAme.isEmpty()){
                                Toast.makeText(view.getContext(),"Enter topic name",Toast.LENGTH_SHORT).show();
                                return;
                            }
                            AppCompatActivity activity = (AppCompatActivity) v.getContext();
                            Fragment myFragment = new TutorTopicDescription(layoutInflater, databaseReference.child(newSubjectNAme));
                            activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, myFragment, "FRAGMENT").addToBackStack("FRAGMENT_OTHER").commit();


                            dialog.cancel();
                        }
                    });

                    dialog.show();
                }
            });
        }
    }

    @Override
    public int getItemCount()
    {
        return data.size()+1;
    }

    public class programmingViewHolder extends RecyclerView.ViewHolder{
        TextView subjectName;
        LinearLayout linearLayout;
        Button AddNewTopic;
        public programmingViewHolder(@NonNull View itemView) {
            super(itemView);
            subjectName=(TextView)itemView.findViewById(R.id.subjectName);
            linearLayout=(LinearLayout)itemView.findViewById(R.id.pant);
            AddNewTopic=itemView.findViewById(R.id.addslotbutton);
        }
    }

    public void filterList(ArrayList<String> filterList)
    {
        this.data = filterList;
        notifyDataSetChanged();

    }

}

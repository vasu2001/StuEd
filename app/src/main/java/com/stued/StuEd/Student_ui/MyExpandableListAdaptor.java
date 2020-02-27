package com.stued.StuEd.Student_ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.stued.StuEd.R;

import java.util.HashMap;
import java.util.List;

public class MyExpandableListAdaptor extends BaseExpandableListAdapter {

    Context context;
    List<String> listgroup;
    HashMap<String,List<String>> listitem,listItemPreference,listItemVenue,listBooking;
    public MyExpandableListAdaptor(Context context,List<String> listgroup,HashMap<String,List<String>> listitem,HashMap<String,List<String>> listItemPreference,HashMap<String,List<String>> listItemVenue, HashMap<String,List<String>> listBooking)
    {
        this.context=context;
        this.listgroup=listgroup;
        this.listitem=listitem;
        this.listItemPreference=listItemPreference;
        this.listItemVenue=listItemVenue;
        this.listBooking=listBooking;
    }


    @Override
    public int getGroupCount() {
        return listgroup.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return this.listitem.get(this.listgroup.get(i)).size();
    }

    @Override
    public Object getGroup(int i) {
        return this.listgroup.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        return this.listitem.get(this.listgroup.get(i)).get(i1);
    }

    public Object getChild1(int i, int i1) {
        return this.listItemPreference.get(this.listgroup.get(i)).get(i1);
    }

    public Object getChild2(int i, int i1) {
        return this.listItemVenue.get(this.listgroup.get(i)).get(i1);
    }

    public Object getChild3(int i, int i1) {
        return this.listBooking.get(this.listgroup.get(i)).get(i1);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
        String group= (String) getGroup(i);
        if(view ==null)
        {
            LayoutInflater inflater=LayoutInflater.from(viewGroup.getContext());
            view =inflater.inflate(R.layout.list_expandablegroup,null);
        }
        TextView textView= view.findViewById(R.id.subjectName);
        textView.setText(group);
        textView.setSelected(true);

        return view;
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
        String child = (String) getChild(i, i1);
        String child1 = (String) getChild1(i, i1);
        String child2 = (String) getChild2(i, i1);
        String child3 = (String) getChild3(i, i1);

        if (view == null) {
            LayoutInflater inflater=LayoutInflater.from(viewGroup.getContext());
            view =inflater.inflate(R.layout.list_expandablechild,null);
        }

        TextView textView= view.findViewById(R.id.subjectName);
        textView.setText(child);
        textView.setSelected(true);

        TextView textView1= view.findViewById(R.id.gp);
        textView1.setText("Gender Preference: "+child1);
        textView1.setSelected(true);

        TextView textView2= view.findViewById(R.id.venue33);
        textView2.setText("Venue: "+child2);
        textView2.setSelected(true);

        TextView textView3= view.findViewById(R.id.slotbooked);
        textView3.setText("Slots Booked : "+child3);
        textView3.setSelected(true);

        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }
}
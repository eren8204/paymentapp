package com.example.paymentapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TeamExpandableListAdapter extends BaseExpandableListAdapter {

    private final Map<Integer, List<Member>> teamData;

    Context context;

    public TeamExpandableListAdapter(MyTeam myTeam, Map<Integer, List<Member>> teamData) {
        context = myTeam.getApplicationContext();
        this.teamData = teamData;
    }

    @Override
    public int getGroupCount() {
        return teamData.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return new ArrayList<>(teamData.keySet()).get(groupPosition);
    }


    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return teamData.get(groupPosition + 1).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_item, parent, false);
        }
        TextView levelText = convertView.findViewById(R.id.levelTextView);
        TextView memberCount = convertView.findViewById(R.id.totalMemberCount);
        Integer level = (Integer) getGroup(groupPosition);
        List<Member> members = teamData.get(level);
        levelText.setText("Level: " + (groupPosition + 1));
        if (members != null) {
            memberCount.setText("Total Members: " + members.size());
        } else {
            memberCount.setText("Total Members: 0");
        }
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ChildViewHolder holder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.child_item, parent, false);
            holder = new ChildViewHolder();
            holder.recyclerView = convertView.findViewById(R.id.recyclerView);
            holder.recyclerView.setLayoutManager(new LinearLayoutManager(context));
            convertView.setTag(holder);
        } else {
            holder = (ChildViewHolder) convertView.getTag();
        }

        Integer level = (Integer) getGroup(groupPosition);
        List<Member> members = teamData.get(level);

        if (members != null) {
            MembersAdapter adapter = new MembersAdapter(context, members);
            holder.recyclerView.setAdapter(adapter);
        }

        return convertView;
    }



    static class ChildViewHolder {
        RecyclerView recyclerView;
    }


    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}

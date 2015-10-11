package com.zhengxiaoyao0716.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;
import com.zhengxiaoyao0716.digimon2048.R;
import org.json.JSONArray;

public class RankExpandableLA extends BaseExpandableListAdapter {
    private Context context;
    private JSONArray data;

    /**
     * 构造器
     * @param context 上下文
     * @param data 外层是JSONArray:所有level(JSONArray)的集合，
     *             中层JSONArray:某level下全部record(JSONObject)的集合，
     *             内层JSONArray:某条记录的内容，均为String
     *             形如[group, number, score, name, time]
     */
    public RankExpandableLA(Context context, JSONArray data)
    {
        this.context = context;
        this.data = data;
    }

    @Override
    public int getGroupCount() {
        return data.length();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return data.optJSONArray(groupPosition).length();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return data.optJSONArray(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return data.optJSONArray(groupPosition).optJSONArray(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(android.R.layout.simple_expandable_list_item_1, null);
            TextView textView = (TextView) view.findViewById(android.R.id.text1);
            textView.setTextSize(26);
            textView.setTextColor(Color.parseColor("#888888"));
        }
        ((TextView)view).setText(
                        data.optJSONArray(groupPosition).optJSONArray(0).optString(0));
        return view;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null)
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_item_rank, null);
        }
        JSONArray recordJO = data.optJSONArray(groupPosition).optJSONArray(childPosition);
        ((TextView) view.findViewById(R.id.rankListItemNumber)).setText(recordJO.optString(1));
        ((TextView) view.findViewById(R.id.rankListItemScore)).setText(recordJO.optString(2));
        ((TextView) view.findViewById(R.id.rankListItemName)).setText(recordJO.optString(3));
        ((TextView) view.findViewById(R.id.rankListItemTime)).setText(recordJO.optString(4));
        return view;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
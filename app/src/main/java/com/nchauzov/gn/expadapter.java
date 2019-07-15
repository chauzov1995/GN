package com.nchauzov.gn;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.LinkedHashSet;

public class expadapter extends BaseExpandableListAdapter {

    private ArrayList<ArrayList<class_detal>> mGroups;
    private Context mContext;
    ArrayList<String> mspis_partiy;
    class_mtara   mtara;

    public expadapter(Context context, ArrayList<ArrayList<class_detal>> groups, ArrayList<String> spis_partiy, class_mtara mtara) {
        mContext = context;
        mGroups = groups;
        this.mtara=mtara;
        mspis_partiy = spis_partiy;
    }

    @Override
    public int getGroupCount() {
        return mGroups.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mGroups.get(groupPosition).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mGroups.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mGroups.get(groupPosition).get(childPosition);
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
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView,
                             ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.explistgroup, null);
        }

        if (isExpanded) {
            //Изменяем что-нибудь, если текущая Group раскрыта
        } else {
            //Изменяем что-нибудь, если текущая Group скрыта
        }

        // TextView textGroup = (TextView) convertView.findViewById(R.id.textGroup);
        ((TextView) convertView.findViewById(R.id.textView9)).setText("Партия " + mspis_partiy.get(groupPosition));
       // ((TextView) convertView.findViewById(R.id.textView9)).setHeight(50);
        return convertView;

    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                             View convertView, ViewGroup parent) {


        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.explistitem, null);
        }
        final class_detal p = mGroups.get(groupPosition).get(childPosition);


        ((TextView) convertView.findViewById(R.id.textView8)).setText(p.NAME);
        ((TextView) convertView.findViewById(R.id.textView6)).setText(
              "Выс. "+  p.V+", "+
                        "Шир. "  +p.S+", "+
                        "Глуб. " + p.G
        );


        LinearLayout ll_listit=(LinearLayout) convertView.findViewById(R.id.ll_listit);
        ll_listit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, detal_obzor.class);
                intent.putExtra("detal", p  );
                intent.putExtra("mtara", mtara);
                mContext.startActivity(intent);
            }
        });
      /*  Button button = (Button) convertView.findViewById(R.id.buttonChild);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext, "button is pressed", Toast.LENGTH_LONG).show();
            }
        });
*/
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
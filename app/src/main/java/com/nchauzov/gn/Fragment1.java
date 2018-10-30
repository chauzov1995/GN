package com.nchauzov.gn;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

@SuppressLint("ValidFragment")
public class Fragment1 extends Fragment {


    private RecyclerView mRecyclerView, rv_operac;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    ArrayList<class_mtara> mtara_l;
    ArrayList<class_detal> detal_list;
    TextView textView5;
    CheckBox checkBox;
    TabHost tabHost;

    public Fragment1(ArrayList<class_detal> _detal_list, ArrayList<class_mtara> _mtara_l) {
        mtara_l = _mtara_l;
        detal_list = _detal_list;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment1, null);

        Log.d("kolbo", mtara_l.size() + "");


        checkBox = (CheckBox) view.findViewById(R.id.checkBox);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recview);
        rv_operac = (RecyclerView) view.findViewById(R.id.rv_operac);

        textView5 = (TextView) view.findViewById(R.id.textView5);
        tabHost = (TabHost) view.findViewById(R.id.tabHost);

        textView5.setText(mtara_l.get(0).NAME);


        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new MyAdapter(detal_list, getActivity());
        mRecyclerView.setAdapter(mAdapter);


        rv_operac.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        rv_operac.setLayoutManager(mLayoutManager);
        mAdapter = new adapter_operac(detal_list, getActivity());
        rv_operac.setAdapter(mAdapter);


        checkBox.setOnClickListener(new View.OnClickListener() {
            public void onClick(View r) {


                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Скомплектовать ёлку?")
                        //      .setMessage("Покормите кота!")
                        .setCancelable(false)
                        .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Toast.makeText(getActivity(), "Ёлка скомплектована",
                                        Toast.LENGTH_LONG).show();
                                checkBox.setClickable(false);
                            }
                        })
                        .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                checkBox.setChecked(false);
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();


            }
        });


        tabHost.setup();

        TabHost.TabSpec tabSpec = tabHost.newTabSpec("tag1");

        tabSpec.setContent(R.id.tab1);
        tabSpec.setIndicator("Детали");
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("tag2");
        tabSpec.setContent(R.id.tab2);
        tabSpec.setIndicator("Операции");
        tabHost.addTab(tabSpec);


        tabHost.setCurrentTab(1);


        return view;
    }
}
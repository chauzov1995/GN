package com.nchauzov.gn;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

import static com.nchauzov.gn.texclass.query_zapros;

@SuppressLint("ValidFragment")
public class Fragment1 extends Fragment {


      RecyclerView rv_operac;
      RecyclerView.Adapter mAdapter;
      RecyclerView.LayoutManager mLayoutManager;
    class_mtara mtara_l;
    ArrayList<class_detal> detal_list;
    TextView textView5;
    CheckBox checkBox;
    TabHost tabHost;
    ExpandableListView expListView;
    ArrayList<class_operac>  operac_list;


    public Fragment1(class_mtara mtara_l, ArrayList<class_detal> detal_list,  ArrayList<class_operac>  operac_list) {
        this.mtara_l = mtara_l;
        this.detal_list = detal_list;
        this.operac_list = operac_list;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment1, null);


        checkBox = (CheckBox) view.findViewById(R.id.checkBox);
        expListView = (ExpandableListView) view.findViewById(R.id.expListView);
        rv_operac = (RecyclerView) view.findViewById(R.id.rv_operac);
        textView5 = (TextView) view.findViewById(R.id.textView5);
        tabHost = (TabHost) view.findViewById(R.id.tabHost);

        textView5.setText(mtara_l.NAME);
        if (mtara_l.STATUS_ID == 3) {

            checkBox.setChecked(true);

            checkBox.setEnabled(false);
        }


        checkBox.setOnClickListener(new View.OnClickListener() {
            public void onClick(View r) {


                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Скомплектовать ёлку?")
                        //      .setMessage("Покормите кота!")
                        .setCancelable(false)
                        .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                new Task_skompl().execute();


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


        tabHost.setCurrentTab(0);



        ArrayList<String> spis_partiy_do = new ArrayList<String>();
        for (class_detal group : detal_list) {
            spis_partiy_do.add(group.MPARTSGROUPS_NAME);
        }
        //ArrayList<String> mGroupsArray = new LinkedHashSet<>(spis_partiy_do);
        Set<String> mGroupsArray=new LinkedHashSet<>(spis_partiy_do);

        ArrayList<ArrayList<class_detal>> groups = new ArrayList<ArrayList<class_detal>>();
        ArrayList<String> spis_partiy_do_arr = new    ArrayList<String>();
        for (String group : mGroupsArray) {
            spis_partiy_do_arr.add(group);
            ArrayList<class_detal> children1 = new ArrayList<class_detal>();
            for (class_detal month : detal_list) {
                if (month.MPARTSGROUPS_NAME.equals(group)) {
                    children1.add(month);
                }
            }
            // добавляем в коллекцию коллекций
            groups.add(children1);
        }
        expadapter adapter = new expadapter(getActivity(), groups,spis_partiy_do_arr, mtara_l);
        expListView.setAdapter(adapter);





/*
        rv_operac.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        rv_operac.setLayoutManager(mLayoutManager);
        mAdapter = new adapter_operac(operac_list, mtara_l.ID, this);
        rv_operac.setAdapter(mAdapter);


*/

        return view;
    }


    private class Task_skompl extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... path) {

            String content;
            try {
                content = query_zapros(new String[]{
                        "update MTARALOG set STATUS_ID = 3 where id=(SELECT MAX(id) FROM MTARALOG WHERE MTARA_ID = " + mtara_l.ID + ")"
                });
            } catch (IOException ex) {
                content = ex.getMessage();
            }

            return content;
        }


        @Override
        protected void onPostExecute(String content) {

            Log.d("asdasd", content);
            Toast.makeText(getActivity(), "Ёлка скомплектована!", Toast.LENGTH_LONG).show();
            // checkBox.setClickable(false);
            checkBox.setEnabled(false);
        }


    }


}
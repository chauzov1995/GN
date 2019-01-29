package com.nchauzov.gn;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import static com.nchauzov.gn.texclass.formir_params_sql;
import static com.nchauzov.gn.texclass.query_zapros;

@SuppressLint("ValidFragment")
public class Fragment1 extends Fragment {


    private static RecyclerView rv_operac;
    private static RecyclerView.Adapter mAdapter;
    private static RecyclerView.LayoutManager mLayoutManager;
    class_mtara mtara_l;
    ArrayList<class_detal> detal_list;
    TextView textView5;
    CheckBox checkBox;
    TabHost tabHost;
    ExpandableListView expListView;


    public Fragment1(class_mtara mtara_l, ArrayList<class_detal> detal_list) {
        this.mtara_l = mtara_l;
        this.detal_list = detal_list;
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

/*
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new MyAdapter(detal_list, getActivity());
        mRecyclerView.setAdapter(mAdapter);
*/


/*

        ArrayList<String> spis_partiy_do = new ArrayList<String>();
        for (class_detal group : detal_list) {
            spis_partiy_do.add(group.MPARTSGROUPS_NAME);
        }
        LinkedHashSet<String> mGroupsArray = new LinkedHashSet<>(spis_partiy_do);


        Map<String, String> map;


        // коллекция для групп
        ArrayList<Map<String, String>> groupDataList = new ArrayList<>();
        // заполняем коллекцию групп из массива с названиями групп

        // создаем общую коллекцию для коллекций элементов
        ArrayList<ArrayList<Map<String, String>>> сhildDataList = new ArrayList<>();

        // в итоге получится сhildDataList = ArrayList<сhildDataItemList>

        for (String group : mGroupsArray) {
            // заполняем список атрибутов для каждой группы
            map = new HashMap<>();
            map.put("groupName", group); // время года
            groupDataList.add(map);


            // создаем коллекцию элементов для первой группы
            ArrayList<Map<String, String>> сhildDataItemList = new ArrayList<>();
            // заполняем список атрибутов для каждого элемента
            for (class_detal month : detal_list) {
                if(month.MPARTSGROUPS_NAME.equals(group)) {
                    map = new HashMap<>();
                    map.put("monthName", month.NAME + ""); // название месяца
                    сhildDataItemList.add(map);
                }
            }
            // добавляем в коллекцию коллекций
            сhildDataList.add(сhildDataItemList);

        }

        // список атрибутов групп для чтения
        String groupFrom[] = new String[]{"groupName"};
        // список ID view-элементов, в которые будет помещены атрибуты групп
        int groupTo[] = new int[]{android.R.id.text1};

        // список атрибутов элементов для чтения
        String childFrom[] = new String[]{"monthName"};
        // список ID view-элементов, в которые будет помещены атрибуты
        // элементов
        int childTo[] = new int[]{android.R.id.text1};

        SimpleExpandableListAdapter adapter = new SimpleExpandableListAdapter(
                getActivity(), groupDataList,
                android.R.layout.simple_expandable_list_item_1, groupFrom,
                groupTo, сhildDataList, android.R.layout.simple_list_item_1,
                childFrom, childTo);


        expListView.setAdapter(adapter);


*/


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

/*
        //Создаем набор данных для адаптера
        ArrayList<ArrayList<String>> groups = new ArrayList<ArrayList<String>>();
        ArrayList<String> children1 = new ArrayList<String>();
        ArrayList<String> children2 = new ArrayList<String>();
        children1.add("Child_1");
        children1.add("Child_2");
        groups.add(children1);
        children2.add("Child_1");
        children2.add("Child_2");
        children2.add("Child_3");
        groups.add(children2);
        */
        //Создаем адаптер и передаем context и список с данными
        expadapter adapter = new expadapter(getActivity(), groups,spis_partiy_do_arr);
        expListView.setAdapter(adapter);


        new getspis_operac(getActivity(),detal_list, mtara_l ).execute();

        return view;
    }


    public static class getspis_operac extends AsyncTask<String, Void, String> {

        ArrayList<class_operac> operac_list;
        Activity ctx;
        ArrayList<class_detal>  detal_list;
        class_mtara mtara_l;

        public getspis_operac(Activity ctx, ArrayList<class_detal> detal_list, class_mtara mtara_l) {
            this.ctx = ctx;
            this.detal_list = detal_list;
            this.mtara_l = mtara_l;
        }


        @Override
        protected String doInBackground(String... path) {

            String content;
            try {
                //Здесь пиши тяжеловестный код

                String where_MTEXPROCID = "";
                for (int i = 0; i < detal_list.size(); i++) {
                    where_MTEXPROCID += "'" + detal_list.get(i).MPARTSGROUPS_ID + "', ";
                }
                where_MTEXPROCID = where_MTEXPROCID.replaceAll(", $", "");
                String otvet = "";
                if (!where_MTEXPROCID.isEmpty()) {
                    otvet = query_zapros(new String[]{
                            "select  b.*, a.mpartsgroups_id from MAGAZINETEXOPER a " +
                                    " LEFT JOIN MTEXOPER b ON a.MTEXOPER_ID = b.ID" +
                                    " where a.mpartsgroups_id IN (" + where_MTEXPROCID + ") and a.current_flag=1"
                    });
                }


                JSONArray friends = null;
                operac_list = new ArrayList<class_operac>();

                friends = new JSONArray(otvet);

                for (int i = 0; i < friends.length(); i++) {

                    JSONObject zakaz = friends.getJSONObject(i);
                    //  Log.d("asdadasd", zakaz.getString("NAME"));
                    int Id_zakaza = zakaz.getInt("ID");

                    boolean zanesti = true;
                    for (class_operac curVal : operac_list) {

                        Log.d("curVal", curVal.NAME);
                        if (curVal.ID == (Id_zakaza)) {
                            zanesti = false;
                        }
                    }
                    //zanesti=true;
                    if (zanesti) {
                        operac_list.add(new class_operac(
                                Id_zakaza,
                                zakaz.optString("NAME", ""),
                                zakaz.optDouble("OPERTIME", 0.0),
                                zakaz.optInt("MTEXPROCID", 0),
                                zakaz.optInt("MOPERID", 0),
                                zakaz.optInt("NN", 0),
                                zakaz.optBoolean("IS_CHECK_POINT", false)
                        ));
                    }
                }


                content = "";
            } catch (IOException | JSONException ex) {
                content = ex.getMessage();
            }

            return content;
        }


        @Override
        protected void onPostExecute(String content) {


            rv_operac.setHasFixedSize(true);
            mLayoutManager = new LinearLayoutManager(ctx);
            rv_operac.setLayoutManager(mLayoutManager);
            mAdapter = new adapter_operac(operac_list, mtara_l.ID, ctx);
            rv_operac.setAdapter(mAdapter);


        }
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
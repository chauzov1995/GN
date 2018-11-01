package com.nchauzov.gn;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Fragment;
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

import static com.nchauzov.gn.texclass.formir_params_sql;
import static com.nchauzov.gn.texclass.query_zapros;

@SuppressLint("ValidFragment")
public class Fragment1 extends Fragment {


    private RecyclerView mRecyclerView, rv_operac;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    class_mtara mtara_l;
    ArrayList<class_detal> detal_list;
    TextView textView5;
    CheckBox checkBox;
    TabHost tabHost;

    public Fragment1(class_mtara mtara_l, ArrayList<class_detal> detal_list) {
        this.mtara_l = mtara_l;
        this.detal_list = detal_list;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment1, null);


        checkBox = (CheckBox) view.findViewById(R.id.checkBox);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recview);
        rv_operac = (RecyclerView) view.findViewById(R.id.rv_operac);

        textView5 = (TextView) view.findViewById(R.id.textView5);
        tabHost = (TabHost) view.findViewById(R.id.tabHost);

        textView5.setText(mtara_l.NAME);


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


        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new MyAdapter(detal_list, getActivity());
        mRecyclerView.setAdapter(mAdapter);


        new getspis_operac().execute("http://www.web.gn/work/public/otdelka/query");

        return view;
    }


    private class getspis_operac extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... path) {

            String content;
            try {
                content = getContent(path[0]);
            } catch (IOException ex) {
                content = ex.getMessage();
            }

            return content;
        }


        @Override
        protected void onPostExecute(String content) {


            JSONArray friends = null;
            ArrayList<class_operac> operac_list = new ArrayList<class_operac>();
            try {
                //    Log.d("asdadasd", content);
                friends = new JSONArray(content);

                for (int i = 0; i < friends.length(); i++) {

                    JSONObject zakaz = friends.getJSONObject(i);
                    Log.d("asdadasd", zakaz.getString("NAME"));
                    operac_list.add(new class_operac(
                            zakaz.getInt("ID"),
                            zakaz.optString("NAME", ""),
                            zakaz.optDouble("OPERTIME", 0.0),
                            zakaz.optInt("MTEXPROCID", 0),
                            zakaz.optInt("MOPERID", 0),
                            zakaz.optInt("NN", 0),
                            zakaz.optBoolean("IS_CHECK_POINT", false)
                    ));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


            rv_operac.setHasFixedSize(true);
            mLayoutManager = new LinearLayoutManager(getActivity());
            rv_operac.setLayoutManager(mLayoutManager);
            mAdapter = new adapter_operac(operac_list, getActivity());
            rv_operac.setAdapter(mAdapter);


        }

        private String getContent(String path) throws IOException {
            BufferedReader reader = null;
            try {
                String login = "admin";
                String password = "4";
                String[] sql = {
                        "select * from MTEXOPER where MTEXPROCID=108"
                };
                String params = formir_params_sql(login, password, sql);

                byte[] data = null;
                URL url = new URL(path);
                HttpURLConnection c = (HttpURLConnection) url.openConnection();
                c.setRequestMethod("POST");
                c.setReadTimeout(10000);

                c.setRequestProperty("Content-Length", "" + Integer.toString(params.getBytes().length));
                OutputStream os = c.getOutputStream();
                data = params.getBytes("UTF-8");
                os.write(data);


                c.connect();
                reader = new BufferedReader(new InputStreamReader(c.getInputStream()));
                StringBuilder buf = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    buf.append(line + "\n");
                }
                return (buf.toString());
            } finally {
                if (reader != null) {
                    reader.close();
                }
            }
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
            checkBox.setClickable(false);
        }


    }


}
package com.nchauzov.gn;

import android.content.Intent;
import android.opengl.Visibility;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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

public class detal_obzor extends AppCompatActivity {

    TextView textView6;
    TextView textView15;
    int mtara_id_upd = 0;
    class_detal detal;
    CardView cview, cview_new;
    int find_bd_id;
    int type_zapros;
    ArrayList<class_mtara> recomend_mtara;
    ArrayList<Integer> mtara_l;
    RecyclerView   mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detal_obzor);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        detal = (class_detal) getIntent().getExtras().getSerializable("detal");

     //   textView6 = (TextView) findViewById(R.id.textView6);
        TextView textView10 = (TextView) findViewById(R.id.textView10);
        TextView textView11 = (TextView) findViewById(R.id.textView11);
        TextView textView12 = (TextView) findViewById(R.id.textView12);
        TextView textView13 = (TextView) findViewById(R.id.textView13);
        TextView textView14 = (TextView) findViewById(R.id.textView14);
        textView15 = (TextView) findViewById(R.id.textView15);
        TextView textView16 = (TextView) findViewById(R.id.textView16);
        Button button3 = (Button) findViewById(R.id.button3);
        Button button2_new = (Button) findViewById(R.id.button2_new);
        Button button2 = (Button) findViewById(R.id.button2);
        // Button button = (Button) findViewById(R.id.button);
        cview = (CardView) findViewById(R.id.cview);
        cview_new = (CardView) findViewById(R.id.cview_new);
   mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);





        cview.setVisibility(View.GONE);
        cview_new.setVisibility(View.GONE);

Log.d("detal.NAME",detal.NAME);
        textView10.setText(detal.NAME);
        textView11.setText("Высота: " + detal.V);
        textView12.setText("Ширина: " + detal.S);
        textView13.setText("Глубина: " + detal.G);
        textView14.setText("Заказ: " + detal.CUSTOMID + "");
        textView15.setText("Принадлежит ёлке: " + detal.MTARA_ID + "");
        textView16.setText("Тип детали: " + detal.PREF);

        if (detal.MTARA_ID == 0) {//если тара не назаначена то лезть в интернет
            new Task_get_elka_recom().execute("http://www.web.gn/work/public/otdelka/query");
        }

        button3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View r) {
                type_zapros = 2;
                Intent intent = new Intent("com.google.zxing.client.android.SCAN");
                intent.putExtra("SCAN_MODE", "MODE");
                startActivityForResult(intent, 0);
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View r) {
                type_zapros = 1;
                Intent intent = new Intent("com.google.zxing.client.android.SCAN");
                intent.putExtra("SCAN_MODE", "MODE");
                startActivityForResult(intent, 0);
                //    cview.setVisibility(View.GONE);
            }
        });
        button2_new.setOnClickListener(new View.OnClickListener() {
            public void onClick(View r) {
                type_zapros = 1;
                Intent intent = new Intent("com.google.zxing.client.android.SCAN");
                intent.putExtra("SCAN_MODE", "MODE");
                startActivityForResult(intent, 0);
                //  cview.setVisibility(View.GONE);
            }
        });
        /*
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View r) {
                mtara_id_upd = 203;
                type_zapros = 2;
                new Task_perenos_det().execute("http://www.web.gn/work/public/otdelka/query");
                //  cview.setVisibility(View.GONE);
            }
        });
        */

    }


    public void onActivityResult(int requestCode, int resultCode, Intent intent) {

        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                Double contents = Double.parseDouble(intent.getStringExtra("SCAN_RESULT")); // This will contain your scan result
                if ((contents > 1500000000) && (contents < 1600000000)) { // код тары
                    contents -= 1500000000;
                    mtara_id_upd = contents.intValue();


                    new Task_get_spis_elka().execute("http://www.web.gn/work/public/otdelka/query");


                } else {
                    Toast.makeText(detal_obzor.this, "Это не ёлка", Toast.LENGTH_LONG).show();
                }
            }
        }
    }


    private class Task_get_elka_recom extends AsyncTask<String, Void, String> {
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
            recomend_mtara = new ArrayList<class_mtara>();
            try {
                friends = new JSONArray(content);
                for (int i = 0; i < friends.length(); i++) {
                    JSONObject zakaz = friends.getJSONObject(i);
                    recomend_mtara.add(new class_mtara(
                            zakaz.getInt("ID"),
                            zakaz.getString("NAME"),
                            zakaz.getInt("MTARATYPE_ID"),
                            zakaz.optInt("STATUS_ID", 1)
                    ));


                    //  Log.d("name", zakaz.getString("MTARA_ID"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (recomend_mtara.size() > 0) {
                cview.setVisibility(View.VISIBLE);
                String text_recom_tara = "";







                // use this setting to improve performance if you know that changes
                // in content do not change the layout size of the RecyclerView
                mRecyclerView.setHasFixedSize(true);

                // use a linear layout manager
                LinearLayoutManager  mLayoutManager = new LinearLayoutManager(detal_obzor.this, LinearLayoutManager.HORIZONTAL, false);
                mRecyclerView.setLayoutManager(mLayoutManager);

                // specify an adapter (see also next example)
                MyAdapterbtn  mAdapter = new MyAdapterbtn(recomend_mtara, detal_obzor.this);
                mRecyclerView.setAdapter(mAdapter);









                for (int i = 0; i < recomend_mtara.size(); i++) {
                    text_recom_tara += recomend_mtara.get(i).NAME + " или";
                }
                text_recom_tara = text_recom_tara.substring(0, text_recom_tara.length() - 4);

           //     textView6.setText(text_recom_tara);
            } else {
                if (detal.MTARA_ID == 0) {
                    cview_new.setVisibility(View.VISIBLE);
                }
            }
        }

        private String getContent(String path) throws IOException {
            BufferedReader reader = null;
            try {
                String login = "admin";
                String password = "4";
                String[] sql = {
                        "select DISTINCT c.ID, c.NAME, c.MTARATYPE_ID, b.STATUS_ID from WOTDELKA a " +
                                "LEFT JOIN MTARALOG b ON a.MTARA_ID=b.MTARA_ID " +
                                "LEFT JOIN MTARA c ON a.MTARA_ID=c.ID " +
                                "where a.SV='" + detal.SV + "' and a.MOTDELKA_ID_POKR=" + detal.MOTDELKA_ID_POKR + " and a.MOTDELKA_ID_ZVET=" + detal.MOTDELKA_ID_ZVET + " AND b.STATUS_ID=2 "

                };
                String params = formir_params_sql(login, password, sql);
                Log.d("sql", params);

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


    private class Task_perenos_det extends AsyncTask<String, Void, String> {
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
            textView15.setText("Принадлежит ёлке: " + mtara_id_upd + "");
            cview_new.setVisibility(View.GONE);
            cview.setVisibility(View.GONE);
        }

        private String getContent(String path) throws IOException {
            BufferedReader reader = null;
            try {
                String login = "admin";
                String password = "4";
                String[] sql = new String[0];

                switch (type_zapros) {
                    case 1://создание новой ёлки
                        sql = new String[]{
                                "UPDATE WOTDELKA SET MTARA_ID=" + mtara_id_upd + " where ID=" + detal.ID,
                                "INSERT INTO MTARALOG (MTARA_ID, STATUS_ID) VALUES (" + mtara_id_upd + ", 2)"
                        };
                        break;
                    case 2://редактирование ёлки или Положили в текущюю ёлку (подтвердить)
                        sql = new String[]{
                                "UPDATE WOTDELKA SET MTARA_ID=" + mtara_id_upd + " where ID=" + detal.ID,

                        };
                        break;
                }

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


    private class Task_get_spis_elka extends AsyncTask<String, Void, String> {
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

            mtara_l = new ArrayList<Integer>();
            JSONArray friends = null;
            Log.d("psad", content);
            try {
                friends = new JSONArray(content);
                for (int i = 0; i < friends.length(); i++) {
                    JSONObject zakaz = friends.getJSONObject(i);
                    mtara_l.add(zakaz.getInt("MTARA_ID"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


            Log.d("uje_estb", "" + mtara_l.size());
            boolean uje_estb = false;
            for (int i = 0; i < mtara_l.size(); i++) {
                if (mtara_l.get(i) == mtara_id_upd) {
                    uje_estb = true;

                }
                Log.d("uje_estb", mtara_l.get(i) + " " + mtara_id_upd);
            }


            if (uje_estb) {
                Toast.makeText(detal_obzor.this, "Нельзя положить в эту ёлку", Toast.LENGTH_LONG).show();
            } else {
                new Task_perenos_det().execute("http://www.web.gn/work/public/otdelka/query");
            }


        }

        private String getContent(String path) throws IOException {
            BufferedReader reader = null;
            try {
                String login = "admin";
                String password = "4";
                String[] sql = {
                        "select DISTINCT mtara_id FROM MTARALOG where STATUS_ID=2"
                };
                String params = formir_params_sql(login, password, sql);
                Log.d("sql", params);
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


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == android.R.id.home) {
            finish();
        }

        return true;
    }

}

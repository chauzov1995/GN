package com.nchauzov.gn;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

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

public class komplektovka extends AppCompatActivity {

    LinearLayout skan_elka;
    private android.app.FragmentTransaction fragmentTransaction;

    Double find_bd_id;
    ArrayList<class_mtara> mtara_l = new ArrayList<class_mtara>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_komplektovka);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        skan_elka = (LinearLayout) findViewById(R.id.skan_elka);
        skan_elka.setOnClickListener(new View.OnClickListener() {
            public void onClick(View r) {


                Intent intent = new Intent("com.google.zxing.client.android.SCAN");
                intent.putExtra("SCAN_MODE", "MODE");
                startActivityForResult(intent, 0);


            }
        });

        ///временннооооо!!!!!!
             find_bd_id = 203.0;
            new Task_get_elka().execute("http://www.web.gn/work/public/otdelka/query");


    }


    public void onActivityResult(int requestCode, int resultCode, Intent intent) {

        Double contents = Double.parseDouble(intent.getStringExtra("SCAN_RESULT")); // This will contain your scan result

        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                if ((contents > 1500000000) && (contents < 1600000000)) { // код тары
                    find_bd_id = contents - 1500000000;
                    new Task_get_elka().execute("http://www.web.gn/work/public/otdelka/query");
                }
            }
        }
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                if (contents > 5000000000.0) { // код тары
                    find_bd_id = contents - 5000000000.0;
                    new Task_get_detal().execute("http://www.web.gn/work/public/otdelka/query");
                }
            }
        }
    }

    private class Task_get_elka extends AsyncTask<String, Void, String> {
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

            mtara_l = new ArrayList<class_mtara>();
            JSONArray friends = null;
            Log.d("psad", content);
            try {
                friends = new JSONArray(content);
                for (int i = 0; i < friends.length(); i++) {
                    JSONObject zakaz = friends.getJSONObject(i);
                    mtara_l.add(new class_mtara(
                            zakaz.getInt("ID"),
                            zakaz.getString("NAME"),
                            zakaz.getInt("MTARATYPE_ID"),
                            zakaz.optInt("STATUS_ID", 1)
                    ));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


            new ProgressTask().execute("http://www.web.gn/work/public/otdelka/query");
        }

        private String getContent(String path) throws IOException {
            BufferedReader reader = null;
            try {
                String login = "admin";
                String password = "4";
                String[] sql = {
                        "select FIRST 1 a.*, b.STATUS_ID from MTARA a LEFT JOIN MTARALOG b ON b.MTARA_ID=a.ID " +
                                "where a.ID=" + find_bd_id + " ORDER BY b.ID DESC"
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

    private class Task_get_detal extends AsyncTask<String, Void, String> {
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
            ArrayList<class_detal> det_list = new ArrayList<class_detal>();
Log.d("cont", content);
            try {
                friends = new JSONArray(content);
                for (int i = 0; i < friends.length(); i++) {
                    JSONObject zakaz = friends.getJSONObject(i);
                    det_list.add(new class_detal(
                            zakaz.getInt("ID"),
                            zakaz.getString("NAME"),
                            zakaz.getInt("CUSTOMID"),
                            zakaz.getString("V"),
                            zakaz.getString("S"),
                            zakaz.getString("G"),
                            zakaz.optInt("MTARA_ID", 0),
                            zakaz.getString("PREF"),
                            zakaz.getString("SV"),
                            zakaz.optInt("MOTDELKA_ID_POKR", 0),
                            zakaz.optInt("MOTDELKA_ID_ZVET", 0)
                    ));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.d("uje_estb", "" +  mtara_l.size());
            Intent intent = new Intent(komplektovka.this, detal_obzor.class);
            intent.putExtra("detal", det_list.get(0));


            komplektovka.this.startActivity(intent);

        }

        private String getContent(String path) throws IOException {
            BufferedReader reader = null;
            try {
                String login = "admin";
                String password = "4";
                String[] sql = {
                        "select * from WOTDELKA where ID=" + find_bd_id
                };
                String params = formir_params_sql(login, password, sql);
Log.d("params",params);

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

    private class ProgressTask extends AsyncTask<String, Void, String> {
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
            ArrayList<class_detal> detal_list = new ArrayList<class_detal>();
            try {
            //    Log.d("asdadasd", content);
                friends = new JSONArray(content);

                for (int i = 0; i < friends.length(); i++) {

                    JSONObject zakaz = friends.getJSONObject(i);
                    Log.d("asdadasd",  zakaz.getString("NAME"));
                    detal_list.add(new class_detal(
                            zakaz.getInt("ID"),
                            zakaz.getString("NAME"),
                            zakaz.getInt("CUSTOMID"),
                            zakaz.getString("V"),
                            zakaz.getString("S"),
                            zakaz.getString("G"),
                            zakaz.optInt("MTARA_ID", 0),
                            zakaz.getString("PREF"),
                            zakaz.getString("SV"),
                            zakaz.optInt("MOTDELKA_ID_POKR", 0),
                            zakaz.optInt("MOTDELKA_ID_ZVET", 0)
                    ));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }




            fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frgmCont, new Fragment1(detal_list, mtara_l));
            fragmentTransaction.commit();


        }

        private String getContent(String path) throws IOException {
            BufferedReader reader = null;
            try {
                String login = "admin";
                String password = "4";
                String[] sql = {
                        "select * from WOTDELKA where MTARA_ID=" + mtara_l.get(0).ID
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


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == android.R.id.home) {
            finish();
        }
        return true;
    }


}

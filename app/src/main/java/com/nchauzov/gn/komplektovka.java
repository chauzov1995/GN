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
import android.widget.SearchView;
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

public class komplektovka extends AppCompatActivity {

    LinearLayout skan_elka;
    private android.app.FragmentTransaction fragmentTransaction;
    SearchView simpleSearchView;
    Double find_bd_id;
    class_mtara mtara_l;
    ArrayList<class_detal> detal_list;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_komplektovka);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        simpleSearchView = (SearchView) findViewById(R.id.simpleSearchView); // inititate a search view

        skan_elka = (LinearLayout) findViewById(R.id.skan_elka);
        skan_elka.setOnClickListener(new View.OnClickListener() {
            public void onClick(View r) {


                Intent intent = new Intent("com.google.zxing.client.android.SCAN");
                intent.putExtra("SCAN_MODE", "MODE");
                startActivityForResult(intent, 0);


            }
        });


        // поиск
        simpleSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
// do something on text submit


                parse_strich(Double.parseDouble(query));
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
// do something when text changes
                return false;
            }
        });


        String query = simpleSearchView.getQuery().toString(); // get the query string currently in the text field


        ///временннооооо!!!!!!
      //  find_bd_id = 203.0;
       // new Task_get_elka().execute();


    }


    public void onActivityResult(int requestCode, int resultCode, Intent intent) {

        Double contents = Double.parseDouble(intent.getStringExtra("SCAN_RESULT")); // This will contain your scan result
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                parse_strich(contents);
            }
        }
    }

    void parse_strich(Double contents) {





        if ((contents > 1500000000) && (contents < 1600000000)) { // код тары
            fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frgmCont, new load_pb());
            fragmentTransaction.commit();
            find_bd_id = contents - 1500000000;
            new Task_get_elka().execute();
        } else if (contents > 5000000000.0) { // код тары
            find_bd_id = contents - 5000000000.0;
            new Task_get_detal().execute();
        } else {
            Toast.makeText(komplektovka.this, "Неизвестный код", Toast.LENGTH_LONG).show();
        }


    }

    private class Task_get_elka extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... path) {

            String content;
            try {
                //Здесь пиши тяжеловестный код
                String otvet = query_zapros(new String[]{
                        "select FIRST 1 a.*, b.STATUS_ID from MTARA a LEFT JOIN MTARALOG b ON b.MTARA_ID=a.ID " +
                                "where a.ID=" + find_bd_id + " ORDER BY b.ID DESC"
                });


                JSONObject zakaz = new JSONArray(otvet).getJSONObject(0);
                mtara_l = new class_mtara(
                        zakaz.getInt("ID"),
                        zakaz.getString("NAME"),
                        zakaz.getInt("MTARATYPE_ID"),
                        zakaz.optInt("STATUS_ID", 1)
                );


                String otvet2 = query_zapros(new String[]{
                        "select * from WOTDELKA where MTARA_ID=" + mtara_l.ID
                });


                JSONArray friends = new JSONArray(otvet2);
                detal_list = new ArrayList<class_detal>();
                for (int i = 0; i < friends.length(); i++) {


                    JSONObject zakaz2 = friends.getJSONObject(i);
                    Log.d("asdadasd", zakaz2.getString("NAME"));
                    detal_list.add(new class_detal(
                            zakaz2.getInt("ID"),
                            zakaz2.getString("NAME"),
                            zakaz2.getInt("CUSTOMID"),
                            zakaz2.getString("V"),
                            zakaz2.getString("S"),
                            zakaz2.getString("G"),
                            zakaz2.optInt("MTARA_ID", 0),
                            zakaz2.getString("PREF"),
                            zakaz2.getString("SV"),
                            zakaz2.optInt("MOTDELKA_ID_POKR", 0),
                            zakaz2.optInt("MOTDELKA_ID_ZVET", 0)
                    ));
                }


                content = "";

            } catch (IOException ex) {
                content = ex.getMessage();
            } catch (JSONException e) {
                content = e.getMessage();
            }
            return content;
        }

        @Override
        protected void onPostExecute(String content) {


            fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frgmCont, new Fragment1(mtara_l, detal_list));
            fragmentTransaction.commit();

        }


    }

    private class Task_get_detal extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... path) {

            String content;
            try {
                //Здесь пиши тяжеловестный код
                content = query_zapros(new String[]{
                        "select * from WOTDELKA where ID=" + find_bd_id
                });
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

            Intent intent = new Intent(komplektovka.this, detal_obzor.class);
            intent.putExtra("detal", det_list.get(0));

            komplektovka.this.startActivity(intent);
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

package com.nchauzov.gn;


import android.app.FragmentTransaction;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class visible_activiti extends AppCompatActivity {

    String id;
    FragmentTransaction fTrans;
    AppCompatActivity getContext;

    public static ArrayList<zakaz_class> history = new ArrayList<zakaz_class>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.visible_activiti);


        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        id = getIntent().getStringExtra("id");
        getSupportActionBar().setTitle(id);

        getContext = this;


    }

    @Override
    protected void onResume() {
        super.onResume();

        load_spisDoh();

    }

    public void load_spisDoh() {
/*
        URL url=new URL("http://ecad.giulianovars.ru/");
        HttpsURLConnection c=(HttpsURLConnection)url.openConnection();
        c.setRequestMethod("GET"); // установка метода получения данных -GET
        c.setReadTimeout(10000); // установка таймаута перед выполнением - 10 000 миллисекунд
        c.connect(); // подключаемся к ресурсу
        BufferedReader reader= new BufferedReader(new InputStreamReader(c.getInputStream()));
        StringBuilder buf=new StringBuilder();
        String line=null;
        while ((line=reader.readLine()) != null) {
            buf.append(line + "\n");
        }
        Toast.makeText(this, buf, Toast.LENGTH_LONG);


*/


        new visible_activiti.ProgressTask().execute("http://ecad.giulianovars.ru/android/load_zakaz.php?command=2&uid=" + id);


    }


    private class ProgressTask extends android.os.AsyncTask<String, Void, String> {
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
            try {
                friends = new JSONArray(content);


                String otvet = "";
                String realsrok = "";

                for (int i = 0; i < friends.length(); i++) {
                    JSONObject zakaz = friends.getJSONObject(i);

                    otvet = zakaz.getString("status");
                    realsrok = zakaz.getString("realsrok");
                }


                ArrayList<String> dohod_list = new ArrayList<String>();

                for (String retval : otvet.split("\\r?\\n")) {
                    String[] errre = retval.split("\\|");
                    String status = "";
                    switch (errre[0]) {
                        case "1":
                            status = "Зарегистрирован";
                            break;
                        case "2":
                            status = "Принят конструктором";
                            break;
                        case "3":
                            status = "Утвержден ";
                            break;
                        case "4":
                            status = "В производстве";
                            break;
                        case "5":
                            status = "Отгружен";
                            break;
                    }
                    java.util.Date time = new java.util.Date((long) (Integer.parseInt(errre[1])) * 1000);
                    java.text.SimpleDateFormat fmtOut = new java.text.SimpleDateFormat("dd.MM.yyyy");

                    dohod_list.add(new String(fmtOut.format(time)) + " - " + status);
                }
                java.util.Collections.reverse(dohod_list);


                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext, R.layout.list_item, dohod_list);
                ListView lvie = (ListView) findViewById(R.id.lvie);

                Toast.makeText(getContext, realsrok, Toast.LENGTH_SHORT).show();
                java.util.Date time2 = new java.util.Date((long) (Integer.parseInt(realsrok)) * 1000);
                java.text.SimpleDateFormat fmtOut2 = new java.text.SimpleDateFormat("dd.MM.yyyy");


                TextView textView2 = (TextView) findViewById(R.id.textView2);
                textView2.setText("Реальный срок: " +new String(fmtOut2.format( time2)));

                lvie.setAdapter(adapter);


            } catch (JSONException e) {
                e.printStackTrace();
            }


        }

        private String getContent(String path) throws IOException {
            BufferedReader reader = null;
            try {
                URL url = new URL(path);
                HttpURLConnection c = (HttpURLConnection) url.openConnection();
                c.setRequestMethod("GET");
                c.setReadTimeout(10000);
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
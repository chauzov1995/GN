package com.nchauzov.gn;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class zakaz_activiti extends AppCompatActivity {


    FragmentTransaction fTrans;
    public static String LOG_TAG = "my_log";
    TextView contentView;
    String contentText = null;
    WebView webView;
    AppCompatActivity getContext;
    SearchView searchView1;
    ImageButton imageButton;
    EditText editText;

    public static ArrayList<zakaz_class> history = new ArrayList<zakaz_class>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zakaz_activiti);


        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getContext = this;

        imageButton = (ImageButton) findViewById(R.id.imageButton);
        editText = (EditText) findViewById(R.id.editText);
        imageButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View r) {
               // Toast.makeText(getContext,  editText.getText(),Toast.LENGTH_LONG).show();
                Intent intent2 = new Intent(getContext, visible_activiti.class);
                intent2.putExtra("id", editText.getText().toString());
                startActivity(intent2);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        load_spisDoh();

    }

    public void load_spisDoh() {
/*
        URL url=new URL("http://ecad.giulianovars.ru/android/load_zakaz.php");
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
        new ProgressTask().execute("http://ecad.giulianovars.ru/android/load_zakaz.php?command=1");


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
            try {
                friends = new JSONArray(content);


                ArrayList<zakaz_class> dohod_list = new ArrayList<zakaz_class>();


                for (int i = 0; i < friends.length(); i++) {
                    JSONObject zakaz = friends.getJSONObject(i);

                    // JSONObject contacts = friend.getJSONObject("contacts");

                    String phone = zakaz.getString("uid");
                    String email = zakaz.getString("name");
                    String skype = zakaz.getString("customer");

                    dohod_list.add(new zakaz_class(
                            1,
                            zakaz.getString("uid"),
                            zakaz.getString("customer"),
                            zakaz.getString("name"),
                            " с 02.04.2018 по 08.04.2018  (14)",
                            zakaz.getString("status")
                    ));

                }

                zakaz_adapter boxAdapter1 = new zakaz_adapter(dohod_list, getContext);
                RecyclerView gvdoh = (RecyclerView) findViewById(R.id.gvdoh);
                LinearLayoutManager glm1 = new LinearLayoutManager(getContext);
                RecyclerView.LayoutManager mLayoutManager1 = glm1;

                gvdoh.setLayoutManager(mLayoutManager1);

                gvdoh.setAdapter(boxAdapter1);
                gvdoh.setHasFixedSize(true);


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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_scan, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.create_menu) {

            Intent intent = new Intent(
                    "com.google.zxing.client.android.SCAN");
            intent.putExtra("SCAN_MODE", "MODE");
            startActivityForResult(intent, 0);
        } else {
        }
        return true;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {

        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {


                String contents = intent.getStringExtra("SCAN_RESULT"); // This will contain your scan result
                String format = intent.getStringExtra("SCAN_RESULT_FORMAT");

                Intent intent2 = new Intent(this, visible_activiti.class);
                intent2.putExtra("id", contents);
                startActivity(intent2);
            }
        }

    }
}
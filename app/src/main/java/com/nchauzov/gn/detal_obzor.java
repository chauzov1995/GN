package com.nchauzov.gn;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import static com.nchauzov.gn.texclass.query_zapros;

public class detal_obzor extends AppCompatActivity {


    TextView textView6;
    Button button_elk;
    int mtara_id_upd;
    class_detal detal;
    CardView cview, cview_new;



    RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detal_obzor);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        detal = (class_detal) getIntent().getExtras().getSerializable("detal");
        class_mtara mtara = (class_mtara) getIntent().getExtras().getSerializable("mtara");
        //   textView6 = (TextView) findViewById(R.id.textView6);

        LinearLayout ll_perel = (LinearLayout) findViewById(R.id.ll_perel);
        TextView textView10 = (TextView) findViewById(R.id.textView10);
        TextView textView11 = (TextView) findViewById(R.id.textView11);
        TextView textView12 = (TextView) findViewById(R.id.textView12);
        TextView textView13 = (TextView) findViewById(R.id.textView13);
        TextView textView14 = (TextView) findViewById(R.id.textView14);
        button_elk = (Button) findViewById(R.id.button_elk);
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

        Log.d("detal.NAME", detal.NAME);
        textView10.setText(detal.NAME);
        textView11.setText("Высота: " + detal.V);
        textView12.setText("Ширина: " + detal.S);
        textView13.setText("Глубина: " + detal.G);
        textView14.setText("Заказ: " + detal.CUSTOMID + "");
        button_elk.setText( detal.MTARA_ID + "");
        textView16.setText("Тип детали: " + detal.PREF);

        if(mtara.STATUS_ID==3){
            button3.setVisibility(View.GONE);
        }

        if (detal.MTARA_ID == 0) {//если тара не назаначена то лезть в интернет
            ll_perel.setVisibility(View.GONE);
            new Task_get_elka_recom().execute();
        }

        button_elk.setOnClickListener(new View.OnClickListener() {
            public void onClick(View r) {
         //       new Task_get_elka(detal.MTARA_ID).execute();
            }
        });
        button3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View r) {
                new Task_elka_set_null(detal_obzor.this, detal).execute();
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View r) {
                scan_open();
            }
        });
        button2_new.setOnClickListener(new View.OnClickListener() {
            public void onClick(View r) {
                scan_open();
            }
        });

    }


    void scan_open() {
        Intent intent = new Intent("com.google.zxing.client.android.SCAN");
        intent.putExtra("SCAN_MODE", "MODE");
        startActivityForResult(intent, 0);
    }


    public void onActivityResult(int requestCode, int resultCode, Intent intent) {

        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                Double contents = Double.parseDouble(intent.getStringExtra("SCAN_RESULT")); // This will contain your scan result
                if ((contents > 1500000000) && (contents < 1600000000)) { // код тары
                    contents -= 1500000000;

                    mtara_id_upd = contents.intValue();
                    new Task_poloj_v_elku(detal_obzor.this, mtara_id_upd, detal).execute();

                } else {
                    Toast.makeText(detal_obzor.this, "Это не ёлка", Toast.LENGTH_LONG).show();
                }
            }
        }
    }


    public class Task_elka_set_null extends AsyncTask<Void, Void, String> {

        detal_obzor ctx;
        class_detal detal;

        public Task_elka_set_null(detal_obzor ctx, class_detal detal_id) {
            this.ctx = ctx;
            this.detal = detal_id;

        }

        @Override
        protected String doInBackground(Void... voids) {

            String content;
            try {
                detal.MTARA_ID=0;

                query_zapros(new String[]{
                        "UPDATE WOTDELKA SET MTARA_ID=null where ID=" + detal.ID,
                });
                content = "";

            } catch (IOException ex) {
                content = ex.getMessage();
            }
            return content;
        }

        @Override
        protected void onPostExecute(String content) {
            //здесь обновление UI

            ((LinearLayout) ctx.findViewById(R.id.ll_perel)).setVisibility(View.GONE);
            ((CardView) ctx.findViewById(R.id.cview_new)).setVisibility(View.GONE);
            ((CardView) ctx.findViewById(R.id.cview)).setVisibility(View.GONE);

            new Task_get_elka_recom().execute();

        }
    }


    private class Task_get_elka_recom extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... path) {

            String content;
            try {
                //Здесь пиши тяжеловестный код
                content = query_zapros(new String[]{
                        "select DISTINCT c.ID, c.NAME, c.MTARATYPE_ID, b.STATUS_ID from WOTDELKA a " +
                                "LEFT JOIN MTARALOG b ON a.MTARA_ID=b.MTARA_ID " +
                                "LEFT JOIN MTARA c ON a.MTARA_ID=c.ID " +
                                "where a.SV='" + detal.SV + "' and a.MOTDELKA_ID_POKR=" + detal.MOTDELKA_ID_POKR + " and a.MOTDELKA_ID_ZVET=" + detal.MOTDELKA_ID_ZVET + " AND b.STATUS_ID=2 "
                });
            } catch (IOException ex) {
                content = ex.getMessage();
            }
            return content;
        }

        @Override
        protected void onPostExecute(String content) {

            JSONArray friends = null;
            ArrayList<class_mtara> recomend_mtara = new ArrayList<class_mtara>();
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

                mRecyclerView.setHasFixedSize(true);
                LinearLayoutManager mLayoutManager = new LinearLayoutManager(detal_obzor.this, LinearLayoutManager.HORIZONTAL, false);
                mRecyclerView.setLayoutManager(mLayoutManager);
                MyAdapterbtn mAdapter = new MyAdapterbtn(recomend_mtara, detal_obzor.this, detal);
                mRecyclerView.setAdapter(mAdapter);

            } else {
                Log.d("asdasdasdas",detal.MTARA_ID+"");
                if (detal.MTARA_ID == 0) {
                    cview_new.setVisibility(View.VISIBLE);
                }
            }
        }

    }

    public static class Task_poloj_v_elku extends AsyncTask<Void, Void, String> {

        AppCompatActivity ctx;
        int mtara_id_upd;
        class_detal detal;

        public Task_poloj_v_elku(detal_obzor ctx, int mtara_id_upd, class_detal detal_id) {
            this.ctx = ctx;
            this.mtara_id_upd = mtara_id_upd;
            this.detal = detal_id;
            Log.d("dsadas", "asdasd");
        }

        @Override
        protected String doInBackground(Void... voids) {

            String content;
            try {
                //Здесь пиши тяжеловестный код
                boolean pustaya_elka = false;
                boolean gotova_k_komplerktazii = false;
                boolean elka_estb_v_base = false;
                boolean sovmestimaya_elka = false;

                Log.d("ewererwr", "pustaya_elka" + pustaya_elka + " gotova_k_komplerktazii" + gotova_k_komplerktazii + " elka_estb_v_base" + elka_estb_v_base + " sovmestimaya_elka" + sovmestimaya_elka);

                //проверим пустая ли ёлка
                String otvet = query_zapros(new String[]{
                        "SELECT * FROM WOTDELKA where MTARA_ID=" + mtara_id_upd,
                });
                JSONArray json_otvet = new JSONArray(otvet);
                if (json_otvet.length() == 0) pustaya_elka = true; //ёлка пустая

                //если ёлка пустая нужно узнать готова ли к комплектации (в мтаралог является ли статус 2 )
                if (pustaya_elka) {
                    String otvet2 = query_zapros(new String[]{
                            "SELECT STATUS_ID FROM MTARALOG where id=(SELECT MAX(id) FROM MTARALOG WHERE mtara_id = " + mtara_id_upd + ")"
                    });
                    JSONArray json_otvet2 = new JSONArray(otvet2);
                    if (json_otvet2.length() > 0) elka_estb_v_base = true; //елка есть в логе
                    if (elka_estb_v_base) {
                        int STATUS_ID = json_otvet2.getJSONObject(0).optInt("STATUS_ID", 1);
                        if (STATUS_ID == 2) gotova_k_komplerktazii = true; //ёлка имеет статус 2
                    } else {
                        //если елки вообще нет в базе то нужно её создать
                        query_zapros(new String[]{
                                "INSERT INTO MTARALOG (MTARA_ID, STATUS_ID) VALUES (" + mtara_id_upd + ", 2)"
                        });
                        gotova_k_komplerktazii = true; //ёлка имеет статус 2
                    }
                } else {


                    String otvet3 = query_zapros(new String[]{
                            "select DISTINCT c.ID, c.NAME, c.MTARATYPE_ID, b.STATUS_ID from WOTDELKA a " +
                                    "LEFT JOIN MTARALOG b ON a.MTARA_ID=b.MTARA_ID " +
                                    "LEFT JOIN MTARA c ON a.MTARA_ID=c.ID " +
                                    "where a.SV='" + detal.SV + "' and a.MOTDELKA_ID_POKR=" + detal.MOTDELKA_ID_POKR + " and a.MOTDELKA_ID_ZVET=" + detal.MOTDELKA_ID_ZVET + " AND b.STATUS_ID=2 "
                    });

                    JSONArray friends = new JSONArray(otvet3);
                    for (int i = 0; i < friends.length(); i++) {
                        int ID = friends.getJSONObject(i).getInt("ID");
                        if (ID == mtara_id_upd) sovmestimaya_elka = true;

                    }
                }
                //либо пустая ёлка либо

                if (gotova_k_komplerktazii || sovmestimaya_elka) {

                    query_zapros(new String[]{
                            "UPDATE WOTDELKA SET MTARA_ID=" + mtara_id_upd + " where ID=" + detal.ID,
                    });
                    content = "";
                } else {
                    content = "1";
                }


                Log.d("ewererwr", "pustaya_elka" + pustaya_elka + " gotova_k_komplerktazii" + gotova_k_komplerktazii + " elka_estb_v_base" + elka_estb_v_base + " sovmestimaya_elka" + sovmestimaya_elka);

            } catch (IOException ex) {
                content = ex.getMessage();
            } catch (JSONException e) {
                content = e.getMessage();
            }
            return content;
        }

        @Override
        protected void onPostExecute(String content) {
            //здесь обновление UI
            Log.d("asdas", content);
            if (content.equals("1")) {
                Toast.makeText(ctx, "В эту ёлку нельзя положить деталь", Toast.LENGTH_LONG).show();
            } else {
                ((LinearLayout) ctx.findViewById(R.id.ll_perel)).setVisibility(View.VISIBLE);
                ((Button) ctx.findViewById(R.id.button_elk)).setText( mtara_id_upd + "");
                ((CardView) ctx.findViewById(R.id.cview_new)).setVisibility(View.GONE);
                ((CardView) ctx.findViewById(R.id.cview)).setVisibility(View.GONE);


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

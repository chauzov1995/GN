package com.nchauzov.gn;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.journeyapps.barcodescanner.ViewfinderView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

import static com.nchauzov.gn.texclass.query_zapros;

public class komplektovka extends AppCompatActivity {

    LinearLayout skan_elka, ll_komplload;
    private FragmentTransaction fragmentTransaction;
    SearchView simpleSearchView;
    Double find_bd_id;
    class_mtara mtara_l;
    ConstraintLayout prog_load;


    RecyclerView rv_operac;
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;


    TextView textView5;
    CheckBox checkBox;
    TabHost tabHost;
    ExpandableListView expListView;

    private CaptureManager capture;
    private DecoratedBarcodeView barcodeScannerView;
    private Button switchFlashlightButton;
    private ViewfinderView viewfinderView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_komplektovka);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        simpleSearchView = (SearchView) findViewById(R.id.simpleSearchView); // inititate a search view
        checkBox = (CheckBox) findViewById(R.id.checkBox);
        expListView = (ExpandableListView) findViewById(R.id.expListView);
        rv_operac = (RecyclerView) findViewById(R.id.rv_operac);
        textView5 = (TextView) findViewById(R.id.textView5);
        tabHost = (TabHost) findViewById(R.id.tabHost);
        prog_load = (ConstraintLayout) findViewById(R.id.prog_load);


        skan_elka = (LinearLayout) findViewById(R.id.skan_elka);
        ll_komplload = (LinearLayout) findViewById(R.id.ll_komplload);

        ll_komplload.setVisibility(View.GONE);
        prog_load.setVisibility(View.GONE);


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


        skan_elka.setOnClickListener(new View.OnClickListener() {
            public void onClick(View r) {

                //  new IntentIntegrator(komplektovka.this).initiateScan(); // `this` is the current Activity


                new IntentIntegrator(komplektovka.this).setOrientationLocked(false).setCaptureActivity(CustomScannerActivity.class).initiateScan();


   /*
                IntentIntegrator integrator = new IntentIntegrator(komplektovka.this);
                integrator.setBeepEnabled(true);//отключить звук
                integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
                integrator.initiateScan();



                Intent intent = new Intent("com.google.zxing.client.android.SCAN");
                intent.putExtra("SCAN_MODE", "MODE");
                startActivityForResult(intent, 0);
*/

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


        //    String query = simpleSearchView.getQuery().toString(); // get the query string currently in the text field


        //   find_bd_id =804359.0;
        //  new Task_get_detal().execute();
        ///временннооооо!!!!!!

        //  new Task_get_elka(204.0).execute();

        //    mScannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view
        //   setContentView(mScannerView);                // Set the scanner view as the content view


    }


    public void onupdate() {
        if (mtara_l != null) {

            new Task_get_elka(Double.valueOf(mtara_l.ID)).execute();

        }
    }

    // Get the results:
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();


                Double contents = Double.parseDouble(result.getContents()); // This will contain your scan result

                parse_strich(contents);


            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }


    }

    void parse_strich(Double contents) {


        if ((contents > 1500000000) && (contents < 1600000000)) { // код тары

            //  fragmentTransaction = getFragmentManager().beginTransaction();
            //  fragmentTransaction.replace(R.id.frgmCont, new load_pb());
            //  fragmentTransaction.commit();
            find_bd_id = contents - 1500000000;
            new Task_get_elka(find_bd_id).execute();

        } else if (contents > 5000000000.0) { // код тары
            find_bd_id = contents - 5000000000.0;
            new Task_get_detal().execute();
        } else {
            Toast.makeText(komplektovka.this, "Неизвестный код", Toast.LENGTH_LONG).show();
        }


    }


    public class Task_get_elka extends AsyncTask<String, Void, String> {

        ArrayList<class_operac> operac_list;
        ArrayList<class_detal> detal_list;
        Double find_bd_id;


        public Task_get_elka(Double find_bd_id) {
            ll_komplload.setVisibility(View.GONE);
            prog_load.setVisibility(View.VISIBLE);
            this.find_bd_id = find_bd_id;
        }

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
                Log.d("asdasdad", mtara_l.ID + "");

                String otvet2 = query_zapros(new String[]{
                        "select a.*, b.name as MPARTSGROUPS_NAME  from WOTDELKA a left join MPARTSGROUPS b on b.ID = a.MPARTSGROUPS_ID  where a.MTARA_ID=" + mtara_l.ID
                });


                JSONArray friends2 = new JSONArray(otvet2);
                detal_list = new ArrayList<class_detal>();
                for (int i = 0; i < friends2.length(); i++) {


                    JSONObject zakaz2 = friends2.getJSONObject(i);
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
                            zakaz2.optInt("MOTDELKA_ID_ZVET", 0),
                            zakaz2.optInt("MPARTSGROUPS_ID", 0),
                            zakaz2.getString("MPARTSGROUPS_NAME")
                    ));
                }


                //загрузка операций СТАРТ  3

                if (mtara_l.STATUS_ID == 3) {


                    String where_MTEXPROCID = "";
                    for (int i = 0; i < detal_list.size(); i++) {
                        where_MTEXPROCID += "'" + detal_list.get(i).MPARTSGROUPS_ID + "', ";
                    }
                    where_MTEXPROCID = where_MTEXPROCID.replaceAll(", $", "");
                    String otvet3 = "";
                    if (!where_MTEXPROCID.isEmpty()) {
                        otvet3 = query_zapros(new String[]{
                                "select  b.*, a.mpartsgroups_id from MAGAZINETEXOPER a " +
                                        " LEFT JOIN MTEXOPER b ON a.MTEXOPER_ID = b.ID" +
                                        " where a.mpartsgroups_id IN (" + where_MTEXPROCID + ") and a.current_flag=1"
                        });
                    }


                    JSONArray friends3 = null;
                    operac_list = new ArrayList<class_operac>();

                    friends3 = new JSONArray(otvet3);

                    for (int i = 0; i < friends3.length(); i++) {

                        JSONObject zakaz3 = friends3.getJSONObject(i);
                        //  Log.d("asdadasd", zakaz.getString("NAME"));
                        int Id_zakaza = zakaz3.getInt("ID");

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
                                    zakaz3.optString("NAME", ""),
                                    zakaz3.optDouble("OPERTIME", 0.0),
                                    zakaz3.optInt("MTEXPROCID", 0),
                                    zakaz3.optInt("MOPERID", 0),
                                    zakaz3.optInt("NN", 0),
                                    zakaz3.optBoolean("IS_CHECK_POINT", false)
                            ));
                        }
                    }

                }
                //загрузка операций ЕНД


                content = "";

            } catch (IOException ex) {

                content = ex.getMessage();

            } catch (JSONException e) {
                content = e.getMessage();
            }
            Log.d("asdasdad", content);
            return content;
        }

        @Override
        protected void onPostExecute(String content) {


            textView5.setText(mtara_l.NAME);
            if (mtara_l.STATUS_ID == 3) {

                checkBox.setChecked(true);

                checkBox.setEnabled(false);
            }


            checkBox.setOnClickListener(new View.OnClickListener() {
                public void onClick(View r) {


                    AlertDialog.Builder builder = new AlertDialog.Builder(komplektovka.this);
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


            ArrayList<String> spis_partiy_do = new ArrayList<String>();
            for (class_detal group : detal_list) {
                spis_partiy_do.add(group.MPARTSGROUPS_NAME);
            }
            //ArrayList<String> mGroupsArray = new LinkedHashSet<>(spis_partiy_do);
            Set<String> mGroupsArray = new LinkedHashSet<>(spis_partiy_do);

            ArrayList<ArrayList<class_detal>> groups = new ArrayList<ArrayList<class_detal>>();
            ArrayList<String> spis_partiy_do_arr = new ArrayList<String>();
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
            expadapter adapter = new expadapter(komplektovka.this, groups, spis_partiy_do_arr, mtara_l);
            expListView.setAdapter(adapter);


            tabHost.getTabWidget().getChildTabViewAt(1).setVisibility(View.GONE);
            if (mtara_l.STATUS_ID == 3) {
                rv_operac.setHasFixedSize(true);
                mLayoutManager = new LinearLayoutManager(komplektovka.this);
                rv_operac.setLayoutManager(mLayoutManager);
                mAdapter = new adapter_operac(operac_list, mtara_l.ID, komplektovka.this);
                rv_operac.setAdapter(mAdapter);
                tabHost.getTabWidget().getChildTabViewAt(1).setVisibility(View.VISIBLE);
            } else {
                tabHost.getTabWidget().getChildTabViewAt(1).setVisibility(View.GONE);
            }

            ll_komplload.setVisibility(View.VISIBLE);
            prog_load.setVisibility(View.GONE);

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
            Toast.makeText(komplektovka.this, "Ёлка скомплектована!", Toast.LENGTH_LONG).show();
            // checkBox.setClickable(false);
            checkBox.setEnabled(false);
        }


    }


    public class Task_get_detal extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... path) {

            String content;
            try {
                //Здесь пиши тяжеловестный код
                content = query_zapros(new String[]{
                        "select a.*, b.name as MPARTSGROUPS_NAME from WOTDELKA a " +
                                "left join MPARTSGROUPS b on b.ID = a.MPARTSGROUPS_ID  " +
                                "where a.ID=" + find_bd_id
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
                            zakaz.optInt("MOTDELKA_ID_ZVET", 0),
                            zakaz.optInt("MPARTSGROUPS_ID", 0),
                            zakaz.getString("MPARTSGROUPS_NAME")
                    ));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Intent intent = new Intent(komplektovka.this, detal_obzor.class);
            intent.putExtra("detal", det_list.get(0));
            intent.putExtra("mtara", mtara_l);
            komplektovka.this.startActivity(intent);
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.refresh, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == android.R.id.home) {
            finish();
        } else if (i == R.id.refrech) {
            onupdate();

        } else {
        }
        return true;
    }


}

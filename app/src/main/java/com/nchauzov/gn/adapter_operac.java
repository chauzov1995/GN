package com.nchauzov.gn;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import static com.nchauzov.gn.texclass.query_zapros;

public class adapter_operac extends RecyclerView.Adapter<adapter_operac.ViewHolder> {
    private ArrayList<class_operac> mDataset;
    komplektovka ctx;
    int mtara_id_slect;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public CheckBox tv1;
        LinearLayout elementves;

        public ViewHolder(View v) {
            super(v);
            //  mTextView = v;
            tv1 = (CheckBox) v.findViewById(R.id.tv1);

            elementves = (LinearLayout) v.findViewById(R.id.elementves);

        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public adapter_operac(ArrayList<class_operac> myDataset, int _mtara_id_slect, komplektovka _ctx) {
        mDataset = myDataset;
        mtara_id_slect = _mtara_id_slect;
        ctx = _ctx;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public adapter_operac.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.element_operac, parent, false);


        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        final class_operac p = mDataset.get(position);
        holder.tv1.setText(p.NAME);

        holder.tv1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View r) {


                AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
                builder.setTitle("Подтвердить выполнение операции?")
                        //      .setMessage("Покормите кота!")
                        .setCancelable(false)
                        .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                operac_id_sele = p.ID;
                                Log.d("(p.NAME)", (p.ID) + "");
                                new Task_operac_vipoln().execute();

                             //   ctx.onupdate();


                              //  new ctx.getspis_operac().execute();
                            }
                        })
                        .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                holder.tv1.setChecked(false);
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();


            }
        });
    }


    int operac_id_sele;

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }


    private class Task_operac_vipoln extends AsyncTask<String, Void, String> {

        ArrayList<class_detal> spis_ne_v_elk;
int rezvip;
        @Override
        protected String doInBackground(String... path) {

            String content;
            try {



// 1. получил все партии на ёлке   !OK
                String MAGAZINETEXOPERquery = query_zapros(new String[]{
                        "select distinct(b.MPARTSGROUPS_ID) from WOTDELKA a " +
                                "left join MAGAZINETEXOPER b ON a.MPARTSGROUPS_ID=b.MPARTSGROUPS_ID and current_flag=1 " +
                                "where  a.mtara_id=" + mtara_id_slect + " and b.MTEXOPER_ID=" + operac_id_sele
                });

                String where_MTEXPROCID = "";
                JSONArray friends = new JSONArray(MAGAZINETEXOPERquery);
                for (int i = 0; i < friends.length(); i++) {
                    JSONObject zakaz = friends.getJSONObject(i);
                    where_MTEXPROCID += "" + zakaz.optString("MPARTSGROUPS_ID", "0") + ", ";
                }
                where_MTEXPROCID = where_MTEXPROCID.replaceAll(", $", "");

                // 2.получил все детали в этих партиях  !OK
                String spisdet_part = query_zapros(new String[]{
                        "select * from WOTDELKA where MPARTSGROUPS_ID in (" + where_MTEXPROCID + ")"
                });
                JSONArray spisdet_partjson = new JSONArray(spisdet_part);

                // 3. проверил все ли детали лежат на ёлках, всем ли назначена ёлка , mtaraid
             spis_ne_v_elk=new ArrayList<class_detal>();
                for (int i = 0; i < spisdet_partjson.length(); i++) {
                    JSONObject zakaz2 = spisdet_partjson.getJSONObject(i);



                    if(zakaz2.optInt("MTARA_ID", 0)==0){
                        spis_ne_v_elk.add(new class_detal(
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
                                ""
                        ));
                    }
                }




if(spis_ne_v_elk.size()==0) {


    query_zapros(new String[]{
            "UPDATE MAGAZINETEXOPER set DateBegin = current_timestamp, DateEnd=current_timestamp where  MPARTSGROUPS_ID in (" + where_MTEXPROCID + ") and MTEXOPER_ID=" + operac_id_sele + " and current_flag=1"
    });
    rezvip=1;
    content = "Успех";
}else{
    rezvip=2;
    content="Операция не выполнена! Имеется "+spis_ne_v_elk.size()+" деталей которым не присвоена ёлка";

}





            } catch (IOException ex) {
                content = ex.getMessage();
            } catch (JSONException e) {
                content = e.getMessage();
            }

            return content;
        }



        @Override
        protected void onPostExecute(String content) {
            Log.d("asdasd", content);
            switch (rezvip) {
                case 1:
                    Toast.makeText(ctx, content, Toast.LENGTH_LONG).show();
                    break;
                case 2:

                 // int nach_size= 1;// spis_ne_v_elk.size();
            //    for (final class_detal elem : spis_ne_v_elk) {

                    recursalert( spis_ne_v_elk,0 );

            //        nach_size++;
            //    }

                // checkBox.setClickable(false);
                //checkBox.setEnabled(false);
                    break;
            }
        }

       void  recursalert(final ArrayList<class_detal> spisdet, final int getpor){
           final int nach_size=spisdet.size();
            if(getpor>=nach_size){
                new Task_operac_vipoln().execute();
                ctx.onupdate();
                return;
            }
           final class_detal elem=spisdet.get(getpor);

            AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
            builder.setTitle(nach_size-getpor + " деталям(и) не присвоена ёлка")
                    .setMessage("Положить " + elem.NAME + " в " + mtara_id_slect + "?")

                    .setCancelable(false)
                    .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {


                            new Task_poloj_v_elku(ctx, mtara_id_slect, elem).execute();

                            recursalert( spisdet, getpor+1);
                        }
                    })
                    .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                   // recursalert( spisdet, getpor+1);
                                }
                            }

                    );
            AlertDialog alert = builder.create();
            alert.show();

        }
    }


    public static class Task_poloj_v_elku extends AsyncTask<Void, Void, String> {

        AppCompatActivity ctx;
        int mtara_id_upd;
        class_detal detal;

        public Task_poloj_v_elku(komplektovka ctx, int mtara_id_upd, class_detal detal_id) {
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


            }

        }
    }
}
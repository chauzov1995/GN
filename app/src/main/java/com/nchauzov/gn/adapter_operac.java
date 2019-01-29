package com.nchauzov.gn;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.nchauzov.gn.texclass.query_zapros;

public class adapter_operac extends RecyclerView.Adapter<adapter_operac.ViewHolder> {
    private ArrayList<class_operac> mDataset;
    Activity ctx;
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
    public adapter_operac(ArrayList<class_operac> myDataset, int _mtara_id_slect, Activity _ctx) {
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
                                new Fragment1.getspis_operac(ctx,).execute();


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
        @Override
        protected String doInBackground(String... path) {

            String content;
            try {



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


                query_zapros(new String[]{
                        "UPDATE MAGAZINETEXOPER set DateBegin = current_timestamp, DateEnd=current_timestamp where  MPARTSGROUPS_ID in (" + where_MTEXPROCID + ") and MTEXOPER_ID=" + operac_id_sele + " and current_flag=1"
                });




/*

                String[] CheckOpenCLoseOper = CheckOpenCLoseOper(mpartsgroup_id, 1, 100);
                if (CheckOpenCLoseOper.equals("1")) {
                    //      if (CheckOpenCLoseOper(mpartsgroup_id, 1, IBDataSet2.FieldByName("NN").AsInteger) = 1) {

                    IBDataSet2.Edit;
                    IBDataSet2.FieldByName("DateBegin").Value=DateOperBegin;
                    IBDataSet2.Post;



                    query_zapros(new String[]{
                            "select M.ID, M.MAGAZINEID, M.MAGAZINETEXPROCID, M.MOPERID,   M.MPARTSGROUPS_ID, M.MTEXPROCID, M.NN, M.OPERTIME,  M.TEORDATEBEGIN, M.DateBegin,M.DateEnd,M.TEORDATEEND,  M.Current_Flag," +
                                    " M.UserId1, M.UserId2, MDay_Work_Part_ID, M.Prim" +
                                    " from MAGAZINETEXOPER M" +
                                    " where M.MPARTSGROUPS_ID=" + DateOperEnd +
                                    " order by MPARTSGROUPS_ID"
                    });


                }
                // закроем операцию
                CheckOpenCLoseOper = CheckOpenCLoseOper(mpartsgroup_id, 0, 100);
                if (CheckOpenCLoseOper[0].equals("1")) {
                    //     if (CheckOpenCLoseOper(mpartsgroup_id, 0, IBDataSet2.FieldByName("NN").AsInteger) = 1) {


                    IBDataSet2.Edit;
                    IBDataSet2.FieldByName("DateEnd").Value=DateOperEnd;
                    IBDataSet2.Post;
                    IBDataSet2.Active=false;
                    IBDataSet2.Active=true;
                    mpartsgroup_id.Refresh;
                    IBDataSet2.Transaction.CommitRetaining;


                    query_zapros(new String[]{
                            "select M.ID, M.MAGAZINEID, M.MAGAZINETEXPROCID, M.MOPERID,   M.MPARTSGROUPS_ID, M.MTEXPROCID, M.NN, M.OPERTIME,  M.TEORDATEBEGIN, M.DateBegin,M.DateEnd,M.TEORDATEEND,  M.Current_Flag," +
                                    " M.UserId1, M.UserId2, MDay_Work_Part_ID, M.Prim" +
                                    " from MAGAZINETEXOPER M" +
                                    " where M.MPARTSGROUPS_ID=" + DateOperEnd +
                                    " order by MPARTSGROUPS_ID"
                    });


                }

*/


                content = "Успех";
            } catch (IOException ex) {
                content = ex.getMessage();
            } catch (JSONException e) {
                content = e.getMessage();
            }

            return content;
        }


        String[] CheckOpenCLoseOper(int PartID, int Open, int NN) throws IOException, JSONException {
            String[] otvet_sats = new String[2];


            if (Open == 1) {// проверим при открытии новой операции все ли другие закрыты


                String count_query = query_zapros(new String[]{
                        "select Count(ID) from MagazineTexOper where MPARTSGROUPS_ID=" + PartID + " and DateBegin is Null and DateEnd is Not Null"
                });

                int count = new JSONArray(count_query).getJSONObject(0).getInt("COUNT");


                if (count > 0) {

                    otvet_sats[0] = "0";
                    otvet_sats[1] = "Нельзя \"Открыть операцию\" если уже открыта другая";
                    return otvet_sats;

                }
            }
            if (Open == 0) {// проверим при закрытии операции открыта ли она
/*
                if (IBDataSet2.FieldByName("DateBegin").
                        IsNull) {
                    Toast.makeText(ctx, "Операция еще не открыта", Toast.LENGTH_LONG).show();
                    return 0;
                }
*/
            }
            if (Open == 1) { // проверим при открытии новой операции вдруг есть пропущенные
                String count_query = query_zapros(new String[]{
                        "select Count(ID) from MagazineTexOper where MPARTSGROUPS_ID=" +
                                PartID + " and NN<" +
                                NN + " and DateEnd is Null"
                });
                int count = new JSONArray(count_query).getJSONObject(0).getInt("COUNT");


                if (count > 0) {
                    //  return "Есть не закрытые операции на более ранней стадии";

                    otvet_sats[0] = "0";
                    otvet_sats[1] = "Есть не закрытые операции на более ранней стадии";
                    return otvet_sats;
                }
            }
            otvet_sats[0] = "1";
            otvet_sats[1] = "Успех";
            return otvet_sats;
        }


        @Override
        protected void onPostExecute(String content) {
            Log.d("asdasd", content);
            Toast.makeText(ctx, content, Toast.LENGTH_LONG).show();

            // checkBox.setClickable(false);
            //checkBox.setEnabled(false);
        }
    }
}
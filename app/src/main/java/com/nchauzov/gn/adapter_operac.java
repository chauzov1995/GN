package com.nchauzov.gn;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class adapter_operac extends RecyclerView.Adapter<adapter_operac.ViewHolder> {
    private ArrayList<class_detal> mDataset;
   Activity ctx;

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
    public adapter_operac(ArrayList<class_detal> myDataset, Activity _ctx) {
        mDataset = myDataset;
        ctx=_ctx;
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
    public void onBindViewHolder(ViewHolder holder, int position) {

        final class_detal p = mDataset.get(position);
        holder.tv1.setText(p.NAME);

        holder.elementves.setOnClickListener(new View.OnClickListener() {
            public void onClick(View r) {

                Intent intent = new Intent(ctx, detal_obzor.class);
         intent.putExtra("detal",  p );
                ctx.startActivity(intent);
            }
        });
    }


    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
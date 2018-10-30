package com.nchauzov.gn;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private ArrayList<class_detal> mDataset;
   Activity ctx;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView tv1;
        LinearLayout elementves;

        public ViewHolder(View v) {
            super(v);
            //  mTextView = v;
            tv1 = (TextView) v.findViewById(R.id.tv1);

            elementves = (LinearLayout) v.findViewById(R.id.elementves);

        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyAdapter(ArrayList<class_detal> myDataset, Activity _ctx) {
        mDataset = myDataset;
        ctx=_ctx;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.element, parent, false);


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
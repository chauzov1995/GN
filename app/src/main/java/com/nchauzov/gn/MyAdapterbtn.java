package com.nchauzov.gn;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;

public class MyAdapterbtn extends RecyclerView.Adapter<MyAdapterbtn.ViewHolder> {
    private ArrayList<class_mtara> mDataset;
    detal_obzor ctx;
    class_detal detal;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public Button button5;

        public ViewHolder(View v) {
            super(v);

            //  mTextView = v;
            button5 = (Button) v.findViewById(R.id.button5);

        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyAdapterbtn(ArrayList<class_mtara> myDataset, detal_obzor _ctx, class_detal detal) {
        mDataset = myDataset;
        ctx = _ctx;
        this.detal = detal;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyAdapterbtn.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.blog_row, parent, false);


        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        final class_mtara p = mDataset.get(position);
        holder.button5.setText(p.NAME);
        holder.button5.setOnClickListener(new View.OnClickListener() {
            public void onClick(View r) {
                new detal_obzor.Task_poloj_v_elku(ctx, p.ID, detal).execute();
            }
        });

    }


    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
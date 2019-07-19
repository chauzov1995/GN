package com.nchauzov.gn.sklad;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.nchauzov.gn.R;

import java.util.List;

class DataAdapter extends RecyclerView.Adapter<DataAdapter.ViewHolder> {

    private LayoutInflater inflater;
    private List<class_zakaz> phones;
    sklad activity;

    DataAdapter(Context context, List<class_zakaz> phones, sklad activity) {
        this.phones = phones;
        this.inflater = LayoutInflater.from(context);
        this.activity=activity;
    }
    @Override
    public DataAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final DataAdapter.ViewHolder holder, int position) {
        final class_zakaz phone = phones.get(position);
     //   holder.imageView.setImageResource(phone.getImage());
      //  holder.nameView.setText(phone);
      //  holder.textView15.setText(phone);

        Glide.with(activity)
                .load(phone.path)
                .into(holder.imageView);
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent = new Intent(activity, Main2Activity.class);
                intent.putExtra("id", phone.ID);
                intent.putExtra("path", phone.path);
                activity.startActivity(intent);

                /*


                Glide
                        .with(activity)
                        .load(phone.path)
                             .into(full);
                full.setVisibility(View.VISIBLE);*/
            }
        });


    }

    @Override
    public int getItemCount() {
        return phones.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final PhotoView imageView;
      //  final TextView textView15;
        ViewHolder(View view){
            super(view);
            imageView = (PhotoView)view.findViewById(R.id.image);
          //  textView15 = view.findViewById(R.id.textView15);
        }
    }
}
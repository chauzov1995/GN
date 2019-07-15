package com.nchauzov.gn;


import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;


public class zakaz_adapter extends RecyclerView.Adapter<zakaz_adapter.ViewHolder> {


    static ArrayList<zakaz_class> objects;
    static AppCompatActivity getactivity;

    // класс view holder-а с помощью которого мы получаем ссылку на каждый элемент
    // отдельного пункта списка
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // наш пункт состоит только из одного TextView
        public TextView zakaz, fio;

        public CardView cv;

        public ViewHolder(View v) {
            super(v);

            zakaz = (TextView) v.findViewById(R.id.zakaz);
            fio = (TextView) v.findViewById(R.id.fio);
            cv = (CardView) v.findViewById(R.id.cv);


        }





    }


    // Конструктор
    public zakaz_adapter(ArrayList<zakaz_class> _objects, AppCompatActivity _getactivity) {
        objects = _objects;
        getactivity = _getactivity;
    }


    // Создает новые views (вызывается layout manager-ом)
    @Override
    public zakaz_adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        ViewHolder vh;
        View v;


        v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.gv_doh, parent, false);
        vh = new ViewHolder(v);


        // тут можно программно менять атрибуты лэйаута (size, margins, paddings и др.)


        return vh;
    }

    // Заменяет контент отдельного view (вызывается layout manager-ом)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final zakaz_class p = objects.get(position);

        holder.fio.setText(p.fio);
        holder.zakaz.setText(p.zakaz);
        holder.cv.setOnClickListener(new View.OnClickListener() {
            public void onClick(View r) {
                Intent intent = new Intent(getactivity, visible_activiti.class);
                intent.putExtra("id", p.zakaz);
                getactivity.startActivity(intent);
            }
        });

    }



    // Возвращает размер данных (вызывается layout manager-ом)
    @Override
    public int getItemCount() {
        return objects.size();
    }

    @Override
    public int getItemViewType(int position) {
        return 1;//(objects.get(position).new_plus == 1) ? 0 : 1;
    }


}
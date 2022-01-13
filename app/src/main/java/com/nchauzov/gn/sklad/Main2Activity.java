package com.nchauzov.gn.sklad;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.nchauzov.gn.R;

import cz.msebera.android.httpclient.Header;

public class Main2Activity extends AppCompatActivity {
    int idfoto;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);


        Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        idfoto = intent.getIntExtra("id",0);
        String path = intent.getStringExtra("path");


        PhotoView photoView = (PhotoView) findViewById(R.id.photo_view);
        Glide.with(this).load(path).into(photoView);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_delete, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == android.R.id.home) {
            finish();
        } else if (i == R.id.delete) {

            AlertDialog.Builder builder = new AlertDialog.Builder(Main2Activity.this);
            builder.setTitle("Удалить фото?")


                    .setCancelable(true)
                    .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            })
                    .setPositiveButton("Да",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {




                                    AsyncHttpClient client = new AsyncHttpClient();
                                    client.get("https://teplogico.ru/gn2/"+idfoto, new AsyncHttpResponseHandler() {


                                        @Override
                                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {



                                            finish();
                                        }

                                        @Override
                                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                                        }
                                    });


                                    dialog.cancel();



                                }
                            });
            AlertDialog alert = builder.create();
            alert.show();








        } else {
        }
        return true;
    }

}

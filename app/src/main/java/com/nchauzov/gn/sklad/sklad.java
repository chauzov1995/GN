package com.nchauzov.gn.sklad;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.nchauzov.gn.R;
import com.symbol.emdk.EMDKManager;
import com.symbol.emdk.barcode.BarcodeManager;
import com.symbol.emdk.barcode.Scanner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import cz.msebera.android.httpclient.Header;

import static androidx.core.content.FileProvider.getUriForFile;


public class sklad extends AppCompatActivity {

    private ProgressBar progressBar3;
    File directory_file;
    static final int REQUEST_IMAGE_CAPTURE = 1;

    ArrayList<class_zakaz> fotozakaz;

    private EMDKManager emdkManager = null;
    private BarcodeManager barcodeManager = null;
    private Scanner scanner = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sklad);








        int permissionStatus = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);

        if (permissionStatus == PackageManager.PERMISSION_GRANTED) {

        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                    6757);
        }


        FloatingActionButton floating_action_button = (FloatingActionButton) findViewById(R.id.floating_action_button);


        floating_action_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (tek_zakaz != null) {
                    dispatchTakePictureIntent();
                } else {
                    Toast.makeText(sklad.this, "Сначала выберите заказ", Toast.LENGTH_LONG).show();
                }

            }

        });

/*
        Glide.with(this)
                .load("https://sun3-13.userapi.com/c852224/v852224241/117426/tzLOGjcg5wM.jpg")
                .into(imageView4);

*/
        recyclerView = (RecyclerView) findViewById(R.id.grid1);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(mLayoutManager);
        // создаем адаптер
        progressBar3 = (ProgressBar) findViewById(R.id.progressBar3);

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);


        //textView15 = findViewById(R.id.textView15);


        progressBar3.setVisibility(View.INVISIBLE);
        recyclerView.setVisibility(View.INVISIBLE);


        searchView = (MaterialSearchView) findViewById(R.id.search_view);

        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //Do some magic


                zaproz(query);


                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Do some magic
                return false;
            }
        });

        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                //Do some magic
            }

            @Override
            public void onSearchViewClosed() {
                //Do some magic
            }
        });


    }


    //  TextView textView15;
    MaterialSearchView searchView;


    private Toolbar mToolbar;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);


        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);

        return true;
    }


    RecyclerView recyclerView;

    public void zaproz(final String zakaz) {
        if (zakaz == null) return;
        tek_zakaz = zakaz;

        progressBar3.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.INVISIBLE);
        Log.d("zpaors", "https://teplogico.ru/gn1/" + tek_zakaz);
        AsyncHttpClient client = new AsyncHttpClient();
        client.get("https://teplogico.ru/gn1/" + zakaz, new JsonHttpResponseHandler() {


            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // If the response is JSONObject instead of expected JSONArray
                Log.d("JSONObject", statusCode + "");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray timeline) {
                // Pull out the first event on the public timeline
                Log.d("JSONArray", statusCode + "");

                try {


                    fotozakaz = new ArrayList<>();

                    for (int i = 0; i < timeline.length(); i++) {

                        JSONObject zakaz = timeline.getJSONObject(i);

                        fotozakaz.add(new class_zakaz(zakaz.getInt("id"), zakaz.getString("path")));
                        Log.d("JSONArray", zakaz.getString("path"));
                    }
                    DataAdapter adapter = new DataAdapter(sklad.this, fotozakaz, sklad.this);
                    // устанавливаем для списка адаптер
                    recyclerView.setAdapter(adapter);

                    //textView15.setText("Заказ " + zakaz);
                    mToolbar.setTitle("Заказ " + zakaz);

                    progressBar3.setVisibility(View.INVISIBLE);
                    recyclerView.setVisibility(View.VISIBLE);
                } catch (JSONException e) {
                    e.printStackTrace();

                    //   Log.d("JSONArray", e.getMessage());
                }
            }


        });


    }

    String tek_zakaz;

    void asdadsad() {

        Bitmap myBitmap = BitmapFactory.decodeFile(directory_file.getAbsolutePath());


        AsyncHttpClient client = new AsyncHttpClient();
        File myFile = directory_file;
        RequestParams params = new RequestParams();
        try {
            params.put("file_input", myFile);
        } catch (FileNotFoundException e) {
        }
        Log.d("post", "http://teplogico.ru/gn/" + tek_zakaz);
        client.post("http://teplogico.ru/gn/" + tek_zakaz, params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                // called before request is started

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                // called when response HTTP status is "200 OK"
                Log.d("status", statusCode + "" + response);
                zaproz(tek_zakaz);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                Log.d("status", statusCode + "");
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            asdadsad();
        }
        if (requestCode == 323) {
            zaproz(tek_zakaz);
        }


    }


    static final int REQUEST_TAKE_PHOTO = 1;

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);


                Log.d("asd", photoURI.getPath() + "");
                directory_file = photoFile;
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);


            }
        }
    }

    String currentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
     //   unregisterReceiver(myBroadcastReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceivers();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unRegisterReceivers();
    }

    // Register/unregister broadcast receiver and filter results

    void registerReceivers() {

/*
        Intent i = new Intent();
        i.setAction("com.symbol.datawedge.api.ACTION");
        i.putExtra("com.symbol.datawedge.api.SET_DEFAULT_PROFILE", "gnprof");
        this.sendBroadcast(i);

*/


        IntentFilter filter = new IntentFilter();
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        filter.addAction(getResources().getString(R.string.activity_intent_filter_action));
        registerReceiver(myBroadcastReceiver, filter);

    }

    void unRegisterReceivers(){

/*
        Intent i = new Intent();
        i.setAction("com.symbol.datawedge.api.ACTION");
        i.putExtra("com.symbol.datawedge.api.RESET_DEFAULT_PROFILE", "");
*/
        unregisterReceiver(myBroadcastReceiver);
    }


    //
    // After registering the broadcast receiver, the next step (below) is to define it.
    // Here it's done in the MainActivity.java, but also can be handled by a separate class.
    // The logic of extracting the scanned data and displaying it on the screen
    // is executed in its own method (later in the code). Note the use of the
    // extra keys defined in the strings.xml file.
    //
    private BroadcastReceiver myBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Bundle b = intent.getExtras();

            //
            // The following is useful for debugging to verify
            // the format of received intents from DataWedge:
            //
            // for (String key : b.keySet())
            // {
            //   Log.v(LOG_TAG, key);
            // }
            //

            Bundle extras = getIntent().getExtras();
            if (intent.hasExtra("com.symbol.datawedge.api.RESULT_GET_ACTIVE_PROFILE")) {
                String activeProfile = extras.getString("com.symbol.datawedge.api.RESULT_GET_ACTIVE_PROFILE");
                Log.d("adasd", activeProfile);
            }


            if (action.equals(getResources().getString(R.string.activity_intent_filter_action))) {
                //
                //  Received a barcode scan
                //

                try {
                    displayScanResult(intent, "via Broadcast");
                } catch (Exception e) {

                    //
                    // Catch if the UI does not exist when broadcast is received
                    //
                }
            }
        }
    };






    //
    // The section below assumes that a UI exists in which to place the data. A production
    // application would be driving much of the behavior following a scan.
    //
    private void displayScanResult(Intent initiatingIntent, String howDataReceived) {
        String decodedSource = initiatingIntent.getStringExtra(getResources().getString(R.string.datawedge_intent_key_source));
        String decodedData = initiatingIntent.getStringExtra(getResources().getString(R.string.datawedge_intent_key_data));
        String decodedLabelType = initiatingIntent.getStringExtra(getResources().getString(R.string.datawedge_intent_key_label_type));

    //    final TextView lblScanSource = (TextView) findViewById(R.id.lblScanSource);
    //    final TextView lblScanData = (TextView) findViewById(R.id.lblScanData);
    //    final TextView lblScanLabelType = (TextView) findViewById(R.id.lblScanDecoder);
     //   lblScanSource.setText(decodedSource + " " + howDataReceived);
    //    lblScanData.setText(decodedData);
        Log.d("adasd", decodedData);
      //  lblScanLabelType.setText(decodedLabelType);


        zaproz(decodedData);
    }

}
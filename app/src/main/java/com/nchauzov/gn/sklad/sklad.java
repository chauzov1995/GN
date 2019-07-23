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
import android.widget.TextView;
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
import com.symbol.emdk.EMDKManager.EMDKListener;
import com.symbol.emdk.EMDKResults;
import com.symbol.emdk.barcode.BarcodeManager;
import com.symbol.emdk.barcode.ScanDataCollection;
import com.symbol.emdk.barcode.Scanner;
import com.symbol.emdk.barcode.Scanner.DataListener;
import com.symbol.emdk.barcode.Scanner.StatusListener;
import com.symbol.emdk.barcode.ScannerConfig;
import com.symbol.emdk.barcode.ScannerException;
import com.symbol.emdk.barcode.ScannerInfo;
import com.symbol.emdk.barcode.ScannerResults;
import com.symbol.emdk.barcode.StatusData;

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


public class sklad extends AppCompatActivity  implements EMDKListener,  DataListener, StatusListener, BarcodeManager.ScannerConnectionListener {

    private ProgressBar progressBar3;
    // Declare a variable to store EMDKManager object
    private EMDKManager emdkManager = null;

    // Declare a variable to store Barcode Manager object
    private BarcodeManager barcodeManager = null;

    // Declare a variable to hold scanner device to scan
    private Scanner scanner = null;



    File directory_file;
    private TextView mSelectText;
    static final int REQUEST_IMAGE_CAPTURE = 1;

    ArrayList<class_zakaz> fotozakaz;



    @Override
    public void onOpened(EMDKManager emdkManager) {

        this.emdkManager = emdkManager;


        // Acquire barcode manager resources
        barcodeManager = (BarcodeManager) emdkManager.getInstance(EMDKManager.FEATURE_TYPE.BARCODE);
        if(barcodeManager == null)
        {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            barcodeManager = (BarcodeManager) emdkManager.getInstance(EMDKManager.FEATURE_TYPE.BARCODE);
        }

        // Add connection listener
        if (barcodeManager != null) {
            barcodeManager.addConnectionListener(this);
            initScanner();
        }


    }


    @Override
    public void onClosed() {

        try {
            if (emdkManager != null) {
                // Release all the resources
                emdkManager.release();
                emdkManager = null;
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStatus(StatusData statusData) {

        StatusData.ScannerStates state = statusData.getState();
        String statusString = statusData.getFriendlyName()+" is " + state.toString().toLowerCase();



        switch(state) {
            case IDLE:
                try {
                    scanner.read();
                } catch (ScannerException e) {
                    e.printStackTrace();

                }
                break;
        }
    }
    @Override
    public void onData(ScanDataCollection scanDataCollection) {
        // TODO Auto-generated method stub


        // The ScanDataCollection object gives scanning result and
// the collection of ScanData. So check the data and its status.
        String dataStr = "";
        if ((scanDataCollection != null) && (scanDataCollection.getResult() == ScannerResults.SUCCESS)) {
            ArrayList<ScanDataCollection.ScanData> scanData = scanDataCollection.getScanData();
            // Iterate through scanned data and prepare the dataStr
            for (ScanDataCollection.ScanData data : scanData) {
                // Get the scanned data
                String barcodeData = data.getData();
                // Get the type of label being scanned
                ScanDataCollection.LabelType labelType = data.getLabelType();
                // Concatenate barcode data and label type
                dataStr = barcodeData;
            }

// Updates EditText with scanned data and type of label on UI thread.
            updateData(dataStr);


        }
    }

// Variable to hold scan data length


    private void updateData(final String result) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Update the dataView EditText on UI thread with barcode data and its label type
Log.d("resuzls", result);
              //  dataView.append(result + "\n");
                zaproz(result);
            }
        });
    }







    @Override
    protected void onDestroy() {
        super.onDestroy();

        try {
            // Release all resources
            if (emdkManager != null) {
                emdkManager.release();
                emdkManager = null;

            }
        }
        catch (Throwable throwable)
        {
            throwable.printStackTrace();
        }
    }

    private void deInitScanner() {
        if (scanner != null) {
            try {
                // Release the scanner
                scanner.release();
            } catch (Exception e) {
                updateStatus(e.getMessage());
            }
            scanner = null;
        }
    }

    private void initBarcodeManager() {

// Get the feature object such as BarcodeManager object for
// accessing the feature.

        barcodeManager = (BarcodeManager) emdkManager.getInstance(EMDKManager.FEATURE_TYPE.BARCODE);

// Add external scanner connection listener

        if (barcodeManager == null) {
            Toast.makeText(this, "Barcode scanning is not supported.", Toast.LENGTH_LONG).show();
            finish();
        }
    }


    private void initScanner() {

        if (scanner == null) {

            scanner = barcodeManager.getDevice(BarcodeManager.DeviceIdentifier.DEFAULT);

            if (scanner != null) {

                scanner.addDataListener(this);
                scanner.addStatusListener(this);

                try {
                    scanner.enable();
                } catch (ScannerException e) {

                }
            }
        }
    }


    private void updateStatus(final String status) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Update the status text view on UI thread with current scanner state
                //statusTextView.setText("" + status);
            }
        });
    }




    private void setConfig() {
        if (scanner != null) {
            try {
                // Get scanner config
                ScannerConfig config = scanner.getConfig();

                // Enable haptic feedback
                if (config.isParamSupported("config.scanParams.decodeHapticFeedback")) {
                    config.scanParams.decodeHapticFeedback = true;
                }

                // Set scanner config
                scanner.setConfig(config);
            } catch (ScannerException e) {
                updateStatus(e.getMessage());
            }
        }


    }


    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            boolean appSentToBackground = intent.getAction().equals(Intent.ACTION_USER_BACKGROUND);
            boolean appCameToForeground = intent.getAction().equals(Intent.ACTION_USER_FOREGROUND);

            // TODO MultiUser
            if (appSentToBackground) {


                try {
                    // Release all resources
                    if (emdkManager != null) {
                        emdkManager.release();
                        emdkManager = null;
                    }

                } catch (Throwable e) {
                    e.printStackTrace();
                }

            }

            if (appCameToForeground) {



                // Setting objects to null if background task are interrupted.
                if(scanner != null)
                    scanner = null;

                if(barcodeManager != null)
                    barcodeManager = null;

                if(emdkManager != null)
                    emdkManager = null;

                EMDKResults results = EMDKManager.getEMDKManager(getApplicationContext(), sklad.this);

                // Use a final variable if MainActivity.this (above) fails.
                // If result == SUCCESS, onOpened is called
                // and scanner object is reacquired
                if (results.statusCode != EMDKResults.STATUS_CODE.SUCCESS) {
                    return;
                }
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sklad);





        try {
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_USER_BACKGROUND);
            filter.addAction(Intent.ACTION_USER_FOREGROUND);
            registerReceiver(broadcastReceiver, filter);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        EMDKResults results = EMDKManager.getEMDKManager(getApplicationContext(), this);
        if (results.statusCode != EMDKResults.STATUS_CODE.SUCCESS) {
            return;
        }





        int permissionStatus = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);

        if (permissionStatus == PackageManager.PERMISSION_GRANTED) {

        } else {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA},
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
        if(zakaz==null)return;
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
        if(requestCode==323){
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
    public void onConnectionChange(ScannerInfo scannerInfo, BarcodeManager.ConnectionState connectionState) {

    }
}

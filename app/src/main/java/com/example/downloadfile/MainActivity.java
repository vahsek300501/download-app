package com.example.downloadfile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_STORAGE_CODE = 100;
    EditText mUrlEt;
    Button mDownloadBtn;
    Long downloadID1;
    Long downloadID2;
    long startTime1;
    long startTime2;
    int crntDownloadNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        initialization
        mUrlEt = findViewById(R.id.urlEt);
        mDownloadBtn = findViewById(R.id.downloadUrl);
        crntDownloadNumber = 1;
        registerReceiver(onDownloadComplete,new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        mDownloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                        String [] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                        requestPermissions(permissions,PERMISSION_STORAGE_CODE);
                    } else {
                        startDownloading();
//                        startDownloading();
                    }
                } else {
                    startDownloading();
//                    startDownloading();
                }

            }
        });
    }

    private void startDownloading() {

        String url = mUrlEt.getText().toString().trim();
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
        request.setTitle("Download");
        request.setDescription("Download");
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        System.out.println(Environment.DIRECTORY_DOWNLOADS);
        request.setDestinationInExternalFilesDir(this,Environment.DIRECTORY_DOWNLOADS,""+System.currentTimeMillis());
        DownloadManager manager = (DownloadManager)getSystemService(Context.DOWNLOAD_SERVICE);
        startTime1 = System.currentTimeMillis();
        downloadID1 = manager.enqueue(request);
        downloadID2 = manager.enqueue(request);
        startTime2 = System.currentTimeMillis();
    }

    private BroadcastReceiver onDownloadComplete = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            if (downloadID1 == id) {
                double endTime = System.currentTimeMillis();
                Toast.makeText(MainActivity.this, "Download Time for 1:  "+ (endTime-startTime1), Toast.LENGTH_LONG).show();
            }
            if (downloadID2 == id) {
                double endTime = System.currentTimeMillis();
                Toast.makeText(MainActivity.this, "Download Time for 2:  "+ (endTime-startTime2), Toast.LENGTH_LONG).show();
            }
        }
    };


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case PERMISSION_STORAGE_CODE: {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startDownloading();
                } else {
                    Toast.makeText(this,"permission Denied....",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(onDownloadComplete);
    }
}
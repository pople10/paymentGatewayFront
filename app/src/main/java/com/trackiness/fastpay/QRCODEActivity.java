package com.trackiness.fastpay;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.budiyev.android.codescanner.*;
import com.trackiness.broadcastReceiver.NetworkBroadCastReceiver;
import com.trackiness.utility.Utility;

public class QRCODEActivity extends AppCompatActivity {

    static boolean  granted = false;
    private CodeScanner mCodeScanner;
    private static final int CAMERA_REQUEST_CODE=101;
    CodeScannerView scannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);
        Utility.authMiddleware(getApplicationContext());
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        BroadcastReceiver br = new NetworkBroadCastReceiver();
        getApplicationContext().registerReceiver(br, filter);
        scannerView = (CodeScannerView) findViewById(R.id.scanner_view);
        System.out.println(scannerView);
        getSupportActionBar().hide();
        setupPermissions();
        if(mCodeScanner != null){
            mCodeScanner.startPreview();
        }else{
            codeScanner();
        }

    }

    public void codeScanner(){
        System.out.println(scannerView);
        mCodeScanner = new CodeScanner(QRCODEActivity.this, scannerView);
        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull com.google.zxing.Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String out = result.getText();
                        if(Utility.isInteger(out))
                        {
                            Intent intent = new Intent(QRCODEActivity.this, TransferActivity.class);
                            intent.putExtra("id",out);
                            startActivity(intent);
                        }
                        else
                        {
                            mCodeScanner.startPreview();
                            new Alert(QRCODEActivity.this,Alert.ERROR)
                                    .setContentText("Invalid QR Code")
                                    .show();
                        }
                    }
                });
            }

        });
        scannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCodeScanner.startPreview();
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        setupPermissions();
        if(mCodeScanner == null){
            codeScanner();
        }else{
            mCodeScanner.startPreview();
        }
        Toast.makeText(this, "restart", Toast.LENGTH_SHORT);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Toast.makeText(this, "resume", Toast.LENGTH_SHORT);

        setupPermissions();
        if(granted){
            if(mCodeScanner == null){
                codeScanner();
            }else{
                mCodeScanner.startPreview();
            }
        }else{
            setupPermissions();
        }
    }

    @Override
    protected void onPause() {
        setupPermissions();
        if(mCodeScanner != null){
            mCodeScanner.releaseResources();
        }

        super.onPause();
    }

    @Override
    protected void onStop() {
        if(mCodeScanner != null){
            mCodeScanner.releaseResources();
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if(mCodeScanner != null){
            mCodeScanner.releaseResources();
        }
        super.onDestroy();
    }

    private void makeRequest(){
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}
                , CAMERA_REQUEST_CODE);
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode){
            case CAMERA_REQUEST_CODE:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    //TODO: show dialog where the user can't retry granting the app permission
                    granted = true;

                }else{
                    Toast.makeText(this, "You need the camera Permission to use the app", Toast.LENGTH_SHORT);

                }
                return;
        }
    }

    private void setupPermissions(){
        int permission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if(permission == PackageManager.PERMISSION_GRANTED){
            //use API that requires app permission
        }else{
            makeRequest();
        }
    }

}
package com.trackiness.fastpay;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.budiyev.android.codescanner.*;
import com.google.zxing.Result;
import com.trackiness.broadcastReceiver.NetworkBroadCastReceiver;
import com.trackiness.utility.Utility;


public class QRtCodeFragment extends Fragment {

    private CodeScanner mCodeScanner;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final Activity activity = getActivity();
        View root = inflater.inflate(R.layout.fragment_qr_code, container, false);
        CodeScannerView scannerView = root.findViewById(R.id.scanner_view);
        Utility.authMiddleware(root.getContext());
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        BroadcastReceiver br = new NetworkBroadCastReceiver();
        root.getContext().registerReceiver(br, filter);
        mCodeScanner = new CodeScanner(activity, scannerView);
        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String out = result.getText();
                        if(Utility.isInteger(out))
                        {
                            Intent intent = new Intent(getContext(), TransferActivity.class);
                            intent.putExtra("id",out);
                            startActivity(intent);
                        }
                        else
                        {
                            mCodeScanner.startPreview();
                            new Alert(activity,Alert.ERROR)
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
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        mCodeScanner.startPreview();
    }

    @Override
    public void onPause() {
        mCodeScanner.releaseResources();
        super.onPause();
    }
}
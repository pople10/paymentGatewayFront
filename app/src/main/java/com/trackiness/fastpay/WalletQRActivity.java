package com.trackiness.fastpay;

import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.trackiness.broadcastReceiver.NetworkBroadCastReceiver;
import com.trackiness.services.CryptoService;
import com.trackiness.services.models.Wallet;
import com.trackiness.utility.Utility;
import com.trackiness.utility.VolleyCallBack;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class WalletQRActivity extends AppCompatActivity {
    CryptoService cryptoService;
    Loader loader;
    private Wallet wallet;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walletqr);
        getSupportActionBar().hide();
        Utility.authMiddleware(getApplicationContext());
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        BroadcastReceiver br = new NetworkBroadCastReceiver();
        getApplicationContext().registerReceiver(br, filter);
        cryptoService = new CryptoService(WalletQRActivity.this);
        loader = new Loader(WalletQRActivity.this);
        init();
    }

    public void init()
    {
        (findViewById(R.id.back_button)).setOnClickListener(e->{
            finish();
        });
        cryptoService.getWallet(new VolleyCallBack() {
            @Override
            public void onSuccess() {
                wallet = cryptoService.getWallet();
                ((ImageView)findViewById(R.id.displayQRWallet)).setImageBitmap(
                        Utility.generateQRCode(wallet.getWallet())
                );
                ((TextView)findViewById(R.id.walletDetailsQR)).setText(wallet.getWallet());
                ((ImageButton)findViewById(R.id.copyWallet)).setOnClickListener(e->{
                    try {
                        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText( "wallet",wallet.getWallet());
                        clipboard.setPrimaryClip(clip);
                        Toast.makeText(WalletQRActivity.this, "Copied to clipboard", Toast.LENGTH_SHORT).show();
                    }catch (Exception exp)
                    {

                    }
                });
            }

            @Override
            public void onError(int error) {
                if(error==404)
                {
                    new Alert(WalletQRActivity.this,Alert.WARNING)
                            .setContentText("There is no wallet yet")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    sweetAlertDialog.dismiss();
                                    finish();
                                }
                            })
                            .show();
                    return;
                }
                finish();
            }

            @Override
            public void beforeSend() {
                loader.show();
            }

            @Override
            public void onFinish() {
                if(loader.isShowing())
                    loader.dismiss();
            }
        });
    }
}
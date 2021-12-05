package com.trackiness.fastpay;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.trackiness.broadcastReceiver.NetworkBroadCastReceiver;
import com.trackiness.services.CryptoService;
import com.trackiness.services.models.Wallet;
import com.trackiness.utility.CallBack;
import com.trackiness.utility.Utility;
import com.trackiness.utility.VolleyCallBack;

import java.math.BigDecimal;

public class CryptoActivity extends AppCompatActivity {
    Loader loader;
    CryptoService cryptoService;
    TextView walletView;
    Wallet wallet;
    MaterialButton toggle;
    MaterialButton withdraw;
    MaterialButton deposit;
    BigDecimal btcRate;
    BigDecimal usdRate;
    TextView rateBTCLabel;
    TextView rateUSDLabel;
    TextInputEditText amountInput;
    TextView preview;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crypto);
        getSupportActionBar().hide();
        Utility.authMiddleware(getApplicationContext());
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        BroadcastReceiver br = new NetworkBroadCastReceiver();
        getApplicationContext().registerReceiver(br, filter);
        cryptoService = new CryptoService(CryptoActivity.this);
        loader = new Loader(CryptoActivity.this);
        init();
    }
    private void init()
    {
        walletView = ((TextView) findViewById(R.id.walletDetails));
        toggle = (MaterialButton) findViewById(R.id.removeWalletBtn);
        withdraw = (MaterialButton) findViewById(R.id.withdrawBtn);
        deposit = (MaterialButton) findViewById(R.id.depositBtn);
        rateBTCLabel = (TextView)  findViewById(R.id.RateBtc);
        rateUSDLabel = (TextView)  findViewById(R.id.RateUSD);
        amountInput = (TextInputEditText) findViewById(R.id.amountConvertWallet);
        preview = (TextView) findViewById(R.id.previewRateWallet);
        (findViewById(R.id.back_button)).setOnClickListener(e->{
            finish();
        });
        withdraw.setOnClickListener(e->{
            cryptoService.depositOrWithraw(new VolleyCallBack() {
                @Override
                public void onSuccess() {
                    refresh();
                }

                @Override
                public void onError(int error) {

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
            },amountInput.getText().toString(),false);
        });
        deposit.setOnClickListener(e->{
            cryptoService.depositOrWithraw(new VolleyCallBack() {
                @Override
                public void onSuccess() {
                    refresh();
                }

                @Override
                public void onError(int error) {

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
            },amountInput.getText().toString(),true);
        });
        amountInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                simulation(""+s);
                System.out.println("changed "+s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        toggle.setOnClickListener(e->{
            String label = toggle.getText().toString();
            if(label.contains("CLOSE"))
            {
                cryptoService.close(new VolleyCallBack() {
                    @Override
                    public void onSuccess() {
                        refresh();
                    }

                    @Override
                    public void onError(int error) {

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
                return;
            }
            cryptoService.open(new VolleyCallBack() {
                @Override
                public void onSuccess() {
                    refresh();
                }

                @Override
                public void onError(int error) {

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
        });
        refresh();
    }

    public void refresh()
    {
        cryptoService.getWallet(new VolleyCallBack() {
            @Override
            public void onSuccess() {
                wallet = cryptoService.getWallet();
                if(wallet!=null)
                    walletView.setText(wallet.generateLabel());
                else
                    walletView.setText("No wallet is available");
                toggleBtn();
                withdraw.setEnabled(true);
            }

            @Override
            public void onError(int error) {
                if(error==404)
                {
                    walletView.setText("No wallet is opened");
                    toggle.setVisibility(View.GONE);
                    withdraw.setEnabled(false);
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
        cryptoService.getRate(new CallBack() {
            @Override
            public void onSuccess(String s) {
                btcRate = BigDecimal.valueOf(Double.parseDouble(s));
                usdRate = BigDecimal.valueOf(1/btcRate.floatValue());
                rateBTCLabel.setText(""+btcRate);
                rateUSDLabel.setText(""+ usdRate.setScale(10, BigDecimal.ROUND_HALF_EVEN));
            }

            @Override
            public void onError() {
                finish();
            }

            @Override
            public void beforeProcessing() {
                loader.show();
            }

            @Override
            public void onFinish() {
                if(loader.isShowing())
                    loader.dismiss();
            }
        });
    }
    public void simulation(String s)
    {
        if(s==null||s.trim().equals("")||!Utility.isDecimal(s))
        {
            preview.setText("");
            preview.setVisibility(View.GONE);
            return;
        }
        Double amount = Double.parseDouble(s);
        preview.setText("\nEstimated deposit : "
                +usdRate.multiply(BigDecimal.valueOf(amount)).setScale(18, BigDecimal.ROUND_HALF_EVEN)+" BTC\n");
        preview.setText(preview.getText()+"Estimated Withdraw : $"+
                btcRate.multiply(BigDecimal.valueOf(amount)).setScale(5, BigDecimal.ROUND_HALF_EVEN)+"\n");
        preview.setVisibility(View.VISIBLE);
    }
    public void toggleBtn()
    {
        toggle.setVisibility(View.VISIBLE);
        toggle.setText("CLOSE WALLET");
        toggle.setBackgroundColor(getResources().getColor(R.color.danger));
        if(!wallet.isActive())
        {
            toggle.setText("OPEN WALLET");
            toggle.setBackgroundColor(getResources().getColor(R.color.primary));
        }
    }
}

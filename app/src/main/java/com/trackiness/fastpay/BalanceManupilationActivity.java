package com.trackiness.fastpay;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.trackiness.broadcastReceiver.NetworkBroadCastReceiver;
import com.trackiness.services.BalanceService;
import com.trackiness.services.models.Balance;
import com.trackiness.services.models.Currency;
import com.trackiness.utility.Utility;
import com.trackiness.utility.VolleyCallBack;

import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class BalanceManupilationActivity extends AppCompatActivity {
    BalanceService balanceService;
    Loader loader;
    Balance balance;
    TextInputEditText label;
    TextInputEditText amount;
    MaterialButton update;
    MaterialButton convert;
    MaterialButton delete;
    AutoCompleteTextView listCurrency;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balance);
        Utility.authMiddleware(getApplicationContext());
        getSupportActionBar().hide();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        BroadcastReceiver br = new NetworkBroadCastReceiver();
        getApplicationContext().registerReceiver(br, filter);
        balanceService = new BalanceService(BalanceManupilationActivity.this);
        loader = new Loader(BalanceManupilationActivity.this);
        String id = getIntent().getStringExtra("id");
        System.out.println(id);
        init(id);
    }

    public void init(String id)
    {
        label = (TextInputEditText)findViewById(R.id.labelBalanceElement);
        amount = (TextInputEditText)findViewById(R.id.amountConvert);
        update = (MaterialButton)findViewById(R.id.updateLabelBtn);
        convert = (MaterialButton)findViewById(R.id.convertBtn);
        delete = (MaterialButton)findViewById(R.id.removeBalanceBtn);
        listCurrency = (AutoCompleteTextView)findViewById(R.id.currencyConvert);
        balanceService.getBalance(new VolleyCallBack() {
            @Override
            public void onSuccess() {
                balance = balanceService.getBalance();
                label.setText(balance.getLabel());
                TextView up = ((TextView)findViewById(R.id.labelBalance));
                up.setText(up.getText().toString()+balance.getLabel());
                balanceService.getUsedCurrenciesFilter(new VolleyCallBack() {
                    @Override
                    public void onSuccess() {
                        List<Currency> currencyList = balanceService.getCurrencies();
                        int size = currencyList.size();
                        String[] currencies = new String[size];
                        int i=0;
                        for(Currency j : currencyList)
                        {
                            currencies[i]=j.getIdCurrency();
                            i++;
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(BalanceManupilationActivity.this, android.R.layout.simple_dropdown_item_1line, currencies);
                        listCurrency.setAdapter(adapter);
                        getRate();
                        (findViewById(R.id.back_button)).setOnClickListener(e->{finish();});
                    }

                    @Override
                    public void onError(int error) {
                        startActivity(new Intent(BalanceManupilationActivity.this, BalanceManagementActivity.class));
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
                },balance.getCurrency());
                setUpActions();
            }

            @Override
            public void onError(int error) {
                startActivity(new Intent(BalanceManupilationActivity.this, BalanceManagementActivity.class));
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
        },id);
    }

    public void getRate()
    {
        listCurrency.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String currencyToExchage = ((TextView)view).getText().toString();
                balanceService.getRate(new VolleyCallBack() {
                    @Override
                    public void onSuccess() {
                        ((TextView)findViewById(R.id.rateText)).setVisibility(View.VISIBLE);
                        ((TextView)findViewById(R.id.rateText)).setText("Estimated rate : "
                                +balanceService.getRate());
                    }

                    @Override
                    public void onError(int error) {
                        ((TextView)findViewById(R.id.rateText)).setVisibility(View.GONE);
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
                },balance.getCurrency(),currencyToExchage);
            }
        });
    }

    public void setUpActions()
    {
        update.setOnClickListener(e->
        {
            String labelToSend = label.getText().toString();
            balance.setLabel(labelToSend);
            balanceService.updateBalance(new VolleyCallBack() {
                @Override
                public void onSuccess() {

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
            },balance);
        });
        convert.setOnClickListener(e->
        {
            String amountToSend = amount.getText().toString();
            String to = listCurrency.getText().toString();
            balanceService.convert(new VolleyCallBack() {
                @Override
                public void onSuccess() {

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
            },balance.getCurrency(),to,amountToSend);
        });
        delete.setOnClickListener(e->
        {
            new Alert(BalanceManupilationActivity.this, Alert.WARNING)
                    .setContentText("Are you sure you want to delete?")
                    .setConfirmText("Delete")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.dismissWithAnimation();

                                balanceService.delete(new VolleyCallBack() {
                                    @Override
                                    public void onSuccess() {
                                        finish();
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
                                },balance);
                        }
                    })
                    .setCancelButton("Cancel", new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.dismissWithAnimation();
                        }
                    })
                    .show();
            });
    }

    @Override
    protected void onStart() {
        super.onStart();

    }
}

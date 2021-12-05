package com.trackiness.fastpay;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import com.google.android.material.textfield.TextInputEditText;
import com.trackiness.broadcastReceiver.NetworkBroadCastReceiver;
import com.trackiness.services.BalanceService;
import com.trackiness.services.models.Balance;
import com.trackiness.services.models.Currency;
import com.trackiness.utility.Utility;
import com.trackiness.utility.VolleyCallBack;

import java.util.List;

public class NewBalanceActivity extends AppCompatActivity {

    String[] currencies = {};
    BalanceService balanceService;
    Loader loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_new_balance);
        Utility.authMiddleware(getApplicationContext());
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        BroadcastReceiver br = new NetworkBroadCastReceiver();
        getApplicationContext().registerReceiver(br, filter);
        loader = new Loader(NewBalanceActivity.this);
        balanceService = new BalanceService(NewBalanceActivity.this);
        initBalance(true);

    }
    public void initBalance(boolean flag)
    {
        balanceService.getAvailableCurrencies(new VolleyCallBack() {
            @Override
            public void onSuccess() {
                List<Currency> arr = balanceService.getCurrencies();
                int size = arr.size();
                if(flag&&size==0)
                {
                    new Alert(NewBalanceActivity.this,Alert.WARNING)
                            .setContentText("No currencies are still available for your.")
                            .show();
                    return;
                }
                currencies = new String[size];
                int i=0;
                for(Currency j : arr)
                {
                    currencies[i]=j.getIdCurrency();
                    i++;
                }
            }

            @Override
            public void onError(int error) {
                (findViewById(R.id.back_button)).callOnClick();
            }

            @Override
            public void beforeSend() {
                loader.show();
            }

            @Override
            public void onFinish() {
                if(loader.isShowing())
                    loader.dismiss();
                init();
            }
        });
    }

    public void init(){
        AutoCompleteTextView currency = findViewById(R.id.currency);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, currencies);
        currency.setAdapter(adapter);
        (findViewById(R.id.back_button)).setOnClickListener(e->{finish();});
        (findViewById(R.id.add)).setOnClickListener(e->
        {
            Balance balance = new Balance();
            balance.setLabel(((TextInputEditText)findViewById(R.id.label)).getText().toString());
            balance.setCurrency(((AutoCompleteTextView)findViewById(R.id.currency))
            .getText().toString());
            if(Utility.isEmpty(balance.getLabel()))
            {
                new Alert(NewBalanceActivity.this,Alert.ERROR)
                        .setContentText("Label is empty")
                        .show();
                return;
            }
            if(Utility.isEmpty(balance.getCurrency()))
            {
                new Alert(NewBalanceActivity.this,Alert.ERROR)
                        .setContentText("Currency is empty")
                        .show();
                return;
            }
            balanceService.openBalance(new VolleyCallBack() {
                @Override
                public void onSuccess() {
                    ((TextInputEditText)findViewById(R.id.label)).setText("");
                    ((AutoCompleteTextView)findViewById(R.id.currency)).setText("");
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
                    initBalance(false);
                }
            },balance);
        });
    }
}
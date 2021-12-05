package com.trackiness.fastpay;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.trackiness.broadcastReceiver.NetworkBroadCastReceiver;
import com.trackiness.services.TransactionService;
import com.trackiness.services.models.TransactionData;
import com.trackiness.utility.Utility;
import com.trackiness.utility.VolleyCallBack;

public class TransactionActivity extends AppCompatActivity {
    TransactionService transactionService;
    LinearLayout loader,TEContainer;
    String id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);
        getSupportActionBar().hide();
        Utility.authMiddleware(getApplicationContext());
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        BroadcastReceiver br = new NetworkBroadCastReceiver();
        getApplicationContext().registerReceiver(br, filter);
        id = getIntent().getStringExtra("id");
        TextView y = findViewById(R.id.idPayment);
        y.setText("#"+(id.substring(0,8)));
        ((MaterialCardView)findViewById(R.id.back_button)).setOnClickListener(e->{finish();});
        transactionService = new TransactionService(TransactionActivity.this);
        TEContainer = (LinearLayout) findViewById(R.id.ContainerTH);
        loader =((LinearLayout) findViewById(R.id.loader));
        MaterialButton refund = findViewById(R.id.refund);
        transactionService.getOneTransaction(id, new VolleyCallBack() {
            @Override
            public void onSuccess() {
                TransactionData td = transactionService.getOnetransactions();
                ((TextView) findViewById(R.id.title)).setText(td.getTitle());
                ((TextView) findViewById(R.id.date)).setText(td.getStringDate());
                ((TextView) findViewById(R.id.userName)).setText(td.getName());
                ((TextView) findViewById(R.id.FullName)).setText(td.getFullname());
                ((TextView) findViewById(R.id.status)).setText(td.getType());
                ((TextView) findViewById(R.id.total)).setText(td.generateLabel());

                if(td.getStatus()==1 && td.getTypeT()==1){
                    refund.setVisibility(View.VISIBLE);
                    refund.setOnClickListener(e->{
                        e.setEnabled(false);
                        transactionService.refundTransaction(id, new VolleyCallBack() {
                            @Override
                            public void onSuccess() {
                                ((TextView) findViewById(R.id.status)).setText("REFUNDED");
                                e.setVisibility(View.GONE);
                            }
                            @Override
                            public void onError(int error) {
                                e.setEnabled(true);
                            }

                            @Override
                            public void beforeSend() {}

                            @Override
                            public void onFinish() {}
                        });
                    });
                }
                hideIndicator();
            }

            @Override
            public void onError(int error) {

            }

            @Override
            public void beforeSend() {

            }

            @Override
            public void onFinish() {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
    public void hideIndicator(){
        loader.setVisibility(View.GONE);
        TEContainer.setVisibility(View.VISIBLE);
    }
}
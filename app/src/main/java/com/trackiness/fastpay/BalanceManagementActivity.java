package com.trackiness.fastpay;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;
import com.trackiness.broadcastReceiver.NetworkBroadCastReceiver;
import com.trackiness.services.BalanceService;
import com.trackiness.services.models.Balance;
import com.trackiness.utility.Utility;
import com.trackiness.utility.VolleyCallBack;

import java.util.List;


public class BalanceManagementActivity extends AppCompatActivity {
    Loader loader;
    BalanceService balanceService;
    boolean starting = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balances);
        Utility.authMiddleware(getApplicationContext());
        getSupportActionBar().hide();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        BroadcastReceiver br = new NetworkBroadCastReceiver();
        getApplicationContext().registerReceiver(br, filter);
        balanceService = new BalanceService(BalanceManagementActivity.this);
        loader = new Loader(BalanceManagementActivity.this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        init();
    }

    public void init(){
        (findViewById(R.id.back_button)).setOnClickListener(e->{
            if(!starting) {
                finish();
                return;
            }
        });
        balanceService.getBalances(new VolleyCallBack() {
            @Override
            public void onSuccess() {
                List<Balance> list = balanceService.getBalancesData();
                LinearLayout TEContainer = (LinearLayout) findViewById(R.id.ContainerBalances);
                TEContainer.removeAllViews();
                for(Balance i : list)
                {
                    CreateBalanceView(TEContainer,i);
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
            }
        });
    }

    public void CreateBalanceView(LinearLayout TEContainer, Balance balance){
        LinearLayout TE = (LinearLayout) getLayoutInflater().inflate(R.layout.element_balance,null);
        ((TextView) TE.findViewById(R.id.balanceLabel)).setText(balance.getLabel());
        ((TextView) TE.findViewById(R.id.lastUpdate)).setText(Utility.generateDate(balance.getCreated_at()));
        ((TextView) TE.findViewById(R.id.amountBalance)).setText(balance.generateLabel());
        if(balance.getAmount()<0)
            ((TextView) TE.findViewById(R.id.amountBalance)).setTextColor(getResources().getColor(R.color.danger));
        MaterialCardView mcv = (MaterialCardView)TE.findViewById(R.id.cardViewElementBalance);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        p.setMargins(8, 8, 8, 8);
        mcv.setLayoutParams(p);
        OnclickElement(mcv, Integer.parseInt(""+balance.getId()));
        TEContainer.addView(TE);
    }

    public void OnclickElement(MaterialCardView c,int id){
        c.setOnClickListener(e->{
            Intent in = new Intent(BalanceManagementActivity.this, BalanceManupilationActivity.class);
            in.putExtra("id",id+"");
            startActivity(in);
        });
    }
}

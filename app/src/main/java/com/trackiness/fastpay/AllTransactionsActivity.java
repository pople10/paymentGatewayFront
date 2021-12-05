package com.trackiness.fastpay;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.trackiness.broadcastReceiver.NetworkBroadCastReceiver;
import com.trackiness.services.TransactionService;
import com.trackiness.services.models.TransactionData;
import com.trackiness.utility.Utility;
import com.trackiness.utility.VolleyCallBack;

import java.util.List;

public class AllTransactionsActivity extends AppCompatActivity {
    LinearLayout loader,TEContainer;
    ScrollView scrollContainer;
    int Page;
    Boolean more;
    TransactionService transactionService;
    LinearProgressIndicator dataIndicator;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_transactions);
        getSupportActionBar().hide();
        Utility.authMiddleware(getApplicationContext());
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        BroadcastReceiver br = new NetworkBroadCastReceiver();
        getApplicationContext().registerReceiver(br, filter);
        Page = 1;
        more = false;
        transactionService = new TransactionService(AllTransactionsActivity.this);
        TEContainer = (LinearLayout) findViewById(R.id.ContainerTH);
        dataIndicator =  findViewById(R.id.data_indicator);
        (findViewById(R.id.back_button)).setOnClickListener(e->{finish();});
        loader =((LinearLayout) findViewById(R.id.loader));
        scrollContainer =((ScrollView) findViewById(R.id.scroll_transaction));
        getData(true);
        scrollContainer.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                View view = (View) scrollContainer.getChildAt(scrollContainer.getChildCount() - 1);

                boolean bottom = (view.getBottom() - (scrollContainer.getHeight() + scrollContainer.getScrollY())) ==0;
                if(bottom && more){
                    getData(false);
                }

            }
        });
    }
    public  void  getData(boolean b){
        transactionService.getTransaction(new VolleyCallBack() {
            @Override
            public void onSuccess() {
                List<TransactionData> ts = transactionService.getTransactions().getData();
                for(TransactionData td :ts){
                    CreateTransaction(TEContainer,new HomeFragment.TransactionElementData(td.getIdPayment(),td.getName(),td.getStringDate(),td.generateLabel(),td.getBalance(),td.getTypeT()));
                }
                more = transactionService.getTransactions().getMore();
                int i = transactionService.getPage() + 1;
                transactionService.setPage(i);
                if(b)
                    hideIndicator();
                if(more) dataIndicator.setVisibility(View.VISIBLE);
                else dataIndicator.setVisibility(View.GONE);
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
    public  void CreateTransaction( LinearLayout TEContainer, HomeFragment.TransactionElementData ted){
        LinearLayout TE = (LinearLayout) getLayoutInflater().inflate(R.layout.element_transaction,null);
        ((TextView) TE.findViewById(R.id.FullName)).setText(ted.fullName);
        ((TextView) TE.findViewById(R.id.date)).setText(ted.dateofTransaction);
        ((TextView) TE.findViewById(R.id.balance)).setText(ted.Balance);

        MaterialButton bt = TE.findViewById(R.id.button_transaction);
        TextView amount =((TextView) TE.findViewById(R.id.amount));
        int am = ted.type;
        if(am>0){
            amount.setText("+ "+ted.amount);
        }
        else if(am<0) {
            amount.setText("- "+ted.amount);
            bt.setIconTint(ColorStateList.valueOf(getResources().getColor(R.color.danger)));
            bt.setBackgroundColor(getResources().getColor(R.color.dangerLight));
            bt.setRotation(180);
        }
        else{
            amount.setText(ted.amount);
            bt.setIconTint(ColorStateList.valueOf(getResources().getColor(R.color.TextRegisterG)));
            bt.setBackgroundColor(getResources().getColor(R.color.grayLight));
            bt.setIcon(getResources().getDrawable(R.drawable.ic_baseline_remove_24));
        }
        MaterialCardView mcv = (MaterialCardView) TE.getChildAt(0);
        mcv.setPadding(0,0,0,0);
        OnclickElement(mcv, ted.id);
        TEContainer.addView(TE);
    }

    public void OnclickElement(MaterialCardView c,String id){
        c.setOnClickListener(e->{
            Intent in = new Intent(AllTransactionsActivity.this, TransactionActivity.class);
            in.putExtra("id",id);
            startActivity(in);
        });
    }
    public void hideIndicator(){
        loader.setVisibility(View.GONE);
        TEContainer.setVisibility(View.VISIBLE);
    }
}
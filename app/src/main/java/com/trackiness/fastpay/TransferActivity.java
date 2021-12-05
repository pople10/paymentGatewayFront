package com.trackiness.fastpay;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.trackiness.broadcastReceiver.NetworkBroadCastReceiver;
import com.trackiness.services.PaymentService;
import com.trackiness.services.models.PaymentConfirmation;
import com.trackiness.utility.Utility;
import com.trackiness.utility.VolleyCallBack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TransferActivity extends AppCompatActivity {

    TextView amount;
    Double amountSned = 0.00;
    int iduser = 0;

    LinearLayout formContainer;
    LinearLayout userDataContainer;
    LinearLayout numbersContainer;
    MaterialButton buttonSend;
    MaterialCardView cardContainer;
    EditText unameED;
    LinearLayout buttonContainer;
    List<MaterialButton> buttonNumbers =new ArrayList<MaterialButton>();
    String id = null;
    String username;

    PaymentService paymentService;
    Loader loader;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer);
        getSupportActionBar().hide();
        Utility.authMiddleware(getApplicationContext());
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        BroadcastReceiver br = new NetworkBroadCastReceiver();
        getApplicationContext().registerReceiver(br, filter);
        paymentService = new PaymentService(TransferActivity.this);
        loader = new Loader(TransferActivity.this);
        try {
            id = getIntent().getStringExtra("id");
        }
        catch (Exception e)
        {

        }
        init();
        if(id!=null&&!id.trim().equals(""))
        {
            unameED.setText(id);
            ((MaterialButton) findViewById(R.id.send_money_button)).callOnClick();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public void init(){
        amount = findViewById(R.id.amount_to_send);
        cardContainer = findViewById(R.id.card_container);
        buttonContainer = findViewById(R.id.button_container);
        LinearLayout c = findViewById(R.id.numbers_container);
        numbersContainer = findViewById(R.id.number_amount_container);
        setonClickNumber(c.findViewById(R.id.number_0),0);
        unameED = findViewById(R.id.uname);
        buttonSend = findViewById(R.id.send_money_button);
        initNumbers(7,9,c);
        initNumbers(4,6,c);
        initNumbers(1,3,c);
        formContainer = findViewById(R.id.enter_user_form);
        userDataContainer = findViewById(R.id.user_correct);
        MaterialButton bdelete =(MaterialButton)findViewById(R.id.delete_button);
        buttonNumbers.add(bdelete);
        bdelete.setOnClickListener(e->{
            amountSned*=10;
            amountSned=(amountSned.intValue())/100.00;
            amount.setText(String.format("%,.2f", amountSned));
        });
        cardContainer.setVisibility(View.GONE);
        OnclickSendbutton();
        ((TextView) findViewById(R.id.text_error_uname)).setVisibility(View.GONE);
        returntoFormUName();
       // (findViewById(R.id.back_button)).setOnClickListener(e->{finish();});

    }
    public void initNumbers(int a,int b,LinearLayout c){
        int i = a;
        LinearLayout number_element = (LinearLayout) getLayoutInflater().inflate(R.layout.number_element,null);
        MaterialButton b1 = ((MaterialButton)number_element.findViewById(R.id.number_1));
        MaterialButton b2 = ((MaterialButton)number_element.findViewById(R.id.number_2));
        MaterialButton b3 = ((MaterialButton)number_element.findViewById(R.id.number_3));
        setonClickNumber(b1,i);
        b1.setText(""+(i++));
        setonClickNumber(b2,i);
        b2.setText(""+(i++));
        setonClickNumber(b3,i);
        b3.setText(""+i);
        c.addView(number_element,0);
    }
    public LinearLayout getnumbersContainer(){
        LinearLayout Container = new LinearLayout(this);
        Container.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        Container.setLayoutParams(p);
        return Container;
    }
    public void setonClickNumber(MaterialButton c,int n){
        buttonNumbers.add(c);
        c.setVisibility(View.GONE);
        c.setOnClickListener(e->{

            amountSned = amountSned*10+n*0.01;
            amount.setText(String.format("%,.2f", amountSned));

        });
    }
    public void OnclickSendbutton(){
        ((MaterialButton) findViewById(R.id.send_money_button)).setOnClickListener(e->{
            TextView errorText = findViewById(R.id.text_error_uname);
            errorText.setVisibility(View.GONE);
            String u = unameED.getText().toString();
            if(id!=null&&!id.trim().equals(""))
                unameED.setText("");
            if((buttonSend.getText().toString()).equals("Next")){
                paymentService.preCheck(new VolleyCallBack() {
                    @Override
                    public void onSuccess() {
                        String name = paymentService.getName();
                        errorText.setVisibility(View.GONE);
                        ((TextView)findViewById(R.id.userfullName)).setText(name);
                        username=u;
                        if(Utility.isEmail(u.trim()))
                            ((TextView)findViewById(R.id.userName)).setText(u);
                        else
                        {
                            ((TextView) findViewById(R.id.userName)).setText("@" + u);
                            if(id!=null&&!id.trim().equals(""))
                                ((TextView) findViewById(R.id.userName)).setText("");
                        }
                        errorText.setVisibility(View.GONE);
                        showButtonwithAnimation();
                    }

                    @Override
                    public void onError(int error) {
                        if(id!=null&&!id.trim().equals(""))
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
                },u);

            }
            else{
                if(amountSned>0){
                    Map<String,String> datos = new HashMap<>();
                    datos.put("email",username);
                    datos.put("currency","USD");
                    datos.put("type","1");
                    datos.put("amount",""+amountSned);
                    datos.put("status","1");
                    paymentService.sendPayment(new VolleyCallBack() {
                        @Override
                        public void onSuccess() {
                            PaymentConfirmation paymentConfirmation = paymentService.getPaymentConfirmation();
                            System.out.println(paymentConfirmation.getIdTransfer());
                            (findViewById(R.id.befor_send)).setVisibility(View.GONE);
                            LinearLayout DoneContainer = (findViewById(R.id.transaction_done));
                            DoneContainer.setVisibility(View.VISIBLE);
                            MaterialCardView vector_done  = (findViewById(R.id.done_animation));
                            DoneContainer.setAlpha(0.0f);
                            DoneContainer.animate().alpha(1.0f);
                            Animation an = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.done_transfer_animation);
                            an.setStartTime(200);
                            vector_done.startAnimation(an);
                            ((TextView)findViewById(R.id.transactionId)).setText("#"+paymentConfirmation.getIdTransfer());
                            ((TextView)findViewById(R.id.transferDate)).setText(paymentConfirmation.generateDate());
                            ((TextView)findViewById(R.id.transferAmount)).setText(paymentConfirmation.generateLabel());
                            ((TextView)findViewById(R.id.receiverName)).setText(paymentConfirmation.getReceiver());
                            ((TextView)findViewById(R.id.senderName)).setText(paymentConfirmation.getSender());
                            (findViewById(R.id.back_to_home)).setOnClickListener(e->{finish();});
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
                    },datos);
                }
                else
                {
                    new Alert(TransferActivity.this,Alert.WARNING)
                            .setContentText("You need to enter an amount first")
                            .show();
                }
            }
        });
    }
    public  void showButtonwithAnimation(){
        buttonContainer.setGravity(Gravity.BOTTOM);
        formContainer.setVisibility(View.GONE);
        userDataContainer.setVisibility(View.VISIBLE);
        cardContainer.setVisibility(View.VISIBLE);
        cardContainer.setAlpha(0.0f);
        cardContainer.animate().alpha(1.0f);
        numbersContainer.setAlpha(0.0f);
        numbersContainer.setVisibility(View.VISIBLE);
        numbersContainer.animate().alpha(1.0f);
        buttonSend.setText("Send Money");

        int n = buttonNumbers.size();
        for(int i =0; i<n;i++){
            Animation a = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.button_number_animation);
            MaterialButton e = buttonNumbers.get(i);
            e.setVisibility(View.VISIBLE);
            e.startAnimation(a);
        }
    }
    public  void returntoFormUName(){
        cardContainer.setOnClickListener(e->{
            if(!(buttonSend.getText().toString()).equals("Next")){
                buttonContainer.setGravity(Gravity.CENTER);
                cardContainer.setVisibility(View.GONE);
                formContainer.setAlpha(0.0f);
                formContainer.setVisibility(View.VISIBLE);
                formContainer.animate().alpha(1.0f);
                numbersContainer.setVisibility(View.GONE);
                buttonSend.setText("Next");
                amountSned = 0.00;
                iduser = 0;
                amount.setText(String.format("%,.2f", 0.00));
            }
        });
    }
}
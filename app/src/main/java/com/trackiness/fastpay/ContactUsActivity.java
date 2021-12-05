package com.trackiness.fastpay;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.google.android.material.button.MaterialButton;
import com.trackiness.broadcastReceiver.NetworkBroadCastReceiver;
import com.trackiness.services.ContactusService;
import com.trackiness.utility.Utility;
import com.trackiness.utility.VolleyCallBack;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ContactUsActivity extends AppCompatActivity {
    boolean islogin;
    EditText fname,lname,email,subject,message;
    ContactusService contactusService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        BroadcastReceiver br = new NetworkBroadCastReceiver();
        getApplicationContext().registerReceiver(br, filter);
        setContentView(R.layout.activity_contact_us);
        String token = Utility.extractToken(ContactUsActivity.this);
        islogin = token!=null;
        contactusService = new ContactusService(ContactUsActivity.this);
        fname = findViewById(R.id.fname);
        lname = findViewById(R.id.lname);
        email = findViewById(R.id.email);
        subject = findViewById(R.id.messagC);
        message = findViewById(R.id.message);
        hideIndicator();

        if(islogin){
            ((LinearLayout) findViewById(R.id.no_register)).setVisibility(View.GONE);
        }
        (findViewById(R.id.back_button)).setOnClickListener(e->{finish();});
        ((MaterialButton) findViewById(R.id.send)).setOnClickListener(e->{
            if(validate()){
                contactusService.sendMessage(islogin, message.getText().toString(), subject.getText().toString(), fname.getText().toString(), lname.getText().toString(), email.getText().toString()
                        , new VolleyCallBack() {
                            @Override
                            public void onSuccess() {
                                (new Handler(Looper.getMainLooper())).postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        finish();
                                    }
                                },1000);
                            }

                            @Override
                            public void onError(int error) {
                                hideIndicator();
                            }

                            @Override
                            public void beforeSend() {
                                showIndicator();
                            }

                            @Override
                            public void onFinish() {

                            }
                        });
            }
        });

    }
    public boolean validate(){
        boolean f = true;
        if(!islogin){
            if(fname.getText().toString().length()<2) {fname.setError("Invalid first name");f=false;}
            if(lname.getText().toString().length()<2) {lname.setError("Invalid last name");f=false;}
            if(!Emailvalidation(email.getText().toString())){lname.setError("Invalid email");f=false;}
        }
        if(subject.getText().toString().length()<2) {subject.setError("Invalid subject");f=false;}
        if(message.getText().toString().length()<2) {message.setError("Invalid message");f=false;}

        return f;
    }

    public boolean Emailvalidation(String email){
        Pattern pattern = Pattern.compile("^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$");
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
    public void  showIndicator(){
        ((ScrollView) findViewById(R.id.scroll_transaction)).setVisibility(View.GONE);
        ((LinearLayout) findViewById(R.id.Loader)).setVisibility(View.VISIBLE);
    }
    public void hideIndicator(){
        ((ScrollView) findViewById(R.id.scroll_transaction)).setVisibility(View.VISIBLE);
        ((LinearLayout) findViewById(R.id.Loader)).setVisibility(View.GONE);
    }
}
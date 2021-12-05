package com.trackiness.fastpay;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.trackiness.broadcastReceiver.NetworkBroadCastReceiver;
import com.trackiness.services.UserService;
import com.trackiness.utility.CallBack;
import com.trackiness.utility.Utility;
import com.trackiness.utility.VolleyCallBack;

public class LoginActivity extends AppCompatActivity {
    private UserService userService;
    private EditText userName,Password,code;
    LinearLayout loader,firstPage,secondPage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();
        Utility.loginMiddleware(getApplicationContext());
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        BroadcastReceiver br = new NetworkBroadCastReceiver();
        getApplicationContext().registerReceiver(br, filter);
        loader = ( findViewById(R.id.Loader));
        firstPage = ( findViewById(R.id.first_page));
        secondPage = ( findViewById(R.id.second_page));
        hideIndicator1();

        userName = findViewById(R.id.uname);
        code = findViewById(R.id.code);
        Password = findViewById(R.id.password);
        userService = new UserService(LoginActivity.this);
        userService.setTAG("Login");
        ((TextView) findViewById(R.id.contact_us)).setOnClickListener(e->{
            startActivity(new Intent(LoginActivity.this,ContactUsActivity.class));
        });
        ((TextView) findViewById(R.id.reset_password)).setOnClickListener(e->{
            startActivity(new Intent(LoginActivity.this,ResetPasswordNonAuthActivity.class));
        });
        ((Button) findViewById(R.id.login)).setOnClickListener(e->{
            hidetKeyboard();

            if(validate1())
                userService.SendMessage(new VolleyCallBack() {
                @Override
                public void onSuccess() {
                    hideIndicator2();
                }

                @Override
                public void onError(int error) {
                    hideIndicator1();
                }
                @Override
                public void beforeSend() {showIndicator(); }

                @Override
                public void onFinish() {

                }
            },userName.getText().toString(),Password.getText().toString());
        });
        ((Button) findViewById(R.id.register)).setOnClickListener(e->{startActivity(new Intent(LoginActivity.this, RegisterActivity.class));});

        ((Button) findViewById(R.id.login2)).setOnClickListener(e->{
            hidetKeyboard();

            if(code.getText().toString().length()!=6) code.setError("Invalid code");
            else
            userService.Login(userName.getText().toString(), Password.getText().toString(), code.getText().toString(), new VolleyCallBack() {
                @Override
                public void onSuccess() {

                   String token = userService.getToken();
                   Utility.savetToken(LoginActivity.this,token);
                    firebase();
                    Intent i = new Intent(LoginActivity.this, BottomNavigation.class);
                    finish();
                    startActivity(i);
                }
                @Override
                public void onError(int error) {

                }

                @Override
                public void beforeSend() {
                    showIndicator();
                }

                @Override
                public void onFinish() {
                }
            });
        });
        ((MaterialButton) findViewById(R.id.cancel)).setOnClickListener(e->{hideIndicator1();});
    }

    public void firebase(){
        (new AsyncTask<Void,Void,Void>(){

            @Override
            protected Void doInBackground(Void... voids) {
                Utility.getFireBaseToken(LoginActivity.this, new CallBack() {
                    @Override
                    public void onSuccess(String s) {
                        System.out.println(s);
                        userService.updateFireBaseToken(new VolleyCallBack() {
                            @Override
                            public void onSuccess() {
                                System.out.println("success Token");
                            }

                            @Override
                            public void onError(int error) {
                                System.out.println("error Token");
                            }

                            @Override
                            public void beforeSend() {

                            }

                            @Override
                            public void onFinish() {

                            }
                        },s);
                    }

                    @Override
                    public void onError() {
                        System.out.println("error");
                    }

                    @Override
                    public void beforeProcessing() {

                    }

                    @Override
                    public void onFinish() {

                    }
                });
                return null;
            }
        }).execute();
    }

    public boolean validate1(){
        boolean f = true;
        if(userName.getText().toString().length()<2) {userName.setError("Invalid user name");f=false;}
        if(Password.getText().toString().length()<2) {Password.setError("Invalid user name");f=false;}
        return f;
    }
    public void  showIndicator(){
        firstPage.setVisibility(View.GONE);
        secondPage.setVisibility(View.GONE);
        loader.setVisibility(View.VISIBLE);
    }
    public void hideIndicator1(){
        firstPage.setVisibility(View.VISIBLE);
        secondPage.setVisibility(View.GONE);
        loader.setVisibility(View.GONE);
    }
    public void hideIndicator2(){
        secondPage.setVisibility(View.VISIBLE);
        loader.setVisibility(View.GONE);
    }
    public void hidetKeyboard(){
        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(((ScrollView) findViewById(R.id.scrollview_login)).getWindowToken(), 0);
    }
}
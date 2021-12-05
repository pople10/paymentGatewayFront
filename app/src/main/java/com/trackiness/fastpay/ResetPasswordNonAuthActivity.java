package com.trackiness.fastpay;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.trackiness.broadcastReceiver.NetworkBroadCastReceiver;
import com.trackiness.services.UserService;
import com.trackiness.utility.Utility;
import com.trackiness.utility.VolleyCallBack;

import java.util.HashMap;
import java.util.Map;

public class ResetPasswordNonAuthActivity extends AppCompatActivity {
    Loader loader;
    UserService userService;
    MaterialButton btn;
    String usernameGlob = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset);
        getSupportActionBar().hide();
        Utility.loginMiddleware(getApplicationContext());
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        BroadcastReceiver br = new NetworkBroadCastReceiver();
        getApplicationContext().registerReceiver(br, filter);
        userService = new UserService(ResetPasswordNonAuthActivity.this);
        loader = new Loader(ResetPasswordNonAuthActivity.this);
        init();
    }


    public void init(){
        btn = (findViewById(R.id.changePasswordReset));
        (findViewById(R.id.back_button)).setOnClickListener(e->{
            if(btn.getText().toString().equals("Reset")) {
                finish();
                return;
            }
            (findViewById(R.id.usernameResetC)).setVisibility(View.VISIBLE);
            (findViewById(R.id.codeResetC)).setVisibility(View.GONE);
            (findViewById(R.id.new_passwordResetC)).setVisibility(View.GONE);
            (findViewById(R.id.confirm_passwordResetC)).setVisibility(View.GONE);
            btn.setText("Reset");
            usernameGlob="";
        });
        btn.setOnClickListener(e->{
            if(btn.getText().toString().equals("Reset")) {
                String username = ((TextInputEditText) findViewById(R.id.usernameReset)).getText().toString();
                Map<String, String> params = new HashMap<>();
                params.put("username", username);
                userService.preResetPassword(new VolleyCallBack() {
                    @Override
                    public void onSuccess() {
                        usernameGlob=username;
                        ((TextInputEditText) findViewById(R.id.usernameReset)).setText("");
                        (findViewById(R.id.usernameResetC)).setVisibility(View.GONE);
                        (findViewById(R.id.codeResetC)).setVisibility(View.VISIBLE);
                        (findViewById(R.id.new_passwordResetC)).setVisibility(View.VISIBLE);
                        (findViewById(R.id.confirm_passwordResetC)).setVisibility(View.VISIBLE);
                        btn.setText("Change Password");
                    }

                    @Override
                    public void onError(int error) {
                        usernameGlob="";
                    }

                    @Override
                    public void beforeSend() {
                        loader.show();
                    }

                    @Override
                    public void onFinish() {
                        if (loader.isShowing())
                            loader.dismiss();
                    }
                }, params);
            }
            else
            {
                Map<String, String> params = new HashMap<>();
                String code = ((TextInputEditText) findViewById(R.id.codeReset)).getText().toString();
                String password = ((TextInputEditText) findViewById(R.id.new_passwordReset)).getText().toString();
                String passwordConfirmation = ((TextInputEditText) findViewById(R.id.confirm_passwordReset)).getText().toString();
                params.put("username", usernameGlob);
                params.put("code", code);
                params.put("password", password);
                params.put("password_confirmation", passwordConfirmation);
                userService.resetPassword(new VolleyCallBack() {
                    @Override
                    public void onSuccess() {
                        (findViewById(R.id.back_button)).callOnClick();
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
                        if (loader.isShowing())
                            loader.dismiss();
                    }
                }, params);
            }
        });
    }
}

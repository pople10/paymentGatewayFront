package com.trackiness.fastpay;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;

import com.google.android.material.textfield.TextInputEditText;
import com.trackiness.broadcastReceiver.NetworkBroadCastReceiver;
import com.trackiness.services.UserService;
import com.trackiness.utility.Utility;
import com.trackiness.utility.VolleyCallBack;

import java.util.HashMap;
import java.util.Map;

public class ResetPasswordActivity extends AppCompatActivity {
    Loader loader;
    UserService userService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_reset_password);
        Utility.authMiddleware(getApplicationContext());
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        BroadcastReceiver br = new NetworkBroadCastReceiver();
        getApplicationContext().registerReceiver(br, filter);
        userService = new UserService(ResetPasswordActivity.this);
        loader = new Loader(ResetPasswordActivity.this);
        init();
    }

    public void init()
    {
        (findViewById(R.id.back_button)).setOnClickListener(e->{finish();});
        (findViewById(R.id.changePassword)).setOnClickListener(e->{
            String oldPassword = ((TextInputEditText)findViewById(R.id.old_password)).getText().toString();
            String newPassword = ((TextInputEditText)findViewById(R.id.new_password)).getText().toString();
            String newPasswordConfirmation = ((TextInputEditText)findViewById(R.id.confirm_password)).getText().toString();
            Map<String,String> params = new HashMap<>();
            params.put("old_password",oldPassword);
            params.put("password",newPassword);
            params.put("password_confirmation",newPasswordConfirmation);
            userService.updatePassword(new VolleyCallBack() {
                @Override
                public void onSuccess() {
                    ((TextInputEditText)findViewById(R.id.old_password)).setText("");
                    ((TextInputEditText)findViewById(R.id.old_password)).clearFocus();
                    ((TextInputEditText)findViewById(R.id.new_password)).setText("");
                    ((TextInputEditText)findViewById(R.id.new_password)).clearFocus();
                    ((TextInputEditText)findViewById(R.id.confirm_password)).setText("");
                    ((TextInputEditText)findViewById(R.id.confirm_password)).clearFocus();
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
            },params);
        });
    }

}
package com.trackiness.fastpay;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;

import com.google.android.material.textfield.TextInputEditText;
import com.trackiness.broadcastReceiver.NetworkBroadCastReceiver;
import com.trackiness.services.ProfileService;
import com.trackiness.services.UserService;
import com.trackiness.services.models.Profile;
import com.trackiness.utility.Utility;
import com.trackiness.utility.VolleyCallBack;

import java.util.HashMap;
import java.util.Map;

public class UpdateDetailsMainActivity extends AppCompatActivity {
    Loader loader;
    UserService userService;
    ProfileService profileService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_details_main);
        getSupportActionBar().hide();
        Utility.authMiddleware(getApplicationContext());
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        BroadcastReceiver br = new NetworkBroadCastReceiver();
        getApplicationContext().registerReceiver(br, filter);
        userService = new UserService(UpdateDetailsMainActivity.this);
        loader = new Loader(UpdateDetailsMainActivity.this);
        profileService = new ProfileService(UpdateDetailsMainActivity.this);
        setData();
        init();
    }

    private void setData() {
        profileService.getProfileData(new VolleyCallBack() {
            @Override
            public void onSuccess() {
                Profile profile = profileService.getProfile();
                ((TextInputEditText)findViewById(R.id.phone)).setText(profile.getPhone());
                ((TextInputEditText)findViewById(R.id.lname)).setText(profile.getLastname());
                ((TextInputEditText)findViewById(R.id.fname)).setText(profile.getFirstname());
                ((TextInputEditText)findViewById(R.id.emailUpdate)).setText(profile.getEmail());
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
        });
    }


    public void init(){

        (findViewById(R.id.back_button)).setOnClickListener(e->{finish();});
        (findViewById(R.id.update_detailsButton)).setOnClickListener(e->{
            String phone = ((TextInputEditText)findViewById(R.id.phone)).getText().toString();
            String lname = ((TextInputEditText)findViewById(R.id.lname)).getText().toString();
            String fname = ((TextInputEditText)findViewById(R.id.fname)).getText().toString();
            String email = ((TextInputEditText)findViewById(R.id.emailUpdate)).getText().toString();
            Map<String,String> params = new HashMap<>();
            params.put("email",email);
            params.put("firstname",fname);
            params.put("lastname",lname);
            params.put("phone", Utility.getPhoneWithCode(phone));
            userService.updateDetails(new VolleyCallBack() {
                @Override
                public void onSuccess() {
                    ((TextInputEditText)findViewById(R.id.phone)).clearFocus();
                    ((TextInputEditText)findViewById(R.id.lname)).clearFocus();
                    ((TextInputEditText)findViewById(R.id.fname)).clearFocus();
                    ((TextInputEditText)findViewById(R.id.emailUpdate)).clearFocus();
                }

                @Override
                public void onError(int error) {
                    setData();
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
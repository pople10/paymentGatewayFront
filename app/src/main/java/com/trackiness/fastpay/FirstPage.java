package com.trackiness.fastpay;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.google.android.material.card.MaterialCardView;
import com.trackiness.broadcastReceiver.NetworkBroadCastReceiver;
import com.trackiness.utility.Utility;

public class FirstPage extends AppCompatActivity {
    Intent i ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        BroadcastReceiver br = new NetworkBroadCastReceiver();
        getApplicationContext().registerReceiver(br, filter);
        setContentView(R.layout.activity_first_page);
        MaterialCardView l1 = findViewById(R.id.logo_l1);
        MaterialCardView l2 = findViewById(R.id.logo_l2);
        String token = Utility.extractToken(FirstPage.this);
        boolean islogin = token!=null;

        i = new Intent(FirstPage.this,LoginActivity.class);
        if(islogin){
            i = new Intent(FirstPage.this,BottomNavigation.class);
        }
        Animation a1 = AnimationUtils.loadAnimation(this,R.anim.logo_anim);
        Animation a2 = AnimationUtils.loadAnimation(this,R.anim.logo_anim_r);
        l1.startAnimation(a1);
        l2.startAnimation(a2);

        start();


}

public void start(){
    (new Handler(Looper.getMainLooper())).postDelayed(new Runnable() {
        @Override
        public void run() {
            i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(i);
        }
    },2700);
    (new Handler(Looper.getMainLooper())).postDelayed(new Runnable() {
        @Override
        public void run() {finish();}},3000);
}
}
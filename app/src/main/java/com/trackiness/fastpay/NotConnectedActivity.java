package com.trackiness.fastpay;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class NotConnectedActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notconnected);
        getSupportActionBar().hide();
        (findViewById(R.id.refresh)).setOnClickListener(e->{
            startActivity(new Intent(NotConnectedActivity.this,FirstPage.class));
        });
    }
}

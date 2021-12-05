package com.trackiness.fastpay;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.trackiness.fastpay.databinding.ActivityBottomNavigationBinding;
import com.trackiness.utility.Utility;

public class BottomNavigation extends AppCompatActivity {

    private ActivityBottomNavigationBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityBottomNavigationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();
        Utility.authMiddleware(getApplicationContext());
        BottomNavigationView navView = findViewById(R.id.nav_view);
        ((Button) findViewById(R.id.qrbutton)).setOnClickListener(e->{startActivity(new Intent(BottomNavigation.this, QRCODEActivity.class));});

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(R.id.navigation_home,R.id.navigation_statistic,R.id.navigation_qrcode,R.id.navigation_cart,R.id.navigation_profile).build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_bottom_navigation);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
    }

}
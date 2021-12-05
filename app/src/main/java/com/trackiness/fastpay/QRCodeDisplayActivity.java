package com.trackiness.fastpay;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.trackiness.broadcastReceiver.NetworkBroadCastReceiver;
import com.trackiness.services.ProfileService;
import com.trackiness.services.models.Profile;
import com.trackiness.utility.Utility;
import com.trackiness.utility.VolleyCallBack;

public class QRCodeDisplayActivity extends AppCompatActivity {
    ProfileService profileService;
    Loader loader;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_qr);
        Utility.authMiddleware(getApplicationContext());
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        BroadcastReceiver br = new NetworkBroadCastReceiver();
        getApplicationContext().registerReceiver(br, filter);
        getSupportActionBar().hide();
        profileService = new ProfileService(QRCodeDisplayActivity.this);
        loader = new Loader(QRCodeDisplayActivity.this);
        init();
    }

    public void init()
    {
        (findViewById(R.id.back_button)).setOnClickListener(e->{
            finish();
        });
        profileService.getProfileData(new VolleyCallBack() {
            @Override
            public void onSuccess() {
                Profile profile = profileService.getProfile();
                Bitmap out = Utility.generateQRCode(""+profile.getIdUser());
                ((ImageView)findViewById(R.id.displayQR)).setImageBitmap(out);
            }

            @Override
            public void onError(int error) {
                (findViewById(R.id.back_button)).callOnClick();
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
}

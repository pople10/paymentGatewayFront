package com.trackiness.broadcastReceiver;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.trackiness.fastpay.NotConnectedActivity;
import com.trackiness.utility.Utility;

public class NetworkBroadCastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {

        System.out.println("connectivity ");
            if (!Utility.isOnline(context)) {
                context.startActivity(new Intent(context, NotConnectedActivity.class).setFlags(FLAG_ACTIVITY_NEW_TASK));
            } else {

            }
    }
}

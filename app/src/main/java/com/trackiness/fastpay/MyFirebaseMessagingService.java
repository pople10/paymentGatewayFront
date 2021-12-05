package com.trackiness.fastpay;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Objects;

/**
 * Created by Belal on 12/8/2017.
 */

//class extending FirebaseMessagingService
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String NOTIFICATION_CHANNEL_ID = "payfastaot";
    private static final String NOTIFICATION_CHANNEL_NAME = "payfastaot";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        //if the message contains data payload
        //It is a map of custom keyvalues
        //we can read it easily
        if(remoteMessage.getData().size() > 0){
            //handle the data message here

        }
        String title = remoteMessage.getNotification().getTitle();
        String body = remoteMessage.getNotification().getBody();
        System.out.println(body);
        NotificationManager notificationManager = (NotificationManager) getSystemService ( Context.NOTIFICATION_SERVICE );
        if (!Objects.equals ( null, remoteMessage.getNotification () )) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel notificationChannel = new NotificationChannel ( NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH );
                notificationManager.createNotificationChannel ( notificationChannel );
            }
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder ( this, NOTIFICATION_CHANNEL_ID );
            notificationBuilder.setAutoCancel ( true )
                    .setStyle ( new NotificationCompat.BigTextStyle ().bigText ( remoteMessage.getNotification ().getBody () ) )
                    .setDefaults ( Notification.DEFAULT_ALL )
                    .setWhen ( System.currentTimeMillis () )
                    .setSmallIcon ( R.mipmap.ic_launcher )
                    .setTicker ( remoteMessage.getNotification ().getTitle () )
                    .setPriority ( Notification.PRIORITY_MAX )
                    .setContentTitle ( remoteMessage.getNotification ().getTitle () )
                    .setContentText ( remoteMessage.getNotification ().getBody () );
            notificationManager.notify ( 1, notificationBuilder.build () );
        }
    }



}

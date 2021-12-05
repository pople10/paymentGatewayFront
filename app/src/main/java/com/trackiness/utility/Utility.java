package com.trackiness.utility;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.trackiness.fastpay.HomeFragment;
import com.trackiness.fastpay.LoginActivity;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.NetworkInterface;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.regex.Pattern;

import static com.google.firebase.BuildConfig.APPLICATION_ID;

public class Utility {
    public static Bitmap generateQRCode(String s)
    {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        int size = 256;
        Hashtable<EncodeHintType, ErrorCorrectionLevel> hintMap = new Hashtable<EncodeHintType, ErrorCorrectionLevel>();
        hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        BitMatrix bitMatrix = null;
        try {
            bitMatrix = qrCodeWriter.encode(s, BarcodeFormat.QR_CODE, size, size, hintMap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        int width = bitMatrix.getWidth();
        Bitmap bmp = Bitmap.createBitmap(width, width, Bitmap.Config.RGB_565);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < width; y++) {
                bmp.setPixel(y, x, bitMatrix.get(x,y) ? Color.BLACK : Color.WHITE);
            }
        }
        return bmp;
    }

    public static boolean isInteger(String e)
    {
        try
        {
            Integer.parseInt(e);
            return true;
        }
        catch(Exception e1)
        {
            return false;
        }
    }

    public static boolean isDecimal(String e)
    {
        try
        {
            Double.parseDouble(e);
            return true;
        }
        catch(Exception e1)
        {
            return false;
        }
    }

    public static void savetToken(Context context,String token)
    {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("token", token);
        editor.apply();
    }
    public static void removeToken(Context context)
    {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove("token");
        editor.apply();
    }

    public static String extractToken(Context context)
    {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        final String token = sharedPref.getString("token",null);
        return token;
    }
    public static Bitmap getImageBitmapFromURL(String url) {
        Bitmap bimage=null;
        try {
            InputStream in=new java.net.URL(url).openStream();
            bimage= BitmapFactory.decodeStream(in);
        } catch (Exception e) {

        }
        return bimage;
    }
    public static void getFireBaseToken(Context context,CallBack callback)
    {
        if (!isMainProcess(context)) {
            FirebaseApp.initializeApp(context);
            FirebaseMessaging.getInstance().getToken()
                    /* lambda function */
                    .addOnCompleteListener(task -> {
                        if (!task.isSuccessful()) {
                            callback.onError();
                            return;
                        }
                        String token = task.getResult();
                        callback.onSuccess(token);
                    });
        }
    }

    private static boolean isMainProcess(Context context) {
        if (null == context) {
            return true;
        }
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        int pid = android.os.Process.myPid();
        for (ActivityManager.RunningAppProcessInfo processInfo : manager.getRunningAppProcesses()) {
            if (APPLICATION_ID.equals(processInfo.processName) && pid == processInfo.pid) {
                return true;
            }
        }
        return false;
    }

    public static boolean isEmpty(String s)
    {
        return s.trim().equals("")||s.equals(null);
    }

    public static boolean isEmail(String s)
    {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (s == null)
            return false;
        return pat.matcher(s).matches();
    }

    public static String getPhoneWithCode(String s)
    {
        if(s==null||s.length()==0)
            return "";
        String res="";
        if(s.charAt(0)=='0')
        {
            res+="212";
            for(int i=1;i<s.length();i++)
                res+=""+s.charAt(i);
        }
        else
            res=s;
        return res;
    }

    public static String generateDate(Date s)
    {
        SimpleDateFormat simpleformat = new SimpleDateFormat("dd MMM yyyy hh:mm:s");
        return simpleformat.format(s);
    }

    public static String getMacAddress() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(Integer.toHexString(b & 0xFF) + ":");
                }
                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ex) {
        }
        return "";
    }

    public static byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    public static boolean isOnline(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return (netInfo != null && netInfo.isConnected());
    }

    public static void loginMiddleware(Context context)
    {
        String token = Utility.extractToken(context);
        if(token!=null)
        {
            context.startActivity(new Intent(context, HomeFragment.class));
        }
    }

    public static void authMiddleware(Context context)
    {
        String token = Utility.extractToken(context);
        if(token==null)
        {
            context.startActivity(new Intent(context, LoginActivity.class));
        }
    }

}

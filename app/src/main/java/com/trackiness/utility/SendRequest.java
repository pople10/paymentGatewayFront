package com.trackiness.utility;

import android.content.Context;
import android.os.AsyncTask;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.trackiness.fastpay.Alert;
import com.trackiness.fastpay.Loader;

import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.concurrent.Callable;

public class SendRequest extends AsyncTask<String, String, Void> {
    Context context;
    Loader loader;
    RequestQueue queue;
    Callable<Void> after;
    Callable<Void> before;
    String TAG;
    int method;

    public SendRequest(Context context, Loader loader, RequestQueue queue,int method,String TAG, Callable<Void> before, Callable<Void> after) {
        this.context = context;
        this.loader = loader;
        this.queue = queue;
        this.after = after;
        this.before = before;
        this.TAG = TAG;
        this.method=method;
    }

    @Override
    protected Void doInBackground(String... strings) {
        StringRequest stringRequest = new StringRequest(this.method, strings[0],
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Map data = new Gson().fromJson(response.toString(),Map.class);
                        new Alert(context,Alert.SUCCESS)
                                .setContentText(data.get("done").toString())
                                .show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String body;
                String statusCode = String.valueOf(error.networkResponse.statusCode);
                if(error.networkResponse.data!=null&&(statusCode.equals("422")
                        ||statusCode.equals("401")||statusCode.equals("401")
                        ||statusCode.equals("433"))) {
                    if(statusCode.equals("401"))
                    {
                        //code here
                    }
                    try {
                        body = new String(error.networkResponse.data,"UTF-8");
                        Map data = new Gson().fromJson(body,Map.class);
                        new Alert(context,Alert.ERROR)
                                .setContentText(data.get("error").toString())
                                .show();
                    } catch (UnsupportedEncodingException e) {

                    }
                }
                else {
                    new Alert(context,Alert.ERROR)
                            .setContentText("Error from the server, contact us.")
                            .show();
                }
            }
        });
        stringRequest.setTag(TAG);
        queue.add(stringRequest);
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        loader.show();
        try {
            before.call();
        } catch (Exception e) {

        }
    }

    @Override
    protected void onPostExecute(Void s) {
        super.onPostExecute(s);
        if(loader.isShowing())
            loader.dismiss();
        try {
            after.call();
        } catch (Exception e) {

        }
    }
}

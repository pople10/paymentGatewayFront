package com.trackiness.services;

import android.content.Context;

import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.trackiness.fastpay.Alert;
import com.trackiness.fastpay.R;
import com.trackiness.utility.Utility;
import com.trackiness.utility.VolleyCallBack;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class ContactusService {
    private static final String TAG = "transaction";
    private Context context;
    RequestQueue queue;
    Map data;

    private int page;
    public Context getContext() {
        return context;
    }


    public static String getTAG() {
        return TAG;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public RequestQueue getQueue() {
        return queue;
    }



    public void setQueue(RequestQueue queue) {
        this.queue = queue;
    }

    public Map getData() {
        return data;
    }

    public void setData(Map data) {
        this.data = data;
    }

    public ContactusService(Context context)
    {
        page = 1;
        this.context=context;
        queue = Volley.newRequestQueue(context);
    }


    public void sendMessage(boolean isuser,String message,String subject,String fname,String lname,String email,VolleyCallBack callBack)
    {
        String url =  context.getString(R.string.server)+(isuser?("/email/message/private"):("/email/message/public"));
        callBack.beforeSend();
        StringRequest stringRequest = new StringRequest(Request.Method.POST,url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Map data = new Gson().fromJson(response.toString(),Map.class);
                        new Alert(context,Alert.SUCCESS)
                                .setContentText(data.get("done").toString())
                                .show();
                        callBack.onSuccess();
                        callBack.onFinish();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String body;
                int statusCode = error.networkResponse.statusCode;
                if(error.networkResponse.data!=null&&(statusCode==422
                        ||statusCode == 401||statusCode == 404
                        ||statusCode==433)) {
                    if(statusCode==401)
                    {
                        //code here
                    }
                    try {
                        body = new String(error.networkResponse.data,"UTF-8");
                        data = new Gson().fromJson(body,Map.class);
                        new Alert(context,Alert.ERROR)
                                .setContentText(data.get("error").toString())
                                .show();
                    } catch (UnsupportedEncodingException e) {

                    }
                }
                else {
                    new Alert(context,Alert.ERROR)
                            .setContentText(context.getString(R.string.internal_error))
                            .show();
                }
                callBack.onError(statusCode);
                callBack.onFinish();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                if(isuser)
                    headers.put("Authorization", "Bearer "+ Utility.extractToken(context));
                return headers;
            }

                @Nullable
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> jsonBody = new HashMap<String,String>();
                jsonBody.put("subject", subject);
                jsonBody.put("message", message);

                if(!isuser){
                    jsonBody.put("firstname", fname);
                    jsonBody.put("lastname", lname);
                    jsonBody.put("email", email);
                }
                return jsonBody;
            }
        };
        stringRequest.setTag(TAG+"one");
        stringRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 500000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 500000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });
        queue.add(stringRequest);

    }

}

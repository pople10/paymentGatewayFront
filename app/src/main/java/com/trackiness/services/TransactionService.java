package com.trackiness.services;

import android.content.Context;
import android.util.Log;

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
import com.google.gson.reflect.TypeToken;
import com.trackiness.fastpay.Alert;
import com.trackiness.fastpay.R;
import com.trackiness.services.models.TransactionData;
import com.trackiness.services.models.TransactionsData;
import com.trackiness.utility.Utility;
import com.trackiness.utility.VolleyCallBack;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TransactionService {
    private static final String TAG = "transaction";
    private Context context;
    RequestQueue queue;
    Map data;
    TransactionsData transactions;
    TransactionData onetransactions;

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

    public TransactionData getOnetransactions() {
        return onetransactions;
    }

    public void setOnetransactions(TransactionData onetransactions) {
        this.onetransactions = onetransactions;
    }

    public void setQueue(RequestQueue queue) {
        this.queue = queue;
    }

    public Map getData() {
        return data;
    }

    public TransactionsData getTransactions() {
        return transactions;
    }

    public void setTransactions(TransactionsData transactions) {
        this.transactions = transactions;
    }

    public void setData(Map data) {
        this.data = data;
    }

    public TransactionService(Context context)
    {
        page = 1;
        this.context=context;
        queue = Volley.newRequestQueue(context);
    }

    public void getTransaction(VolleyCallBack callBack)
    {
        String url =  context.getString(R.string.server)+"/homeTransactions?page="+page;
        callBack.beforeSend();
            StringRequest stringRequest = new StringRequest(Request.Method.GET,url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Type listType = new TypeToken<TransactionsData>(){}.getType();
                            transactions = new Gson().fromJson(response, listType);
                            Log.e("mmmm",transactions.toString());
                            callBack.onSuccess();
                            callBack.onFinish();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    setTransactions(null);
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
                    headers.put("Authorization", "Bearer "+ Utility.extractToken(context));
                    return headers;
                }

            };
            stringRequest.setTag(TAG);
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
    public void getOneTransaction(String idPayment,VolleyCallBack callBack)
    {
        String url =  context.getString(R.string.server)+"/Transaction";
        callBack.beforeSend();
        StringRequest stringRequest = new StringRequest(Request.Method.POST,url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Type listType = new TypeToken<TransactionData>(){}.getType();
                        onetransactions = new Gson().fromJson(response, listType);
                        callBack.onSuccess();
                        callBack.onFinish();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                setOnetransactions(null);
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
                headers.put("Authorization", "Bearer "+ Utility.extractToken(context));
                return headers;
            }

                @Nullable
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> jsonBody = new HashMap<String,String>();
                jsonBody.put("idPayment", idPayment);
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

    public void refundTransaction(String idPayment,VolleyCallBack callBack)
    {
        String url =  context.getString(R.string.server)+"/payment/refund/full";
        callBack.beforeSend();
        StringRequest stringRequest = new StringRequest(Request.Method.POST,url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Type listType = new TypeToken<TransactionData>(){}.getType();
                        onetransactions = new Gson().fromJson(response, listType);
                        callBack.onSuccess();
                        callBack.onFinish();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                setOnetransactions(null);
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
                headers.put("Authorization", "Bearer "+ Utility.extractToken(context));
                return headers;
            }

            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> jsonBody = new HashMap<String,String>();
                jsonBody.put("id", idPayment);
                return jsonBody;
            }
        };
        stringRequest.setTag(TAG+"refund");
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

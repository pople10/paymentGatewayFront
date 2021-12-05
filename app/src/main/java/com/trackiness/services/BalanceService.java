package com.trackiness.services;

import android.content.Context;
import android.os.Handler;

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
import com.trackiness.services.models.Balance;
import com.trackiness.services.models.Currency;
import com.trackiness.utility.Utility;
import com.trackiness.utility.VolleyCallBack;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class BalanceService {
    private static final String TAG = "balance";
    private Context context;
    RequestQueue queue;
    Map data;
    List<Balance> balances;
    List<Currency> currencies = new ArrayList<>();
    Balance balance;
    String rate;

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public Balance getBalance() {
        return balance;
    }

    public void setBalance(Balance balance) {
        this.balance = balance;
    }

    public Context getContext() {
        return context;
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

    public void setBalances(List<Balance> balances) {
        this.balances = balances;
    }

    public List<Balance> getBalancesData()
    {
        return this.balances;
    }

    public void setData(Map data) {
        this.data = data;
    }

    public BalanceService(Context context)
    {
        this.context=context;
        queue = Volley.newRequestQueue(context);
    }

    public void getBalances(VolleyCallBack callBack)
    {
        currencies = new ArrayList<>();
        String url = context.getString(R.string.server)+"/balance";
        callBack.beforeSend();
        StringRequest stringRequest = new StringRequest(Request.Method.GET,url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Type listType = new TypeToken<List<Balance>>(){}.getType();
                            balances = new Gson().fromJson(response, listType);
                            callBack.onSuccess();
                            callBack.onFinish();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    setBalances(null);
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

    public void getBalance(VolleyCallBack callBack,String id)
    {
        currencies = new ArrayList<>();
        String url = context.getString(R.string.server)+"/balance/"+id;
        callBack.beforeSend();
        StringRequest stringRequest = new StringRequest(Request.Method.GET,url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Type listType = new TypeToken<Balance>(){}.getType();
                        balance = new Gson().fromJson(response, listType);
                        callBack.onSuccess();
                        callBack.onFinish();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                setBalance(null);
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

    public List<Currency> getCurrencies() {
        return currencies;
    }

    public void setCurrencies(List<Currency> currencies) {
        this.currencies = currencies;
    }

    public void getAvailableCurrencies(VolleyCallBack callBack)
    {
        String url = context.getString(R.string.server)+"/balance/currency/available";
        callBack.beforeSend();
        StringRequest stringRequest = new StringRequest(Request.Method.GET,url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Type listType = new TypeToken<List<Currency>>(){}.getType();
                        currencies = new Gson().fromJson(response, listType);
                        callBack.onSuccess();
                        callBack.onFinish();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                setBalances(null);
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

    public void getUsedCurrenciesFilter(VolleyCallBack callBack,String currency)
    {
        String url = context.getString(R.string.server)+"/balance/currency/used/"+currency;
        callBack.beforeSend();
        StringRequest stringRequest = new StringRequest(Request.Method.GET,url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Type listType = new TypeToken<List<Currency>>(){}.getType();
                        currencies = new Gson().fromJson(response, listType);
                        callBack.onSuccess();
                        callBack.onFinish();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                setBalances(null);
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

    public void getAllCurrency(VolleyCallBack callBack)
    {
        String url = context.getString(R.string.server)+"/currency";
        callBack.beforeSend();
        StringRequest stringRequest = new StringRequest(Request.Method.GET,url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Type listType = new TypeToken<List<Currency>>(){}.getType();
                        currencies = new Gson().fromJson(response, listType);
                        callBack.onSuccess();
                        callBack.onFinish();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                setBalances(null);
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

    public void openBalance(VolleyCallBack callBack,Balance balance)
    {
        String url = context.getString(R.string.server)+"/balance/open";
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
                setBalances(null);
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
                Map<String,String> params = new HashMap<String,String>();
                params.put("label",balance.getLabel());
                params.put("currency",balance.getCurrency());
                return params;
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

    public void updateBalance(VolleyCallBack callBack,Balance balance)
    {
        String url = context.getString(R.string.server)+"/balance/edit";
        callBack.beforeSend();
        StringRequest stringRequest = new StringRequest(Request.Method.PUT,url,
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
                setBalances(null);
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
                Map<String,String> params = new HashMap<String,String>();
                params.put("label",balance.getLabel());
                params.put("id",""+balance.getId());
                return params;
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

    public void getRate(VolleyCallBack callBack,String from,String to)
    {
        String url = context.getString(R.string.server)+"/getRate/"+from+"/"+to;
        callBack.beforeSend();
        StringRequest stringRequest = new StringRequest(Request.Method.GET,url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        setRate(response);
                        callBack.onSuccess();
                        callBack.onFinish();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                setRate(null);
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

    public void convert(VolleyCallBack callBack,String from,String to,String amount)
    {
        String url = context.getString(R.string.server)+"/balance/convert";
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
                headers.put("Authorization", "Bearer "+ Utility.extractToken(context));
                return headers;
            }

            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String,String>();
                params.put("base",from);
                params.put("to",to);
                params.put("amount",amount);
                return params;
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

    public void delete(VolleyCallBack callBack,Balance balance)
    {
        String url = context.getString(R.string.server)+"/balance/close/"+balance.getId();
        callBack.beforeSend();
        StringRequest stringRequest = new StringRequest(Request.Method.DELETE,url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Map data = new Gson().fromJson(response.toString(),Map.class);
                        new Alert(context,Alert.SUCCESS)
                                .setContentText(data.get("done").toString())
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        callBack.onSuccess();
                                    }
                                })
                                .show();
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
}

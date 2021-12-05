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
import com.google.gson.reflect.TypeToken;
import com.trackiness.fastpay.Alert;
import com.trackiness.fastpay.R;
import com.trackiness.services.models.CreditCard;
import com.trackiness.utility.Utility;
import com.trackiness.utility.VolleyCallBack;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreditCardService {

    private static final String TAG = "creditcard";
    private Context context;
    RequestQueue queue;
    Map data;
    List<CreditCard> creditcards;

    public CreditCard getVCC() {
        return VCC;
    }

    public void setVCC(CreditCard VCC) {
        this.VCC = VCC;
    }

    CreditCard VCC;

    public CreditCardService(Context context){
        this.context=context;
        queue = Volley.newRequestQueue(context);
    }

    public static String getTAG() {
        return TAG;
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

    public void setData(Map data) {
        this.data = data;
    }


    public void setCreditCards(List<CreditCard> creditcards) {
        this.creditcards = creditcards;
    }

    public List<CreditCard> getCreditCardsData(){
        return creditcards;
    }


    public void createCreditCard(VolleyCallBack callBack){

        String url = context.getString(R.string.server) + "/vcc/create";
        callBack.beforeSend();
        StringRequest request = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>(){

                        @Override
                        public void onResponse(String response) {
                            Map data = new Gson().fromJson(response,Map.class);
                            new Alert(context,Alert.SUCCESS)
                                    .setContentText(data.get("done").toString())
                                    .show();
                            callBack.onSuccess();
                            callBack.onFinish();
                        }
                    },
                new Response.ErrorListener(){

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
                Map<String, String> map = new HashMap<>();
                map.put("currency", "USD");
                return map;
            }
        };
        request.setTag(TAG);
        request.setRetryPolicy(new RetryPolicy() {
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
        queue.add(request);
    }


    public void getCreditCards(VolleyCallBack callBack){

        String url = context.getString(R.string.server) + "/vcc";
        callBack.beforeSend();

        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>(){

                    @Override
                    public void onResponse(String response) {
                        Type listType = new TypeToken<List<CreditCard>>(){}.getType();
                        creditcards = new Gson().fromJson(response, listType);
                        callBack.onSuccess();
                        callBack.onFinish();
                    }
                },  new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    setCreditCards(null);
                    String body;
                    int statusCode = error.networkResponse.statusCode;
                    if (error.networkResponse.data != null && (statusCode == 422
                            || statusCode == 401 || statusCode == 404
                            || statusCode == 433)) {
                        if (statusCode == 404) {

/*                            try {
                                body = new String(error.networkResponse.data, "UTF-8");
                                data = new Gson().fromJson(body, Map.class);

                                new Alert(context, Alert.ERROR)
                                        .setContentText(data.get("error").toString())
                                        .show();
                            } catch (UnsupportedEncodingException e) {

                            }  */                      }

                    } else {
                        new Alert(context, Alert.ERROR)
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
                Map<String, String> map = new HashMap<>();
                map.put("currency", "USD");
                return map;
            }
        };

       request.setTag(TAG);
       request.setRetryPolicy(new RetryPolicy() {
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
       queue.add(request);

   }

    public void deleteVCC(VolleyCallBack callBack,String number){

        String url = context.getString(R.string.server) + "/vcc/delete/"+number;
        callBack.beforeSend();

        StringRequest request = new StringRequest(Request.Method.DELETE, url,
                new Response.Listener<String>(){

                    @Override
                    public void onResponse(String response) {
                        Map data = new Gson().fromJson(response,Map.class);
                        new Alert(context,Alert.SUCCESS)
                                .setContentText(data.get("done").toString())
                                .show();
                        callBack.onSuccess();
                        callBack.onFinish();
                    }
                },  new Response.ErrorListener() {
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
                Map<String, String> map = new HashMap<>();
                return map;
            }
        };

        request.setTag(TAG);
        request.setRetryPolicy(new RetryPolicy() {
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
        queue.add(request);

    }



}

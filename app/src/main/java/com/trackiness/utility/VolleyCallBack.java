package com.trackiness.utility;

public interface VolleyCallBack {
    void onSuccess();
    void onError(int error);
    void beforeSend();
    void onFinish();
}

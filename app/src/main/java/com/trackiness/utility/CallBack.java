package com.trackiness.utility;

public interface CallBack {
    void onSuccess(String s);
    void onError();
    void beforeProcessing();
    void onFinish();
}

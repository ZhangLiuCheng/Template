package com.zlc.library.http;

public interface IHttpCallback {

    void onSuccess(String result);
    void onFailure(Exception ex);

}

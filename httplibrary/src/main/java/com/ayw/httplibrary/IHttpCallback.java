package com.ayw.httplibrary;

public interface IHttpCallback {

    void onSuccess(String result);
    void onFailure(Exception ex);

}

package com.ayw.template.model.http;

public interface IHttpCallback {

    void onSuccess(String result);
    void onFailure(Exception ex);

}

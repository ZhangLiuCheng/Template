package com.ayw.template.model.http;

import java.util.Map;

public interface IHttpProcessor {

    void get(String url, Map<String, String> params, IHttpCallback callback);
    void post(String url, Map<String, String> params, IHttpCallback callback);
}

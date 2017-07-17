package com.zlc.library.http;

import java.util.Map;

public interface IHttpProcessor {

    void get(String url, Map<String, String> params, IHttpCallback callback);
    void post(String url, Map<String, String> params, IHttpCallback callback);

}

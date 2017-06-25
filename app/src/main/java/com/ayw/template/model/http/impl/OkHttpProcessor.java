package com.ayw.template.model.http.impl;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.ayw.template.model.http.IHttpCallback;
import com.ayw.template.model.http.IHttpProcessor;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OkHttpProcessor implements IHttpProcessor {

    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private final OkHttpClient mOkHttpClient;

    public OkHttpProcessor(Context context) {
        mOkHttpClient = new OkHttpClient.Builder()
                .cache(getCache(context))
                .build();
    }

    @Override
    public void get(String url, Map<String, String> params, final IHttpCallback callback) {
        Request request = new Request.Builder()
                .url(appendParam(url, params))
                .build();
        performRequest(request, callback);
    }

    @Override
    public void post(String url, Map<String, String> params, IHttpCallback callback) {
        FormBody.Builder builder = new FormBody.Builder();
        if (null != params) {
            Set<Map.Entry<String, String>> entrySet = params.entrySet();
            for (Map.Entry<String, String> entry : entrySet) {
                builder.add(entry.getKey(), entry.getValue());
            }
        }
        Request request = new Request.Builder()
                .url(url)
                .post(builder.build())
                .build();
        performRequest(request, callback);
    }

    private Call performRequest(Request request, final IHttpCallback callback) {
        final Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                if (call.isCanceled()) return;
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onFailure(e);
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (call.isCanceled()) return;
                final String result = response.body().string();
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onSuccess(result);
                    }
                });
            }
        });
        return call;
    }

    private String appendParam(String url, Map<String, String> params) {
        if (null == params) return url;
        StringBuffer sb = new StringBuffer(url);
        sb.append("?");
        Set<Map.Entry<String, String>> entrySet = params.entrySet();
        for (Map.Entry<String, String> entry : entrySet) {
            sb.append(entry.getKey()).append("=").append(entry.getValue());
        }
        return sb.toString();
    }

    private Cache getCache(Context context) {
        File cacheFile = getCacheFile(context);
        int cacheSize = 10 * 1024 * 1024;
        return new Cache(cacheFile, cacheSize);
    }

    private File getCacheFile(Context context) {
        File cacheFile = context.getExternalCacheDir();
        if (!cacheFile.exists()) {
            cacheFile = context.getCacheDir();
        }
        return cacheFile;
    }
}

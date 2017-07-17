package com.zlc.template;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import com.zlc.library.download.DownloadManager;
import com.zlc.library.http.HttpHelper;
import com.zlc.library.http.IHttpParamSign;
import com.zlc.library.http.IResultConvert;
import com.zlc.library.http.impl.OkHttpProcessor;
import com.zlc.library.http.impl.QyHttpParamSign;
import com.zlc.library.http.impl.QyResultConvert;

public class AywApplication extends Application implements Application.ActivityLifecycleCallbacks {

    public DownloadManager downloadManager;

    @Override
    public void onCreate() {
        super.onCreate();

        registerActivityLifecycleCallbacks(this);

        // 初始化Http请求
        IHttpParamSign paramSign = new QyHttpParamSign(getApplicationContext(), "appId", "privateKey");
        IResultConvert resultConvert = new QyResultConvert();
        HttpHelper.init(new OkHttpProcessor(getApplicationContext()), paramSign, resultConvert);

        // 初始化DownloadMananger
        downloadManager = DownloadManager.newInstance(this);
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        Log.v("TAG", "onActivityCreated  " + activity);
    }

    @Override
    public void onActivityStarted(Activity activity) {
        Log.v("TAG", "onActivityStarted  " + activity);
    }

    @Override
    public void onActivityResumed(Activity activity) {
        Log.v("TAG", "onActivityResumed  " + activity);
    }

    @Override
    public void onActivityPaused(Activity activity) {
        Log.v("TAG", "onActivityPaused  " + activity);
    }

    @Override
    public void onActivityStopped(Activity activity) {
        Log.v("TAG", "onActivityStopped  " + activity);
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        Log.v("TAG", "onActivitySaveInstanceState  " + activity);
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        Log.v("TAG", "onActivityDestroyed  " + activity);
    }
}

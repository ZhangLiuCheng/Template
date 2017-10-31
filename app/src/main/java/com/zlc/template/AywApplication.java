package com.zlc.template;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;
import com.zlc.library.download.DownloadManager;
import com.zlc.library.http.HttpHelper;
import com.zlc.library.http.IHttpParamSign;
import com.zlc.library.http.IResultConvert;
import com.zlc.template.model.http.OkHttpProcessor;
import com.zlc.template.model.http.QyHttpParamSign;
import com.zlc.template.model.http.QyResultConvert;

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

        initLeakCanary();
    }

    public static RefWatcher getRefWatcher(Context context) {
        AywApplication application = (AywApplication) context.getApplicationContext();
        return application.refWatcher;
    }

    private RefWatcher refWatcher;
    // 初始化内存泄漏监测
    private void initLeakCanary() {
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        refWatcher = LeakCanary.install(this);
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

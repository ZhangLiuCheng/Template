package com.ayw.updatelibrary;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

public class UpdateService extends Service {

    public class UpdateBinder extends Binder {
        public UpdateService getService() {
            return UpdateService.this;
        }
    }

    private final UpdateBinder updateBinder = new UpdateBinder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return updateBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.v(UpdateManager.TAG, getClass().getSimpleName() + " onCreate");
    }

    public void test(final String val) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Log.v(UpdateManager.TAG, getClass().getSimpleName() + " test   " + val);
                }
            }
        }).start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.v(UpdateManager.TAG, getClass().getSimpleName() + " onStartCommand");
        return START_NOT_STICKY;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.v(UpdateManager.TAG, getClass().getSimpleName() + " onUnbind");
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v(UpdateManager.TAG, getClass().getSimpleName() + " onDestroy");
    }
}

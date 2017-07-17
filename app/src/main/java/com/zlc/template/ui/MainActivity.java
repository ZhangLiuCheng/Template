package com.zlc.template.ui;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.hardware.input.InputManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.view.InputDeviceCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.InputEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.zlc.template.QyUpdateNetwork;
import com.zlc.template.R;
import com.zlc.library.update.IUpdateListener;
import com.zlc.library.update.UpdateManager;
import com.zlc.library.update.UpdateService;

public class MainActivity extends AppCompatActivity {

    static {
        System.loadLibrary("native");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toast.makeText(this, getTest(), Toast.LENGTH_SHORT).show();

        UpdateManager.init(this, new QyUpdateNetwork(this)).check(new IUpdateListener() {
            @Override
            public void lastVersion() {
                Toast.makeText(MainActivity.this, "当前版本为最新", Toast.LENGTH_SHORT).show();
            }

            @Override
            public Dialog infoDailog(String title, String message, DialogInterface.OnClickListener cancel,
                                     DialogInterface.OnClickListener install) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle(title)
                        .setMessage(message)
                        .setNegativeButton("取消", cancel)
                        .setPositiveButton("安装", install);
                return builder.create();
            }

            @Override
            public void onFailure(Exception ex) {

            }
        });
    }

    public native String getTest();

    /**
     * 网络层隔离
     * 实现IHttpProcessor，可以切换网络请求的框架
     * 实现IHttpParamSign，给请求http的参数签名
     * 实现IResutConvert，过滤请求结果.
     */
    public void http(View view) {
//        Intent intent = new Intent(this, HttpActivity.class);
//        startActivity(intent);

        Intent intent = new Intent(MainActivity.this, UpdateService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);

        Log.v(UpdateManager.TAG, getClass().getSimpleName() + " bind before");

//

        /*
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("title")
                            .setMessage("message")
                            .setNegativeButton("取消", null).create().show();
                } catch (Exception ex) {
                    Log.v("TAG", "============>  " + ex);
                }
            }
        }, 2000);
        */
    }

    /**
     * 下载管理器
     * 1.断点续传
     * 2.通过eTag，lastModify等信息，当服务器资源改变时，取消断点，重新下载。
     */
    public void download(View view) {
        Intent intent = new Intent(this, DownloadActivity.class);
        startActivity(intent);
//        unbindService(connection);
    }

    private UpdateService updateService;
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            UpdateService.UpdateBinder binder = (UpdateService.UpdateBinder) service;
            updateService = binder.getService();
            updateService.test(String.valueOf(System.currentTimeMillis()));
//
            Log.v(UpdateManager.TAG, getClass().getSimpleName() + " bind success   " + Thread.currentThread().getName());
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
}


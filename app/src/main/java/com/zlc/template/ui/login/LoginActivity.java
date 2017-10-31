package com.zlc.template.ui.login;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.tbruyelle.rxpermissions.RxPermissions;
import com.zlc.template.R;
import com.zlc.template.base.BaseActivity;
import com.zlc.template.model.bean.User;

import rx.Subscriber;


public class LoginActivity extends BaseActivity<ILoginView, LoginPresenter> implements ILoginView {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    @Override
    public LoginPresenter createPresenter() {
        return new LoginPresenter();
    }

    @Override
    public void loginSuccess(User user) {
        Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show();
    }

    public void login() {
        Toast.makeText(this, "logn", Toast.LENGTH_SHORT).show();
    }

    public void xingShiZheng() {
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.request(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .subscribe(new Subscriber<Boolean>() {
                    @Override
                    public void onCompleted() {}
                    @Override
                    public void onError(Throwable e) {
                        Log.v("TAG", "onError 权限：" + e.getMessage());
                    }
                    @Override
                    public void onNext(Boolean granted) {
                        if (granted) {
                        } else {
                            requestPermission(LoginActivity.this, "需要您授权摄像头和读取相册权限才可以使用哦！");
                        }
                    }
                });
    }

    public static void requestPermission(final Context context, String message) {
        new AlertDialog.Builder(context)
                .setMessage(message)
                .setPositiveButton("设置", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", context.getPackageName(), null);
                        intent.setData(uri);
                        context.startActivity(intent);
                    }
                })
                .setNegativeButton("取消", null).create().show();
    }
}

package com.zlc.template.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.zlc.template.AywApplication;
import com.zlc.template.R;

public abstract class BaseActivity<V, T extends BasePresenter<V>> extends AppCompatActivity {

    protected T presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = createPresenter();
        if (null != presenter) {
            presenter.attachView((V) this);
        }
    }


    public abstract T createPresenter();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != presenter) {
            presenter.dettachView();
        }
        AywApplication.getRefWatcher(this).watch(this);
    }

    /**
     * 导航栏和物理按键返回点击调用该方法.
     */
    public void back(View view) {
        finishActivityWithAnim();
    }

    @Override
    public void onBackPressed() {
        back(null);
    }

    /**
     * 设置push动画启动.
     * @param intent
     */
    public void startActivityWithAnim(Intent intent) {
        super.startActivity(intent);
        overridePendingTransition(R.anim.activity_right_in, R.anim.activity_left_out);
    }

    public void startActivityForResultWithAnim(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
        overridePendingTransition(R.anim.activity_right_in, R.anim.activity_left_out);
    }

    /**
     * 设置pop动画返回.
     */
    public void finishActivityWithAnim() {
        this.finish();
        overridePendingTransition(R.anim.activity_left_in, R.anim.activity_right_out);
    }
}

package com.zlc.template.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.zlc.library.http.HttpHelper;
import com.zlc.library.http.ResultCallback;
import com.zlc.template.R;

import java.util.HashMap;
import java.util.Map;

/**
 * 网络层隔离
 * 实现IHttpProcessor，可以切换网络请求的框架
 * 实现IHttpParamSign，给请求http的参数签名
 * 实现IResutConvert，过滤请求结果.
 */
public class HttpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_http);
    }

    public void request(View view) {
        Map<String, String> params = new HashMap<>();
        params.put("key", "value");
        HttpHelper.obtian().post("http://www.baidu.com", params, new ResultCallback<String>() {
            @Override
            public void onSuccess(String s) {
                TextView tv = (TextView) findViewById(R.id.result);
                tv.setText("onSuccess :\r\n" + s);
            }

            @Override
            public void onFailure(Exception ex) {
                TextView tv = (TextView) findViewById(R.id.result);
                tv.setText("onFailure :\r\n" + ex.toString());
            }
        });
    }
}

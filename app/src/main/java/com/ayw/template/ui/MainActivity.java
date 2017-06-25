package com.ayw.template.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.ayw.template.R;
import com.ayw.template.model.http.HttpHelper;
import com.ayw.template.model.http.ResultCallback;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    OkHttpClient client = new OkHttpClient();

    String requestRun(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    public void request(View view) {
        /*
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.v("TAG", "======>  " + requestRun("http://www.blog.zhangliuc.com"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        */


        Map<String, String> params = new HashMap<>();
        params.put("key", "value");
        HttpHelper.obtian().post("http://www.baidu.com", params, new ResultCallback<String>() {
            @Override
            public void onSuccess(String s) {
                TextView tv = (TextView) findViewById(R.id.result);
                tv.setText("onSuccess :\r\n" + s);
//                Log.v("TAG", "onSuccess  "  + s);
            }

            @Override
            public void onFailure(Exception ex) {
                Log.v("TAG", "onFailure  "  + ex);
                TextView tv = (TextView) findViewById(R.id.result);
                tv.setText("onFailure :\r\n" + ex.toString());
            }
        });

    }
}

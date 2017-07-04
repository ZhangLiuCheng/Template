package com.ayw.template.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ayw.downloadlibary.DownloadCallback;
import com.ayw.downloadlibary.DownloadManager;
import com.ayw.downloadlibary.IFileProvider;
import com.ayw.template.R;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /*
    public void request(View view) {
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
    */

    public void download(View view) {
        Intent intent = new Intent(this, DownloadActivity.class);
        startActivity(intent);
    }
}

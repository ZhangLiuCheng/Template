package com.zlc.library.http.impl;

import android.util.Log;

import com.zlc.library.http.IResultConvert;
import com.google.gson.Gson;

import org.json.JSONObject;

public class QyResultConvert implements IResultConvert {

    @Override
    public <T> T convert(Class clazz, String result) throws Exception {
        Log.v("TAG", "clazz ---> " + clazz);

        if (clazz == String.class) {
            T t = (T) result;
            return t;
        }

        JSONObject obj = new JSONObject(result);
        int code = obj.optInt("code");
        if (code == 200) {
            Gson gson = new Gson();
            T val = (T) gson.fromJson(result, clazz);
            return val;
        } else {
            throw new Exception(obj.optString("msg"));
        }
    }
}

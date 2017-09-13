package com.zlc.template.model.http;

import com.google.gson.Gson;
import com.zlc.library.http.IResultConvert;

import org.json.JSONObject;

import java.lang.reflect.Type;

public class QyResultConvert implements IResultConvert {

    @Override
    public <T> T convert(Type type, String result) throws Exception {
        JSONObject obj = new JSONObject(result);
        int code = obj.optInt("code");
        if (code == 2000) {
            Gson gson = new Gson();
            T val = (T) gson.fromJson(obj.optString("result"), type);
            return val;
        } else {
            throw new Exception(obj.optString("msg"));
        }
    }
}

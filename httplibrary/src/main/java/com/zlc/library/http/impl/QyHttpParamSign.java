package com.zlc.library.http.impl;

import android.content.Context;
import android.text.TextUtils;

import com.zlc.library.http.IHttpParamSign;

import java.util.Map;
import java.util.TreeMap;

/**
 * 清源接口的请求参数.
 */
public class QyHttpParamSign implements IHttpParamSign {

    private Context context;
    private String appId;
    private String privateKey;

    public QyHttpParamSign(Context context, String appId, String privateKey) {
        this.context = context;
        this.appId = appId;
        this.privateKey = privateKey;
    }

    @Override
    public Map<String, String> doSign(Map<String, String> params) {
        Map<String, String> signParams = new TreeMap<String, String>();
        signParams.put("platform", "1");
        signParams.put("udid", QYHttpUtil.udid());
        signParams.put("model", QYHttpUtil.deviceModel());
        signParams.put("os", QYHttpUtil.deviceOs());
        signParams.put("appid", appId);
        signParams.put("r", QYHttpUtil.appR());
        signParams.put("t", String.valueOf(System.currentTimeMillis()));

        signParams.put("adid", QYHttpUtil.deviceAndroidId(context));
        signParams.put("imei", QYHttpUtil.deviceImei(context));
        signParams.put("mac", QYHttpUtil.deviceMac(context));
        signParams.put("brand", QYHttpUtil.deviceBrand());
//        signParams.put("ch", QYConfig.getInstance().getChannel());

        if (null != params) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                signParams.put(entry.getKey(), entry.getValue());
            }
        }
        // 拼接sign
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : signParams.entrySet()) {
            String value = TextUtils.isEmpty(entry.getValue()) ? "" : entry.getValue();
            signParams.put(entry.getKey(), value);
            sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
        }
        if (sb.length() > 0) {
            sb.delete(sb.length() - 1, sb.length());
        }
        signParams.put("sign", QYRSAUtil.sign(privateKey, sb.toString()));
        return signParams;
    }
}

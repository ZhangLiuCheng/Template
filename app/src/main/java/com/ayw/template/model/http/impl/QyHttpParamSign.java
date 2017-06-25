package com.ayw.template.model.http.impl;

import android.content.Context;
import android.text.TextUtils;

import com.ayw.template.model.http.IHttpParamSign;
import com.ayw.template.utils.DeviceUtil;
import com.ayw.template.utils.QYRSAUtil;

import java.util.Map;
import java.util.TreeMap;

/**
 * 清源接口的请求参数.
 */
public class QyHttpParamSign implements IHttpParamSign {

    private Context context;
    private String appId;
    private String privateKey;

    public QyHttpParamSign(String appId, String privateKey, Context context) {
        this.context = context;
    }

    @Override
    public Map<String, String> doSign(Map<String, String> params) {
        Map<String, String> signParams = new TreeMap<String, String>();
        signParams.put("platform", "1");
        signParams.put("udid", DeviceUtil.udid());
        signParams.put("model", DeviceUtil.deviceModel());
        signParams.put("os", DeviceUtil.deviceOs());
        signParams.put("appid", appId);
        signParams.put("r", DeviceUtil.appR());
        signParams.put("t", String.valueOf(System.currentTimeMillis()));

        signParams.put("adid", DeviceUtil.deviceAndroidId(context));
        signParams.put("imei", DeviceUtil.deviceImei(context));
        signParams.put("mac", DeviceUtil.deviceMac(context));
        signParams.put("brand", DeviceUtil.deviceBrand());
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

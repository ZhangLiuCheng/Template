package com.ayw.httplibrary;

import java.util.Map;

public class HttpHelper implements IHttpProcessor {

    private static class HttpHelperHolder {
        private static final HttpHelper INSTANCE = new HttpHelper();
    }

    private static IHttpProcessor mHttpProcessor;
    private static IHttpParamSign mHttpParamSign;
    private static IResultConvert mResultConvert;

    private HttpHelper() {}

    public static void init(IHttpProcessor httpProcessor) {
        mHttpProcessor = httpProcessor;
    }

    /**
     * 初始化
     * @param httpProcessor 处理http的请求.
     * @param httpParamSign 处理请求参数的签名.
     */
    public static void init(IHttpProcessor httpProcessor, IHttpParamSign httpParamSign,
                            IResultConvert resultConvert) {
        init(httpProcessor);
        mHttpParamSign = httpParamSign;
        mResultConvert = resultConvert;
    }

    public static HttpHelper obtian() {
        if (null == mHttpProcessor) {
            throw new RuntimeException("请先初始化该类，调用HttpHelper.init(IHttpProcessor)方法");
        }
        return HttpHelperHolder.INSTANCE;
    }

    public IResultConvert getResultConvert() {
        return mResultConvert;
    }

    @Override
    public void get(String url, Map<String, String> params, IHttpCallback callback) {
        mHttpProcessor.get(url, getSignParams(params), callback);
    }

    @Override
    public void post(String url, Map<String, String> params, IHttpCallback callback) {
        mHttpProcessor.post(url, getSignParams(params), callback);
    }

    // 给请求参数签名
    private Map<String, String> getSignParams(Map<String ,String> params) {
        if (null != mHttpParamSign) {
            return mHttpParamSign.doSign(params);
        }
        return params;
    }
}

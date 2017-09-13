package com.zlc.library.http;

import java.util.Map;

public class HttpHelper implements IHttpProcessor {

    private static class HttpHelperHolder {
        private static final HttpHelper INSTANCE = new HttpHelper();
    }

    private static IHttpProcessor mHttpProcessor;
    private static IHttpParamSign mHttpParamSign;
    private static IResultConvert mResultConvert;

    private HttpHelper() {}

    /**
     * 初始化
     * @param httpProcessor 处理http的请求.
     * @param httpParamSign 处理请求参数的签名.
     * @param resultConvert 处理请求结果转换成对象.
     */
    public static void init(IHttpProcessor httpProcessor, IHttpParamSign httpParamSign,
                            IResultConvert resultConvert) {
        mHttpProcessor = httpProcessor;
        mHttpParamSign = httpParamSign;
        mResultConvert = resultConvert;
    }

    public static void init(IHttpProcessor httpProcessor, IResultConvert resultConvert) {
        init(httpProcessor, null, resultConvert);
    }

    public static void init(IHttpProcessor httpProcessor) {
        init(httpProcessor, null, null);
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

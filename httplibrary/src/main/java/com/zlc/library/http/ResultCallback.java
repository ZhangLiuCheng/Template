package com.zlc.library.http;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class ResultCallback<T> implements IHttpCallback {

    @Override
    public void onSuccess(String result) {
        try {
            Type type = analysisClassInfo(this);
            IResultConvert resultConvert = HttpHelper.obtian().getResultConvert();
            if (null == result) {
                throw new RuntimeException("没有给HttpHelper设置IResultConvert");
            }
            T t = resultConvert.convert(type, result);
            onSuccess(t);
        } catch (Exception e) {
            onFailure(e);
        }
    }

    public abstract void onSuccess(T result);

    private Type analysisClassInfo(Object objcet) {
        Type type = objcet.getClass().getGenericSuperclass();
        Type[] params = ((ParameterizedType) type).getActualTypeArguments();
        return params[0];
    }
}

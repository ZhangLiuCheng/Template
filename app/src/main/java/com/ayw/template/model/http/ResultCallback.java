package com.ayw.template.model.http;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class ResultCallback<T> implements IHttpCallback {

    @Override
    public void onSuccess(String result) {
        IResultConvert resultConvert = HttpHelper.obtian().getResultConvert();
        try {
            T t = resultConvert.convert(analysisClassInfo(this), result);
            onSuccess(t);
        } catch (Exception e) {
            onFailure(e);
        }
    }

    public abstract void onSuccess(T result);

    private Class<?> analysisClassInfo(Object objcet) {
        Type type = objcet.getClass().getGenericSuperclass();
        Type[] params = ((ParameterizedType) type).getActualTypeArguments();
        return (Class<?>) params[0];
    }
}

package com.zlc.library.http;

public interface IResultConvert {

    <T> T convert(Class clazz, String result) throws Exception;
}

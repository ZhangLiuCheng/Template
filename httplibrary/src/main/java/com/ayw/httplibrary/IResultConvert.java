package com.ayw.httplibrary;

public interface IResultConvert {

    <T> T convert(Class clazz, String result) throws Exception;
}

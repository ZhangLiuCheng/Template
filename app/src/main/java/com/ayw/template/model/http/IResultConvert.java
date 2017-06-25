package com.ayw.template.model.http;

public interface IResultConvert {

    <T> T convert(Class clazz, String result) throws Exception;
}

package com.zlc.library.http;

import java.lang.reflect.Type;

public interface IResultConvert {

    <T> T convert(Type type, String result) throws Exception;
}

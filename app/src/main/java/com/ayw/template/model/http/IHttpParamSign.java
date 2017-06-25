package com.ayw.template.model.http;

import java.util.Map;

/**
 * 给请求参数签名.
 */
public interface IHttpParamSign {

    Map<String, String> doSign(Map<String ,String> params);
}

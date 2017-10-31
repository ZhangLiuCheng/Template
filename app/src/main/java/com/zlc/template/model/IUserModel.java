package com.zlc.template.model;

import com.zlc.library.http.ResultCallback;
import com.zlc.template.model.bean.User;

public interface IUserModel {

    void exists(String phone, ResultCallback<String> resultCallback);

    void sendCode(String phone, ResultCallback<String> resultCallback);

    void login(String username, String password, ResultCallback<User> resultCallback);
}

package com.zlc.template.model;

import com.zlc.library.http.ResultCallback;
import com.zlc.template.model.bean.User;

public class UserModel implements IUserModel {
    @Override
    public void exists(String phone, ResultCallback<String> resultCallback) {

    }

    @Override
    public void sendCode(String phone, ResultCallback<String> resultCallback) {

    }

    @Override
    public void login(String username, String password, ResultCallback<User> resultCallback) {

    }
}

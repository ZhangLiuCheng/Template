package com.zlc.template.ui.login;


import com.zlc.library.http.ResultCallback;
import com.zlc.template.base.BasePresenter;
import com.zlc.template.model.IUserModel;
import com.zlc.template.model.UserModel;
import com.zlc.template.model.bean.User;

public class LoginPresenter extends BasePresenter<ILoginView> {

    private IUserModel userModel;

    public LoginPresenter() {
        this.userModel = new UserModel();
    }

    public void login(String phone, String pwd) {
        userModel.login(phone, pwd, new ResultCallback<User>() {
            @Override
            public void onSuccess(User result) {
                if (null != mReferenceView.get()) {
                    mReferenceView.get().loginSuccess(result);
                }
            }

            @Override
            public void onFailure(Exception ex) {

            }
        });
    }
}

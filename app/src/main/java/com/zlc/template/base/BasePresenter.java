package com.zlc.template.base;

import java.lang.ref.WeakReference;

public class BasePresenter<V> {

    protected WeakReference<V> mReferenceView;

    public void attachView(V view) {
        mReferenceView = new WeakReference<>(view);
    }

    public void dettachView() {
        if (mReferenceView != null) {
            mReferenceView.clear();
        }
    }
}

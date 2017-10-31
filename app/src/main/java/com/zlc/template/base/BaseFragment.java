package com.zlc.template.base;

import android.support.v4.app.Fragment;

import com.zlc.template.AywApplication;

public class BaseFragment extends Fragment {

    @Override
    public void onDestroy() {
        super.onDestroy();
        AywApplication.getRefWatcher(getContext()).watch(this);
    }

//    @Override
//    public void onResume() {
//        super.onResume();
//        setUserVisibleHint(true);
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        setUserVisibleHint(false);
//    }
}

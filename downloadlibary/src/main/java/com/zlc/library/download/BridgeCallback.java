package com.zlc.library.download;

import android.os.Handler;
import android.os.Looper;

import java.io.File;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

final class BridgeCallback extends DownloadCallback {

    private static final Handler mHandler = new Handler(Looper.getMainLooper());

    private WeakReference<DownloadCallback> mSignCallback;
    private List<WeakReference<DownloadCallback>> mMultCallbacks;

    private List<DownloadFile> mDownloadFiles;
    private IFileProvider mFileProvider;

    public BridgeCallback(DownloadCallback callback, List<DownloadFile> downloadFiles,
                          IFileProvider fileProvider) {
        this.mSignCallback = new WeakReference<>(callback);
        this.mDownloadFiles = downloadFiles;
        this.mFileProvider = fileProvider;
    }

    public void addDownloadCallback(DownloadCallback downloadCallback) {
        if (null == mMultCallbacks) {
            mMultCallbacks = new ArrayList<>();
        }
        if (null != mSignCallback) {
            mMultCallbacks.add(mSignCallback);
            mSignCallback = null;
        }
        mMultCallbacks.add(new WeakReference<>(downloadCallback));
    }

    @Override
    public void onStart(final String url, final IFileProvider.Header header) {
        invokeCallbacks("onStart", new Object[]{url, header});
        mFileProvider.saveHeaderByUrl(url, header);
    }

    @Override
    public void onLoading(final String url, final long bytesWritten, final long totalSize) {
        invokeCallbacks("onLoading", new Object[]{url, bytesWritten, totalSize});
    }

    @Override
    public void onPause(final String url) {
        invokeCallbacks("onPause", new Object[]{url});
        removeDownloadUrl(url);
        clearCallbacks();
    }

    @Override
    public void onCancel(final String url) {
        invokeCallbacks("onCancel", new Object[]{url});
        mFileProvider.deleteFileByUrl(url);
        mFileProvider.deleteHeaderByUrl(url);
        removeDownloadUrl(url);
        clearCallbacks();
    }

    @Override
    public void onSuccess(final String url, final File file) {
        invokeCallbacks("onSuccess", new Object[]{url, file});
        removeDownloadUrl(url);
        clearCallbacks();
    }

    @Override
    public void onFailure(final String url, final Exception ex) {
        invokeCallbacks("onFailure", new Object[]{url, ex});
        removeDownloadUrl(url);
        clearCallbacks();
    }

    private void invokeCallbacks(String methodName, final Object[] args) {
        final Method method = getMethod(methodName);
        if (null == method) return;
        if (null != mSignCallback && mSignCallback.get() != null) {
            invokeOnUiThread(mSignCallback.get(), method, args);
            return;
        }
        if (null == mMultCallbacks) return;
        Iterator<WeakReference<DownloadCallback>> it = mMultCallbacks.iterator();
        WeakReference<DownloadCallback> weakCallback;
        while (it.hasNext()) {
            weakCallback = it.next();
            final DownloadCallback callback = weakCallback.get();
            if (callback != null) {
                invokeOnUiThread(callback, method, args);
            }
        }
    }

    private void invokeOnUiThread(final DownloadCallback callback, final Method method,
                                  final Object[] args) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    method.invoke(callback, args);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private Method getMethod(String methodName) {
        Method[] methods = DownloadCallback.class.getMethods();
        for (Method m: methods) {
            if (methodName.equals(m.getName()))
                return m;
        }
        return null;
    }

    private void clearCallbacks() {
        if (null != mSignCallback) {
            if (mSignCallback.get() != null) {
                mSignCallback.clear();
            }
            mSignCallback = null;
        }

        if (null == mMultCallbacks) return;
        Iterator<WeakReference<DownloadCallback>> it = mMultCallbacks.iterator();
        WeakReference<DownloadCallback> weakCallback;
        while (it.hasNext()) {
            weakCallback = it.next();
            if (weakCallback.get() != null) {
                weakCallback.clear();
            }
        }
        mMultCallbacks.clear();
    }

    private void removeDownloadUrl(String url) {
        Iterator<DownloadFile> it = mDownloadFiles.iterator();
        DownloadFile df;
        while (it.hasNext()) {
            df = it.next();
            if (df.getDownloadUrl().equals(url)) {
                it.remove();
            }
        }
    }
}

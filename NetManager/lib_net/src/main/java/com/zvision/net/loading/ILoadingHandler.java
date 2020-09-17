package com.zvision.net.loading;

import android.app.Activity;

public interface ILoadingHandler {
    void create(Activity context);

    void showLoading();

    void dismissLoading();

    void showToast(int msgId);

    void showToast(String msg);
}

package com.zvision.net.okhttp;

import android.app.Application;

import com.zvision.net.loading.ILoadingHandler;
import com.zvision.net.loading.LoadingDelegate;
import com.zvision.net.module.CodeCheckDelegate;
import com.zvision.net.module.ICodeChecker;

/**
 * 网络库管理类，需要在application里初始化
 */
public class NetManager {
    public static void init(Application context,BaseClientOption option){
        OkHttpServerClientImpl.init(option);
        context.registerActivityLifecycleCallbacks(RequestLifecycle.getInstance());
    }

    public static void debugMode(boolean debug) {
        RuntimeHelper.DEBUG = debug;
    }

    public static void domain(String domain) {
        RuntimeHelper.DOMAIN = domain;
    }

    public static void loading(ILoadingHandler loadingHandler) {
        LoadingDelegate.init(loadingHandler);
    }
    public static void codeCheck(ICodeChecker checker) {
        CodeCheckDelegate.init(checker);
    }
}

package com.zvision.net.okhttp;

import okhttp3.Headers;

/**
 * 配置参数，考虑到不通项目可能会有不同的配置，需要重新实现此类
 */
public abstract class BaseClientOption {

    public abstract Headers headers(Object body);

    public abstract OkHttpClientBuilderOption option();
}

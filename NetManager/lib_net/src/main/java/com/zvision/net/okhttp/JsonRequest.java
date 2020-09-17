package com.zvision.net.okhttp;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public abstract class JsonRequest<T> extends BaseRequest<T> {

    @Override
    protected RequestBody buildBody() throws Exception {
        return RequestBody.create(MediaType.parse("application/json; charset=utf-8"), body());
    }

    protected abstract String body();

    @Override
    protected String buildParams() {
        return null;
    }

    @Override
    protected String info() {
        return  body();
    }

    @Override
    protected Headers buildHeader(BaseClientOption mOption) {
        return mOption.headers(body());
    }

    @Override
    protected abstract Method method();
}

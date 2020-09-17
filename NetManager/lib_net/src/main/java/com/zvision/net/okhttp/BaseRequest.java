package com.zvision.net.okhttp;

import android.app.Activity;

import com.zvision.net.loading.LoadingDelegate;
import com.zvision.net.module.CodeCheckDelegate;

import java.lang.ref.WeakReference;
import java.util.Set;

import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.Request;
import okhttp3.RequestBody;

public abstract class BaseRequest<R> implements Observer<String> {

    private static final String TAG = "BaseRequest";

    private RequestListener<R> listener;
    private Disposable mDisposable;
    private WeakReference<Activity> mLifecycleBinder;

    private boolean mIgnoreException = false;
    private boolean mHideToast = false;
    protected boolean mShowLoading = false;
    private boolean needCheckCode = true;
    protected abstract String url();

    protected abstract RequestBody buildBody() throws Exception;

    protected abstract String buildParams();


    protected abstract String info() throws Exception;

    protected abstract Headers buildHeader(BaseClientOption mOption) throws Exception;


    private Request request;

    protected abstract Method method();

    protected Request build(BaseClientOption mOption) throws Exception {
        Headers headers = buildHeader(mOption);
        if (getExtHeader() != null) {
            headers = joinHeader(headers);
        }
        Request.Builder builder = new Request.Builder()
                .headers(headers)
                .url(createUrl(url()));
        if (Method.GET.equals(method())) {
            builder.url(createUrl(url())+buildParams());
            builder.get();
        }else if (Method.POST.equals(method())){
            RequestBody body = buildBody();
            builder.url(createUrl(url()))
            .post(body);
        }
        request = builder.build();
        return request;
    }

    public Request getRequest() {
        return request;
    }

    private Headers joinHeader(Headers headers) {
        Set<String> names = getExtHeader().names();
        Headers.Builder builder = headers.newBuilder();
        for (String name : names) {
            builder.set(name, getExtHeader().get(name));
        }
        return builder.build();
    }

    private String createUrl(String url) {
        if (getDomain() != null && getDomain().length() > 0) {
            return getDomain() + url;
        } else {
            return RuntimeHelper.DOMAIN + url;
        }
    }
    private String createGetWithParams(String url, FormBody.Builder builder){
        return createUrl(url);
    }
    public String getDomain() {
        return "";
    }

    public Headers getExtHeader() {
        return null;
    }


    public void schedule(RequestListener<R> listener) {
        this.listener = listener;
        try {
            OkHttpServerClientImpl.getInstance().enqueue(this);
        } catch (Exception e) {
            if (listener != null) {
                listener.onFailed(e);
            }
        }
    }

    public R execute() throws Exception {

        String result;
        try {
            // 请求接口。
            result = OkHttpServerClientImpl.getInstance().execute(this);
            return result(result);

        } catch (Exception e) {
            if (mIgnoreException) {
                return null;
            } else {
                throw e;
            }
        }
    }

    public void cancel() {
        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
        }
        if (mLifecycleBinder != null) {
            mLifecycleBinder.clear();
            mLifecycleBinder = null;
        }
        if (mShowLoading) {
            LoadingDelegate.getInstance().dismissLoading();
        }
        listener = null;
    }

    public boolean needLogin() {
        return false;
    }

    protected abstract R result(String json) throws Exception;


    @Override
    public void onSubscribe(Disposable d) {
        if (mShowLoading) {
            LoadingDelegate.getInstance().showLoading();
        }
        mDisposable = d;
    }

    @Override
    public void onNext(String value) {
        try {
            //接口格式都不一样？？？我佛了。检测code
            if (needCheckCode){
                CodeCheckDelegate.getInstance().checkGlobalCode(value);
            }
            if (listener != null) {
                listener.onSuccess(result(value));
            }
        } catch (Exception e) {
            onError(e);
        }
    }

    @Override
    public void onError(Throwable e) {
        if (!mHideToast) {
            LoadingDelegate.getInstance().showToast(e.getMessage());
        }
        if (mShowLoading) {
            LoadingDelegate.getInstance().dismissLoading();
        }
        if (mLifecycleBinder != null) {
            RequestLifecycle.getInstance().unregister(mLifecycleBinder.get(), this);
            mLifecycleBinder.clear();
            mLifecycleBinder = null;
        }
        if (listener != null) {
            listener.onFailed(e);
        }
        e.printStackTrace();
    }


    @Override
    public void onComplete() {
        if (mShowLoading) {
            LoadingDelegate.getInstance().dismissLoading();
        }
        if (mLifecycleBinder != null) {
            RequestLifecycle.getInstance().unregister(mLifecycleBinder.get(), this);
            mLifecycleBinder.clear();
            mLifecycleBinder = null;
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // 扩展：忽略异常，只在execute 方法生效
    ///////////////////////////////////////////////////////////////////////////

    public BaseRequest<R> ignoreException() {
        mIgnoreException = true;
        return this;
    }

    ///////////////////////////////////////////////////////////////////////////
    // 扩展：异常后不弹toast提示
    ///////////////////////////////////////////////////////////////////////////
    public BaseRequest<R> hideToast() {
        mHideToast = true;
        return this;
    }

    ///////////////////////////////////////////////////////////////////////////
    // 扩展：旋转进度条
    ///////////////////////////////////////////////////////////////////////////
    public BaseRequest<R> loading(Activity context) {
        LoadingDelegate.getInstance().create(context);
        mShowLoading = true;
        bindLifecycle(context);
        return this;
    }

    ///////////////////////////////////////////////////////////////////////////
    // 扩展：绑定生命周期
    ///////////////////////////////////////////////////////////////////////////

    public BaseRequest<R> bindLifecycle(Activity context) {
        mLifecycleBinder = new WeakReference<Activity>(context);
        RequestLifecycle.getInstance().register(context, this);
        return this;
    }

    public BaseRequest<R> needCheckCode(boolean needCheck){
        this.needCheckCode = needCheck;
        return this;
    }

    public enum Method{
        GET,POST
    }
}
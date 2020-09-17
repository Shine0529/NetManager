package com.zvision.netmanager;

import com.zvision.net.okhttp.FormRequest;
import com.zvision.net.okhttp.JsonRequest;

import java.util.LinkedHashMap;

class TestRequest extends FormRequest<String> {
    @Override
    protected LinkedHashMap<String, Object> body() {
        return null;
    }

    @Override
    protected String url() {
        return null;
    }

    @Override
    protected Method method() {
        return null;
    }

    @Override
    protected String result(String json) throws Exception {
        return null;
    }

    class Test1Request extends JsonRequest<String>{

        @Override
        protected String body() {
            return null;
        }

        @Override
        protected String url() {
            return null;
        }

        @Override
        protected Method method() {
            return null;
        }

        @Override
        protected String result(String json) throws Exception {
            return null;
        }
    }
}

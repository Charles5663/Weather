package com.neko642.weather;

/**
 * Created by charl on 2016/2/22.
 */
public interface HttpCallBackListener {
    void onFinish(String response);

    void onError(Exception ex);
}

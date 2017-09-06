package com.zhidain.haoyuliu.sdklibrary.bluetooth;

/**
 * @author haoyuliu
 * @since 2017/9/4
 */

public interface CallBack<T> {
    void onSuccess(T result);

    void onError(Throwable e);
}

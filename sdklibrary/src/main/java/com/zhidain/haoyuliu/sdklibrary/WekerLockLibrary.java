package com.zhidain.haoyuliu.sdklibrary;

/**
 * @author haoyuliu
 * @since 2017/9/4
 */

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.zhidain.haoyuliu.sdklibrary.bluetooth.CallBack;
import com.zhidain.haoyuliu.sdklibrary.bluetooth.impl.WekerLockImpl;
import com.zhidain.haoyuliu.sdklibrary.model.LockModel;


public class WekerLockLibrary {
    private static WekerLockLibrary sSingleton;
    private static WekerLockImpl sInstance;

    private WekerLockLibrary() {
    }

    public static WekerLockLibrary getInstance() {
        if (sSingleton == null) {
            synchronized (WekerLockLibrary.class) {
                if (sSingleton == null) {
                    sInstance = WekerLockImpl.getInstance();
                    sSingleton = new WekerLockLibrary();
                }
            }
        }
        return sSingleton;
    }

    public void init(Context context) {
        sInstance.init(context);
        Log.e("haha","init");

    }
    /**
     * 添加门锁
     * @param activity
     * @param phone
     * @param callBack
     */
    public void addLock(Activity activity, String phone, CallBack<LockModel>callBack){
        sInstance.addLock(activity,phone,callBack);
    }
    /**
     * 开锁
     * @param activity
     * @param pwd
     * @param callBack
     */
    public void openLock(Activity activity,String pwd,CallBack<String> callBack){
        sInstance.openLock(activity,pwd,callBack);
    }
    /**
     * 设置密码
     * @param activity
     * @param userPwd 用户密码
     * @param userAuth 权限 1:永久 2:期限 3:一次性
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param pwdName 密码名称
     * @param pwd  密码
     * @param callBack
     */
    public void setPassWord(Activity activity, String userPwd,  String userAuth, String startTime,  String endTime,
                            String pwdName,String pwd,CallBack<String> callBack){
        sInstance.setPassWord(activity,userPwd,userAuth,startTime,endTime,pwdName,pwd,callBack);
    }

    /**
     * 释放资源
     */
    public void destory(){
        sInstance.destory();
    }
}

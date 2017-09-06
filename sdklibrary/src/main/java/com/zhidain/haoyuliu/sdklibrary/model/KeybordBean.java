package com.zhidain.haoyuliu.sdklibrary.model;

import java.io.Serializable;

/**
 * Created by lenovo on 2016/4/26.
 */
public class KeybordBean implements Serializable {
    private String id;
    private String lockId;
    private String userLockId;
    private String nickName;
    private String keyPassword;
    private String keyType;
    private String keyStatus;//4 未激活;5失效；6待重置
    private String startTime;
    private String endTime;
    private String senStatus;//发送状态
    private String lockNumber;
    private String lockPassword;

    public String getLockPassword() {
        return lockPassword;
    }

    public void setLockPassword(String lockPassword) {
        this.lockPassword = lockPassword;
    }

    public String getLockNumber() {
        return lockNumber;
    }

    public void setLockNumber(String lockNumber) {
        this.lockNumber = lockNumber;
    }

    public String getSenStatus() {
        return senStatus;
    }

    public void setSenStatus(String senStatus) {
        this.senStatus = senStatus;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKeyPassword() {
        return keyPassword;
    }

    public void setKeyPassword(String keyPassword) {
        this.keyPassword = keyPassword;
    }

    public String getKeyStatus() {
        return keyStatus;
    }

    public void setKeyStatus(String keyStatus) {
        this.keyStatus = keyStatus;
    }

    public String getKeyType() {
        return keyType;
    }

    public void setKeyType(String keyType) {
        this.keyType = keyType;
    }

    public String getLockId() {
        return lockId;
    }

    public void setLockId(String lockId) {
        this.lockId = lockId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getUserLockId() {
        return userLockId;
    }

    public void setUserLockId(String userLockId) {
        this.userLockId = userLockId;
    }
}

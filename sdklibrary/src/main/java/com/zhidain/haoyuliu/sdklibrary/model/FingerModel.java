package com.zhidain.haoyuliu.sdklibrary.model;

import java.io.Serializable;

/**
 * 指纹对象
 * Created by lenovo on 2016/5/26.
 */
public class FingerModel implements Serializable {

    /**
     * createTime : null
     * endTime : null
     * fingerKeyId : 2
     * keyName : 11
     * keyStatus : null
     * keyType : null
     * lockId : 22
     * startTime : null
     * userLockId : null
     */

    private String createTime;
    private String endTime;
    private String fingerKeyId;
    private String keyName;
    private String keyStatus;
    private String keyType;
    private String lockId;
    private String startTime;
    private String userLockId;//编号

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    private String nickName;//昵称
    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getFingerKeyId() {
        return fingerKeyId;
    }

    public void setFingerKeyId(String fingerKeyId) {
        this.fingerKeyId = fingerKeyId;
    }

    public String getKeyName() {
        return keyName;
    }

    public void setKeyName(String keyName) {
        this.keyName = keyName;
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

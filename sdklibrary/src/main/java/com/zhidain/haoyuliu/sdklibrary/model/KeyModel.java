package com.zhidain.haoyuliu.sdklibrary.model;


import java.io.Serializable;

/**
 * 钥匙实体类 Created by lenovo on 2016/4/7.
 */
public class KeyModel implements Serializable {
    public String getKeyId() {
        return keyId;
    }

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }


    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getStartAllTime() {
        return startAllTime;
    }

    public void setStartAllTime(String startAllTime) {
        this.startAllTime = startAllTime;
    }

    public String getEndAllTime() {
        return endAllTime;
    }

    public void setEndAllTime(String endAllTime) {
        this.endAllTime = endAllTime;
    }

    public String getUserLevel() {
        return userLevel;
    }

    public void setUserLevel(String userLevel) {
        this.userLevel = userLevel;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public String getUserMobile() {
        return userMobile;
    }

    public void setUserMobile(String userMobile) {
        this.userMobile = userMobile;
    }

    public String getLockId() {
        return lockId;
    }

    public void setLockId(String lockId) {
        this.lockId = lockId;
    }

    public String getLockName() {
        return lockName;
    }

    public void setLockName(String lockName) {
        this.lockName = lockName;
    }

    public String getAdminId() {
        return adminId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    public String getAdminName() {
        return adminName;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }

    public String getAdminPhone() {
        return adminPhone;
    }

    public void setAdminPhone(String adminPhone) {
        this.adminPhone = adminPhone;
    }

    public String getLockEnergy() {
        return lockEnergy;
    }

    public void setLockEnergy(String lockEnergy) {
        this.lockEnergy = lockEnergy;
    }

    public String getLockNumber() {
        return lockNumber;
    }

    public void setLockNumber(String lockNumber) {
        this.lockNumber = lockNumber;
    }

    public String getLockPassword() {
        return lockPassword;
    }

    public void setLockPassword(String lockPassword) {
        this.lockPassword = lockPassword;
    }

    public String getFingerLockIndex() {
        return fingerLockIndex;
    }

    public void setFingerLockIndex(String fingerLockIndex) {
        this.fingerLockIndex = fingerLockIndex;
    }

    public String getFingerUserCount() {
        return fingerUserCount;
    }

    public void setFingerUserCount(String fingerUserCount) {
        this.fingerUserCount = fingerUserCount;
    }

    public String getIsOpened() {
        return isOpened;
    }

    public void setIsOpened(String isOpened) {
        this.isOpened = isOpened;
    }


    public String getFinger() {
        return finger;
    }

    public void setFinger(String finger) {
        this.finger = finger;
    }

    public String getUserLockId() {
        return userLockId;
    }

    public void setUserLockId(String userLockId) {
        this.userLockId = userLockId;
    }

    public String getKeyType() {
        return keyType;
    }

    public void setKeyType(String keyType) {
        this.keyType = keyType;
    }

    public String getKeyStatus() {
        return keyStatus;
    }

    public void setKeyStatus(String keyStatus) {
        this.keyStatus = keyStatus;
    }

    private String keyId; //492822
    private String userName;// null
    private String finger;//: null,
    private String userLockId;//: 0,
    private String keyType;//: 1,
    private String keyStatus;// 1 2 3
    private String startTime;// null
    private String endTime;// null
    private String startAllTime;// null
    private String endAllTime;// null
    private String userLevel;// A
    private String userImage;// http//121.43.114.2108080/zhidian//uploadFile/image/userImage/little/user_image_20160405103706388_little.jpg
    private String userMobile;// null
    private String userId;//

    private String lockId;// 4914
    private String lockName;// 新的锁
    private String adminId;// 63
    private String adminName;// 用户2165
    private String adminPhone;// 18768122165
    private String lockEnergy;// 58
    private String lockNumber;// 001583003a95
    private String lockPassword;// 0877776891
    private String fingerLockIndex;// 0
    private String fingerUserCount;// 0
    private String isOpened;//
    private String sendType;//发送的钥匙类型 1一级用户 2 二级用户

    private KeybordBean keybordKeyVo;//键盘钥匙
    private String      lockVersion;//版本
    private String      lockType;// (1, "蓝牙+键盘+指纹"),(2, "蓝牙+键盘"),(3, "蓝牙+指纹");
    private String      roleType;//用户类型 1 管理员 2 特权用户 3 普通用户
    private String      needUpgrade;//是否要更新 1 不 3 要
    private String      model;//版本名称
    private String      isOpenLockNotify;//是否打开开锁记录（1 是 0否）
    private String      isOpenKeySend;// 是否可以发送钥匙（1 是 0 否）
    private String      commonlyUsed;//1常用 0 其他

    public String getIsOpenLockNotify() {
        return isOpenLockNotify;
    }

    public String getCommonlyUsed() {
        return commonlyUsed;
    }

    public void setCommonlyUsed(String commonlyUsed) {
        this.commonlyUsed = commonlyUsed;
    }

    public void setIsOpenLockNotify(String isOpenLockNotify) {
        this.isOpenLockNotify = isOpenLockNotify;
    }

    public String getIsOpenKeySend() {
        return isOpenKeySend;
    }

    public void setIsOpenKeySend(String isOpenKeySend) {
        this.isOpenKeySend = isOpenKeySend;
    }


    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getNeedUpgrade() {
        return needUpgrade;
    }

    public void setNeedUpgrade(String needUpgrade) {
        this.needUpgrade = needUpgrade;
    }

    public String getRoleType() {
        return roleType;
    }

    public void setRoleType(String roleType) {
        this.roleType = roleType;
    }

    public KeybordBean getKeybordKeyVo() {
        return keybordKeyVo;
    }

    public void setKeybordKeyVo(KeybordBean keybordKeyVo) {
        this.keybordKeyVo = keybordKeyVo;
    }

    public String getSendType() {
        return sendType;
    }

    public void setSendType(String sendType) {
        this.sendType = sendType;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getLockType() {
        return lockType;
    }

    public void setLockType(String lockType) {
        this.lockType = lockType;
    }

    public String getLockVersion() {
        return lockVersion;
    }

    public void setLockVersion(String lockVersion) {
        this.lockVersion = lockVersion;
    }

}

package com.zhidain.haoyuliu.sdklibrary.model;

/**
 * @author haoyuliu
 * @since 2017/10/9
 */

public class LockModel {
    private String lockName;
    private String lockNumber;
    private String lockPassword;
    private String lockInfoDesc;
    private String lockEnergy;

    public LockModel(String lockName, String lockNumber, String lockPassword, String lockInfoDesc, String lockEnergy) {
        this.lockName = lockName;
        this.lockNumber = lockNumber;
        this.lockPassword = lockPassword;
        this.lockInfoDesc = lockInfoDesc;
        this.lockEnergy = lockEnergy;
    }

    @Override
    public String toString() {
        return "LockModel{" +
                "lockName='" + lockName + '\'' +
                ", lockNumber='" + lockNumber + '\'' +
                ", lockPassword='" + lockPassword + '\'' +
                ", lockInfoDesc='" + lockInfoDesc + '\'' +
                ", lockEnergy='" + lockEnergy + '\'' +
                '}';
    }

    public String getLockName() {
        return lockName;
    }

    public void setLockName(String lockName) {
        this.lockName = lockName;
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

    public String getLockInfoDesc() {
        return lockInfoDesc;
    }

    public void setLockInfoDesc(String lockInfoDesc) {
        this.lockInfoDesc = lockInfoDesc;
    }

    public String getLockEnergy() {
        return lockEnergy;
    }

    public void setLockEnergy(String lockEnergy) {
        this.lockEnergy = lockEnergy;
    }
}

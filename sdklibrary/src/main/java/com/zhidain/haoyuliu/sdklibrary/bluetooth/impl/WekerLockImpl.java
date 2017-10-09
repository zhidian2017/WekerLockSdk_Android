package com.zhidain.haoyuliu.sdklibrary.bluetooth.impl;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;
import android.util.Log;
import android.util.StringBuilderPrinter;
import android.widget.Toast;

import com.qindachang.bluetoothle.BluetoothLe;
import com.qindachang.bluetoothle.OnLeConnectListener;
import com.qindachang.bluetoothle.OnLeNotificationListener;
import com.qindachang.bluetoothle.OnLeScanListener;
import com.qindachang.bluetoothle.exception.BleException;
import com.qindachang.bluetoothle.exception.ConnBleException;
import com.qindachang.bluetoothle.exception.ScanBleException;
import com.qindachang.bluetoothle.scanner.ScanRecord;
import com.qindachang.bluetoothle.scanner.ScanResult;
import com.zhidain.haoyuliu.sdklibrary.BlueToothApi;
import com.zhidain.haoyuliu.sdklibrary.Constants;
import com.zhidain.haoyuliu.sdklibrary.bluetooth.CallBack;
import com.zhidain.haoyuliu.sdklibrary.model.KeyModel;
import com.zhidain.haoyuliu.sdklibrary.model.LockModel;
import com.zhidain.haoyuliu.sdklibrary.utils.CrcUtils;

import java.util.Arrays;
import java.util.List;

/**
 * @author haoyuliu
 * @since 2017/9/4
 */

public class WekerLockImpl {
    private static final String TAG = "WekerLockImpl";
    private BlueToothApi mBlueToothApi;
    private static WekerLockImpl sInstance;
    private  BluetoothLe mBluetoothLe;
    private byte[] byteAllData;//获取的数据字节
    private byte[] byteActivePwd = new byte[5];// 动态密码(5Byte)
    private byte[] byteVersion;//版本信息
    private byte[] byteRandowPwd = new byte[10];//十位随机密码
    private String energy;//电量
    private String macAddress;//Mac地址
    private String phone;
    private int nowReadTimes;//读取的次数
    private Context context;
    private Activity activity;
    private CallBack<String> callBack;
    private CallBack<LockModel>lockCallBack;
    private String userPwd;
    private String userAuth;
    private String startTime;
    private String endTime;
    private String pwdName;
    private String pwd;
    private int cmdType= 0;//0 添加锁 1 开锁 2 设置密码
    public WekerLockImpl() {
    }

    public static WekerLockImpl getInstance() {
        if (sInstance == null) {
            synchronized (WekerLockImpl.class) {
                if (sInstance == null) {
                    sInstance = new WekerLockImpl();
                }
            }
        }
        return sInstance;
    }
    public void init(Context context){
        this.context = context;
        mBluetoothLe = BluetoothLe.getDefault();
        mBluetoothLe.init(context);
        mBlueToothApi = new BlueToothApi(mBluetoothLe);
        mBluetoothLe.setOnScanListener(new OnLeScanListener() {

            @Override
            public void onScanResult(final BluetoothDevice bluetoothDevice, int rssi, ScanRecord scanRecord) {
                Log.e(TAG, "onScanResult: " + bluetoothDevice.getAddress());
                mBluetoothLe.stopScan();
                mBluetoothLe.startConnect(false,bluetoothDevice, new OnLeConnectListener() {
                    @Override
                    public void onDeviceConnecting() {
                        mBluetoothLe.stopScan();
                    }

                    @Override
                    public void onDeviceConnected() {
                        Log.e(TAG, "onDeviceConnected: 成功");
                    }

                    @Override
                    public void onDeviceDisconnected() {
                        Log.e(TAG, "onDeviceConnected: 断开");
                    }

                    @Override
                    public void onServicesDiscovered(BluetoothGatt gatt) {
                        macAddress = bluetoothDevice.getAddress();
                        mBluetoothLe.readCharacteristic(Constants.SERVICE_UUID,Constants.GATT_UUID);
                        mBluetoothLe.enableNotification(true,Constants.SERVICE_UUID,Constants.GATT_UUID);
                        mBlueToothApi.sendHandShake();
                    }

                    @Override
                    public void onDeviceConnectFail(ConnBleException e) {
                        Log.e(TAG, "ConnBleException: "+e.getDetailMessage());

                    }
                });
            }

            @Override
            public void onBatchScanResults(List<ScanResult> results) {

            }

            @Override
            public void onScanCompleted() {


            }

            @Override
            public void onScanFailed(ScanBleException e) {
                Log.e(TAG, "ScanBleException: "+e.getDetailMessage());

            }
        });
        //监听通知，类型notification
        mBluetoothLe.setOnNotificationListener(TAG, new OnLeNotificationListener() {
            @Override
            public void onSuccess(BluetoothGattCharacteristic characteristic) {
                Log.i(TAG, "收到notification : " + Arrays.toString(characteristic.getValue()));
                byte[] byteData = characteristic.getValue();
                //收到数据的开头zd
                if (byteData[0]==90&&byteData[1]==68){
                    byteAllData = new byte[byteData[5]+8];
                    nowReadTimes = 0;
                    for (int i = 0; i < byteData.length; i++) {
                        byteAllData[i]=byteData[i];
                    }
                    if (byteData[5]+8<=20){
                        handleByteData(byteAllData);
                    }
                }else {
                    nowReadTimes++;
                    for (int i = 0; i < byteData.length; i++) {
                        byteAllData[i+20*nowReadTimes]=byteData[i];
                    }
                    if (nowReadTimes==byteAllData.length/20){
                        handleByteData(byteAllData);
                    }
                }
            }

            @Override
            public void onFailed(BleException e) {
                Log.e(TAG, "notification通知错误：" + e.toString());
            }
        });
    }
    /**
     * 蓝牙数据处理
     * @param byteData
     */
    private void handleByteData(byte[] byteData){
        int size = byteData.length;
        // 计算CRC校验码
        byte[] strForCRC = new byte[size - 2];
        for (int i = 0; i < strForCRC.length; i++) {
            strForCRC[i] = byteData[i];
        }
        byte[] crcCode = CrcUtils.crc16(strForCRC);
        if (crcCode[0] != byteData[size - 2] || crcCode[1] != byteData[size - 1]) {
            Toast.makeText(context, "校验失败", Toast.LENGTH_SHORT).show();
            return;
        }
        //握手码
        if (byteData[2]==1){
            //动态密码
            for (int i = 0; i < 5; i++) {
                byteActivePwd[i] = byteData[i + 6];
            }
            // 获取电量
            energy = String.valueOf(byteData[17]);
            //版本信息
            byteVersion = new byte[byteData.length-20];
            for (int i = 0; i < byteVersion.length; i++) {
                byteVersion[i] = byteData[i+18];
            }
            if (cmdType==0){
                mBlueToothApi.sendPhone(phone);
            }else if (cmdType==1){
                KeyModel keyModel = new KeyModel();
                keyModel.setLockPassword(userPwd);
                keyModel.setUserLockId("0");
                mBlueToothApi.openLock(macAddress,byteActivePwd,keyModel);
            }else if (cmdType==2){
                mBlueToothApi.sendPasswordReady(userPwd);
            }

        }else if (byteAllData[2]==2){
            /**设置管理员**/
            if (byteAllData[6]==0){
                Log.e(TAG, "handleByteData: 成功" );
                for (int i = 0; i < 10; i++) {
                    byteRandowPwd[i] = byteData[i + 7];
                }
                // 随机密码（10Byte）
                StringBuffer randowPwd = new StringBuffer("");
                for (int i = 0; i < 10; i++) {
                    randowPwd.append((char)byteRandowPwd[i]);
                }
               StringBuilder byteVersionSb = new StringBuilder();
                for (int i = 0; i <byteVersion.length ; i++) {
                    byteVersionSb.append((char)byteVersion[i]);
                }
                mBlueToothApi.addLockSuccess();
                Log.e(TAG, "handleByteData: "+Arrays.toString(byteRandowPwd) );
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                KeyModel keyModel = new KeyModel();
                keyModel.setLockPassword(randowPwd.toString());
                keyModel.setUserLockId("0");
                mBlueToothApi.openLock(macAddress,byteActivePwd,keyModel);
                LockModel lockModel = new LockModel("myLock",macAddress,randowPwd.toString(),byteVersionSb.toString(),energy);
                lockCallBack.onSuccess(lockModel);
            }else {
                lockCallBack.onError(new Throwable("该锁已有管理员，请重置后添加"));
            }
        }else if (byteAllData[2]==3){
            /**开门结果**/
            if (byteAllData[6] == 0) {
                callBack.onSuccess("开门成功");
            }else if (byteAllData[6]==1){
                callBack.onError(new Throwable("钥匙过期"));
            }else {
                callBack.onError(new Throwable("开门失败"));
            }

        }else if (byteAllData[2]==4){
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            mBlueToothApi.openHistoryScuess();
        }else if (byteAllData[2]==97){
            /**密码准备**/
            if (byteAllData[6] == 0) {
                mBlueToothApi.addPwdOnAndroid(userPwd,userAuth,startTime,endTime,pwdName,pwd);
            }else {
                callBack.onError(new Throwable("密码错误"));
            }
        }else if (byteAllData[2]==98){
            /**设置密码**/
            if (byteAllData[6]==0){
                if (byteAllData[7]<10){
                    callBack.onSuccess("0"+byteAllData[7]+pwd);
                }else {
                    callBack.onSuccess(byteAllData[7]+pwd);
                }
            }else if (byteAllData[6]==1){
                callBack.onError(new Throwable("密码错误"));
            }else if (byteAllData[6]==2){
                callBack.onError(new Throwable("用户信息重复"));
            }else if (byteAllData[6]==3){
                callBack.onError(new Throwable("用户已满"));
            }else {
                callBack.onError(new Throwable("随机码有误"));
            }

        }
    }

    /**
     * 添加门锁
     * @param activity
     * @param phone
     * @param callBack
     */
    public void addLock(Activity activity, String phone, CallBack<LockModel>callBack){
        Log.e(TAG, "addLock: "+phone );
        this.phone = phone;
        this.lockCallBack = callBack;
        this.activity = activity;
        cmdType = 0;
        startScan();

    }

    /**
     * 开锁
     * @param activity
     * @param pwd
     * @param callBack
     */
    public void openLock(Activity activity, String pwd, CallBack<String> callBack){
        this.callBack = callBack;
        this.userPwd = pwd;
        this.activity = activity;
        cmdType = 1;
        startScan();

    }

    /**
     * 设置密码
     * @param activity
     * @param userPwd 用户密码
     * @param userAuth 权限 0x00 ——永久 0xff——期限 0x01——一次性
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param pwdName 密码名称
     * @param pwd  密码
     * @param callBack
     */
    public void setPassWord(Activity activity, String userPwd,  String userAuth, String startTime,  String endTime, 
                            String pwdName,String pwd,CallBack<String> callBack){
        this.callBack = callBack;
        this.activity = activity;
        this.userPwd = userPwd;
        this.userAuth = userAuth;
        this.startTime = startTime;
        this.endTime = endTime;
        this.pwdName = pwdName;
        this.pwd = pwd;
        cmdType =2;
        startScan();

    }

    /**
     * 开始扫描
     */
    private void startScan(){
        mBluetoothLe.setScanPeriod(5000)//设置扫描时长，单位毫秒，默认10秒
                .setScanWithServiceUUID(Constants.SERVICE_UUID)//设置根据服务uuid过滤扫描
                .setScanWithDeviceName("Lock")//设置根据设备名称过滤扫描
                .setReportDelay(0)//如果为0，则回调onScanResult()方法，如果大于0, 则每隔你设置的时长回调onBatchScanResults()方法，不能小于0
                .startScan(activity);
    }
    /**
     * 释放蓝牙
     */
    public void destory(){
        mBluetoothLe.close();
        mBluetoothLe.destroy();
    }}

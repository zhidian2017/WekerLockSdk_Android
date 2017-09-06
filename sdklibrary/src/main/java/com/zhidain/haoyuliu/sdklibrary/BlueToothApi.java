package com.zhidain.haoyuliu.sdklibrary;

import android.text.TextUtils;


import com.qindachang.bluetoothle.BluetoothLe;
import com.zhidain.haoyuliu.sdklibrary.model.FingerModel;
import com.zhidain.haoyuliu.sdklibrary.model.KeyModel;
import com.zhidain.haoyuliu.sdklibrary.utils.CrcUtils;

import java.util.Calendar;

/**
 * Created by haoyuliu on 2017/4/20.
 */

public class BlueToothApi {
    private BluetoothLe mBluetoothLe;
    public BlueToothApi(BluetoothLe mBluetoothLe) {
        this.mBluetoothLe = mBluetoothLe;
    }

    /**
     * 发送握手码
     */
    public void sendHandShake(){
        // 连接成功 开始发送握手码
        byte[] sendstr = {0x5a, 0x44 , 0x01, 0x01, 0x00, 0x00, 0x00, 0x00};
        // 计算CRC校验码
        byte[] strForCRC = new byte[sendstr.length - 2];
        for (int i = 0; i < strForCRC.length; i++) {
            strForCRC[i] = sendstr[i];
        }
        byte[] crcCode = CrcUtils.crc16(strForCRC);
        // 添加CRC校验码
        sendstr[6] = crcCode[0];
        sendstr[7] = crcCode[1];
        mBluetoothLe.writeDataToCharacteristic(sendstr, Constants.SERVICE_UUID, Constants.GATT_UUID);
    }

    /**
     * 获取十位密码
     * @param phone
     */
    public void sendPhone(String phone){
        // 命令——0x02 手机向锁申请10位随机密码
        byte[] inputByte = {0x5a, 0x44, 0x02, 0x01, 0x00, 0x0b, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
        if (phone != null && !"".equals(phone)) {
            for (int i = 0; i < 11; i++) {
                inputByte[i + 6] = (byte) phone.charAt(i);
            }
        }
        // 计算CRC校验码
        byte[] strForCRC = new byte[inputByte.length - 2];
        for (int i = 0; i < strForCRC.length; i++) {
            strForCRC[i] = inputByte[i];
        }
        byte[] crcCode = CrcUtils.crc16(strForCRC);
        // 添加CRC校验码
        inputByte[17] = crcCode[0];
        inputByte[18] = crcCode[1];
        mBluetoothLe.writeDataToCharacteristic(inputByte, Constants.SERVICE_UUID, Constants.GATT_UUID);
    }

    /**
     * 开锁
     * @param deviceAddress
     * @param byteActivePwd
     * @param keyModel
     */
    public void openLock(String deviceAddress, byte[] byteActivePwd, KeyModel keyModel){
        // 开锁代码
        byte[] input = { 0x5a, 0x44, 0x03, 0x01, 0x00, 0x27, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };

        byte[] byteMacAddress = new byte[6];
        int j = 0;
        for (String mac : deviceAddress.split(":")) {
            byteMacAddress[j] = (byte)((int) Integer.valueOf(mac.toLowerCase(), 16));
            j ++;
        }
        // mac地址（6Byte）
        for (int i = 0; i < 6; i++) {
            input[i + 6] = byteMacAddress[i];
        }
        byte[] byteRandowPwd = keyModel.getLockPassword().getBytes();
        // 随机密码（10Byte）
        for (int i = 0; i < 10; i++) {
            input[i + 12] = byteRandowPwd[i];
        }
        // 动态密码（5Byte）
        for (int i = 0; i < 5; i++) {
            input[i + 22] = byteActivePwd[i];
        }
        String startTime = keyModel.getStartTime();
        // 起始时间（5Byte）
        if (!"开始时间".equals(startTime) && !TextUtils.isEmpty(startTime)) {
            if (startTime.indexOf("-")!=2){
                StringBuilder sb =new StringBuilder();
                sb.append(startTime.substring(2,10)).append("-00-00");
                startTime = sb.toString();
            }
            String[] startTimeArr = startTime.split("-");
            if (startTimeArr.length > 0) {
                // 起始时间（5Byte）YY-MM-DD-HH-MI
                for (int i = 0; i < startTimeArr.length; i++) {
                    input[i + 27] = (byte)Integer.parseInt(startTimeArr[i]);
                }
            }
        }
        // 结束时间（5Byte）
        String endTime = keyModel.getEndTime();
        if (!"结束时间".equals(endTime) && !TextUtils.isEmpty(endTime)) {
            if (endTime.indexOf("-")!=2){
                StringBuilder sb =new StringBuilder();
                sb.append(endTime.substring(2,10)).append("-00-00");
                endTime = sb.toString();
            }
            // 结束时间（5Byte）YY-MM-DD-HH-MI
            String[] endTimeArr = endTime.split("-");
            if (endTimeArr.length > 0) {
                for (int i = 0; i < endTimeArr.length; i++) {
                    input[i + 32] = (byte)Integer.parseInt(endTimeArr[i]);
                }
            }
        }
        // 用户编号（1Byte）0-管理员
        String userLockId = keyModel.getUserLockId();
        input[37] = (byte)Integer.parseInt(userLockId);
        // 用户权限（1Byte）1:永久 2:期限 3:一次性  0x00:永久 0xff:期限 0x01:一次性
        String userAuth = keyModel.getKeyType();
        if ("1".equals(userAuth)) {
            // 永久的场合
            input[38] = 0x00;
        } else if ("2".equals(userAuth)) {
            // 期限的场合
            input[38] = (byte)0xff;
        } else {
            // 一次性的场合
            input[38] = 0x01;
        }
        // 实时时间（6Byte）
        Calendar ca = Calendar.getInstance();
        int year = ca.get(Calendar.YEAR);

        input[39] = (byte) (Integer.parseInt(String.valueOf(year)
                .substring(2)));
        input[40] = (byte) (ca.get(Calendar.MONTH) + 1);
        input[41] = (byte) ca.get(Calendar.DAY_OF_MONTH);
        input[42] = (byte) ca.get(Calendar.HOUR_OF_DAY);
        input[43] = (byte) ca.get(Calendar.MINUTE);
        input[44] = (byte) ca.get(Calendar.SECOND);

        // 计算CRC校验码
        byte[] strForCRC = new byte[input.length - 2];
        for (int i = 0; i < strForCRC.length; i++) {
            strForCRC[i] = input[i];
        }
        byte[] crcCode = CrcUtils.crc16(strForCRC);
        // 添加CRC校验码
        input[45] = crcCode[0];
        input[46] = crcCode[1];
        mBluetoothLe.writeDataToCharacteristic(input, Constants.SERVICE_UUID, Constants.GATT_UUID);
    }
    /** 开锁记录上传成功命令 */
    public void openHistoryScuess() {
        try {
            byte[] input = {0x5a, 0x44, 0x04, 0x02, 0x00, 0x01, 0x00, 0x00, 0x00};

            // 计算CRC校验码
            byte[] strForCRC = new byte[input.length - 2];
            for (int i = 0; i < strForCRC.length; i++) {
                strForCRC[i] = input[i];
            }
            byte[] crcCode = CrcUtils.crc16(strForCRC);
            // 添加CRC校验码
            input[7] = crcCode[0];
            input[8] = crcCode[1];
            mBluetoothLe.writeDataToCharacteristic(input, Constants.SERVICE_UUID, Constants.GATT_UUID);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }
    /**
     * 添加锁成功
     */
    public void addLockSuccess(){
        // 添加锁成功代码
        byte[] input = { 0x5a, 0x44, 0x02, 0x02, 0x00, 0x01, 0x00, 0x00, 0x00 };

        // 计算CRC校验码
        byte[] strForCRC = new byte[input.length - 2];
        for (int i = 0; i < strForCRC.length; i++) {
            strForCRC[i] = input[i];
        }
        byte[] crcCode = CrcUtils.crc16(strForCRC);
        // 添加CRC校验码
        input[7] = crcCode[0];
        input[8] = crcCode[1];
        mBluetoothLe.writeDataToCharacteristic(input, Constants.SERVICE_UUID, Constants.GATT_UUID);
    }
    /*指纹模块*/

    /**
     * 发送指纹准备命令
     * @param lockPassword
     */
    private void sendFingerReady(String lockPassword) {
        try {
            byte[] input = {0x5a, 0x44, 0x21, 0x01, 0x00, 0x0a, 0x00, 0x00, 0x00, 0x00,
                    0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};

            byte[] lockPassArr = lockPassword.getBytes();

            // 随机密码（10Byte）
            for (int i = 0; i < 10; i++) {
                input[i + 6] = lockPassArr[i];
            }
            // 计算CRC校验码
            byte[] strForCRC = new byte[input.length - 2];
            for (int i = 0; i < strForCRC.length; i++) {
                strForCRC[i] = input[i];
            }
            byte[] crcCode = CrcUtils.crc16(strForCRC);
            // 添加CRC校验码
            input[16] = crcCode[0];
            input[17] = crcCode[1];
            mBluetoothLe.writeDataToCharacteristic(input, Constants.SERVICE_UUID, Constants.GATT_UUID);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    /**
     * 添加指纹命令
     * @param randompwd
     * @param userAuth
     * @param fingerName
     * @param startTime
     * @param endTime
     * @param userName
     */
    public void addFingerprintOnAndroid(final String randompwd, final String userAuth, final String fingerName,
                                        String startTime, String endTime, final String userName) {

        try {
            byte[] input = {0x5a, 0x44, 0x22, 0x01, 0x00, 0x20, 0x00, 0x00, 0x00, 0x00,
                    0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00
                    , 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};

            byte[] lockPassArr = randompwd.getBytes();

            // 随机密码（10Byte）
            for (int i = 0; i < 10; i++) {
                input[i + 6] = lockPassArr[i];
            }

            // 用户权限（1Byte）1:永久 2:期限 3:一次性  0x00:永久 0xff:期限 0x01:一次性
            if ("1".equals(userAuth)) {
                // 永久的场合
                input[16] = 0x00;
            } else if ("2".equals(userAuth)) {
                // 期限的场合
                input[16] = (byte)0xff;
            } else {
                // 一次性的场合
                input[16] = 0x01;
            }

            // 手指名字（1Byte）
            if ("1".equals(fingerName)) {
                // 左大拇指的场合
                input[17] = 0x01;
            } else if ("2".equals(fingerName)) {
                // 左食指的场合
                input[17] = 0x02;
            } else if ("3".equals(fingerName)) {
                // 左中指的场合
                input[17] = 0x03;
            } else if ("4".equals(fingerName)) {
                // 左无名指的场合
                input[17] = 0x04;
            } else if ("5".equals(fingerName)) {
                // 左小指的场合
                input[17] = 0x05;
            } else if ("6".equals(fingerName)) {
                // 右大拇指的场合
                input[17] = 0x06;
            } else if ("7".equals(fingerName)) {
                // 右食指的场合
                input[17] = 0x07;
            } else if ("8".equals(fingerName)) {
                // 右中指的场合
                input[17] = 0x08;
            } else if ("9".equals(fingerName)) {
                // 右无名指的场合
                input[17] = 0x09;
            } else {
                // 右小指的场合
                input[17] = 0x0a;
            }

            if (!"开始时间".equals(startTime) && !"".equals(startTime)) {
                if (startTime.indexOf("-")!=2){
                    StringBuilder sb =new StringBuilder();
                    sb.append(startTime.substring(2,10)).append("-00-00");
                    startTime = sb.toString();
                }
                String[] startTimeArr = startTime.split("-");
                if (startTimeArr.length > 0) {
                    // 起始时间（5Byte）YY-MM-DD-HH-MI
                    for (int i = 0; i < startTimeArr.length; i++) {
                        input[i + 18] = (byte)Integer.parseInt(startTimeArr[i]);
                    }
                }
            }

            if (!"结束时间".equals(endTime) && !"".equals(endTime)) {
                if (endTime.indexOf("-")!=2){
                    StringBuilder sb =new StringBuilder();
                    sb.append(endTime.substring(2,10)).append("-00-00");
                    endTime  = sb.toString();
                }
                // 结束时间（5Byte）YY-MM-DD-HH-MI
                String[] endTimeArr = endTime.split("-");
                if (endTimeArr.length > 0) {
                    for (int i = 0; i < endTimeArr.length; i++) {
                        input[i + 23] = (byte)Integer.parseInt(endTimeArr[i]);
                    }
                }
            }

            // 用户名字
            byte[] userNameByte = userName.getBytes("GBK");
            for (int i = 0; i < 10; i ++) {
                if (i >= userNameByte.length) {
                    break;
                }
                input[i + 28] = userNameByte[i];
            }

            // 计算CRC校验码
            byte[] strForCRC = new byte[input.length - 2];
            for (int i = 0; i < strForCRC.length; i++) {
                strForCRC[i] = input[i];
            }
            byte[] crcCode = CrcUtils.crc16(strForCRC);
            // 添加CRC校验码
            input[38] = crcCode[0];
            input[39] = crcCode[1];

            // 添加指纹
            mBluetoothLe.writeDataToCharacteristic(input, Constants.SERVICE_UUID, Constants.GATT_UUID);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

    }

    /**
     * 添加指纹成功命令 手机锁
     */
    public void addFingerprintSuccess() {
        try {
            byte[] input = {0x5a, 0x44, 0x24, 0x02, 0x00, 0x01, 0x00, 0x00, 0x00};

            // 计算CRC校验码
            byte[] strForCRC = new byte[input.length - 2];
            for (int i = 0; i < strForCRC.length; i++) {
                strForCRC[i] = input[i];
            }
            byte[] crcCode = CrcUtils.crc16(strForCRC);
            // 添加CRC校验码
            input[7] = crcCode[0];
            input[8] = crcCode[1];

            mBluetoothLe.writeDataToCharacteristic(input, Constants.SERVICE_UUID, Constants.GATT_UUID);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

    }
    /**
     *  取消指纹录制指令
     */
    private void cancelFingerPrint(String lockPassword) {
        try {

            byte[] input = {0x5a, 0x44, 0x25, 0x01, 0x00, 0x0a, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};

            byte[] lockPassArr = lockPassword.getBytes();

            // 随机密码（10Byte）
            for (int i = 0; i < 10; i++) {
                input[i + 6] = lockPassArr[i];
            }
            // 计算CRC校验码
            byte[] strForCRC = new byte[input.length - 2];
            for (int i = 0; i < strForCRC.length; i++) {
                strForCRC[i] = input[i];
            }
            byte[] crcCode = CrcUtils.crc16(strForCRC);
            // 添加CRC校验码
            input[16] = crcCode[0];
            input[17] = crcCode[1];

            // 取消添加指纹
            mBluetoothLe.writeDataToCharacteristic(input, Constants.SERVICE_UUID, Constants.GATT_UUID);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    /**
     * 修改指纹信息命令
     * @param fingerModel
     * @param lockPassword
     * @param keyName
     * @param mType
     * @param startTime
     * @param endTime
     */
    public void editeFingerOnAndroid(final FingerModel fingerModel, String lockPassword, String keyName, String mType, String startTime, String endTime) {

        try {
            byte[] input = {0x5a, 0x44, 0x28, 0x01, 0x00, 0x39, 0x00, 0x00, 0x00, 0x00,
                    0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                    0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                    0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                    0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                    0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                    0x00, 0x00, 0x00, 0x00, 0x00};

            byte[] lockPassArr = lockPassword.getBytes();

            // 随机密码（10Byte）
            for (int i = 0; i < 10; i++) {
                input[i + 6] = lockPassArr[i];
            }
            //密码编号
            input[16] = (byte)Integer.parseInt(fingerModel.getUserLockId());
            //是否冻结
//            if ("0".equals("0")) {
            input[17]=0x00;
//            }else {
//                input[17]=0x01;
//            }
            //用户权限（1Byte）1:永久 2:期限 3:一次性  0x00:永久 0xff:期限 0x01:一次性
            if ("1".equals(fingerModel.getKeyType())) {
                // 永久的场合
                input[18] = 0x00;
            } else if ("2".equals(fingerModel.getKeyType())) {
//                 期限的场合
                input[18] = (byte)0xff;
                String newStartTime = fingerModel.getStartTime();
                if (!"开始时间".equals(newStartTime) && !TextUtils.isEmpty(newStartTime)) {
                    if (newStartTime.indexOf("-")!=2){
                        StringBuilder sb =new StringBuilder();
                        sb.append(newStartTime.substring(2,10)).append("-00-00");
                        newStartTime = sb.toString();
                    }
                    String[] startTimeArr = newStartTime.split("-");
                    if (startTimeArr.length > 0) {
                        // 起始时间（5Byte）YY-MM-DD-HH-MI
                        for (int i = 0; i < startTimeArr.length; i++) {
                            input[i + 20] = (byte)Integer.parseInt(startTimeArr[i]);
                        }
                    }
                }
                String newEndTime = fingerModel.getEndTime();
                if (!"结束时间".equals(newEndTime) && !TextUtils.isEmpty(newEndTime)) {
                    if (newEndTime.indexOf("-")!=2){
                        StringBuilder sb =new StringBuilder();
                        sb.append(newEndTime.substring(2,10)).append("-00-00");
                        newEndTime = sb.toString();
                    }
                    // 结束时间（5Byte）YY-MM-DD-HH-MI
                    String[] endTimeArr = newEndTime.split("-");
                    if (endTimeArr.length > 0) {
                        for (int i = 0; i < endTimeArr.length; i++) {
                            input[i + 25] = (byte)Integer.parseInt(endTimeArr[i]);
                        }
                    }
                }
            } else {
                // 一次性的场合
                input[18] = 0x01;
            }
            input[19] = (byte)0x01;


            // 用户名字
            byte[] userNameByte = fingerModel.getNickName().getBytes("GBK");
            for (int i = 0; i < 10; i ++) {
                if (i >= userNameByte.length) {
                    break;
                }
                input[i + 30] = userNameByte[i];
            }
            // 密码
            input[40]=0x00;
            if ("1".equals(mType)) {
                // 永久的场合
                input[41]  = 0x00;
            } else if ("2".equals(mType)) {
//                 期限的场合
                input[41]  = (byte)0xff;
                String newStartTime1 = startTime;
                if (!"开始时间".equals(newStartTime1) && !TextUtils.isEmpty(newStartTime1)) {
                    if (newStartTime1.indexOf("-")!=2){
                        StringBuilder sb =new StringBuilder();
                        sb.append(newStartTime1.substring(2,10)).append("-00-00");
                        newStartTime1 = sb.toString();
                    }
                    String[] startTimeArr = newStartTime1.split("-");
                    if (startTimeArr.length > 0) {
                        // 起始时间（5Byte）YY-MM-DD-HH-MI
                        for (int i = 0; i < startTimeArr.length; i++) {
                            input[i + 43] = (byte)Integer.parseInt(startTimeArr[i]);
                        }
                    }
                }
                String newEndTime1 = endTime;
                if (!"结束时间".equals(newEndTime1) && !TextUtils.isEmpty(newEndTime1)) {
                    if (newEndTime1.indexOf("-")!=2){
                        StringBuilder sb =new StringBuilder();
                        sb.append(newEndTime1.substring(2,10)).append("-00-00");
                        newEndTime1 = sb.toString();
                    }
                    // 结束时间（5Byte）YY-MM-DD-HH-MI
                    String[] endTimeArr = newEndTime1.split("-");
                    if (endTimeArr.length > 0) {
                        for (int i = 0; i < endTimeArr.length; i++) {
                            input[i + 48] = (byte)Integer.parseInt(endTimeArr[i]);
                        }
                    }
                }
            } else {
                // 一次性的场合
                input[41]  = 0x01;
            }
            input[42] = (byte)0x01;


            // 用户名字
            byte[] userNameByte1 = keyName.getBytes("GBK");
            for (int i = 0; i < 10; i ++) {
                if (i >= userNameByte1.length) {
                    break;
                }
                input[i + 53] = userNameByte1[i];
            }
            // 计算CRC校验码
            byte[] strForCRC = new byte[input.length - 2];
            for (int i = 0; i < strForCRC.length; i++) {
                strForCRC[i] = input[i];
            }
            byte[] crcCode = CrcUtils.crc16(strForCRC);
            // 添加CRC校验码
            input[63] = crcCode[0];
            input[64] = crcCode[1];

            mBluetoothLe.writeDataToCharacteristic(input, Constants.SERVICE_UUID, Constants.GATT_UUID);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

    }

    /**
     * 修改指纹成功
     */
    public void editeFingerprintSucess() {
        try {
            byte[] input = {0x5a, 0x44, 0x28, 0x02, 0x00, 0x00 , 0x00, 0x00};

            // 计算CRC校验码
            byte[] strForCRC = new byte[input.length - 2];
            for (int i = 0; i < strForCRC.length; i++) {
                strForCRC[i] = input[i];
            }
            byte[] crcCode = CrcUtils.crc16(strForCRC);
            // 添加CRC校验码
            input[6] = crcCode[0];
            input[7] = crcCode[1];

            mBluetoothLe.writeDataToCharacteristic(input, Constants.SERVICE_UUID, Constants.GATT_UUID);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

    }

    /**
     * 删除指纹命令
     * @param lockPassword
     * @param userNum
     * @param userAuth
     * @param fingerName
     * @param startTime
     * @param endTime
     * @param userName
     */
    public void deleteFingerprintOnAndroid(String lockPassword,final String userNum, final String userAuth,
                                           final String fingerName, String startTime, String endTime, final String userName) {
        try {
            byte[] input = {0x5a, 0x44, 0x26, 0x01, 0x00, 0x21, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                    0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00
                    , 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};

            byte[] lockPassArr = lockPassword.getBytes();

            // 随机密码（10Byte）
            for (int i = 0; i < 10; i++) {
                input[i + 6] = lockPassArr[i];
            }

            input[16] = (byte)Integer.parseInt(userNum);

            // 用户权限（1Byte）1:永久 2:期限 3:一次性  0x00:永久 0xff:期限 0x01:一次性
            if ("1".equals(userAuth)) {
                // 永久的场合
                input[17] = 0x00;
            } else if ("2".equals(userAuth)) {
                // 期限的场合
                input[17] = (byte)0xff;
            } else {
                // 一次性的场合
                input[17] = 0x01;
            }

            // 手指名字（1Byte）
            if ("1".equals(fingerName)) {
                // 左大拇指的场合
                input[18] = 0x01;
            } else if ("2".equals(fingerName)) {
                // 左食指的场合
                input[18] = 0x02;
            } else if ("3".equals(fingerName)) {
                // 左中指的场合
                input[18] = 0x03;
            } else if ("4".equals(fingerName)) {
                // 左无名指的场合
                input[18] = 0x04;
            } else if ("5".equals(fingerName)) {
                // 左小指的场合
                input[18] = 0x05;
            } else if ("6".equals(fingerName)) {
                // 右大拇指的场合
                input[18] = 0x06;
            } else if ("7".equals(fingerName)) {
                // 右食指的场合
                input[18] = 0x07;
            } else if ("8".equals(fingerName)) {
                // 右中指的场合
                input[18] = 0x08;
            } else if ("9".equals(fingerName)) {
                // 右无名指的场合
                input[18] = 0x09;
            } else {
                // 右小指的场合
                input[18] = 0x0a;
            }

            if (!"开始时间".equals(startTime) && !TextUtils.isEmpty(startTime)) {
                if (startTime.indexOf("-")!=2){
                    StringBuilder sb =new StringBuilder();
                    sb.append(startTime.substring(2,10)).append("-00-00");
                    startTime = sb.toString();
                }
                String[] startTimeArr = startTime.split("-");
                if (startTimeArr.length > 0) {
                    // 起始时间（5Byte）YY-MM-DD-HH-MI
                    for (int i = 0; i < startTimeArr.length; i++) {
                        input[i + 19] = (byte)Integer.parseInt(startTimeArr[i]);
                    }
                }
            }

            if (!"结束时间".equals(endTime) && !TextUtils.isEmpty(endTime)) {
                if (endTime.indexOf("-")!=2){
                    StringBuilder sb =new StringBuilder();
                    sb.append(endTime.substring(2,10)).append("-00-00");
                    endTime = sb.toString();
                }
                // 结束时间（5Byte）YY-MM-DD-HH-MI
                String[] endTimeArr = endTime.split("-");
                if (endTimeArr.length > 0) {
                    for (int i = 0; i < endTimeArr.length; i++) {
                        input[i + 24] = (byte)Integer.parseInt(endTimeArr[i]);
                    }
                }
            }

            // 用户名字
            byte[] userNameByte = userName.getBytes("GBK");
            for (int i = 0; i < 10; i ++) {
                if (i >= userNameByte.length) {
                    break;
                }
                input[i + 29] = userNameByte[i];
            }

            // 计算CRC校验码
            byte[] strForCRC = new byte[input.length - 2];
            for (int i = 0; i < strForCRC.length; i++) {
                strForCRC[i] = input[i];
            }
            byte[] crcCode = CrcUtils.crc16(strForCRC);
            // 添加CRC校验码
            input[39] = crcCode[0];
            input[40] = crcCode[1];

            // 删除指定用户指纹
            mBluetoothLe.writeDataToCharacteristic(input, Constants.SERVICE_UUID, Constants.GATT_UUID);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

    }
    /**
     * 删除指纹成功
     */
    public void deleteFingerprintSuccess() {
        try {
            byte[] input = {0x5a, 0x44, 0x26, 0x02, 0x00, 0x00, 0x00, 0x00};

            // 计算CRC校验码
            byte[] strForCRC = new byte[input.length - 2];
            for (int i = 0; i < strForCRC.length; i++) {
                strForCRC[i] = input[i];
            }
            byte[] crcCode = CrcUtils.crc16(strForCRC);
            // 添加CRC校验码
            input[6] = crcCode[0];
            input[7] = crcCode[1];

            mBluetoothLe.writeDataToCharacteristic(input, Constants.SERVICE_UUID, Constants.GATT_UUID);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

    }
    /**
     * 同步指纹列表命令应答 手机->锁
     */
    private void responseSynchronize() {
        try {
            byte[] input = {0x5a, 0x44, 0x29, 0x02, 0x00, 0x00, 0x00, 0x00};

            // 计算CRC校验码
            byte[] strForCRC = new byte[input.length - 2];
            for (int i = 0; i < strForCRC.length; i++) {
                strForCRC[i] = input[i];
            }
            byte[] crcCode = CrcUtils.crc16(strForCRC);
            // 添加CRC校验码
            input[6] = crcCode[0];
            input[7] = crcCode[1];
            mBluetoothLe.writeDataToCharacteristic(input, Constants.SERVICE_UUID, Constants.GATT_UUID);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }
    /**
     * 指纹用户同步命令：手机—>锁
     * @param lockPassword
     */
    private void sendFingerSynchronize(String lockPassword) {
        try {

            byte[] input = {0x5a, 0x44, 0x30, 0x01, 0x00, 0x0a, 0x00, 0x00, 0x00, 0x00,
                    0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};

            byte[] lockPassArr = lockPassword.getBytes();

            // 随机密码（10Byte）
            for (int i = 0; i < 10; i++) {
                input[i + 6] = lockPassArr[i];
            }
            // 计算CRC校验码
            byte[] strForCRC = new byte[input.length - 2];
            for (int i = 0; i < strForCRC.length; i++) {
                strForCRC[i] = input[i];
            }
            byte[] crcCode = CrcUtils.crc16(strForCRC);
            // 添加CRC校验码
            input[16] = crcCode[0];
            input[17] = crcCode[1];
            mBluetoothLe.writeDataToCharacteristic(input, Constants.SERVICE_UUID, Constants.GATT_UUID);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    /**
     * 指纹用户同步命令：手机锁
     */
    public void synchronizeFingerprint() {
        try {
            byte[] input = {0x5a, 0x44, 0x30, 0x02, 0x00, 0x00, 0x00, 0x00};

            // 计算CRC校验码
            byte[] strForCRC = new byte[input.length - 2];
            for (int i = 0; i < strForCRC.length; i++) {
                strForCRC[i] = input[i];
            }
            byte[] crcCode = CrcUtils.crc16(strForCRC);
            // 添加CRC校验码
            input[6] = crcCode[0];
            input[7] = crcCode[1];
            mBluetoothLe.writeDataToCharacteristic(input, Constants.SERVICE_UUID, Constants.GATT_UUID);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

    }
    /*密码模块*/

    /**
     * 密码准备命令
     * @param lockPassword
     */
    public void sendPasswordReady(String lockPassword) {
        try {
            byte[] input = {0x5a, 0x44, 0x61, 0x01, 0x00, 0x0a, 0x00, 0x00, 0x00, 0x00,
                    0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};

            byte[] lockPassArr = lockPassword.getBytes();

            // 随机密码（10Byte）
            for (int i = 0; i < 10; i++) {
                input[i + 6] = lockPassArr[i];
            }
            // 计算CRC校验码
            byte[] strForCRC = new byte[input.length - 2];
            for (int i = 0; i < strForCRC.length; i++) {
                strForCRC[i] = input[i];
            }
            byte[] crcCode = CrcUtils.crc16(strForCRC);
            // 添加CRC校验码
            input[16] = crcCode[0];
            input[17] = crcCode[1];

            mBluetoothLe.writeDataToCharacteristic(input, Constants.SERVICE_UUID, Constants.GATT_UUID);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    /**
     * 添加密码命令
     * @param randompwd
     * @param userAuth
     * @param startTime
     * @param endTime
     * @param userName
     * @param pwd
     */
    public void addPwdOnAndroid(final String randompwd, final String userAuth,
                                String startTime,  String endTime,
                                final String userName,String pwd) {

        try {
            byte[] input = {0x5a, 0x44, 0x62, 0x01, 0x00, 0x25, 0x00, 0x00, 0x00, 0x00,
                    0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                    0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                    0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                    0x00, 0x00, 0x00, 0x00, 0x00};

            byte[] lockPassArr = randompwd.getBytes();

            // 随机密码（10Byte）
            for (int i = 0; i < 10; i++) {
                input[i + 6] = lockPassArr[i];
            }

            // 用户权限（1Byte）1:永久 2:期限 3:一次性  0x00:永久 0xff:期限 0x01:一次性
            if ("1".equals(userAuth)) {
                // 永久的场合
                input[16] = 0x00;
            } else if ("2".equals(userAuth)) {
                // 期限的场合
                input[16] = (byte)0xff;
            } else {
                // 一次性的场合
                input[16] = 0x01;
            }

            if (!"开始时间".equals(startTime) && !TextUtils.isEmpty(startTime)) {
                if (startTime.indexOf("-")!=2){
                    StringBuilder sb =new StringBuilder();
                    sb.append(startTime.substring(2,10)).append("-00-00");
                    startTime = sb.toString();
                }
                String[] startTimeArr = startTime.split("-");
                if (startTimeArr.length > 0) {
                    // 起始时间（5Byte）YY-MM-DD-HH-MI
                    for (int i = 0; i < startTimeArr.length; i++) {
                        input[i + 17] = (byte)Integer.parseInt(startTimeArr[i]);
                    }
                }
            }

            if (!"结束时间".equals(endTime) && !TextUtils.isEmpty(endTime)) {
                if (endTime.indexOf("-")!=2){
                    StringBuilder sb =new StringBuilder();
                    sb.append(endTime.substring(2,10)).append("-00-00");
                    endTime = sb.toString();
                }
                // 结束时间（5Byte）YY-MM-DD-HH-MI
                String[] endTimeArr = endTime.split("-");
                if (endTimeArr.length > 0) {
                    for (int i = 0; i < endTimeArr.length; i++) {
                        input[i + 22] = (byte)Integer.parseInt(endTimeArr[i]);
                    }
                }
            }

            // 用户名字
            byte[] userNameByte = userName.getBytes("UTF-8");
            for (int i = 0; i < 10; i ++) {
                if (i >= userNameByte.length) {
                    break;
                }
                input[i + 27] = userNameByte[i];
            }
            // 密码
            byte[] userPwdByte = pwd.getBytes("UTF-8");
            for (int i = 0; i < 6; i ++) {
                if (i >= userPwdByte.length) {
                    break;
                }
                input[i + 37] = userPwdByte[i];
            }
            // 计算CRC校验码
            byte[] strForCRC = new byte[input.length - 2];
            for (int i = 0; i < strForCRC.length; i++) {
                strForCRC[i] = input[i];
            }
            byte[] crcCode = CrcUtils.crc16(strForCRC);
            // 添加CRC校验码
            input[43] = crcCode[0];
            input[44] = crcCode[1];

            mBluetoothLe.writeDataToCharacteristic(input, Constants.SERVICE_UUID, Constants.GATT_UUID);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    /** 密码设置成功后命令 */
    private void sendScuess() {
        try {
            byte[] input = {0x5a, 0x44, 0x62, 0x02, 0x00, 0x01, 0x00, 0x00, 0x00};

            // 计算CRC校验码
            byte[] strForCRC = new byte[input.length - 2];
            for (int i = 0; i < strForCRC.length; i++) {
                strForCRC[i] = input[i];
            }
            byte[] crcCode = CrcUtils.crc16(strForCRC);
            // 添加CRC校验码
            input[7] = crcCode[0];
            input[8] = crcCode[1];

            // 密码成功回调
            mBluetoothLe.writeDataToCharacteristic(input, Constants.SERVICE_UUID, Constants.GATT_UUID);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    /**
     * 修改密码命令
     * @param randompwd
     * @param status
     * @param keyModel
     */
    public void editePwdOnAndroid(final String randompwd, String status,KeyModel keyModel) {

        try {
            byte[] input = {0x5a, 0x44, 0x65, 0x01, 0x00, 0x27, 0x00, 0x00, 0x00, 0x00,
                    0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                    0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                    0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                    0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};

            byte[] lockPassArr = randompwd.getBytes();

            // 随机密码（10Byte）
            for (int i = 0; i < 10; i++) {
                input[i + 6] = lockPassArr[i];
            }
            //密码编号
            input[16] = (byte)Integer.parseInt(keyModel.getKeybordKeyVo().getKeyPassword().substring(0,2));
            //是否冻结
            if ("0".equals(status)) {
                input[17]=0x00;
            }else {
                input[17]=0x01;
            }
            //用户权限（1Byte）1:永久 2:期限 3:一次性  0x00:永久 0xff:期限 0x01:一次性
            if ("1".equals(keyModel.getKeyType())) {
                // 永久的场合
                input[18] = 0x00;
            } else if ("2".equals(keyModel.getKeyType())) {
                // 期限的场合
                input[18] = (byte)0xff;
            } else {
                // 一次性的场合
                input[18] = 0x01;
            }
            String newStartTime = keyModel.getStartTime();
            if (!"开始时间".equals(newStartTime) && !TextUtils.isEmpty(newStartTime)) {
                if (newStartTime.indexOf("-")!=2){
                    StringBuilder sb =new StringBuilder();
                    sb.append(newStartTime.substring(2,10)).append("-00-00");
                    newStartTime = sb.toString();
                }
                String[] startTimeArr = newStartTime.split("-");
                if (startTimeArr.length > 0) {
                    // 起始时间（5Byte）YY-MM-DD-HH-MI
                    for (int i = 0; i < startTimeArr.length; i++) {
                        input[i + 19] = (byte)Integer.parseInt(startTimeArr[i]);
                    }
                }
            }
            String newEndTime = keyModel.getEndTime();
            if (!"结束时间".equals(newEndTime) && !TextUtils.isEmpty(newEndTime)) {
                if (newEndTime.indexOf("-")!=2){
                    StringBuilder sb =new StringBuilder();
                    sb.append(newEndTime.substring(2,10)).append("-00-00");
                    newEndTime = sb.toString();
                }
                // 结束时间（5Byte）YY-MM-DD-HH-MI
                String[] endTimeArr = newEndTime.split("-");
                if (endTimeArr.length > 0) {
                    for (int i = 0; i < endTimeArr.length; i++) {
                        input[i + 24] = (byte)Integer.parseInt(endTimeArr[i]);
                    }
                }
            }

            // 用户名字
            byte[] userNameByte = keyModel.getKeybordKeyVo().getNickName().getBytes("UTF-8");
            for (int i = 0; i < 10; i ++) {
                if (i >= userNameByte.length) {
                    break;
                }
                input[i + 29] = userNameByte[i];
            }
            // 密码
            byte[] userPwdByte = keyModel.getKeybordKeyVo().getKeyPassword().substring(2).getBytes("UTF-8");
            for (int i = 0; i < 6; i ++) {
                if (i >= userPwdByte.length) {
                    break;
                }
                input[i + 39] = userPwdByte[i];
            }

            // 计算CRC校验码
            byte[] strForCRC = new byte[input.length - 2];
            for (int i = 0; i < strForCRC.length; i++) {
                strForCRC[i] = input[i];
            }
            byte[] crcCode = CrcUtils.crc16(strForCRC);
            // 添加CRC校验码
            input[45] = crcCode[0];
            input[46] = crcCode[1];

            mBluetoothLe.writeDataToCharacteristic(input, Constants.SERVICE_UUID, Constants.GATT_UUID);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    /**
     * 删除密码
     * @param randompwd
     * @param pwdNum
     */
    public void deletePwdOnAndroid(String randompwd,String pwdNum) {
        try {
            byte[] input = {0x5a, 0x44, 0x63, 0x01, 0x00, 0x0b, 0x00, 0x00, 0x00, 0x00
                    ,0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};

            byte[] lockPassArr = randompwd.getBytes();

            // 随机密码（10Byte）
            for (int i = 0; i < 10; i++) {
                input[i + 6] = lockPassArr[i];
            }
            //密码编号
            input[16] = (byte)Integer.parseInt(pwdNum);

            // 计算CRC校验码
            byte[] strForCRC = new byte[input.length - 2];
            for (int i = 0; i < strForCRC.length; i++) {
                strForCRC[i] = input[i];
            }
            byte[] crcCode = CrcUtils.crc16(strForCRC);
            // 添加CRC校验码
            input[17] = crcCode[0];
            input[18] = crcCode[1];
            mBluetoothLe.writeDataToCharacteristic(input, Constants.SERVICE_UUID, Constants.GATT_UUID);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

    }

}

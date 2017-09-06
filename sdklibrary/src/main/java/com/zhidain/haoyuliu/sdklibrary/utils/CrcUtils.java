package com.zhidain.haoyuliu.sdklibrary.utils;

public class CrcUtils {

    public static String getCRC16(String crc16) { // B8C3
        long lon = GetModBusCRC(crc16.replace(" ", ""));
        int h1, l0;
        l0 = (int) lon / 256;
        h1 = (int) lon % 256;
        String s = "";
        if (Integer.toHexString(h1).length() < 2) {
            s = "0" + Integer.toHexString(h1);
        } else {
            s = Integer.toHexString(h1);
        }
        if (Integer.toHexString(l0).length() < 2) {
            s = s + "0" + Integer.toHexString(l0);
        } else {
            s = s + Integer.toHexString(l0);
        }
        return s;
    }

    private static int[] strToToHexByte(String hexString) {
        hexString = hexString.replace(" ", "");
        // 如果长度不是偶数，那么后面添加空格。

        if ((hexString.length() % 2) != 0) {
            hexString += " ";
        }

        // 定义数组，长度为待转换字符串长度的一半。
        int[] returnBytes = new int[hexString.length() / 2];

        for (int i = 0; i < returnBytes.length; i++)
            // 这里为什么会出现负数呢?
            returnBytes[i] = (0xff & Integer.parseInt(hexString.substring(i * 2, i * 2 + 2), 16));
        return returnBytes;
    }

    public static long GetModBusCRC(String DATA) {
        long functionReturnValue = 0;
        long i = 0;

        long J = 0;
        int[] v = null;
        // 之前之所以错误,是因为有的数字被认为是负数了.
        v = strToToHexByte(DATA);

        long CRC = 0;
        CRC = 0xffffL;
        for (i = 0; i <= (v).length - 1; i++) { // 2.把第一个8位二进制数据（既通讯信息帧的第一个字节）与16位的CRC寄存器的低8位相异或，把结果放于CRC寄存器；
            CRC = (CRC / 256) * 256L + (CRC % 256L) ^ v[(int) i];
            for (J = 0; J <= 7; J++) { // 3.把CRC寄存器的内容右移一位（朝低位）用0填补最高位，并检查最低位；
                // 4.如果最低位为0：重复第3步（再次右移一位）；
                // 如果最低位为1：CRC寄存器与多项式A001（1010 0000 0000
                // 0001）进行异或；
                // 5.重复步骤3和4，直到右移8次，这样整个8位数据全部进行了处理；
                long d0 = 0;
                d0 = CRC & 1L;
                CRC = CRC / 2;
                if (d0 == 1)
                    CRC = CRC ^ 0xa001L;
            } // 6.重复步骤2到步骤5，进行通讯信息帧下一字节的处理；
        } // 7.最后得到的CRC寄存器内容即为：CRC码。
        CRC = CRC % 65536;
        functionReturnValue = CRC;
        return functionReturnValue;
    }

    public static byte[] crc16(byte[] dateByte) {
        byte[] rstByte = {0x00, 0x00};
        try {
            String ssStr = "";
            for (byte be : dateByte) {
                ssStr += String.format("%02X", be).toLowerCase();
            }
            ssStr = getCRC16(ssStr);
            int a = Integer.parseInt(ssStr.substring(0, 2).replaceAll("^0[x|X]", ""), 16);
            int b = Integer.parseInt(ssStr.substring(2).replaceAll("^0[x|X]", ""), 16);
            //			Integer.toHexString(a)
            rstByte[0] = (byte) b;
            rstByte[1] = (byte) a;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return rstByte;
    }


    //	public static void main(String[] args) {
    //		byte[] sendstr = {0x5a, 0x44 , 0x01, 0x01, 0x00, 0x00};
    //		String ssStr = "";
    //		for (byte be : sendstr) {
    //			ssStr += String.format("%02X", be).toLowerCase();
    //		}
    //		System.out.println(ssStr);
    //		System.out.println(getCRC16(ssStr));
    //	}

}

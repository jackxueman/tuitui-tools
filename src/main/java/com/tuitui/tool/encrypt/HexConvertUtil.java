package com.tuitui.tool.encrypt;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class HexConvertUtil {

    private static final String HEXS = "0123456789ABCDEF";
    private static Logger logger = LoggerFactory.getLogger(HexConverter.class);

    public static byte[] toBytes(String hexString) {
        byte[] bytes = new byte[0];
        if (hexString == null) {
            return bytes;
        }
        String str = hexString.toUpperCase();
        int len = str.length();

        try {
            if (len % 2 == 1) {
                throw new RuntimeException("The length of string must be odd.");
            }
            bytes = new byte[len / 2];
            for (int i = 0; i < len; i += 2) {
                bytes[i / 2] = (byte) (HEXS.indexOf(str.charAt(i)) << 4 | HEXS
                        .indexOf(str.charAt(i + 1)));
            }
        } catch (Exception e) {
            logger.error(""+e);
        }
        return bytes;
    }

    public static String toHexString(byte[] hexBytes) {
        StringBuffer strBuffer = new StringBuffer();
        try {
            for (byte b : hexBytes) {
                strBuffer.append(HEXS.charAt((b & 0xF0) >> 4));
                strBuffer.append(HEXS.charAt((b & 0x0F)));
            }
        } catch (Exception e) {
            logger.error(""+e);
        }
        return strBuffer.toString();
    }

    public static String toHexString(byte hexByte) {
        return toHexString(new byte[] { hexByte });
    }

    public static String toHexString(String string) {
        return toHexString(string.getBytes());
    }

    public static char toChar(int pos) {
        try {
            return HEXS.charAt(pos);
        } catch (Exception e) {
            logger.error(""+e);
        }
        return 0;
    }
    public static String toHexString(int hexByte) {
        byte[] targets = new byte[4];
        targets[0] = (byte) (hexByte & 0xff);// 最低位
        targets[1] = (byte) ((hexByte >> 8) & 0xff);// 次低位
        targets[2] = (byte) ((hexByte >> 16) & 0xff);// 次高位
        targets[3] = (byte) (hexByte >>> 24);// 最高位,无符号右移。

        return toHexString(targets);
    }

    public static String toHexString(Long hexByte) {

        byte[] targets = new byte[8];
        targets[0] = (byte) (hexByte & 0xff);// 最低位
        targets[1] = (byte) ((hexByte >> 8) & 0xff);// 次低位
        targets[2] = (byte) ((hexByte >> 16) & 0xff);// 次高位
        targets[3] = (byte) ((hexByte >> 24) & 0xff);// 次高位
        targets[4] = (byte) ((hexByte >> 32) & 0xff);// 次高位
        targets[5] = (byte) ((hexByte >> 40) & 0xff);// 次高位
        targets[6] = (byte) ((hexByte >> 48) & 0xff);// 次高位
        targets[7] = (byte) (hexByte >>> 56);// 最高位,无符号右移。

        return toHexString(targets);
    }

}


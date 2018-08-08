package com.tuitui.tool.encrypt;

/**
 * @author liujianxue
 * @since 2018/8/8
 */
public class HexConverter {

    public HexConverter() {
    }

    public static byte[] parseHexBinary(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];

        for(int i = 0; i < len; i += 2) {
            data[i / 2] = (byte)((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
        }

        return data;
    }

}

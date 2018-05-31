package com.example.gzs11543.jxcplayer.util;

public class decodeUnicode {
    public static String decode(String unicode){
        StringBuffer buffer = new StringBuffer();
        String[] hex = unicode.split("\\\\u");
        for(int i = 0;i < hex.length;i++){
            int data = Integer.parseInt(hex[i],16);
            buffer.append((char)data);
        }
        return buffer.toString();
    }
}

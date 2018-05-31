package com.example.player.player.util;

import java.util.Locale;

public class StringUtil {
    public static String stringToTime(int timeMs){
        int time = timeMs/1000;
        int min = time/60;
        int second = time%60;
        return String.format(Locale.getDefault(),"%02d:%02d",min,second);
    }
}

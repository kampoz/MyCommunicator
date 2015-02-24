package com.example.kp.mycommunicator;


import java.text.SimpleDateFormat;
import java.util.Date;

public class Time {

    public static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss:SSS");
    public static final SimpleDateFormat simpleTime = new SimpleDateFormat("HH:mm:ss:SSS");


    public static String getTimeStamp(){
        return simpleDateFormat.format(new Date());
    }

    public static String getTime() {
        return simpleTime.format(new Date());
    }

}

package com.zf.facesign;

import java.util.Calendar;

public class Date {
    public static String getDate() {
        Calendar cas = Calendar.getInstance();
        int year = cas.get(Calendar.YEAR);//获取年份  
        int month = cas.get(Calendar.MONTH);//获取月份  
        int day = cas.get(Calendar.DATE);//获取日  
        int hour = cas.get(Calendar.HOUR);//小时
        System.out.println("时间：" + year + (month + 1) + day + hour);
        String string = "" + year + (month + 1) + day;
        if (hour >= 8 && hour <= 18) {
            return string + "1";
        }
        if (hour >= 10 && hour <= 11) {
            return string + "2";
        }
        if (hour >= 2 && hour <= 3) {
            return string + "3";
        }
        if (hour >= 4 && hour <= 5) {
            return string + "4";
        }
        return "";
    }
}

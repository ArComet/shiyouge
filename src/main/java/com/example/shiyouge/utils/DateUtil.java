package com.example.shiyouge.utils;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {
    /**
     * 指定日期加上时间段后的日期
     * @param num  增加的天数
     * @param oldTime 指定的时间
     * @return 指定日期加上时间段后的日期
     */
    public static Date goToDayChange(int num , Date oldTime){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(oldTime);
        calendar.add(Calendar.DATE,num); //加日期
        //calendar.add(Calendar.MONTH,num); //加月
        //calendar.add(Calendar.HOUR,num); //加小时
        return calendar.getTime();
    }

//    public static void main(String[] args) {
//        Date date = new Date();
//        System.out.println(date);
//        date = DateUtil.goToDayChange(1, date);
//        System.out.println(date);
//        Timestamp createTime =  new Timestamp(date.getTime());
//        System.out.println(createTime);
//    }
}

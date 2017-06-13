/*
 * Copyright (c) 2004 - 2015, EugeneLab. All Rights Reserved
 */
package com.eugenelab.tram.util;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author eugene
 */
public class Utils {

    public static Date beginOfDay(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime(); // берем начало дня
    }

    public static Date endOfDay(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        c.set(Calendar.MILLISECOND, 999);
        return c.getTime(); // берем конец дня
    }

    public static Date timeToDate(Date time, Date date) {
        try {
            String stime = Constant.FORMAT.format(time);
            String sdate = Constant.FORMAT_DATE.format(date);
            Date rdate = Constant.FORMAT_DATE_TIME.parse(sdate + " " + stime);
            return rdate;
        } catch (ParseException ex) {
            Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public static Date timeToday(Date time) {
        return Utils.timeToDate(time, new Date());
    }

    public static Date previousDay(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, -1);
        return c.getTime();
    }

    public static Date cutSeconds(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }

    public static Date prevFiveSecond(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.MILLISECOND, 0);
        int modulo = calendar.get(Calendar.SECOND) % 5;
        if (modulo > 0) {
            calendar.add(Calendar.SECOND, -modulo);
        }
        return calendar.getTime();
    }
    

}

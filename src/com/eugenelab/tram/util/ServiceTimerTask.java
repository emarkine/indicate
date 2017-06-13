/*
 * Copyright (c) 2004 - 2015, EugeneLab. All Rights Reserved
 */
package com.eugenelab.tram.util;

import com.eugenelab.tram.interfaces.Serviceable;
import java.util.Calendar;
import java.util.Date;
import java.util.TimerTask;
import org.apache.commons.lang3.time.DateUtils;

/**
 *
 * @author eugene
 */
public class ServiceTimerTask extends TimerTask {
    
        private final Serviceable service;
        private final int refresh;
        private static int second_counter = 0;
        private static int five_second_counter = 0;
        private static int ten_second_counter = 0;
        private static int fifteen_second_counter = 0;
        private static int minute_counter = 0;
        private static int five_minute_counter = 0;
        private static int ten_minute_counter = 0;
        private static int fifteen_minute_counter = 0;
        private static int hour_counter = 0;

        public ServiceTimerTask(Serviceable service) {
            this.service = service;
            this.refresh = service.refreshPeriod();
        }

        public long frameMs() {
            return refresh * 1000L;
        }
        
        public Date alignedTime() {
            long time = System.currentTimeMillis() + frameMs(); 
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date(time));
            calendar.set( Calendar.MILLISECOND, 0 );
            long second = calendar.getTime().getTime();
            calendar.set( Calendar.SECOND, 0 );
            long minute = calendar.getTime().getTime();

            int modulo = calendar.get(Calendar.MINUTE) % 5;
            if(modulo > 0) {
                calendar.add(Calendar.MINUTE, -modulo);
            }
            calendar.add(Calendar.MINUTE, 5);
            long five_minute = calendar.getTime().getTime();
            
            calendar.setTime(new Date(time));
            calendar.set( Calendar.MILLISECOND, 0 );
            calendar.set( Calendar.SECOND, 0 );
            modulo = calendar.get(Calendar.MINUTE) % 10;
            if(modulo > 0) {
                calendar.add(Calendar.MINUTE, -modulo);
            }
            calendar.add(Calendar.MINUTE, 10);
            long ten_minute = calendar.getTime().getTime();

            calendar.setTime(new Date(time));
            calendar.set( Calendar.MILLISECOND, 0 );
            calendar.set( Calendar.SECOND, 0 );
            modulo = calendar.get(Calendar.MINUTE) % 15;
            if(modulo > 0) {
                calendar.add(Calendar.MINUTE, -modulo);
            }
            calendar.set( Calendar.MINUTE, 15 );
            long fifteen_minute = calendar.getTime().getTime();

            calendar.set( Calendar.MILLISECOND, 0 );
            calendar.set( Calendar.SECOND, 0 );
            calendar.set( Calendar.MINUTE, 0 );
            long hour = calendar.getTime().getTime();
            switch (refresh) {
            case 1:
                ++second_counter;
                return new Date(second + 1 * second_counter);
            case 5:
                ++five_second_counter;
                return new Date(minute + frameMs() + 5 * five_second_counter);
            case 10:
                ++ten_second_counter;
                return new Date(minute + frameMs() + 10 * ten_second_counter);
            case 15:
                ++fifteen_second_counter;
                return new Date(minute + frameMs() + 15 * fifteen_second_counter);
            case 60:
                ++minute_counter;
                return new Date(minute + 1000 + 100 * minute_counter);
            case 300:
                ++five_minute_counter;
                return new Date(five_minute + 1000 + 100 * five_minute_counter);
            case 600:
                ++ten_minute_counter;
                return new Date(ten_minute + 2000 + 100 * ten_minute_counter);
            case 900:
                ++fifteen_minute_counter;
                return new Date(fifteen_minute + 5000 + 100 * fifteen_minute_counter);
            case 3600:
                ++hour_counter;
                return new Date(hour + 10000 + 100 * hour_counter);
        }
        return new Date(time);
    }

        @Override
        public void run() {
            service.run();
//            System.out.println("ServiceTimerTask: " + FORMAT.format(new Date()));
        }
    
}


/* SELLAR
            long second = DateUtils.round(now, Calendar.SECOND).getTime();
            long minute = DateUtils.round(now, Calendar.MINUTE).getTime();
            long hour = DateUtils.round(now, Calendar.HOUR).getTime();
 */

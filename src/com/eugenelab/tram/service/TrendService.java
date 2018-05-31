/*
 * Copyright (c) 2004 - 2015, EugeneLab. All Rights Reserved
 */
package com.eugenelab.tram.service;

import com.eugenelab.tram.domain.Bar;
import com.eugenelab.tram.domain.Point;
import com.eugenelab.tram.domain.ServiceData;
import java.util.Map;
import javax.persistence.EntityManager;

/**
 * Сервис для отрисовки линий поддержки/сопротивления
 * 
 * @author eugene
 */
public class TrendService extends Service {

    public TrendService(ServiceData source, EntityManager manager) {
        super(source, manager);
        print = false;
    }

    @Override
    public void start() {
        super.start();
    }

    private boolean checkHigh(Bar[] bars, int index, int level) {
        Bar bar = bars[index];
        for (int i=1; i<=level; ++i ) {
            Bar bar_prev = bars[index-i];
            Bar bar_next = bars[index+i];
            if ( bar.getHigh().doubleValue() < bar_prev.getHigh().doubleValue() ||
                 bar.getHigh().doubleValue() < bar_next.getHigh().doubleValue() )
                return false;
        }
        return true;
    }

    private boolean checkLow(Bar[] bars, int index, int level) {
        Bar bar = bars[index];
        for (int i=1; i<=level; ++i ) {
            Bar bar_prev = bars[index-i];
            Bar bar_next = bars[index+i];
            if ( bar.getLow().doubleValue() > bar_prev.getLow().doubleValue() ||
                 bar.getLow().doubleValue() > bar_next.getLow().doubleValue() )
                return false;
        }
        return true;
    }
    
    @Override
    public void run() {
        super.run();
        time();
        Bar[] bars = reader.abars();
        Map<Long, Point> points_high = reader.points("trend_bar_high");
        Map<Long, Point> points_low = reader.points("trend_bar_low");
        Point prev_point_high = null;
        Point prev_point_low = null;
        int level =  set.getLevelDepth();
        for (int i=level; i<bars.length-level; ++i ) {
            Bar bar = bars[i];
            if ( checkHigh(bars,i,level) ) {
                long tm = bar.getTime().getTime();
                if ( !points_high.containsKey(tm) ) {
                    Point point = writer.createPoint(tm, bar.getHigh().doubleValue(), "trend_bar_high", prev_point_high);
                    point.setService(data);
                    points_high.put(tm, point);
                    puts(point);
                }
                prev_point_high = points_high.get(tm);
            }
            if ( checkLow(bars,i,level) ) {
                long tm = bar.getTime().getTime();
                if ( !points_low.containsKey(tm) ) {
                    Point point = writer.createPoint(tm, bar.getLow().doubleValue(), "trend_bar_low", prev_point_low);
                    point.setService(data);
                    points_low.put(tm, point);
                    puts(point);
                }
                prev_point_low = points_low.get(tm);
            }
//            if ( bar != null && bar_prev != null && bar_next != null ) {
////                puts(bar);
//            } else {
//                puts("Error");
//            }
        }
        calculation(points_high);
        calculation(points_low);
        transaction();    
        runtime();
    }

}

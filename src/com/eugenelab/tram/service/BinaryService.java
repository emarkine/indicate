/*
 * Copyright (c) 2004 - 2015, EugeneLab. All Rights Reserved
 */
package com.eugenelab.tram.service;

import com.eugenelab.tram.domain.Bar;
import com.eugenelab.tram.domain.Point;
import com.eugenelab.tram.domain.ServiceData;
import java.util.Map;
import java.util.Random;
import javax.persistence.EntityManager;

/**
 *
 * @author eugene
 */
public class BinaryService extends Service {
//    private final Random random = new Random();

    public BinaryService(ServiceData data, EntityManager manager) {
        super(data, manager);
//        print = true;
    }

    @Override
    public void run() {
        time();
        Bar[] bars = reader.abars(); // считываем все имеющиеся бары 
        Map<Long, Point> points = reader.points();
        Point prev = reader.prev(calculator.beginTime);
        puts("bars: " + bars.length + ", points: " + points.size() );
        for (Bar bar : bars) {
            long t = bar.getCloseTime().getTime();
            Point point = points.get(t);
            if (point == null) {
                double rnd = Math.random();
                point = writer.createPoint(t, rnd, prev);
                point.setService(subservice);
                points.put(t, point);
                puts(point);
            }
            prev = point;
        }
        calculation(points);
        runtime();
    }


}

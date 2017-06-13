/*
 * Copyright (c) 2004 - 2015, EugeneLab. All Rights Reserved
 */
package com.eugenelab.tram.service;

import com.eugenelab.tram.database.Bar;
import com.eugenelab.tram.interfaces.Rateable;
import com.eugenelab.tram.database.Setting;
import com.eugenelab.tram.database.Point;
import com.eugenelab.tram.database.ServiceData;
import com.eugenelab.tram.util.Reader;
import java.util.Date;
import java.util.Map;
import javax.persistence.EntityManager;

/**
 *
 * @author eugene
 */
public class MovingAverageConvergenceDivergenceService extends Service {

    public MovingAverageConvergenceDivergenceService(ServiceData source, EntityManager manager) {
        super(source, manager);
    }

    private Map<Long, Point> calcDiff(Setting s, Map<Long, Point> firstPoints, Map<Long, Point> secondPoints) {
        Point[] array = firstPoints.values().toArray(new Point[firstPoints.size()]);
        Date t1 = array[0].getTime();
        Date t2 = array[array.length - 1].getTime();
        Map<Long, Point> ps = reader.points(t1, t2, s);
        Point prev = reader.point(t1);
        for (Long time : firstPoints.keySet()) {
            Point point = ps.get(time);
            if (point == null) {
                Point first = firstPoints.get(time);
                Point second = secondPoints.get(time);
                if (first != null && second != null) {
                    double diff = first.getValue().doubleValue() - second.getValue().doubleValue();
                    point = writer.createPoint(time, diff, s, prev);
                    point.setService(subservice);
                    ps.put(time, point);
                }
            }
            prev = point;
        }
        return ps;
    }

    @Override
    public void run() {
        time();
        Setting first_set = reader.set(set.getFirst());
        Map<Long, Point> firstPoints = ema(first_set);
        if (firstPoints == null || firstPoints.isEmpty()) {
            eputs("firstPoints: " + firstPoints);
            return;
        }
        Setting second_set = reader.set(set.getSecond());
        Map<Long, Point> secondPoints = ema(second_set);
//        if (firstPoints.isEmpty() || firstPoints.isEmpty())
//            return;
        Setting set_macd = reader.set("macd_macd");
        Map<Long, Point> macdPoints = calcDiff(set_macd, firstPoints, secondPoints);
        Setting set_signal = reader.set("macd_signal");
        Rateable[] macdArray = macdPoints.values().toArray(new Rateable[macdPoints.size()]);
        Map<Long, Point> signalPoints = sma(set_signal, macdArray, set_signal.getPeriod(), 0.33);
        if (macdPoints != null && signalPoints != null) {
            Setting set_chart = reader.set("macd_chart");
            Map<Long, Point> chartPoints = calcDiff(set_chart, macdPoints, signalPoints);
            calculation(chartPoints);
        }
        calculation(macdPoints);
        calculation(signalPoints);
        transaction();
        runtime();
    }

}

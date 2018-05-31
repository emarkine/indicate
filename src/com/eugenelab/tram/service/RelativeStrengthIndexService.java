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
 *
 * @author eugene
 */
public class RelativeStrengthIndexService extends Service  {

    public RelativeStrengthIndexService(ServiceData data, EntityManager manager) {
        super(data, manager);
//        print = true;
    }

    private double calc_rsi(Bar[] bars, int index) {
        double[] uu = new double[period];
        double[] dd = new double[period];
        for (int i = (index - period + 1), j = 0; i <= index; i++, j++) {
            double yesterday = bars[i - 1].getClose().doubleValue();
            double today = bars[i].getClose().doubleValue();
            if (today > yesterday) {
                uu[j] = today - yesterday;
                dd[j] = 0;
            } else if (today < yesterday) {
                uu[j] = 0;
                dd[j] = yesterday - today;
            } else {
                uu[j] = 0;
                dd[j] = 0;
            }
        }
        double cu = 0.0;
        double cd = 0.0;
        switch (set.getMethod()) {
            case "simple":
                cu = calculator.sma(uu);
                cd = calculator.sma(dd);
                if (cd == 0.0) {
                    return 100.0;
                }
                break;
            case "exponential":
                cu = calculator.ema(uu, period);
                cd = calculator.ema(dd, period);
                break;
            default:
                eputs("No method definition: " + set.getMethod());
                break;
        }
        double rs = cu / cd;
        double rsi = 100.0 - (100.0 / (1.0 + rs));
        return rsi;
    }

    @Override
    public void run() {
        super.run();
        time();
        Bar[] bars = reader.abars(); // считываем все имеющиеся бары 
        Map<Long, Point> ps = reader.points();
        Point prev = null;
        if (!ps.isEmpty()) {
            prev = ps.values().iterator().next();
        }
//        puts("bars: " + bars.length);
//        puts("points: " + ps.size());
        for (int i = period; i < bars.length; i++) {
            Bar b = bars[i];
            long t = b.getTime().getTime();
            Point p = ps.get(t);
            if (p == null) {
                double rsi = calc_rsi(bars, i);
                p = writer.createPoint(t, rsi, prev);
                p.setService(data);
                puts(p);
            }
            prev = p;
        }
        calculation(ps);
        transaction();
        runtime();
    }

}

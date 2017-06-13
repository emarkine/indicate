/*
 * Copyright (c) 2004 - 2015, EugeneLab. All Rights Reserved
 */
package com.eugenelab.tram.service;

import com.eugenelab.tram.database.Bar;
import com.eugenelab.tram.database.Point;
import com.eugenelab.tram.database.ServiceData;
import com.eugenelab.tram.interfaces.Rateable;
import java.util.Map;
import javax.persistence.EntityManager;

/**
 *
 * @author eugene
 */
public class ExponentialMovingAverageService extends Service {

    public ExponentialMovingAverageService(ServiceData source, EntityManager manager) {
        super(source, manager);
    }

    @Override
    public void run() {
        time();
        Map<Long, Point> points = ema();
        calculation(points);
        runtime();
    }

}


/* SELLAR
 public void run() {
 time();
 if (frame.getId() == 1) { // ticks
 Frame frame1m = Reader.frame(manager, "1m"); // временно использую 1m для калибровки
 reader.bars(frame1m); // нужно чтобы прсчитать range
 Map<Long, Rateable> ticks = reader.ticks();
 ema(set.getName(), ticks);
 } else {
 ema();
 }
 runtime();
 }
 //    public void run_() {
 //        long time = System.currentTimeMillis();
 //        if (print) {
 //            System.out.println("ExponentialMovingAverageService.run " + FORMAT.format(time));
 //        }
 //        // время в данный момент с учетом локальной разницы плюс одна секунда
 //        this.endTime = new Date(System.currentTimeMillis() + 1000L - TIME_OFFSET);
 //        this.beginTime = new Date(endTime.getTime() - shift); // время начала графика
 //        if (print) {
 //            System.out.println("begin_time: " + FORMAT.format(beginTime) + ", end_time: " + FORMAT.format(endTime));
 //        }
 //        Map<Date, Rateable> bars = readBars();
 //        Map<Date, Point> points = readPoints();
 //        if (print) {
 //            System.out.println("fund: " + fund.getName() + ", bars: " + bars.size() + ", points: " + points.size());
 //        }
 //        if (!manager.getTransaction().isActive()) {
 //            manager.getTransaction().begin();
 //        }
 //        Point prev = firstPoint(bars, points);
 //        for (Rateable bar : bars.values()) {
 //            Point point = points.get(bar.getTime());
 //            if (point == null) {
 //                double prev_ema = prev.getValue().doubleValue();
 //                if (bar != null && bar.getRate() != null) {
 //                    double rate = bar.getRate().doubleValue();
 //                    double ema = calc_ema(period, prev_ema, rate);
 //                    point = createPoint(bar.getTime(), BigDecimal.valueOf(ema));
 //                }
 //            }
 //            if (point != null) {
 //                prev = point;
 //            }
 //        }
 //        manager.getTransaction().commit();
 //        if (print_ms) {
 //            long ms = System.currentTimeMillis() - time;
 //            System.out.println("ExponentialMovingAverageService[" + setting.getName() + "]: " + ms + " ms");
 //        }
 //
 //    }

 private Tick firstTick(Fund fund, Date time) {
 Query query = manager.createQuery("SELECT o FROM Tick o WHERE o.fundId = ?1 AND o.time < ?2");
 query.setParameter(1, fund.getId());
 query.setParameter(2, time);
 return (Tick)query.getSingleResult();
 }
    

 */

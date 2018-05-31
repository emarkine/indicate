/*
 * Copyright (c) 2004 - 2015, EugeneLab. All Rights Reserved
 */
package com.eugenelab.tram.service;

import com.eugenelab.tram.interfaces.Rateable;
import com.eugenelab.tram.domain.Bar;
import com.eugenelab.tram.domain.Point;
import com.eugenelab.tram.domain.ServiceData;
import com.eugenelab.tram.domain.Setting;
import com.eugenelab.tram.util.Parser;
import static com.eugenelab.tram.util.Constant.FORMAT;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;
import javax.persistence.EntityManager;

/**
 *
 * @author eugene
 */
public class BollingerService extends Service {

//    private final boolean print = true;
//    private final boolean print_ms = true;
    private final double standard_deviations;

    public BollingerService(ServiceData source, EntityManager manager) {
        super(source, manager);
        this.standard_deviations = set.getStandardDeviations();
//        this.print = false;
//        this.print_ms = false;
    }

    // TD=√[(∑(x-xs)*(x-xs))/n]
    private double calc_standard_deviations(double xs, int index, Bar[] bs) {
        double sum = 0.0;
        for (int i = (index - period + 1); i <= index; ++i) {
            Bar bar = bs[i];
            double x = bar.getRate().doubleValue(); // текущий курс
            double diff = x - xs; //  разница со средним
            sum += diff * diff; // квадрат разницы
        }
        double dev = Math.sqrt(sum / period) * standard_deviations;
        return dev;

    }

    @Override
    public void run() {
        super.run();
        time();
        Map<Long, Point> points = ema();
        Map<Long, Point> widthPoints = reader.points(reader.set("bol_width"));
        Map<Long, Point> highPoints = reader.points(reader.set("bol_high"));
        Map<Long, Point> lowPoints = reader.points(reader.set("bol_low"));
        Bar[] bars = reader.abars();
        Point bol_width_prev = null, bol_high_prev = null, bol_low_prev = null;
        for (int i = period; i < bars.length; i++) {
            long t = bars[i].getTime().getTime(); // время текущего бара
            long pt = bars[i-1].getTime().getTime(); // время предыдущего бара
            Point widthPoint = widthPoints.get(t); // проверяем былы ли уже посчитаны bol точки
            if (widthPoint == null) { // создаем новые точки
                if (bol_width_prev == null ) { // это первая точка, которую мы создаем
                    bol_width_prev = widthPoints.get(pt);
                    bol_high_prev = highPoints.get(pt);
                    bol_low_prev = lowPoints.get(pt);
                }
                Point point = points.get(t); // ema опорная точка
                double value = point.getValue().doubleValue();
                double dev = calc_standard_deviations(value, i, bars);
                bol_width_prev = writer.createPoint(t, dev, "bol_width", bol_width_prev);
                bol_width_prev.setService(data);
                bol_high_prev = writer.createPoint(t, value + dev, "bol_high", bol_high_prev);
                bol_high_prev.setService(data);
                bol_low_prev = writer.createPoint(t, value - dev, "bol_low", bol_low_prev);
                bol_low_prev.setService(data);
            }
        }
        calculation(points);
        calculation(widthPoints);
        calculation(highPoints);
        calculation(lowPoints);
        runtime();
    }

}

/* SELLAR
 private void init_points(Bar[] bs) {
 //        time();
 //        this.endTime = new Date(System.currentTimeMillis() - TIME_OFFSET); // время в данный момент с учетом локальной разницы
 //        this.beginTime = new Date(endTime.getTime() - shift); // время начала графика
 Map<Long, Point> points = reader.points();
 if (print) {
 System.out.println("fund: " + fund.getName() + ", bars: " + bs.length + ", points: " + points.size());
 }
 Setting set_bollinger_width = reader.set("bollinger_width");
 Map<Long, Point> widthPoints = reader.points(set_bollinger_width);
 for (int index = bs.length - 1; index > period; --index) {
 Bar bar = bs[index];
 long time = bar.getTime().getTime();
 Point widthPoint = widthPoints.get(time);
 if (widthPoint == null) { // создаем дополнительные  точки
 Point point = points.get(time);
 puts(point);
 double value = point.getValue().doubleValue();
 double dev = calc_standard_deviations(value, index, bs);
 writer.createPoint(time, dev, "bollinger_width", null);
 writer.createPoint(time, value + dev, "bollinger_high", null);
 writer.createPoint(time, value - dev, "bollinger_low", null);
 puts("dev: " + dev);
 }
 }
 }

 Map<Date, Point> widthPoints = readPoints("bollinger_width");
 init_points(bs);
 for (int index = bs.length - 1; index > period; --index) {
 Bar bar = bs[index];
 Date time = bar.getTime();
 Point widthPoint = widthPoints.get(time);
 if (widthPoint == null) { // создаем дополнительные  точки
 Point point = points.get(time);
 if (print) {
 System.out.println("time: " + FORMAT.format(time) + ", point: " + point);
 }
 double value = point.getValue().doubleValue();
 double dev = calc_standard_deviations(value, index, bs);
 createPoint(time, BigDecimal.valueOf(dev), "bollinger_width");
 createPoint(time, BigDecimal.valueOf(value + dev), "bollinger_high");
 createPoint(time, BigDecimal.valueOf(value - dev), "bollinger_low");
 if (print) {
 System.out.println("dev: " + dev + ", time: " + FORMAT.format(time));
 }
 }
 }


 private Tick firstTick(Fund fund, Date time) {
 Query query = manager.createQuery("SELECT o FROM Tick o WHERE o.fundId = ?1 AND o.time < ?2");
 query.setParameter(1, fund.getId());
 query.setParameter(2, time);
 return (Tick)query.getSingleResult();
 }
    

 */

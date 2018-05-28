/*
 * Copyright (c) 2004 - 2015, EugeneLab. All Rights Reserved
 */
package com.eugenelab.tram.util;

import com.eugenelab.tram.domain.Bar;
import com.eugenelab.tram.domain.Crystal;
import com.eugenelab.tram.domain.Datum;
import com.eugenelab.tram.domain.Edge;
import com.eugenelab.tram.domain.Frame;
import com.eugenelab.tram.domain.Fund;
import com.eugenelab.tram.domain.Line;
import com.eugenelab.tram.domain.Neuron;
import com.eugenelab.tram.domain.Point;
import com.eugenelab.tram.domain.Response;
import com.eugenelab.tram.domain.Setting;
import com.eugenelab.tram.domain.Weight;
import com.eugenelab.tram.interfaces.Rateable;
import static com.eugenelab.tram.util.Constant.FORMAT_MS;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.persistence.EntityManager;
import org.apache.commons.lang3.time.DateUtils;

/**
 *
 * @author eugene
 */
public class Calculator {

    private final boolean print = false;

//    public final static int SIGN_SIZE = 9;
//    public final static int[] TURN_WEIGHT = {4, 3, 2, 1, 1};
//    public final static int TURN_SIZE = TURN_WEIGHT.length;
    public final static long VIEWPORT_SIZE = 100; // размер окна просмотра в барах
    public final static int XX = 1000; // ширина окна просмотра в пикселях
//    public final static int YY = 618; // высота окна просмотра в пикселях
    public final static int YY = 100; // высота дополнительного окна в пикселях

//    private final Frame frame;
    private final EntityManager manager;
    private final Fund fund;
    private final Frame frame;
    private final Setting setting;
    public final long bar_size; // временной шаг в ms
    public final long viewport; // размер окна в ms
    public double max; // максимальное значение точки
    public double min; // минимальное значени точки
//    public double bars_range; // размер окна по вертикали для баров
    public double range; // размер окна по вертикали для точек
    public int period = 0;
    public long runtime; // время старта сервиса
    public Date beginTime; // начальное время выборки
    public Date endTime; // конечное время выборки
    public int seconds; // число секунд между beginTime и endTime
    public int days; // число дней между beginTime и endTime

    public Calculator(EntityManager manager, Fund fund, Frame frame, Setting setting, int period) {
        this.manager = manager;
        this.fund = fund;
        this.frame = frame;
        this.setting = setting;
        this.period = period;
        this.bar_size = frame.getId() * 1000L;
        this.viewport = bar_size * VIEWPORT_SIZE;
        time(period);
    }

    /**
     * Внесение изменений в базу данных для всех записей одновременно
     */
    protected void transaction() {
        if (!manager.getTransaction().isActive()) {
            manager.getTransaction().begin();
        }
        manager.getTransaction().commit();
    }

    @Override
    public String toString() {
        String s = "Calculator";
        String beginDateStr = Constant.FORMAT_DATE.format(beginTime);
        String beginTimeStr = Constant.FORMAT_TIME.format(beginTime);
        String endDateStr = Constant.FORMAT_DATE.format(endTime);
        String endTimeStr = Constant.FORMAT_TIME.format(endTime);
        if (beginDateStr.endsWith(endDateStr)) {
            s += " " + beginDateStr;
            s += " " + beginTimeStr;
            s += " - " + endTimeStr;
        } else {
            s += beginDateStr + " " + beginTimeStr;
            s += " - " + endDateStr + " " + endTimeStr;
        }
        return s;
    }

    private void puts(Object o) {
        if (print) {
            if (o instanceof Date) {
                o = FORMAT_MS.format(o);
            }
            System.out.println(o);
//            System.out.println(FORMAT_MS.format(new Date()) + " " + o);
        }
    }

    /**
     * Инициализация времени со стандартным периодом
     *
     * @return
     */
    public long time() {
        return time(this.period);
    }

    /**
     * Инициализация времени с периодом
     *
     * @param period период
     *
     * @return
     */
    public long time(int period) {
        return time(new Date(), period);
    }

    /**
     * Инициализация виртуального времени с перенесением начальной точки в
     * будущее
     *
     * @param time виртуальное время
     *
     * @return
     */
    public long virtualTime(Date time) {
        long shift_period = bar_size * period; // добавка времени для периода
        Date add_time = new Date(time.getTime() + viewport + shift_period);
        return time(time, add_time, period);
    }

    /**
     * Инициализация времени
     *
     * @param time точка работы
     * @param period период
     *
     * @return
     */
    public long time(Date time, int period) {
        long shift_period = bar_size * period; // добавка времени для периода
        Date begin = new Date(time.getTime() - viewport - shift_period);
        return time(begin, time, period);
    }

    /**
     * Инициализация времени
     *
     * @param beginTime начальное время
     * @param endTime конечное время
     * @param period период
     *
     * @return
     */
    public long time(Date beginTime, Date endTime, int period) {
        this.runtime = System.currentTimeMillis();
        this.beginTime = beginTime;
//        this.endTime = endTime;
        this.endTime = new Date(endTime.getTime() - bar_size / 2);
        long diff = endTime.getTime() - beginTime.getTime();
        this.seconds = (int) (TimeUnit.SECONDS.convert(diff, TimeUnit.MILLISECONDS));
        this.days = (int) (TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS) + 1);
        return runtime;
    }

    /**
     * Набор времен со стандартным периодом
     *
     * @return
     */
    public List<Date> times() {
        return times(this.period);
    }

    /**
     * Набор времен со стандартным периодом
     *
     * @return
     */
    public List<Date> times(int period) {
        time(period);
        long start = DateUtils.round(beginTime, Calendar.MINUTE).getTime() + bar_size / 2;
        List<Date> list = new ArrayList();
        for (long time = start; time <= runtime + bar_size; time += bar_size) {
            list.add(new Date(time));
        }
        return list;
    }

    /**
     * Связи между точками
     *
     * @param points
     */
    public void setPointsPrevNext(Map<Long, Point> points) {
        if (points == null) {
            return;
        }
        Collection<Point> list = points.values();
        Point prev = list.iterator().next();
        for (Point point : list) { // проходим по всем точкам
            if (prev != point && prev.getId() != null) // существует prev и он записан в базу
            {
                if (point.getPrevId() == null) { // связи нет и надо ее создать
                    point.setPrevId(prev.getId());
                    manager.persist(point);
                }
                if (prev.getNextId() == null) { // связи нет и надо ее создать
                    prev.setNextId(point.getId());
                    manager.persist(prev);
                }
            }
            prev = point;
        }
    }

    /**
     * Расчет границ для цены на основе переданных баров
     */
    public void bars_scopes(List<Bar> bars) {
        if (bars.isEmpty()) {
            return;
        }
        this.max = Collections.max(bars, new Comparator<Bar>() {
            @Override
            public int compare(Bar first, Bar second) {
                double diff = first.getHigh().doubleValue() - second.getRate().doubleValue();
                if (diff > 0) {
                    return 1;
                }
                if (diff < 0) {
                    return -1;
                }
                return 0;
            }
        }).getHigh().doubleValue();
        this.min = Collections.min(bars, new Comparator<Bar>() {
            @Override
            public int compare(Bar first, Bar second) {
                double diff = first.getLow().doubleValue() - second.getLow().doubleValue();
                if (diff > 0) {
                    return 1;
                }
                if (diff < 0) {
                    return -1;
                }
                return 0;
            }
        }).getLow().doubleValue();
        this.range = max - min;
        if (print) {
            System.out.println("scopes(" + frame.getName() + ") bars[" + bars.size() + "] min: " + min + ", max: " + max + ", range: " + range);
        }
    }

    /**
     * Расчет границ для точек
     *
     * @param points набор точек
     */
    public void scopes(Collection<Point> points) {
        if (points.isEmpty()) {
            this.range = 0;
            return;
        }
        this.max = Collections.max(points, new Comparator<Point>() {
            @Override
            public int compare(Point first, Point second) {
                double diff = first.getValue().doubleValue() - second.getValue().doubleValue();
                if (diff > 0) {
                    return 1;
                }
                if (diff < 0) {
                    return -1;
                }
                return 0;
            }
        }).getValue().doubleValue();
        this.min = Collections.min(points, new Comparator<Point>() {
            @Override
            public int compare(Point first, Point second) {
                double diff = first.getValue().doubleValue() - second.getValue().doubleValue();
                if (diff > 0) {
                    return 1;
                }
                if (diff < 0) {
                    return -1;
                }
                return 0;
            }
        }).getValue().doubleValue();
        this.range = max - min;
        puts("scopes(" + frame.getName() + ") points[" + points.size()
                + "] min: " + String.format("%.5f", min)
                + ", max: " + String.format("%.5f", max)
                + ", range: " + String.format("%.5f", range));
    }

    /**
     * Расчет угла наклона между двумя точками в градусах, первая и вторая
     * производные. Для расчетов необходимо связь с предыдущей точкой.
     *
     * @param point текущая точка
     * @param prev предыдущая точка
     */
    public void derivatives(Point point, Point prev) {
//        Point prev = point.getPrev();
        // расстояние между точками в милисекундах
        double dx = point.getTime().getTime() - prev.getTime().getTime();
        // расстояние между точками в значениях
        double dy = point.getValue().doubleValue() - prev.getValue().doubleValue();
        double px = dx / viewport * XX; // размер длины треугольника в пикселях
        double py = dy / range * YY; // размер высоты треугольника в пикселях
        double derivative = py / px; // производная
        point.setDerivative(derivative);
        double secondDerivative = 0; // вторая производная
        if (prev.getDerivative() != null) {
            double sdy = derivative - prev.getDerivative();// разница в первой производной
            secondDerivative = sdy / px; // вторая производная
        }
        point.setSecondDerivative(secondDerivative);
        double alpha = Math.atan2(py, px); // угол в радианах на основе котангенса
        point.setAlpha(alpha);
        int angle = (int) Math.round(alpha / Math.PI * 180.0); // угол в градусах
        point.setAngleDegrees(angle);
        manager.persist(point);
        if (print) {
            System.out.print("{" + point.getSetting().getName() + "} ");
            System.out.printf("px: %.2f", px);
            System.out.printf(", py: %.2f", py);
            System.out.printf(", der: %.4f", derivative);
            System.out.printf(", 2der: %.4f", secondDerivative);
            System.out.printf(", alpha: %.4f", alpha);
            System.out.print(", angle: " + angle);
            System.out.println();
        }
    }

    /**
     * Расчет производных и нейроданных
     *
     * @param bars
     * @param points
     */
    public void params(Map<Long, Point> points) {
        scopes(points.values()); // вычисляем границы для точек
        if (this.range == 0.0) { // по каким-то причинам границ нет
            System.err.println("No vertical range found for derivatives");
            return;
        }
        Point prev = null;
        for (long time : points.keySet()) { // проходим по всем точкам
            Point point = points.get(time);
            if (prev != null && point.getAlpha() == null) { // вычислений не было произведено
                derivatives(point, prev);
//                puts(point);
            }
            prev = point;
        }
        transaction();
    }

    /**
     * Расчет разворота для точки, основываясь на данных из ближайшей истории
     * длиной SIGN_SIZE = 9
     *
     * @param ps входной набор точек
     */
//    public void turn(Point[] ps) {
//        for (int i = TURN_SIZE; i < ps.length; i++) {
//            Point point = ps[i];
//            if (point.getTurn() == null) {
//                int value = 0;
//                for (int j = 0; j < TURN_SIZE; j++) {
//                    Point p = ps[i - j];
//                    if (p.getSecondDerivative() != null) {
//                        int weight = TURN_WEIGHT[j];
//                        if (p.getSecondDerivative() > 0) {
//                            value += weight;
//                        } else if (p.getSecondDerivative() < 0) {
//                            value -= weight;
//                        }
//                    }
//                }
//                if (value > 9) {
//                    value = 9;
//                } else if (value < -9) {
//                    value = -9;
//                }
//                point.setTurn(value);
//                manager.persist(point);
//                if (print) {
//                    System.out.println("[" + point.getId() + "] turn: " + point.getTurn());
//                }
//            }
//        }
//    }
    /**
     * Расчет тренда для точки, основываясь на данных из ближайшей истории
     * длиной SIGN_SIZE = 9
     *
     * @param ps входной набор точек
     */
//    public void trend(Point[] ps) {
//        for (int i = SIGN_SIZE; i < ps.length; i++) {
//            Point point = ps[i];
//            if (point.getTrend() == null) {
//                int value = 0;
//                for (int j = 0; j < SIGN_SIZE; j++) {
//                    Point p = ps[i - j];
//                    if (p.getDerivative() != null) {
//                        if (p.getDerivative() > 0) {
//                            value += 1;
//                        } else if (p.getDerivative() < 0) {
//                            value -= 1;
//                        }
//                    }
//                }
//                point.setTrend(value);
//                manager.persist(point);
//                if (print) {
//                    System.out.println("[" + point.getId() + "] trend: " + point.getTrend());
//                }
//            }
//        }
//    }
    /**
     * Расчет простой скользящей средней
     *
     * @param a массив значений
     * @return
     */
    public double sma(double[] a) {
        double sum = 0.0;
        for (double v : a) {
            sum += v;
        }
        double sma = (sum / a.length);
        return sma;
    }

    /**
     * Расчет экспонециальной скользящей средней
     *
     * @param a массив значений
     * @param period период скользяще средней
     * @return
     */
    public double ema(double[] a, int period) {
        return 0;
    }

}


/*

 /**
 * Расчет знака точки
 *
 * @param ps входной набор точек
 public void sign(Point[] ps) {
 for (Point point : ps) {
 if (point.getSign() == null && point.getValue() != null) {
 if (point.getValue().doubleValue() > 0) {
 point.setSign(1);
 } else if (point.getValue().doubleValue() < 0) {
 point.setSign(-1);
 } else {
 point.setSign(0);
 }
 manager.persist(point);
 if (print) {
 System.out.println("["+point.getId()+"] sign: " + point.getSign());
 }
 }
 }
 }

 /**
 * Расчет угла наклона линии на основе последних точек
 *
 * @param points
 * @return
 protected int calcAngle(Map<Date, Point> points) {
 Point[] ps = points.values().toArray(new Point[points.size()]);
 // берем две последние опорные точки
 Point a = ps[ps.length - 2];
 Point b = ps[ps.length - 1];
 // считаем между ними сдвиг во времени
 double dx = b.getTime().getTime() - a.getTime().getTime();
 // считаем между ними сдвиг в цене
 double dy = b.getValue().doubleValue() - a.getValue().doubleValue();
 //       double derivative = dx.to_f / dy # производная
 return 0;
 }

 */

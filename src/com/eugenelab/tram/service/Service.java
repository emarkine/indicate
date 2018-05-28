/*
 * Copyright (c) 2004 - 2015, EugeneLab. All Rights Reserved
 */
package com.eugenelab.tram.service;

import com.eugenelab.tram.container.Points;
import com.eugenelab.tram.domain.Bar;
import com.eugenelab.tram.domain.State;
import com.eugenelab.tram.interfaces.Serviceable;
import com.eugenelab.tram.interfaces.Rateable;
import com.eugenelab.tram.domain.Frame;
import com.eugenelab.tram.domain.Fund;
import com.eugenelab.tram.domain.Host;
import com.eugenelab.tram.domain.Indicator;
import com.eugenelab.tram.domain.Setting;
import com.eugenelab.tram.domain.Point;
import com.eugenelab.tram.domain.ServiceData;
import com.eugenelab.tram.domain.Tick;
import com.eugenelab.tram.domain.Weight;
import com.eugenelab.tram.util.Calculator;
import static com.eugenelab.tram.util.Constant.FORMAT;
import static com.eugenelab.tram.util.Constant.FORMAT_MS;
import static com.eugenelab.tram.util.Constant.ONE_DAY;
import com.eugenelab.tram.util.Linker;
import com.eugenelab.tram.util.NeuroCalc;
import com.eugenelab.tram.util.Reader;
import com.eugenelab.tram.util.Utils;
import com.eugenelab.tram.util.Writer;
import com.ib.controller.ApiController;
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.persistence.EntityManager;
import org.apache.commons.lang3.time.DateUtils;

/**
 *
 * @author eugene
 */
public abstract class Service implements Serviceable {

    protected boolean print = false;
    protected boolean print_point = false;
    protected boolean print_ms = true;

    protected final EntityManager manager;
    protected final ApiController controller;
    protected final Reader reader;
    protected final Writer writer;
    protected final Calculator calculator;
    protected final NeuroCalc neuro;

//    protected long shift; // сдвиг во времени в ms
//    protected Map<Date, Tick> ticks = new TreeMap<>();
//    protected Map<Date, Bar> bars = new TreeMap<>();
//    protected Map<Date, Point> points = new TreeMap<>();
    protected final ServiceData data;
    protected final Indicator indicator;
    protected final Setting set;
    protected final Fund fund;
    protected Frame frame;
//    protected final long viewport;
//    protected final long bar_size;
    protected int period = 0;
//    protected long start;
    protected long start_time;
    protected long current_time;
    protected long virtual_time;
//    protected long runtime; // время старта сервиса
//    protected Date beginTime; // начальное время выборки
//    protected Date endTime; // конечное время выборки
    private boolean single = false;
    protected Bar last;
    protected Bar prev = null; // предшевствующий бар
    private Date date; // передана дата
    protected Date vtime; // передано вирутальное время
    protected long service_time;
    public boolean force = false; // принудительное создание баров/точек

    protected final List<Service> listUpdatableServices = new ArrayList<>();

//    public static final Points points;
//    private boolean virtual = false;
    public Service(ServiceData data, EntityManager manager) {
        this(data, manager, null);
    }

    public Service(ServiceData data, EntityManager manager, ApiController controller) {
//        print = true;
        this.data = data;
        this.set = data.getSetting();
        this.single = data.isSingle();
        this.indicator = set.getIndicator();
        this.manager = manager;
        this.controller = controller;
        this.fund = data.getFund();
        this.frame = data.getFrame();
        this.period = set.getPeriod();
        this.start_time = new Date().getTime();
        this.current_time = start_time;
        this.calculator = new Calculator(manager, fund, frame, set, period);
        this.neuro = new NeuroCalc(manager, calculator, fund, frame, set);
        this.reader = new Reader(manager, fund, frame, indicator, set, calculator);
        this.writer = new Writer(manager, fund, frame, indicator, set, calculator);
//        this.viewport = frame.getId() * Commander.VIEWPORT_SIZE; // размер окна в сек
//        long add = frame.getId() * (period + 1); // добавка времени с учетом frame 
//        this.shift = (viewport + add) * 1000L; // общий сдвиг в ms
//        this.bar_size = frame.getId() * 1000L; // размер бара в секундах
//        this.endTime = new Date(System.currentTimeMillis() + TIME_OFFSET); // время в данный момент с учетом локали 
//        this.beginTime = new Date(endTime.getTime() - shift); // время начала графика
    }

    @Override
    public String toString() {
        String s = "Service";
        if (isSingle()) {
            s += " single";
        }
        s += "\n " + data;
        return s;
    }

    /**
     * @return the date
     */
    @Override
    public Date getDate() {
        return date;
    }

    /**
     * @param date the date to set
     */
    @Override
    public void setDate(Date date) {
        this.date = date;
        Date beginTime = this.date;
        Date endTime = new Date(beginTime.getTime() + ONE_DAY - 1);
        calculator.time(beginTime, endTime, period);
    }

    @Override
    public boolean isDate() {
        return this.getDate() != null;
    }

    @Override
    public boolean isVirtual() {
        return (this.getDate() != null) && (this.vtime != null);
    }

    @Override
    public int getId() {
        return data.getId();
    }


    private void initUpdatable() {
        puts("Updatable Services:");
        for (Service service : Commander.services) {
            if (service.fund == this.fund && service != this && service.isActive()) { // 
                if (service.data.getTrigger() == this.data) {
                    listUpdatableServices.add(service);
                    puts(service.data.getName());
                }
            }
        }
    }

    @Override
    public void start() {
        initUpdatable();
        if (isDate()) { // передана дата
            Date beginDate = Utils.timeToDate(fund.getOpenTime(), getDate());
            Date endDate = new Date();
            if (!isDateToday()) { // передана не сегодняшняя дата
                endDate = Utils.timeToDate(fund.getCloseTime(), getDate());
            }
            calculator.time(beginDate, endDate, period);
        }
        setState("start");
    }

    @Override
    public void run() {
        service_time = System.currentTimeMillis();
        setState("run");
    }

    @Override
    public void stop() {
        setState("stop");
    }

    @Override
    public void setSingle(boolean single) {
        this.single = single;
    }

    @Override
    public boolean isSingle() {
        return single;
    }

    public void puts(Object o) {
        if (print) {
            if (o instanceof Date) {
                o = FORMAT_MS.format(o);
            }
            System.out.println(FORMAT_MS.format(new Date()) + " {" + set.getName() + "} " + " [" + frame.getName() + "] " + o);
        }
    }

    public void eputs(Object o) {
        if (print) {
            if (o instanceof Date) {
                o = FORMAT.format(o);
            }
            System.err.println(FORMAT_MS.format(new Date()) + " {" + set.getName() + "} " + o);
        }
    }

    public void sendError(Object o) {
        String message = FORMAT_MS.format(new Date()) + " {" + set.getName() + "} " + o;
        System.err.println(message);
        setState("error", message);
//        System.exit(-1);
    }

    /**
     * обновление виртуального времени
     */
    protected long time() {
        if (isVirtual()) { // мы находимся в режиме виртуального времени
            long diff = System.currentTimeMillis() - start_time;
            virtual_time = current_time + diff; // пересчитывае текущее время с учетом сдвига 
            service_time = virtual_time;
            puts(new Date(virtual_time));
//            calculator.virtualTime(new Date(virtual_time));
        } else if (isDate()) {
            service_time = current_time;
        } else {
            service_time = calculator.time();
        }
//        puts(calculator);
        return service_time;
    }

    protected void runtime() {
        runtime(null);
    }

    protected void runtime(String label) {
        if (print_ms || print) {
            long ms = System.currentTimeMillis() - calculator.runtime;
            System.out.print(FORMAT_MS.format(new Date()));
            if (label != null) {
                System.out.print(" [" + label + "]");
            }
            System.out.println(" {" + set.getName() + "} " + fund.getName() + " " + ms + " ms");
//            System.out.println(FORMAT_MS.format(new Date()) + " {" + data.getName() + "} " + ms + " ms");
        }
    }

    @Override
    public boolean isRefresh() {
        return data.getRefresh() != null;
    }

    @Override
    public boolean isUpdatable() {
        return data.getTrigger() != null;
    }

    @Override
    public int refreshPeriod() {
        return data.getRefresh();
    }

    protected double calc_ema(int period, double prev_ema, double rate) {
        double alpha = 2.0 / (period + 1.0);
        double value = (prev_ema + alpha * (rate - prev_ema));
        return value;
    }

    protected Point firstPoint(Rateable bar, Map<Long, Point> points, Setting set) {
        long t = bar.getTime().getTime();
        Point point = points.get(t);
        if (point == null) {
            point = writer.createPoint(t, bar.getRate().doubleValue(), set, null);
        }
        return point;

    }

    protected Map<Long, Point> ema() {
        Map<Long, Rateable> bars = reader.bars();
        return ema(set, bars, this.period);
    }

    protected Map<Long, Point> ema(int period) {
        return ema(set, reader.bars(period), period);
    }

    protected Map<Long, Point> ema(Map<Long, Rateable> bars) {
        return ema(set, bars, this.period);
    }

    protected Map<Long, Point> ema(Setting s) {
        return ema(s, reader.bars(period), s.getPeriod());
    }

    protected Map<Long, Point> ema(Setting set, Map<Long, Rateable> bars, int period) {
        if (bars.isEmpty()) {
            return null;
        }
        Rateable[] bs = bars.values().toArray(new Rateable[bars.size()]);
        Date t1 = bs[0].getTime();
        Date t2 = bs[bs.length - 1].getTime();
        Map<Long, Point> ps = reader.points(t1, t2, set);
        puts("fund: " + fund.getName() + ", bars: " + bars.size() + ", ps: " + ps.size());
        Point prev = firstPoint(bs[0], ps, set);
        if (prev != null) {
            for (int i = 1; i < bs.length; ++i) {
                Rateable bar = bs[i];
                long time = bar.getTime().getTime();
                Point point = ps.get(time);
                if (point == null) {
                    double prev_ema = prev.getValue().doubleValue();
                    double rate = bar.getRate().doubleValue();
                    double ema = calc_ema(period, prev_ema, rate);
                    point = writer.createPoint(time, ema, set, prev);
                    point.setService(data);
                    ps.put(time, point);
//                } else if ( bar instanceof Bar && i == (bs.length-1)) {// last Rateable element
//                    Bar b = reader.bar(new Date(t)); // перечитываем бар
//                    double prev_ema = prev.getValue().doubleValue();
//                    double rate = b.getClose().doubleValue();
//                    double ema = calc_ema(period, prev_ema, rate);
//                    writer.updatePoint(point, BigDecimal.valueOf(ema), prev);
//                    puts("bar: " + b);
//                    puts("last: " + point);
                }
                prev = point;
            }
        }
        return ps;
    }

//    protected boolean isTicks() {
//        return setting.getFrame() != null && setting.getFrame().getId() == 1;
//    }
    // расчет значения простой скользящей средней
    protected double calc_sma(int period, int index, Rateable[] bs) {
//        System.out.println("period: " + period + ", index: " + index);
        double sum = 0.0;
//        int n = 0;
        for (int i = (index - period + 1); i <= index; ++i) {
            Rateable bar = bs[i];
            if (bar != null && bar.getRate() != null) {
                double rate = bs[i].getRate().doubleValue();
                sum += rate;
            }
//            System.out.println("i: " + i + ", rate: " + rate + ", sum: " + sum);
//            n += 1;
        }
        double sma = (sum / period);
//        System.out.println("n: " + n + ", sma: " + sma);
        return sma;
    }

    protected Map<Long, Point> sma() {
        return sma(set);
    }

    protected Map<Long, Point> sma(Setting s) {
        return sma(s, this.period);
    }

    protected Map<Long, Point> sma(Setting s, int period) {
        Map<Long, Rateable> rb = reader.bars(period);
        Rateable[] bs = rb.values().toArray(new Rateable[rb.size()]);
        return sma(s, bs, period, 0.1);
    }

    protected Map<Long, Point> sma(Setting s, Rateable[] bs) {
        return sma(s, bs, this.period, 0.1);
    }

    protected Map<Long, Point> sma(Setting s, Rateable[] bs, int period, double factor) {
        if (bs.length <= 2) {
            return null;
        }
        Date t1 = bs[0].getTime();
        Date t2 = bs[bs.length - 1].getTime();
        Map<Long, Point> ps = reader.points(t1, t2, s);
        puts("fund: " + fund.getName() + ", bars: " + bs.length + ", points: " + ps.size());
        Point prev = ps.get(bs[0].getTime().getTime());
        if (bs.length > period) {
            prev = reader.prev(bs[period].getTime());
        }
        for (int index = period; index < bs.length; ++index) {
//        for (Bar bar : bars.values()) {
            Rateable bar = bs[index];
            long time = bar.getTime().getTime();
            Point point = ps.get(time);
            if (point == null) {
                double sma = calc_sma(period, index, bs);
                point = writer.createPoint(time, sma, s, prev);
                point.setService(data);
                ps.put(time, point);
            }
            prev = point;
        }
        return ps;
    }

    /**
     * Создание связей prev и next для точек Вычисление производных и данных
     * входных нейронов Запись в базу
     *
     * @param points набор точек
     */
    protected void calculation(Map<Long, Point> points) {
        if (points != null && points.size() > 0) {
            Linker.setPointsPrevNext(manager,points);
            calculator.params(points);
            Map<Long, Rateable> bars = reader.bars();
            neuro.edge(points, bars);
        }
    }

    /**
     * Внесение изменений в базу данных для всех записей одновременно
     */
    protected void transaction() {
//        try {
        if (!manager.getTransaction().isActive()) {
            manager.getTransaction().begin();
        }
        manager.getTransaction().commit();
//        } catch (Exception e) {
//            eputs(e);
//        }
    }

    @Override
    public boolean isActive() {
        return data.isActive();
    }

    /**
     * Проверка на то, что дата в date сегодняшняя
     *
     * @return true если дата сегодняшняя
     */
    protected boolean isDateToday() {
        if (!isDate()) { // дата вообще не была передана
            return false;
        }
        Date todayDate = new Date();
        return DateUtils.isSameDay(getDate(), todayDate);
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//        return sdf.format(date).equals(sdf.format(todayDate));
    }

    /**
     * Проверка на выходные и время открытия фонда
     *
     * @return true если фонд активен
     */
    protected boolean checkTime() {
        Calendar calendar = Calendar.getInstance();
        int dow = calendar.get(Calendar.DAY_OF_WEEK);
        if (dow == Calendar.SATURDAY || dow == Calendar.SUNDAY) {
            return false;
        }
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        long current_time = calendar.getTime().getTime();
        calendar.setTime(this.fund.getOpenTime());
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        long open_time = calendar.getTime().getTime();
        calendar.setTime(this.fund.getCloseTime());
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        long close_time = calendar.getTime().getTime();
        if (current_time < open_time) {
            return false;
        }
        if (current_time > close_time) {
            return false;
        }
        return true;
    }

    @Override
    public boolean isAsynchronous() {
        return false;
    }

//    @Override
//    public void update(Tick tick) {
//        for (Updatable service : listUpdatableServices) {
//            service.update(tick);
//        }
//    }
    @Override
    public void updateSelf(Bar bar) {
        run();
    }

    @Override
    public void updateTrigger(Bar bar) {
        for (Service service : listUpdatableServices) {
            service.updateSelf(bar);
        }
    }

    @Override
    public void setHost(Host host) {
       data.setHost(host);
       writer.updateData(data);
    }
    
    @Override
    public void setState(String status) {
        setState(status,null);
    }

    @Override
    public void setState(String status, String message) {
       State state = getState();
       state.setName(status);
       state.setMessage(message);
       state.setTime(Utils.time());
       state.setService(data);
       writer.updateState(state);
    }

 
    @Override
    public State getState() {
        return reader.state(data);
    }

}

/* SELLAR

 protected Map<Date, Point> ema(String name, Map<Date, Rateable> bars, int period) {
 if (bars.isEmpty()) {
 return null;
 }
 Rateable[] bs = bars.values().toArray(new Rateable[bars.size()]);
 Date t1 = bs[0].getTime();
 Date t2 = bs[bs.length - 1].getTime();
 Map<Date, Point> ps = reader.points(t1, t2, name);
 if (print) {
 System.out.println("fund: " + fund.getName() + ", bars: " + bars.size() + ", ps: " + ps.size());
 }
 Point prev = firstPoint(bars, ps, name);
 if (prev != null) {
 for (Rateable bar : bars.values()) {
 Date time = bar.getTime();
 Point point = ps.get(time);
 if (point == null || time == t2) {
 double prev_ema = prev.getValue().doubleValue();
 if (bar.getRate() != null) {
 double rate = bar.getRate().doubleValue();
 double ema = calc_ema(period, prev_ema, rate);
 if (point == null) { // new one
 point = createPoint(bar.getTime(), BigDecimal.valueOf(ema), name);
 //                            point.setAngle(angle(point, prev));
 ps.put(time, point);
 } else { // last point
 if (!isTicks()) { // not for ticks
 point.setValue(ema);
 //                                point.setAngle(angle(point, prev));
 if (print) {
 System.out.println("Last Point: " + point);
 }
 }
 }
 }
 }
 if (point != null) {
 prev = point;
 }
 }
 transaction();
 }
 return ps;
 }
 protected Map<Date, Point> sma(String name, Rateable[] bs, int period) {
 Date t1 = bs[0].getTime();
 Date t2 = bs[bs.length - 1].getTime();
 Map<Date, Point> ps = reader.points(t1, t2, name);
 if (print) {
 System.out.println("fund: " + fund.getName() + ", bars: " + bs.length + ", points: " + ps.size());
 }
 for (int index = bs.length - 1; index > period; --index) {
 //        for (Bar bar : bars.values()) {
 Rateable bar = bs[index];
 Date time = bar.getTime();
 Point point = ps.get(time);
 if (point == null || time == t2) {
 double sma = calc_sma(period, index, bs);
 if (point == null) { // new one
 point = createPoint(time, sma, name);
 ps.put(time, point);
 } else { // last point
 if (!isTicks()) { // not for ticks
 point.setValue(sma);
 if (print) {
 System.out.println("Last Point: " + point);
 }
 }
 }
 }
 }
 transaction();
 return ps;
 }
   
     
 */

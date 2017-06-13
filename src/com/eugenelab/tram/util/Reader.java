/*
 * Copyright (c) 2004 - 2015, EugeneLab. All Rights Reserved
 */
package com.eugenelab.tram.util;

import com.eugenelab.tram.database.Bar;
import com.eugenelab.tram.database.Datum;
import com.eugenelab.tram.database.Edge;
import com.eugenelab.tram.database.Frame;
import com.eugenelab.tram.database.Fund;
import com.eugenelab.tram.database.Host;
import com.eugenelab.tram.database.Indicator;
import com.eugenelab.tram.database.Nerve;
import com.eugenelab.tram.database.Neuron;
import com.eugenelab.tram.database.Setting;
import com.eugenelab.tram.database.Point;
import com.eugenelab.tram.database.Response;
import com.eugenelab.tram.database.ServiceData;
import com.eugenelab.tram.database.Tick;
import com.eugenelab.tram.database.Weight;
import com.eugenelab.tram.interfaces.Rateable;
import com.eugenelab.tram.service.Commander;
import static com.eugenelab.tram.service.Commander.ARG;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.persistence.EntityManager;
import javax.persistence.Query;

/**
 *
 * @author eugene
 */
public final class Reader {

    private final EntityManager manager;
    private final Fund fund;
    private final Frame frame;
    private final Indicator indicator;
    private final Setting setting;
    private final Calculator calculator;
    public Map<Long, Rateable> bars;
    public Map<Long, Rateable> ticks;
    public Map<Long, Point> points;
    public Tick lastTick;
    public Bar lastBar;
    public Point lastPoint;

    public static Frame frame1m;

    public Reader(EntityManager manager, Fund fund, Frame frame, Indicator indicator, Setting setting, Calculator calculator) {
        this.manager = manager;
        this.fund = fund;
        this.frame = frame;
        this.indicator = indicator;
        this.setting = setting;
        this.calculator = calculator;
        this.bars = new TreeMap<>();
        if (frame1m == null) {
            frame1m = frame("1m");
        }
    }

    public static Host host(EntityManager manager, String name) {
        Query query = manager.createQuery("SELECT o FROM Host o WHERE o.name = ?1");
        query.setParameter(1, name);
        return (Host) query.getSingleResult();
    }

//    public static ServiceData singleService(EntityManager manager, String name) {
//        return service(manager,name);
//    }
    public static ServiceData single_service(EntityManager manager) {
        String single = Commander.ARG.getItem("single");
        String frame = Commander.ARG.getItem("frame");
        String fund = Commander.ARG.getItem("fund");
        if (single != null && frame != null && fund != null) {
            return service_by_set(manager, frame, fund, single);
        } else {
            return service(manager, single);
        }
    }

    public static ServiceData service(EntityManager manager, String name) {
        Query query = manager.createNamedQuery("ServiceData.findByName");
        query.setParameter("name", name);
        return (ServiceData) query.getSingleResult();
    }

    public static List<ServiceData> services_by_group(EntityManager manager, String group) {
        Query query = manager.createQuery("SELECT o FROM ServiceData o WHERE o.ngroup = ?1 ORDER BY o.position");
        query.setParameter(1, group);
        return (List<ServiceData>) query.getResultList();
    }

    public static List<ServiceData> services_by_name(EntityManager manager, String name) {
        return services_by(manager, "name", name);
    }

    public static List<ServiceData> services_by(EntityManager manager, String field, String value) {
        Query query = manager.createQuery("SELECT o FROM ServiceData o WHERE o." + field + " = ?1");
        query.setParameter(1, value);
        return (List<ServiceData>) query.getResultList();
    }

    public static ServiceData service(EntityManager manager, String name, String frame_name) {
        Frame frame = frame(manager, frame_name);
        Query query = manager.createQuery("SELECT o FROM ServiceData o WHERE o.name = ?1 AND o.frame = ?2");
        query.setParameter(1, name);
        query.setParameter(2, frame);
        return (ServiceData) query.getSingleResult();
    }

    public static ServiceData service(EntityManager manager, String set_name, String frame_name, String fund_name) {
        Frame frame = frame(manager, frame_name);
        Fund fund = fund(manager, fund_name);
        Setting set = set(manager, set_name);
        Query query = manager.createQuery("SELECT o FROM ServiceData o WHERE o.setting = ?1 AND o.frame = ?2 AND o.fund = ?3");
        query.setParameter(1, set);
        query.setParameter(2, frame);
        query.setParameter(3, fund);
        return (ServiceData) query.getSingleResult();
    }

    public static ServiceData service(EntityManager manager, String name, String frame_name, String fund_name, String set_name) {
        Frame frame = frame(manager, frame_name);
        Fund fund = fund(manager, fund_name);
        Setting set = set(manager, set_name);
        Query query = manager.createQuery("SELECT o FROM ServiceData o WHERE o.name = ?1 AND o.frame = ?2 AND o.fund = ?3 AND o.setting = ?4");
        query.setParameter(1, name);
        query.setParameter(2, frame);
        query.setParameter(3, fund);
        query.setParameter(4, set);
        return (ServiceData) query.getSingleResult();
    }

    public static ServiceData service_by_set(EntityManager manager, String frame_name, String fund_name, String set_name) {
        Frame frame = frame(manager, frame_name);
        Fund fund = fund(manager, fund_name);
        Setting set = set(manager, set_name);
        Query query = manager.createQuery("SELECT o FROM ServiceData o WHERE o.frame = ?1 AND o.fund = ?2 AND o.setting = ?3");
        query.setParameter(1, frame);
        query.setParameter(2, fund);
        query.setParameter(3, set);
        List<ServiceData> list = query.getResultList();
        if (list.isEmpty()) {
            System.err.println("Service not found set: " + set + ", fund: " + fund + " frame: " + frame);
            return null;
        } else {
            return list.get(0);
        }
//        return (ServiceData) query.getSingleResult();
    }

    public static Frame frame(EntityManager manager, String name) {
        Query query = manager.createQuery("SELECT o FROM Frame o WHERE o.name = ?1");
        query.setParameter(1, name);
        return (Frame) query.getSingleResult();
    }

    public Frame frame(String name) {
        Query query = manager.createQuery("SELECT o FROM Frame o WHERE o.name = ?1");
        query.setParameter(1, name);
        return (Frame) query.getSingleResult();
    }

    public static Edge edge(EntityManager manager, Setting set) {
        Query query = manager.createQuery("SELECT o FROM Edge o WHERE o.setting = ?1");
        query.setParameter(1, set);
        List<Edge> list = query.getResultList();
        if (list.isEmpty()) {
            System.err.println("Edge not found for set: " + set.getName());
            return null;
        } else {
            return list.get(0);
        }
    }

    public static Neuron neuron(EntityManager manager, Edge edge, String type) {
        Query query = manager.createQuery("SELECT o FROM Neuron o WHERE o.edge = ?1 AND o.type = ?2");
        query.setParameter(1, edge);
        query.setParameter(2, type);
        List<Neuron> list = query.getResultList();
        if (list.isEmpty()) {
            System.err.println("Neuron " + type + " not found edge: " + edge.getName());
            return null;
        } else {
            return list.get(0);
        }
    }

    public static Nerve nerve(EntityManager manager, Neuron source, Neuron recipient) {
        Query query = manager.createQuery("SELECT o FROM Nerve o WHERE o.source = ?1 AND o.recipient = ?2");
        query.setParameter(1, source);
        query.setParameter(2, recipient);
        List<Nerve> list = query.getResultList();
        if (list.isEmpty()) {
            System.err.println("Nerve not found sourse[" + source.getId() + "] -> recipient["+ recipient.getId() + "]");
            return null;
        } else {
            return list.get(0);
        }
    }
    
    public static Response response(EntityManager manager, Nerve nerve, Fund fund, Frame frame) {
        Query query = manager.createQuery("SELECT o FROM Response o WHERE o.nerve = ?1 AND o.fund = ?2 AND o.frame = ?3");
        query.setParameter(1, nerve);
        query.setParameter(2, fund);
        query.setParameter(3, frame);
        List<Response> list = query.getResultList();
        if (list.isEmpty()) {
            System.err.println("Response not found nerve: " + nerve);
            return null;
        } else {
            return list.get(0);
        }
    }

    public static List<Frame> frames(EntityManager manager) {
        Query query = manager.createNamedQuery("Frame.findAll");
        return query.getResultList();
    }

    public List<Frame> frames() {
        Query query = manager.createNamedQuery("Frame.findAll");
        return query.getResultList();
    }

    public static Fund fund(EntityManager manager, String param) {
        Query query = manager.createQuery("SELECT o FROM Fund o WHERE o.name = ?1");
        query.setParameter(1, param);
        return (Fund) query.getSingleResult();
    }

    public static Fund fund_id(EntityManager manager, String param) {
        int id = Integer.parseInt(param);
        if (id != 0) {
            Query query = manager.createQuery("SELECT o FROM Fund o WHERE o.id = ?1");
            query.setParameter(1, id);
            return (Fund) query.getSingleResult();
        } else {
            Query query = manager.createQuery("SELECT o FROM Fund o WHERE o.name = ?1");
            query.setParameter(1, param);
            return (Fund) query.getSingleResult();
        }
    }

    public static Setting set(EntityManager manager, String name) {
        Query query = manager.createQuery("SELECT o FROM Setting o WHERE o.name = ?1");
        query.setParameter(1, name);
        return (Setting) query.getSingleResult();
    }

    public Setting set(String name) {
        return Reader.set(manager, name);
    }

    public Weight weight(Setting set) {
        Query query = manager.createQuery("SELECT o FROM Weight o WHERE o.setting = ?1");
        query.setParameter(1, set);
        return (Weight) query.getSingleResult();
    }

    /**
     * Чтение последнего бара
     *
     * @return считанный бар или null, если он не найден
     */
//    public Bar bar() {
//        Query query = manager.createQuery("SELECT o FROM Bar o WHERE o.fund = ?1 AND o.frame = ?2");
//        query.setParameter(1, fund);
//        query.setParameter(2, frame);
//        List<Bar> list = query.getResultList();
//        if (list.isEmpty()) {
//            return null;
//        } else {
//            return list.get(list.size()-1);
//        }
//    }
    /**
     * Поиск подходящего бара на основе переданного времени
     *
     * @param time время в промежутке open_time и close_time
     * @return считанный бар или null, если он не найден
     */
    public Bar bar(Date time) {
        Query query = manager.createQuery("SELECT o FROM Bar o WHERE o.fund = ?1 AND o.frame = ?2 AND o.openTime < ?3 AND o.closeTime >= ?4");
        query.setParameter(1, fund);
        query.setParameter(2, frame);
        query.setParameter(3, time);
        query.setParameter(4, time);
        List<Bar> list = query.getResultList();
        if (list.isEmpty()) {
            return null;
        } else {
            return list.get(0);
        }
    }

    /**
     * Поиск подходящего бара на основе переданного времени и frame
     *
     * @param time время в промежутке open_time и close_time
     * @param frame таймфрейм для бара
     * @return считанный бар или null, если он не найден
     */
    public Bar bar(Date time, Frame frame) {
        Query query = manager.createQuery("SELECT o FROM Bar o WHERE o.fund = ?1 AND o.frame = ?2 AND o.openTime < ?3 AND o.closeTime >= ?4");
        query.setParameter(1, fund);
        query.setParameter(2, frame);
        query.setParameter(3, time);
        query.setParameter(4, time);
        List<Bar> list = query.getResultList();
        if (list.isEmpty()) {
            return null;
        } else {
            return list.get(0);
        }
    }

    /**
     * Считываем бар который находтся пред баром для текущего времени
     *
     * @param time текущее время
     * @return bar если они есть или null
     */
    public Bar prevBar(Date time) {
        Bar bar = bar(time);
        if (bar != null && bar.getPrev_id() != null) {
            Query query = manager.createQuery("SELECT o FROM Bar o WHERE o.id = ?1");
            query.setParameter(1, bar.getPrev_id());
            return (Bar) query.getSingleResult();
        } else {
            time = new Date(time.getTime() - frame.getId() * 1000L);
            Query query = manager.createQuery("SELECT o FROM Bar o WHERE o.fund = ?1 AND o.frame = ?2 AND o.openTime < ?3 AND o.closeTime >= ?4");
            query.setParameter(1, fund);
            query.setParameter(2, frame);
            query.setParameter(3, time);
            query.setParameter(4, time);
            List<Bar> list = query.getResultList();
            if (list.isEmpty()) {
                return null;
            } else {
                return list.get(0);
            }
        }
    }

    private Bar createBar(com.ib.controller.Bar ib_bar, Date time) {
        this.manager.getTransaction().begin();
        Date open_time = new Date(time.getTime() - calculator.bar_size / 2);
        Date close_time = new Date(time.getTime() + calculator.bar_size / 2);
        Bar bar = new Bar();
        bar.setFund(fund);
        bar.setFrame(frame);
        bar.setOpenTime(open_time);
        bar.setCloseTime(close_time);
        bar.setTime(time);
        bar.setOpen(ib_bar.open());
        bar.setClose(ib_bar.close());
        bar.setHigh(ib_bar.high());
        bar.setLow(ib_bar.low());
        // среднее арифметическое из 4 величин: open, close, high, low
        double rate = (ib_bar.open() + ib_bar.close() + ib_bar.high() + ib_bar.low()) / 4.0;
        bar.setRate(rate);
        this.manager.persist(bar);
        this.manager.getTransaction().commit();
//        System.out.print('.');
//        System.out.println(bar);
        return bar;
    }

    /**
     * Считываем имеющиеся бары для периода окна в виде массива
     *
     * @return
     */
    public Bar[] abars() {
//        Date addEndTime = new Date(calculator.endTime.getTime() + calculator.bar_size);
//        Map<Long, Rateable> bs = bars(calculator.beginTime, addEndTime);
        Map<Long, Rateable> bs = bars(calculator.beginTime, calculator.endTime);
        return bs.values().toArray(new Bar[bs.size()]);
    }

    public Map<Long, Rateable> bars() {
//        calculator.time();
        return bars(calculator.beginTime, calculator.endTime);
    }

    public Map<Long, Rateable> bars(int period) {
//        calculator.time(period);
        return bars(calculator.beginTime, calculator.endTime);
    }

    public Map<Long, Rateable> bars(Frame frm) {
//        calculator.time(period);
        return bars(calculator.beginTime, calculator.endTime, frm);
    }

    public Map<Long, Rateable> bars(Date beginTime, Date endTime) {
        return bars(beginTime, endTime, frame);
    }

    public Bar[] abars(Date beginTime, Date endTime, Frame frm) {
        Map<Long, Rateable> bs = bars(beginTime, endTime, frm);
        return bs.values().toArray(new Bar[bs.size()]);
    }

    /**
     * Чтение баров
     *
     * @param beginTime начальное время
     * @param endTime конечное время
     * @param frm таймфрейм
     * @return бары отсортированые по времени
     */
    public Map<Long, Rateable> bars(Date beginTime, Date endTime, Frame frm) {
        Query query = manager.createQuery("SELECT b FROM Bar b WHERE b.fund = ?1 AND b.frame = ?2 "
                + "AND b.time >= ?3 AND b.time <= ?4");
        query.setParameter(1, fund);
        query.setParameter(2, frm);
        query.setParameter(3, beginTime);
        query.setParameter(4, endTime);
        List<Bar> list = query.getResultList();
        if (!list.isEmpty()) {
            lastBar = list.get(list.size() - 1);
        }
//        calculator.scopes(list);
        this.bars = new TreeMap<>();
        for (Bar bar : list) {
            bars.put(bar.getTime().getTime(), bar);
        }
        return bars;
    }

    /**
     * Считываем ближайший тик из базы
     *
     * @param time время
     * @return найденный тик или null
     */
    public Tick tick(Date time) {
        Query query = manager.createQuery("SELECT o FROM Tick o WHERE o.fund = ?1 AND o.time <= ?2");
        query.setParameter(1, fund);
        query.setParameter(2, time);
        List<Tick> list = query.getResultList();
        if (list.isEmpty()) {
            return null;
        } else {
            return list.get(0);
        }
    }

    /**
     * Поиск тика ближе всего находящегося по времени
     *
     * @param time время поискм
     * @return найденный тик или null
     */
    public Tick findTick(Long time) {
        if (ticks.isEmpty()) {
            return null;
        }
        if (ticks.containsKey(time)) {
            return (Tick) ticks.get(time);
        } else {
            Tick[] list = ticks.values().toArray(new Tick[ticks.size()]);
            Tick prev = list[0];
            if (time <= prev.getTime().getTime()) {
                return prev;
            }
            for (int i = 1; i < list.length - 1; i++) {
                long t1 = prev.getTime().getTime();
                long t2 = list[i].getTime().getTime();
                if (time > t1 && time <= t2) {
                    return prev;
                }
                prev = list[i];
            }
            return list[list.length - 1];
        }

//        return (Tick) findRateable(time, ticks);
    }

    /**
     * Поиск бара ближе всего находящегося по времени
     *
     * @param time время поискм
     * @return найденный бар или null
     */
    public Bar findBar(Long time) {
        return (Bar) findRateable(time, bars);
    }

    /**
     * Поиск елемента ближе всего находящегося по времени
     *
     * @param time время поиска
     * @param map карта, где искать
     * @return найденный тик или null
     */
    public Rateable findRateable(Long time, Map<Long, Rateable> map) {
        if (map.containsKey(time)) {
            return map.get(time);
        } else {
            List<Long> list = new ArrayList<>(map.keySet());
            Collections.reverse(list);
            for (Long l : list) {
                if (time > l) {
                    return map.get(l);
                }
            }
            return map.get(list.get(0));
        }
    }

    public Map<Long, Rateable> ticks() {
//        calculator.time();
        return ticks(calculator.beginTime, calculator.endTime);
    }

    /**
     * Чтение тиков
     *
     * @param beginTime начальное время
     * @param endTime конечное время
     * @return тики отсортированые по времени
     */
    public Map<Long, Rateable> ticks(Date beginTime, Date endTime) {
        Query query = manager.createQuery("SELECT o FROM Tick o WHERE o.fund = ?1 AND o.time >= ?2 AND o.time <= ?3");
        query.setParameter(1, fund);
        query.setParameter(2, beginTime);
        query.setParameter(3, endTime);
        List<Tick> list = query.getResultList();
        if (!list.isEmpty()) {
            lastTick = list.get(list.size() - 1);
        }
        this.ticks = new TreeMap<>();
        for (Tick tick : list) {
            ticks.put(tick.getTime().getTime(), tick);
        }
//        this.max = Collections.max(list).getRate().doubleValue();
//        this.min = Collections.min(list).getRate().doubleValue();
//        this.range = max - min;
//        System.out.println("tick range:"+range);
        return ticks;
    }

    public Point point(Date time) {
        Query query = manager.createQuery("SELECT o FROM Point o WHERE o.fund = ?1 AND o.frame = ?2"
                + " AND o.setting = ?3 AND o.time = ?4");
        query.setParameter(1, fund);
        query.setParameter(2, frame);
        query.setParameter(3, setting);
        query.setParameter(4, time);
        List<Point> list = query.getResultList();
        if (list.isEmpty()) {
            return null;
        } else {
            return list.get(0);
        }
    }

    public Point prev(Date time) {
        Query query = manager.createQuery("SELECT o FROM Point o WHERE o.fund = ?1 AND o.frame = ?2"
                + " AND o.setting = ?3 AND o.time < ?4");
        query.setParameter(1, fund);
        query.setParameter(2, frame);
        query.setParameter(3, setting);
        query.setParameter(4, time);
        List<Point> list = query.getResultList();
        if (list.isEmpty()) {
            return null;
        } else {
            return list.get(0);
        }
    }
    
    public Point last() {
        return last(setting);
    }

    public Point last(Setting set) {
        Query query = manager.createQuery("SELECT o FROM Point o WHERE o.fund = ?1 AND o.frame = ?2"
                + " AND o.setting = ?3");
        query.setParameter(1, fund);
        query.setParameter(2, frame);
        query.setParameter(3, set);
        List<Point> list = query.getResultList();
        if (list.isEmpty()) {
            return null;
        } else {
            return list.get(list.size() - 1);
        }
    }

    /**
     * Чтение точек по имени набора
     *
     * @param set_name имя набора точек
     * @return
     */
    public List<Point> listPoints(String set_name) {
        Setting set = set(manager, set_name);
        return listPoints(calculator.beginTime, calculator.endTime, set);
    }

    /**
     * Чтение точек из набора
     *
     * @param набор точек
     * @return
     */
    public List<Point> listPoints(Setting set) {
//        calculator.time();
        return listPoints(calculator.beginTime, calculator.endTime, set);
    }

    /**
     * Чтение точек из набора
     *
     * @param beginTime начальное время
     * @param endTime конечное время
     * @param name имя набора точек
     * @return
     */
    public List<Point> listPoints(Date beginTime, Date endTime, Setting set) {
        Query query = manager.createQuery("SELECT p FROM Point p WHERE "
                + "p.setting = ?1 "
                + "AND p.fund = ?2 "
                + "AND p.frame = ?3 "
                + "AND p.time >= ?4 AND p.time <= ?5");
        query.setParameter(1, set);
        query.setParameter(2, fund);
        query.setParameter(3, frame);
        query.setParameter(4, beginTime);
        query.setParameter(5, endTime);
        return query.getResultList();
    }

    /**
     * Чтение точек из набора из занесение их в Map отсортированы по времени
     *
     * @return считанные точки отсортированые по времени
     */
    public Map<Long, Point> points() {
//        calculator.time();
        return points(calculator.beginTime, calculator.endTime, setting);
    }

    /**
     * Чтение точек по имени набора и занесение их в Map отсортированы по
     * времени
     *
     * @param set_name имя набора
     * @return считанные точки отсортированые по времени
     */
    public Map<Long, Point> points(String set_name) {
        Setting set = set(manager, set_name);
        return points(calculator.beginTime, calculator.endTime, set);
    }

    /**
     * Чтение точек из набора из занесение их в Map отсортированы по времени
     *
     * @param name
     * @return считанные точки отсортированые по времени
     */
    public Map<Long, Point> points(Setting set) {
//        calculator.time();
        return points(calculator.beginTime, calculator.endTime, set);
    }

    /**
     * Чтение точек из набора из занесение их в Map отсортированы по времени
     *
     * @param beginTime начальное время
     * @param endTime конечное время
     * @return считанные точки отсортированые по времени
     */
    public Map<Long, Point> points(Date beginTime, Date endTime) {
        return points(beginTime, endTime, setting);
    }

    /**
     * Чтение точек из набора из занесение их в Map отсортированы по времени
     *
     * @param beginTime начальное время
     * @param endTime конечное время
     * @param set сета
     * @return считанные точки отсортированые по времени
     */
    public Map<Long, Point> points(Date beginTime, Date endTime, Setting set) {
        List<Point> list = listPoints(beginTime, endTime, set);
        if (!list.isEmpty()) {
            lastPoint = list.get(list.size() - 1);
        }
        this.points = new TreeMap<>();
        for (Point point : list) {
            points.put(point.getTime().getTime(), point);
        }
        return points;
    }

    /**
     * Чтение нейроданных из набора
     *
     * @param manager базасчитыватель
     * @param fund актив
     * @param frame частота
     * @param neuron нейрон
     * @param beginTime начальное время
     * @param endTime конечное время
     * @return считанные данные
     */
    public static List<Datum> listData(EntityManager manager, Fund fund, Frame frame, Neuron neuron, Date beginTime, Date endTime) {
        Query query = manager.createQuery("SELECT o FROM Datum o WHERE "
                + "o.neuron= ?1 "
                + "AND o.fund = ?2 "
                + "AND o.frame = ?3 "
                + "AND o.time >= ?4 AND o.time <= ?5");
        query.setParameter(1, neuron);
        query.setParameter(2, fund);
        query.setParameter(3, frame);
        query.setParameter(4, beginTime);
        query.setParameter(5, endTime);
        return query.getResultList();
    }

    /**
     * Чтение нейроданных из набора из занесение их в Map отсортированы по
     * времени
     *
     * @param manager базасчитыватель
     * @param fund актив
     * @param frame частота
     * @param neuron нейрон
     * @param beginTime начальное время
     * @param endTime конечное время
     * @return считанные данные отсортированые по времени
     */
    public static Map<Long, Datum> data(EntityManager manager, Fund fund, Frame frame, Neuron neuron, Date beginTime, Date endTime) {
        List<Datum> list = listData(manager, fund, frame, neuron, beginTime, endTime);
        Map<Long, Datum> data = new TreeMap<>();
        for (Datum d : list) {
            data.put(d.getTime().getTime(), d);
        }
        return data;
    }
    
    /**
     * Чтение одного данного 
     *
     * @param manager базасчитыватель
     * @param fund актив
     * @param frame частота
     * @param neuron нейрон
     * @param time время
     * @return считанные данные отсортированые по времени
     */
    public static Datum datum(EntityManager manager, Fund fund, Frame frame, Neuron neuron, Date time) {
        Query query = manager.createQuery("SELECT o FROM Datum o WHERE "
                + "o.neuron= ?1 "
                + "AND o.fund = ?2 "
                + "AND o.frame = ?3 "
                + "AND o.time = ?4");
        query.setParameter(1, neuron);
        query.setParameter(2, fund);
        query.setParameter(3, frame);
        query.setParameter(4, time);
        List<Datum> list = query.getResultList();
        if (list.isEmpty()) {
            return null;
        } else {
            return list.get(0);
        }
    }
    
    /**
     * Чтение нервов для нейрона
     *
     * @param manager базасчитыватель
     * @param neuron нейрон
     * @return считанные данные
     */
    public static List<Nerve> nerves(EntityManager manager, Neuron neuron) {
        Query query = manager.createQuery("SELECT o FROM Nerve o WHERE o.recipient = ?1");
        query.setParameter(1, neuron);
        return query.getResultList();
    }

}
// SELLAR
//    private Bar createBar(com.ib.controller.Bar ib_bar, Date time_utc) {
//        this.manager.getTransaction().begin();
//        Date open_time = time_utc;
//        Date close_time = new Date(time_utc.getTime() + this.frame.getId() * 1000 - 1);
//        Date time = new Date(time_utc.getTime() + this.frame.getId() * 1000 / 2);
//        Bar bar = new Bar();
//        bar.setFund(fund);
//        bar.setFrame(frame);
//        bar.setOpenTime(open_time);
//        bar.setCloseTime(close_time);
//        bar.setTime(time);
//        bar.setOpen(ib_bar.open());
//        bar.setClose(ib_bar.close());
//        bar.setHigh(ib_bar.high());
//        bar.setLow(ib_bar.low());
//        // среднее арифметическое из 4 величин: open, close, high, low
//        double rate = (ib_bar.open() + ib_bar.close() + ib_bar.high() + ib_bar.low()) / 4.0;
//        bar.setRate(rate);
//        this.manager.persist(bar);
//        this.manager.getTransaction().commit();
////        System.out.print('.');
////        System.out.println(bar);
//        return bar;
//    }
//    protected Tick readLastTick() {
//        Query query = manager.createQuery("SELECT o FROM Tick o WHERE o.fundId = ?1");
//        query.setParameter(1, fund.getId());
//        return (Tick) query.getSingleResult();
//    }
/**
 * Чтение точек из набора из занесение их в Map отсортированы по времени
 *
 * @param t1 начальное время
 * @param t2 конечное время
 */
//    protected void readMapPoints(Date t1, Date t2) {
//        this.beginTime = t1;
//        this.endTime = t2;
//        List<Point> list = readPoints(t1, t2, setting.getName());
//        for (Point point : list) {
//            points.put(point.getTime(), point);
//        }
//    }
//    protected Point detectPoint(Date time, BigDecimal value) {
//        if (points.containsKey(time)) {
//            return points.get(time);
//        } else {
//            return createPoint(time, value, setting.getName());
//        }
//    }
//        query = manager.createQuery("SELECT MAX(b.high) FROM Bar b WHERE b.fundId = ?1 AND b.frame = ?2 "
//                + "AND b.time >= ?3 AND b.time <= ?4", BigDecimal.class);
//        query.setParameter(1, fund.getId());
//        query.setParameter(2, frame.getId());
//        query.setParameter(3, beginTime);
//        query.setParameter(4, endTime);
//        this.max = ((BigDecimal)query.getSingleResult()).doubleValue();
//        query = manager.createQuery("SELECT MIN(b.high) FROM Bar b WHERE b.fundId = ?1 AND b.frame = ?2 "
//                + "AND b.time >= ?3 AND b.time <= ?4", BigDecimal.class);
//        query.setParameter(1, fund.getId());
//        query.setParameter(2, frame.getId());
//        query.setParameter(3, beginTime);
//        query.setParameter(4, endTime);
//        this.min = ((BigDecimal)query.getSingleResult()).doubleValue();
//    protected Bar detectBar(Date time) {
//        Bar bar = readBar(time);
//        if (bar != null) {
//            return bar;
//        } else {
//            return createEmptyBar(time);
//        }
//    }
//

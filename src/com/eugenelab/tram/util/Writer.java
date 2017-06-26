/*
 * Copyright (c) 2004 - 2015, EugeneLab. All Rights Reserved
 */
package com.eugenelab.tram.util;

import com.eugenelab.tram.domain.Bar;
import com.eugenelab.tram.domain.Datum;
import com.eugenelab.tram.domain.Frame;
import com.eugenelab.tram.domain.Fund;
import com.eugenelab.tram.domain.Indicator;
import com.eugenelab.tram.domain.Neuron;
import com.eugenelab.tram.domain.Setting;
import com.eugenelab.tram.domain.Point;
import com.eugenelab.tram.domain.Tick;
import com.ib.controller.ApiController;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;
import javax.persistence.EntityManager;

/**
 *
 * @author eugene
 */
public class Writer {

    private final EntityManager manager;
    private final Fund fund;
    private final Frame frame;
    private final Indicator indicator;
    private final Setting setting;
    private final Calculator calculator;

    public Writer(EntityManager manager, Fund fund, Frame frame, Indicator indicator, Setting setting, Calculator calculator) {
        this.manager = manager;
        this.fund = fund;
        this.frame = frame;
        this.indicator = indicator;
        this.setting = setting;
        this.calculator = calculator;
    }

    private Bar commint(Bar bar) {
        if (!manager.getTransaction().isActive()) {
            this.manager.getTransaction().begin();
        }
        this.manager.persist(bar);
        this.manager.getTransaction().commit();
        return bar;
    }

    public Bar update(Bar prev, Bar bar, com.ib.controller.Bar ib_bar) {
        Date open_time = new Date(ib_bar.time() * 1000L);
        Date close_time = new Date(open_time.getTime() + calculator.bar_size);
        Date time = new Date(open_time.getTime() + calculator.bar_size / 2);
        bar.setMs(System.currentTimeMillis());
        bar.setLast(false);
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
        commint(bar);
        if (prev != null) {
            bar.setPrev_id(prev.getId());
            prev.setNext_id(bar.getId());
            commint(prev);
            commint(bar);
        }
        return bar;
    }

    public Bar create(Bar prev, com.ib.controller.Bar ib_bar) {
        return update(prev, new Bar(), ib_bar);
    }

    public Bar create(Date openTime, Date closeTime, Bar[] list) {
        Bar first = list[0];
        Bar last = list[list.length - 1];
        Bar bar = new Bar();
        Date time = new Date(openTime.getTime() + calculator.bar_size / 2);
        bar.setFund(fund);
        bar.setFrame(frame);
        bar.setOpenTime(openTime);
        bar.setCloseTime(closeTime);
        bar.setTime(time);
        bar.setOpen(first.getOpen());
        bar.setClose(last.getClose());
        bar.setLow(calculator.min);
        bar.setHigh(calculator.max);
        // среднее арифметическое из 4 величин: open, close, high, low
        double rate = (bar.getOpen().doubleValue()
                + bar.getClose().doubleValue()
                + bar.getHigh().doubleValue()
                + bar.getLow().doubleValue()) / 4.0;
        bar.setRate(rate);
        return commint(bar);
    }

    public Bar update(Bar bar, Bar[] list) {
        Bar first = list[0];
        Bar last = list[list.length - 1];
        bar.setOpen(first.getOpen());
        bar.setClose(last.getClose());
        bar.setLow(calculator.min);
        bar.setHigh(calculator.max);
        // среднее арифметическое из 4 величин: open, close, high, low
        double rate = (bar.getOpen().doubleValue()
                + bar.getClose().doubleValue()
                + bar.getHigh().doubleValue()
                + bar.getLow().doubleValue()) / 4.0;
        bar.setRate(rate);
        return commint(bar);
    }

    public Bar create(Bar prev) {
        Bar bar = new Bar();
        Date openTime = prev.getCloseTime();
        Date time = new Date(openTime.getTime() + calculator.bar_size / 2);
        Date closeTime = new Date(openTime.getTime() + calculator.bar_size);
        bar.setFund(fund);
        bar.setFrame(frame);
        bar.setOpenTime(openTime);
        bar.setCloseTime(closeTime);
        bar.setTime(time);
        bar.setOpen(prev.getClose());
        bar.setClose(prev.getClose());
        bar.setLow(prev.getClose());
        bar.setHigh(prev.getClose());
        bar.setRate(prev.getClose());
        return commint(bar);
    }

    public Bar update(Bar bar, Tick[] list) {
        for (Tick tick : list) {
            double rate = tick.getRate().doubleValue();
            if (rate > bar.getHigh().doubleValue()) {
                bar.setHigh(rate);
            }
            if (rate < bar.getLow().doubleValue()) {
                bar.setLow(rate);
            }
        }
        // среднее арифметическое из 4 величин: open, close, high, low
        double rate = (bar.getOpen().doubleValue()
                + bar.getClose().doubleValue()
                + bar.getHigh().doubleValue()
                + bar.getLow().doubleValue()) / 4.0;
        bar.setRate(rate);
        bar.setClose(list[list.length - 1].getRate());
        return commint(bar);
    }

    public void delete(Bar bar) {
        manager.getTransaction().begin();
        manager.remove(bar);
        manager.getTransaction().commit();

    }

    public Tick createTick(Bar bar) {
        Tick tick = new Tick();
        tick.setFund(bar.getFund());
        tick.setTime(bar.getCloseTime());
        tick.setMs(bar.getCloseTime().getTime());
        tick.setRate(bar.getClose());
        manager.persist(tick);
        return tick;
    }

    /**
     * Создаем бар на основе переданного бара меньшего размера
     *
     * @param prev предыдущий бар
     * @param b бар меньшего размера
     * @return вновь созданный бар
     */
    public Bar create(Bar prev, Bar b) {
        Bar bar = new Bar();
        Date open_time = b.getOpenTime();
        long size = frame.getId() * 1000L;
        Date close_time = new Date(open_time.getTime() + size);
        Date time = new Date(open_time.getTime() + size / 2);
        bar.setMs(System.currentTimeMillis());
        bar.setLast(false);
        bar.setFund(fund);
        bar.setFrame(frame);
        bar.setOpenTime(open_time);
        bar.setCloseTime(close_time);
        bar.setTime(time);
        bar.setOpen(b.getOpen());
        bar.setClose(b.getClose());
        bar.setHigh(b.getHigh());
        bar.setLow(b.getLow());
        bar.setRate(b.getRate());
        commint(bar);
        if (prev != null) {
            bar.setPrev_id(prev.getId());
            prev.setNext_id(bar.getId());
            commint(prev);
        }
        return commint(bar);
//        return update(new Bar(), Bar b, frm);
    }

    /**
     * Обновление бара на основе бара меньшего размера
     *
     * @param prev предыдущий бар
     * @param bar обновляемый бар
     * @param b бар меньшего размера
     * @return вновь созданный бар
     */
    public Bar update(Bar prev, Bar bar, Bar b) {
        bar.setLast(true);
        if (b.getHigh().doubleValue() > bar.getHigh().doubleValue()) {
            bar.setHigh(b.getHigh());
        }
        if (b.getLow().doubleValue() < bar.getLow().doubleValue()) {
            bar.setLow(b.getLow());
        }
        bar.setClose(b.getClose());
        // среднее арифметическое из 4 величин: open, close, high, low
        double mrate = (bar.getOpen().doubleValue()
                + bar.getClose().doubleValue()
                + bar.getHigh().doubleValue()
                + bar.getLow().doubleValue()) / 4.0;
        bar.setRate(mrate);
        if (bar.getPrev_id() == null && prev != null) {
            bar.setPrev_id(prev.getId());
            prev.setNext_id(bar.getId());
            commint(prev);
        }
        return commint(bar);
    }

    /**
     * Создание бара на основе тика
     *
     * @param prev предыдущий бар
     * @param tick тик
     * @return сознанный бар
     */
    public Bar create(Bar prev, Tick tick) {
        Bar bar = new Bar();
        bar.setLast(true);
        BigDecimal rate = tick.getRate();
        bar.setFund(tick.getFund());
        bar.setFrame(frame);
        bar.setOpen(rate);
        bar.setClose(rate);
        bar.setHigh(rate);
        bar.setLow(rate);
        bar.setRate(rate);
        long size = frame.getId() * 1000L; // размер бара
        long ntime = tick.getTime().getTime() / size;
        Date open_time = new Date(ntime * size);
        Date close_time = new Date(open_time.getTime() + size);
        Date time = new Date(open_time.getTime() + size / 2);
        bar.setOpenTime(open_time);
        bar.setCloseTime(close_time);
        bar.setTime(time);
        commint(bar);
        if (prev != null) {
            bar.setPrev_id(prev.getId());
            prev.setNext_id(bar.getId());
            commint(prev);
        }
        return commint(bar);
    }
//
//    public Bar link(Bar prev, Bar bar) {
//        bar.setPrev_id(prev.getId());
//        prev.setNext_id(bar.getId());
//        commint(prev);
//        return commint(bar);
//    }

    /**
     * Обновление бара на основе тика
     *
     * @param bar
     * @param prev предыдущий бар
     * @param tick
     * @return
     */
    public Bar update(Bar prev, Bar bar, Tick tick) {
        bar.setLast(true);
        double rate = tick.getRate().doubleValue();
        if (rate > bar.getHigh().doubleValue()) {
            bar.setHigh(rate);
        }
        if (rate < bar.getLow().doubleValue()) {
            bar.setLow(rate);
        }
        bar.setClose(tick.getRate());
        // среднее арифметическое из 4 величин: open, close, high, low
        double mrate = (bar.getOpen().doubleValue()
                + bar.getClose().doubleValue()
                + bar.getHigh().doubleValue()
                + bar.getLow().doubleValue()) / 4.0;
        bar.setRate(mrate);
        commint(bar);
        if (prev != null) {
            bar.setPrev_id(prev.getId());
            prev.setNext_id(bar.getId());
            commint(prev);
        }
        return commint(bar);
    }

//    public Bar createLastBar(Bar source) {
//        Date open_time = last.getCloseTime();
//        Date close_time = new Date(open_time.getTime() + calculator.bar_size);
//        Date time = new Date(open_time.getTime() + calculator.bar_size / 2);
//        Bar bar = new Bar();
//        bar.setLast(true);
//        bar.setFund(fund);
//        bar.setFrame(frame);
//        bar.setTime(time);
//        bar.setOpenTime(open_time);
//        bar.setCloseTime(close_time);
//        bar.setRate(rate);
//        bar.setOpen(last.getClose());
//        bar.setClose(rate);
//        bar.setHigh(rate);
//        bar.setLow(rate);
//        return commint(bar);
//    }
    /**
     * Создание нового последнего бара на основе предыдущего
     *
     * @param prev предыдущий бар
     * @param rate новый курс
     * @return вновь созданный бар
     */
    public Bar createLastBar(Bar prev, BigDecimal rate) {
        Date open_time = prev.getCloseTime();
        Date close_time = new Date(open_time.getTime() + calculator.bar_size);
        Date time = new Date(open_time.getTime() + calculator.bar_size / 2);
        Bar bar = new Bar();
        bar.setLast(true);
        bar.setFund(fund);
        bar.setFrame(frame);
        bar.setTime(time);
        bar.setOpenTime(open_time);
        bar.setCloseTime(close_time);
        bar.setRate(rate);
        bar.setOpen(prev.getClose());
        bar.setClose(rate);
        bar.setHigh(rate);
        bar.setLow(rate);
        bar.setPrev_id(prev.getId());
        commint(bar);
        prev.setNext_id(bar.getId());
        commint(prev);
        return bar;
    }

    public Bar updateLastBar(Bar bar, double rate) {
        bar.setLast(true);
        if (rate > bar.getHigh().doubleValue()) {
            bar.setHigh(rate);
        } else if (rate < bar.getLow().doubleValue()) {
            bar.setLow(rate);
        }
        bar.setClose(rate);
        return commint(bar);
    }

    public Bar updateLastBar(Bar bar) {
        bar.setLast(true);
        return commint(bar);
    }

    public Bar updateOpenRate(Bar prev, Bar bar) {
        bar.setOpen(prev.getClose());
        return commint(bar);
    }

    public Point createPoint(long time, double value) {
        return createPoint(time, value, setting, null);
    }

    public Point createPoint(long time, double value, Point prev) {
        return createPoint(time, value, setting, prev);
    }

    public Point createPoint(long time, double value, String name) {
        Setting set = Reader.set(manager, name);
        return createPoint(time, value, set, null);
    }

    public Point createPoint(long time, double value, String name, Point prev) {
        Setting set = Reader.set(manager, name);
        return createPoint(time, value, set, prev);
    }

    /**
     * Создание точки
     *
     * @param time время
     * @param value значение
     * @param set установка
     * @param prev предыдущая точка
     * @return вновь созданая точка
     */
    public Point createPoint(long time, double value, Setting set, Point prev) {
        Point point = new Point();
//        point.setName(name);
//        point.setIndicator(indicator);
        point.setMs(time);
        point.setSetting(set);
        point.setFund(fund);
        point.setFrame(frame);
        point.setTime(time);
        point.setValue(value);
        /**
         * TODO По уму здесь надо делать через OneToOne ассоциации prev и next К
         * сожалению все работает через одно место - то клеится то нет
         */
//        if (prev != null && prev.getId() != null ) {
//            point.setPrevId(prev.getId());
//            prev.setNextId(point.getId());
//            manager.persist(prev);
//        }
        manager.persist(point);
        return point;
    }


    /**
     * Обновление точки
     *
     * @param point точка
     * @param value значение
     * @param prev предыдущая точка
     * @return обновленная точка
     */
//    public Point updatePoint(Point point, double value, Point prev, double factor) {
////        point.setIndicator(indicator);
//        point.setValue(value);
//        if (prev != null) {
//            calculator.derivatives(point, prev, factor);
//        }
//        manager.persist(point);
//        return point;
//    }
    public Datum createDatum(Neuron neuron, long time, int value) {
        Datum datum = new Datum();
        datum.setFund(fund);
        datum.setFrame(frame);
        datum.setNeuron(neuron);
        datum.setTime(new Date(time));
//        datum.setMs(System.currentTimeMillis());
        datum.setValue(value);
        manager.persist(datum);
        return datum;
    }

}

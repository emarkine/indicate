/*
 * Copyright (c) 2004 - 2015, EugeneLab. All Rights Reserved
 */
package com.eugenelab.tram.util;

import com.eugenelab.tram.database.Bar;
import com.eugenelab.tram.database.Datum;
import com.eugenelab.tram.database.Edge;
import com.eugenelab.tram.database.Frame;
import com.eugenelab.tram.database.Fund;
import com.eugenelab.tram.database.Nerve;
import com.eugenelab.tram.database.Neuron;
import com.eugenelab.tram.database.Point;
import com.eugenelab.tram.database.Response;
import com.eugenelab.tram.database.Setting;
import com.eugenelab.tram.interfaces.Rateable;
import static com.eugenelab.tram.util.Constant.FORMAT_MS;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;

/**
 *
 * @author eugene
 */
public class NeuroCalc {

    private final boolean print = true;

    private final EntityManager manager;
    protected final Calculator calculator;
    private final Fund fund;
    private final Frame frame;
    private final Setting setting;

    public NeuroCalc(EntityManager manager, Calculator calculator, Fund fund, Frame frame, Setting setting) {
        this.manager = manager;
        this.calculator = calculator;
        this.fund = fund;
        this.frame = frame;
        this.setting = setting;
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


    public void edge(Map<Long, Point> points, Map<Long, Rateable> bars) {
        if (calculator.range == 0.0) { // по каким-то причинам границ нет
            System.err.println("No vertical range found");
            return;
        }
        Edge edge = Reader.edge(manager, setting);
        if (edge != null) {
            Map<Long, Datum> angles = angle(edge, points);
            Linker.setDataPrevNext(manager,angles);
            Map<Long, Datum> signs = sign(edge, angles);
            Linker.setDataPrevNext(manager,signs);
            Map<Long, Datum> trends = trend(edge, signs);
            Linker.setDataPrevNext(manager,trends);
            Map<Long, Datum> reverses = reverse(edge, points, bars);
            Linker.setDataPrevNext(manager,reverses);
            Map<Long, Datum> levels = level(edge, points);
            Linker.setDataPrevNext(manager,levels);
            Map<Long, Datum> outs = out(edge, points);
            Linker.setDataPrevNext(manager,outs);
        }
    }

    /**
     * Расчет уровня наклона кривой angle с учетом фактора умножения
     *
     * @param edge грань
     * @param points набор точек
     * @return вычисленные данные
     */
    private Map<Long, Datum> angle(Edge edge, Map<Long, Point> points) {
        Neuron neuron = Reader.neuron(manager, edge, "Neuron::Angle");
        if (neuron == null) {
            return null;
        }
//        puts(neuron);
        double factor = neuron.getFactor();
        Map<Long, Datum> data = Reader.data(manager, fund, frame, neuron, calculator.beginTime, calculator.endTime);
        Point prev = null;
        for (long time : points.keySet()) { // проходим по всем точкам
            Point point = points.get(time);
            if (prev != null && !data.containsKey(time)) {
                Datum d = new Datum();
                d.setFund(fund);
                d.setFrame(frame);
                d.setNeuron(neuron);
                d.setTime(new Date(time));
                // здесь я заново вычисляю угол в градусах потому что значения int
                // которое хранится в Point не достаточно для вычисления угла MA с большим периодом
                int value = (int) Math.round(point.getAlpha() / Math.PI * 180.0 * factor);
                if (value > 9) {
                    value = 9;
                } else if (value < -9) {
                    value = -9;
                }
                d.setValue(value);
                manager.persist(d);
                data.put(time, d);
                puts(d);
            }
            prev = point;
        }
        return data;
    }

    /**
     * Расчет знака точек на основе угла
     *
     * @param edge грань
     * @param points набор точек
     * @return вычисленные данные
     */
    private Map<Long, Datum> sign(Edge edge, Map<Long, Datum> angles) {
        Neuron neuron = Reader.neuron(manager, edge, "Neuron::Sign");
        if (neuron == null) {
            return null;
        }
//        puts(neuron);
        Map<Long, Datum> data = Reader.data(manager, fund, frame, neuron, calculator.beginTime, calculator.endTime);
        Datum prev = null;
        for (long time : angles.keySet()) { // проходим по всем точкам
            if (data.containsKey(time)) {
                prev = data.get(time);
            } else {
                Datum d = new Datum();
                d.setFund(fund);
                d.setFrame(frame);
                d.setNeuron(neuron);
                d.setTime(new Date(time));
                int angle = angles.get(time).getValue();
                d.setSignValue(angle);
                manager.persist(d);
                data.put(time, d);
                puts(d);
                prev = d;
            }
        }
        return data;
    }

    /**
     * Расчет уровня в зависомости от крайних значений индикатора 
     * TODO если границы заданы особо, то считается по другому алгоритму
     *
     * @param edge грань
     * @param points набор точек
     * @return вычисленные данные
     */
    private Map<Long, Datum> level(Edge edge, Map<Long, Point> points) {
        Neuron neuron = Reader.neuron(manager, edge, "Neuron::Level");
        if (neuron == null) {
            return null;
        }
//        puts(neuron);
        double factor = neuron.getFactor();
        Map<Long, Datum> data = Reader.data(manager, fund, frame, neuron, calculator.beginTime, calculator.endTime);
        Point prev = null;
        for (long time : points.keySet()) { // проходим по всем точкам
            Point point = points.get(time);
            if (prev != null && !data.containsKey(time)) {
                Datum d = new Datum();
                d.setFund(fund);
                d.setFrame(frame);
                d.setNeuron(neuron);
                d.setTime(new Date(time));
                double rate = point.getRate().doubleValue();
                double value = 9.0 - (calculator.max - rate) / calculator.range * 18.0;
                d.setValue(Math.round((float) value));
                manager.persist(d);
                data.put(time, d);
                puts(d);
            }
            prev = point;
        }
        return data;
    }

    /**
     * Расчет тренда с учетом фактора умножения
     *
     * @param edge грань
     * @param signs знаков
     * @return вычисленные данные
     */
    public Map<Long, Datum> trend(Edge edge, Map<Long, Datum> signs) {
        Neuron neuron = Reader.neuron(manager, edge, "Neuron::Trend");
        if (neuron == null) {
            return null;
        }
//        puts(neuron);
        int factor = neuron.getFactor().intValue();
        Map<Long, Datum> data = Reader.data(manager, fund, frame, neuron, calculator.beginTime, calculator.endTime);
        Datum prev = null;
        for (long time : signs.keySet()) { // проходим по всем точкам
            if (data.containsKey(time)) {
                prev = data.get(time);
            } else {
                Datum d = new Datum();
                d.setFund(fund);
                d.setFrame(frame);
                d.setNeuron(neuron);
                d.setTime(new Date(time));
                int sign = signs.get(time).getValue();
                double value = 0;
                if (prev != null) {
                    value = prev.getValue();
                }
//                value = value + sign * factor;
                value = value + sign;
                d.setValue(Math.round((float) value));
                manager.persist(d);
                data.put(time, d);
                puts(d);
                prev = d;
            }
        }
        return data;
    }

    /**
     * Расчет разворота
     *
     * @param edge грань
     * @return вычисленные данные
     */
    public Map<Long, Datum> reverse(Edge edge, Map<Long, Point> points, Map<Long, Rateable> bars) {
        Neuron neuron = Reader.neuron(manager, edge, "Neuron::Reverse");
        if (neuron == null) {
            return null;
        }
        Map<Long, Datum> data = Reader.data(manager, fund, frame, neuron, calculator.beginTime, calculator.endTime);
//        Bar[] bars = Reader.bars(); 
        Datum prev = null;
        for (long time : points.keySet()) { // проходим по всем точкам
            Point point = points.get(time); // берем точку
            Datum datum = data.get(time);
            if (datum == null) { // нет такого значения
                datum = new Datum();
                datum.setFund(fund);
                datum.setFrame(frame);
                datum.setNeuron(neuron);
                datum.setTime(new Date(time));
                double rate = point.getRate().doubleValue();
                Bar bar = (Bar) bars.get(time); // берем бар
                if (bar == null || bar.isInside(rate)) { // бара нет или попали акурат в промежуточек
                    datum.setValue(0);
                } else {
                    int value = 0;
                    if (prev != null) { // было предыдущее значение
                        value = prev.getValue(); // берем его
                    }
                    if (!bar.isEmpty()) { // бар не пустой 
                        if (bar.isAbove(rate)) {// бар сверху кривой
                            if (bar.isUp()) { // бар зелененький
                                value -= 1; // увеличиваем значение разворота вниз
                            } else { // бар зелененький
                                value += 1; // уменьшаем значение разворота вниз
                            }
//                            value -= 1;
                        }
                        if (bar.isBelow(rate)) {// бар снизу кривой
                            if (bar.isDown()) { // бар красненький
                                value += 1; // увеличиваем значение разворота вверх
                            } else { // бар зелененький
                                value -= 1; // уменьшаем значение разворота вверх
                            }
//                            value += 1;
                        }
                    }
                    datum.setValue(value);
                }
                manager.persist(datum);
                data.put(time, datum);
                puts(datum);
            }
            prev = datum;
        }
        return data;
    }

    private double response(Edge edge, Neuron out, String type) {
        Neuron neuron = Reader.neuron(manager, edge, type);
        Nerve nerve = Reader.nerve(manager, neuron, out);
        Response response = Reader.response(manager, nerve, this.fund, this.frame);
        if (response != null) {
            return response.getValue().doubleValue();
        } else {
            return 0.0;
        }
    }

    /**
     * Расчет выходного нейрона грани
     *
     * @param edge грань
     * @param points набор точек
     * @return вычисленные данные
     */
    private Map<Long, Datum> out_(Edge edge,
            Map<Long, Point> points,
            Map<Long, Datum> angles,
            Map<Long, Datum> signs,
            Map<Long, Datum> trends,
            Map<Long, Datum> reverses,
            Map<Long, Datum> levels) {
        Neuron neuron = Reader.neuron(manager, edge, "Neuron::Out");
        if (neuron == null) {
            return null;
        }
        int div = 5;
        double response_angle = response(edge, neuron, "Neuron::Angle");
        if (response_angle == 0.0) {
            --div;
        }
        double response_sign = response(edge, neuron, "Neuron::Sign");
        if (response_sign == 0.0) {
            --div;
        }
        double response_trend = response(edge, neuron, "Neuron::Trend");
        if (response_trend == 0.0) {
            --div;
        }
        double response_reverse = response(edge, neuron, "Neuron::Reverse");
        if (response_reverse == 0.0) {
            --div;
        }
        double response_level = response(edge, neuron, "Neuron::Level");
        if (response_level == 0.0) {
            --div;
        }
        Map<Long, Datum> data = Reader.data(manager, fund, frame, neuron, calculator.beginTime, calculator.endTime);
        for (long time : points.keySet()) { // проходим по всем точкам
            if (!data.containsKey(time)) { // новое значение
                Datum datum = new Datum();
                datum.setFund(fund);
                datum.setFrame(frame);
                datum.setNeuron(neuron);
                datum.setTime(new Date(time));
                if (div == 0) { // нет вообще активных нервов
                    datum.setValue(0);
                } else { // вычисляем сигнал
                    double value = angles.get(time).getValue() * response_angle;
                    value += signs.get(time).getSignValue() * response_sign;
                    value += trends.get(time).getValue() * response_trend;
                    value += reverses.get(time).getValue() * response_reverse;
                    value += levels.get(time).getValue() * response_level;
                    value /= div;
                    datum.setValue(Math.round((float) value));
                }
                manager.persist(datum);
                data.put(time, datum);
                puts(datum);
            }
        }
        return data;
    }

    /**
     * Расчет выходного нейрона грани
     *
     * @param edge грань
     * @param points набор точек
     * @return вычисленные данные
     */
    private Map<Long, Datum> out(Edge edge,
            Map<Long, Point> points) {
        Neuron neuron = Reader.neuron(manager, edge, "Neuron::Out");
        if (neuron == null) {
            return null;
        }
        Map<Long, Datum> data = Reader.data(manager, fund, frame, neuron, calculator.beginTime, calculator.endTime);
        for (long time : points.keySet()) { // проходим по всем точкам
            if (!data.containsKey(time)) { // новое значение
                Datum datum = thread(neuron, new Date(time));
                data.put(time, datum);
                puts(datum);
            }
        }
        return data;
    }

    /**
     * Вычисление одного данного для нервных связей нейрона
     *
     * @param neuron нейрон получающий сигнал
     * @param time время
     * @return вычисленное данное
     */
    public Datum thread_(Neuron neuron, Date time) {
        double value = 0;
        // считываем все нервы для нейрона
        List<Nerve> nerves = Reader.nerves(manager, neuron);
        int div = nerves.size();
        for (Nerve nerve : nerves) { // проходим по нервам
            Response response = Reader.response(manager, nerve, this.fund, this.frame); // считываем откклик
            Datum datum = Reader.datum(manager, fund, frame, nerve.getSource(), time); // находим источник данных
            if (response != null && datum != null) {
                if (response.getValue() != 0.0) {
                    value += response.getValue() * datum.getSignValue();
                } else {
                    --div;
                }
            } else {
                --div;
            }
            if (div == 0) {
                puts("!div=0  neuron: " + neuron.getType() + ", datum: " + datum + "response: " + response);
            }
        }
        Datum datum = new Datum();
        datum.setFund(fund);
        datum.setFrame(frame);
        datum.setNeuron(neuron);
        datum.setTime(time);
        if (div > 0) {
            datum.setSignValue(Math.round((float) (value / div)));
        } else {
            datum.setValue(0);
        }
        manager.persist(datum);
        return datum;
    }

    /**
     * Вычисление одного данного для нервных связей нейрона
     *
     * @param neuron нейрон получающий сигнал
     * @param time время
     * @return вычисленное данное
     */
    public Datum thread(Neuron neuron, Date time) {
        double value = 0;
        // считываем все нервы для нейрона
        List<Nerve> nerves = Reader.nerves(manager, neuron);
        for (Nerve nerve : nerves) { // проходим по нервам
//            Response response = Reader.response(manager, nerve, this.fund, this.frame); // считываем откклик
            Datum datum = Reader.datum(manager, fund, frame, nerve.getSource(), time); // находим источник данных
            if (datum != null) {
                if ( datum.getValue() >=  nerve.getLevel() || datum.getValue() <=  -nerve.getLevel() ) {
                    value += nerve.getValue() * datum.getSignValue();
                } 
            } else {
                puts(FORMAT_MS.format(time) + " neuron: " + neuron + " datum: null");
            }
        }
        Datum datum = new Datum();
        datum.setFund(fund);
        datum.setFrame(frame);
        datum.setNeuron(neuron);
        datum.setTime(time);
        datum.setSignValue(Math.round((float) (value / nerves.size())));
        manager.persist(datum);
        return datum;
    }

}

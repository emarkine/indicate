/*
 * Copyright (c) 2004 - 2015, EugeneLab. All Rights Reserved
 */
package com.eugenelab.tram.service;

import com.eugenelab.tram.database.Bar;
import com.eugenelab.tram.database.Point;
import com.eugenelab.tram.database.ServiceData;
import java.util.Map;
import javax.persistence.EntityManager;

/**
 * https://en.wikipedia.org/wiki/Parabolic_SAR
 * http://theignatpost.ru/magazine/index.php?mlid=3366
 * http://theignatpost.ru/magazine/index.php?mlid=3425
 * http://theignatpost.ru/magazine/index.php?mlid=3471
 *
 * @author eugene
 */
public class ParabolicStopAndReverseService extends Service {

    private final double initial_step;
    private final double maximum_step;
    private final double incremental_step;
    private int direction = 1; // call выбирается произвольным образом
    private boolean stop_flag = true;
    private double acceleration;
    private double high;
    private double low;
    private double sar;

    public ParabolicStopAndReverseService(ServiceData source, EntityManager manager) {
        super(source, manager);
//        print = true;
        initial_step = this.set.getInitialStep();
        maximum_step = this.set.getMaximumStep();
        incremental_step = this.set.getIncrementalStep();
    }

    /**
     * инициализация начальных параметров
     *
     * @param bar
     */
    private void init_params(Bar bar) {
        stop_flag = true; // флаг того, что только что произошел разворот
        acceleration = initial_step; // начальное ускорееие
        high = bar.getHigh().doubleValue(); // новый максимум
        low = bar.getLow().doubleValue(); // новый минимум
        if (direction > 0) {
            sar = low; // начальное значение sar для должно быть внизу для call
        } else {
            sar = high; // начальное значение sar для должно быть вверху для put
        }
    }

    /**
     * увеличение ускорения с проверкой на граничные условия
     *
     */
    private void increment_acceleration() {
        acceleration += incremental_step; // увеличивааем ускорение
//        acceleration = acceleration.round(2); // округляем его
        if (acceleration > maximum_step) {
            acceleration = maximum_step;
        }
        puts("acceleration: " + acceleration);
    }

    /**
     * расчет фвктора ускорения с проверкой условий роста
     *
     * @param bar
     */
    private void check_acceleration(Bar bar) {
        if (direction > 0) {
            if (bar.getHigh().doubleValue() > high) { // новое максимальное значение
                high = bar.getHigh().doubleValue();
                increment_acceleration();
            }
        } else {
            if (bar.getLow().doubleValue() < low) { // новое минимальное значение
                low = bar.getLow().doubleValue();
                increment_acceleration();
            }
        }
    }

    /**
     * расчет стопа и разворота
     *
     * @param bar
     * @return true - разворот индикатора
     */
    private boolean stop_and_reverse(Bar bar) {
        if (direction > 0) {
            return bar.getLow().doubleValue() < sar;
        } else {
            return bar.getHigh().doubleValue() > sar;
        }
    }

    /**
     * вычисление текущего SAR для одного бара
     *
     * @param bar
     * @return
     */
    private void calc_sar(Bar bar) {
        if (direction > 0) {
            sar = sar + (bar.getHigh().doubleValue() - sar) * acceleration;
        } else {
            sar = sar - (sar - bar.getLow().doubleValue()) * acceleration;
        }
//                    .round(@fund.comma)
        puts("sar: " + sar);
    }

    /**
     * расчет sar для всего периода
     *
     * @param bars
     */
    private void calc(Bar[] bars, Map<Long, Point> points) {
        init_params(bars[0]);
        for (Bar bar : bars) {
            long t = bar.getTime().getTime();
            Point p = points.get(t);
            if (p != null) { // если точка найдена, то ничего не надо делать
                sar = p.getValue().doubleValue(); // берем текущее значение sar из этой точки
            } else { // точки нет - ведем расчет 
                if (stop_and_reverse(bar)) { // только что произошел разворот
                    stop_flag = true; // устанавливаем флаг разворота
                    if (direction > 0) {
                        direction = -1;
                    } else {
                        direction = 1;
                    }
                    init_params(bar);
                } else { // разворота не было - продолжается движение 
                    stop_flag = false; // сбрасываем флаг разворота
                    calc_sar(bar);
                }
                writer.createPoint(t, sar);
            }
        }
    }

    @Override
    public void run() {
        time();
        Bar[] bars = reader.abars();
        if (bars.length <= 2) {
            return;
        }
        Map<Long, Point> points = reader.points();
        puts("bars: " + bars.length);
        puts("points: " + points.size());
        calc(bars, points);
        calculation(points);
        transaction();
        runtime();
    }

}


/* SELLAR


 */

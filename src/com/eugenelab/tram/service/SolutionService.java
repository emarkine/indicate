/*
 * Copyright (c) 2004 - 2015, EugeneLab. All Rights Reserved
 */
package com.eugenelab.tram.service;

import com.eugenelab.tram.domain.Bar;
import com.eugenelab.tram.domain.Datum;
import com.eugenelab.tram.domain.Edge;
import com.eugenelab.tram.domain.Neuron;
import com.eugenelab.tram.domain.Point;
import com.eugenelab.tram.domain.ServiceData;
import com.eugenelab.tram.util.Linker;
import com.eugenelab.tram.util.Reader;
import java.util.Date;
import java.util.Map;
import javax.persistence.EntityManager;

/**
 *
 * @author eugene
 */
public class SolutionService extends Service {

    public SolutionService(ServiceData data, EntityManager manager) {
        super(data, manager);
        print = false;
    }

    @Override
    public void run() {
        time();
        Bar[] bars = reader.abars(); // считываем все имеющиеся бары 
        Map<Long, Point> points = reader.points();
        Point prev = reader.prev(calculator.beginTime);
        int delta = this.set.getDelta();
        int delta_index = (int) (delta * 1000L / calculator.bar_size);
        Edge edge = Reader.edge(manager, set);
        Neuron neuron = Reader.neuron(manager, edge, "Neuron::Sign");
        Map<Long, Datum> data = Reader.data(manager, fund, frame, neuron, calculator.beginTime, calculator.endTime);
        puts("bars: " + bars.length + ", delta: " + delta_index + ", points: " + points.size());
        for (int i = 0; i < bars.length - delta_index; i++) {
            Bar b1 = bars[i];
            Bar b2 = bars[i + delta_index];
            long time = b1.getTime().getTime();
            Point point = points.get(time);
            if (point == null) { // нет такой точки
                double rate = b2.getClose().doubleValue() - b1.getClose().doubleValue();
                point = writer.createPoint(time, rate, prev);
                point.setService(subservice);
                points.put(time, point);
                puts(point);
                int value = (rate == 0.0) ? 0 : (rate > 0) ? 1 : -1;
                Datum datum = writer.createDatum(neuron, time, value);
                data.put(time, datum);
                puts(datum);
            }
            prev = point;
        }
        Linker.setPointsPrevNext(manager, points);
        Linker.setDataPrevNext(manager, data);
        runtime();
    }

}

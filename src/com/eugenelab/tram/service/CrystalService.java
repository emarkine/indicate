/*
 * Copyright (c) 2004 - 2015, EugeneLab. All Rights Reserved
 */
package com.eugenelab.tram.service;

import com.eugenelab.tram.domain.Datum;
import com.eugenelab.tram.domain.Edge;
import com.eugenelab.tram.domain.Neuron;
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
public class CrystalService extends Service {

    public CrystalService(ServiceData data, EntityManager manager) {
        super(data, manager);
        print = false;
    }
    
    @Override
    public void start() {
         super.start();
    }


    @Override
    public void run() {
        time();
        Edge edge = Reader.edge(manager, set); // осевая грань кристалла
        // загоняем не
        for (Neuron neuron : edge.getNeurons()) { // проходим по всем нейронам грани
//            puts(neuron);
            Map<Long, Datum> data = Reader.data(manager, fund, frame, neuron, calculator.beginTime, calculator.endTime);
            for (long t : reader.bars().keySet()) { // временной цикл основываем на считанных барах
                if ( !data.containsKey(t)) { // данные не были вычеслены
                    Datum datum = neuro.thread(neuron,new Date(t)); // считаем их
                    data.put(t, datum); // добавляем вновь созданное данное
                    puts(datum); // печатаем его
                }
            }
            Linker.setDataPrevNext(manager,data); // связываем данные друг с другом
        }
        runtime();
    }

}

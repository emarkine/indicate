/*
 * Copyright (c) 2004 - 2015, EugeneLab. All Rights Reserved
 */
package com.eugenelab.tram.util;

import com.eugenelab.tram.database.Datum;
import com.eugenelab.tram.database.Point;
import java.util.Collection;
import java.util.Map;
import javax.persistence.EntityManager;

/**
 *
 * @author eugene
 */
public class Linker {

    /**
     * Внесение изменений в базу данных для всех записей одновременно
     * @param manager
     */
    private static void transaction(EntityManager manager) {
        if (!manager.getTransaction().isActive()) {
            manager.getTransaction().begin();
        }
        manager.getTransaction().commit();
    }
    /**
     * Связи между точками
     *
     * @param points
     */
    public static void setPointsPrevNext(EntityManager manager, Map<Long, Point> points) {
        Point prev = null;
        for (long time : points.keySet()) { // проходим по всем точкам
            Point point = points.get(time);
            if (prev != null && prev.getId() != null) // существует prev и он записан в базу
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
     * Связи между данными
     *
     * @param manager
     * @param data
     */
    public static void setDataPrevNext(EntityManager manager, Map<Long, Datum> data) {
        transaction(manager);
        if (data == null || data.isEmpty()) {
            return;
        }
        Collection<Datum> list = data.values();
        Datum prev = list.iterator().next();
        for (Datum datum : list) { // проходим по всем точкам
            if (prev != datum && prev.getId() != null) // существует prev и он записан в базу
            {
                if (datum.getPrevId() == null) { // связи нет и надо ее создать
                    datum.setPrevId(prev.getId());
                    manager.persist(datum);
                }
                if (prev.getNextId() == null) { // связи нет и надо ее создать
                    prev.setNextId(datum.getId());
                    manager.persist(prev);
                }
//                puts("prev: " + prev);
//                puts("datum: " + datum);
//                if (datum.getPrev() == null) { // связи нет и надо ее создать
//                    datum.setPrev(prev);
//                    manager.persist(datum);
//                }
//                if (prev.getNext() == null) { // связи нет и надо ее создать
//                    prev.setNext(datum);
//                    manager.persist(prev);
//                }
            }
            prev = datum;
        }
        transaction(manager);
    }

}

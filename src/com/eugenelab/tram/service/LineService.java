/*
 * Copyright (c) 2004 - 2015, EugeneLab. All Rights Reserved
 */
package com.eugenelab.tram.service;

import com.eugenelab.tram.database.Point;
import com.eugenelab.tram.database.ServiceData;
import javax.persistence.EntityManager;

/**
 * Сервис для отрисовки линий поддержки/сопротивления
 * 
 * @author eugene
 */
public class LineService extends Service {

    private boolean hasOpen = false;
    private boolean hasClose = false;

    public LineService(ServiceData source, EntityManager manager) {
        super(source, manager);
        print = true;
    }

    @Override
    public void start() {
        super.start();
    }

    @Override
    public void run() {
        time();
        if (hasOpen && hasClose) {
        }
        runtime();
    }

    protected void savePoint(String set_name, double price) {
        Point point = writer.createPoint(System.currentTimeMillis(), price, set_name);
        manager.persist(point);
        transaction();
        puts(point);
    }

}

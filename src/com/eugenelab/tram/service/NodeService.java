/*
 * Copyright (c) 2004 - 2018, EugeneLab. All Rights Reserved
 */
package com.eugenelab.tram.service;

import com.eugenelab.tram.domain.Point;
import com.eugenelab.tram.domain.ServiceData;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import javax.persistence.EntityManager;

/**
 * 
 *
 * @author eugene
 */
public abstract class NodeService extends Service {

    protected Process process;
    protected BufferedReader bufferedReader;

    public NodeService(ServiceData data, EntityManager manager) {
        super(data, manager);
        this.print = true;
    }

    /**
     * 
     */
    @Override
    public void start() {
        manager.getTransaction().begin();
        super.start();
        manager.getTransaction().commit();
    }

    protected abstract int value();
    
    /**
     *
     */
    @Override
    public void run() {
        manager.getTransaction().begin();
        Point point = writer.createPoint(value());
        point.setService(data);
        super.run();
        manager.getTransaction().commit();
        puts(point);
    }

    /**
     *
     */
    @Override
    public void stop() {
        manager.getTransaction().begin();
        super.stop();
        manager.getTransaction().commit();
    }

}

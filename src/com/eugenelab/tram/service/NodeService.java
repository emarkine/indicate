/*
 * Copyright (c) 2004 - 2018, EugeneLab. All Rights Reserved
 */
package com.eugenelab.tram.service;

import com.eugenelab.tram.domain.ServiceData;
import javax.persistence.EntityManager;

/**
 * Получение данных о состоянии сервисов
 * 
 * @author eugene
 */
public class NodeService extends Service {

    public NodeService(ServiceData data, EntityManager manager) {
        super(data, manager);
        this.print = true;
    }
    
    /**
     * Connection to Arduino 
     */
    @Override
    public void start() {
        manager.getTransaction().begin();
        super.start();
        manager.getTransaction().commit();
    }

    /**
     * 
     */
    @Override
    public void run() {
        manager.getTransaction().begin();
        int value = 0;
        super.run();
        puts(value);
        manager.getTransaction().commit();
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

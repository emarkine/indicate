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
    }
    
    /**
     * Connection to Arduino USB
     */
    @Override
    public void start() {
        super.start();
    }

    /**
     * 
     */
    @Override
    public void run() {
        super.run();
    }
    /**
     * 
     */
    @Override
    public void stop() {
        super.stop();
    }
    
}

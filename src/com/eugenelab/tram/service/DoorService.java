/*
 * Copyright (c) 2004 - 2018, EugeneLab. All Rights Reserved
 */
package com.eugenelab.tram.service;

import com.eugenelab.tram.domain.ServiceData;
import javax.persistence.EntityManager;

/**
 *
 * 
 * @author eugene
 */
public class DoorService extends NodeService {

    public DoorService(ServiceData data, EntityManager manager) {
        super(data, manager);
    }
    
    protected int value() {
        return 0;
    }

    /**
     * 
     */
    @Override
    public void start() {
//        System.err.println("error");
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

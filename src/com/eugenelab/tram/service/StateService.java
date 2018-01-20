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
public class StateService extends Service {

    public StateService(ServiceData data, EntityManager manager) {
        super(data, manager);
    }
    
    /**
     * Бары обновляются на основе исторических данных
     */
    @Override
    public void run() {
    }

    
}

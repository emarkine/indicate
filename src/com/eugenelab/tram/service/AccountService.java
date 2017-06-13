/*
 * Copyright (c) 2004 - 2015, EugeneLab. All Rights Reserved
 */
package com.eugenelab.tram.service;

import com.eugenelab.tram.database.ServiceData;
import com.ib.controller.ApiController;
import javax.persistence.EntityManager;

/**
 * Получение данныты о состоянии счета
 * 
 * @author eugene
 */
public class AccountService extends Service {

    public AccountService(ServiceData data, EntityManager manager, ApiController controller) {
        super(data, manager, controller);
    }
    
    /**
     * Бары обновляются на основе исторических данных
     */
    @Override
    public void run() {
    }

    
}

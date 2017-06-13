/*
 * Copyright (c) 2004 - 2016, EugeneLab. All Rights Reserved
 */
package com.eugenelab.tram.service;

import com.eugenelab.tram.database.Point;
import com.eugenelab.tram.database.ServiceData;
import java.util.Map;
import javax.persistence.EntityManager;

/**
 *
 * @author eugene
 */
public class SimpleMovingAverageService extends Service {

    public SimpleMovingAverageService(ServiceData source, EntityManager manager) {
        super(source, manager);
    }

    @Override
    public void run() {
        time();
        Map<Long, Point> points = sma();
        if (points != null) {
            calculation(points);
        }
        runtime();
    }

}


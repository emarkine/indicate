/*
 * Copyright (c) 2004 - 2015, EugeneLab. All Rights Reserved
 */
package com.eugenelab.tram.container;

import com.eugenelab.tram.database.Point;
import com.eugenelab.tram.util.Reader;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

/**
 * Контейнер для хранения точек
 * @author eugene
 */
public class Points {
    private static final Map<String,Object> sets = new TreeMap<String, Object>();
    private final Reader reader;
    
    public Points(Reader reader) {
        this.reader = reader;
    }
    
    public static Map<Long, Point> map(Date beginTime, Date endTime, String name) {
        return null;
//        if (sets.)
    }
    
}

/*
 * Copyright (c) 2004 - 2015, EugeneLab. All Rights Reserved
 */
package com.eugenelab.tram.util;

import java.util.Date;

/**
 * И почему, блин, в стандартной Java нет такого класса?
 * 
 * @author eugene
 */
public class Period {
    public Date begin; // начальное время 
    public Date end; // конечное время 
    
    public Period() { // по умолчания с начала дня до текущего момента
        this.end = new Date();
        this.begin = Utils.beginOfDay(this.end);
    }

    public Period(Period period) {
        this.begin = period.begin;
        this.end = period.end;
    }
    
    public Period(Date begin, Date end) {
        this.begin = begin;
        this.end = end;
    }
    
    public long getSeconds() { // длина периода в секундах
        return (end.getTime() - begin.getTime()) / 1000L; // всего секунд
    }
    
    @Override
    public String toString() {
        return Constant.FORMAT_DATE_TIME.format(begin) + " - " + Constant.FORMAT_DATE_TIME.format(end);
    }
    
    
}

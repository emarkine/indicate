/*
 * Copyright (c) 2004 - 2015, EugeneLab. All Rights Reserved
 */
package com.eugenelab.tram.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 *
 * @author eugene
 */
public class Constant {
    public static final DateFormat FORMAT = new SimpleDateFormat("HH:mm:ss");
    public static final DateFormat FORMAT_MS = new SimpleDateFormat("HH:mm:ss:SSS");
    public static final DateFormat FORMAT_DATE = new SimpleDateFormat( "yyyy-MM-dd"); 
    public static final DateFormat FORMAT_TIME = new SimpleDateFormat( "HH:mm:ss"); 
    public static final DateFormat FORMAT_DATE_TIME = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static final DateFormat FORMAT_IB_REQUEST = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
    public static final long ONE_DAY = 24L * 60L * 60L * 1000L;
    public static final long TWO_HOURS = 2L * 60L * 60L * 1000L;
}

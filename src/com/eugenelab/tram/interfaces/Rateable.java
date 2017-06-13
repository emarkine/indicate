/*
 * Copyright (c) 2004 - 2015, EugeneLab. All Rights Reserved
 */
package com.eugenelab.tram.interfaces;

import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author eugene
 */
public interface Rateable {

    BigDecimal getRate();

    void setRate(double rate);

    void setRate(BigDecimal rate);

    Date getTime();

    void setTime(Date time);

    void setTime(long time);

}

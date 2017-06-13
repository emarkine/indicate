/*
 * Copyright (c) 2004 - 2015, EugeneLab. All Rights Reserved
 */
package com.eugenelab.tram.interfaces;

import com.eugenelab.tram.database.Bar;

/**
 *
 * @author eugene
 */
public interface Barable {

    Bar make(com.ib.controller.Bar ib_bar); // создание бара на основе IB бара
    void complete(); // выход из основного thread после окончание сервиса

}

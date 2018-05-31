/*
 * Copyright (c) 2004 - 2015, EugeneLab. All Rights Reserved
 */
package com.eugenelab.tram.interfaces;

import com.eugenelab.tram.domain.Bar;
import com.eugenelab.tram.domain.Host;
import java.util.Date;

/**
 *
 * @author eugene
 */
public interface Serviceable {
    int getId(); 
    void start(); // подготовка сервиса
    void run();  // работа сервиса
    void stop(); // остановка сервиса
    boolean isActive(); // активен ли сервис, он может быть загружен, но не активен
    void setSingle(boolean single);
    boolean isSingle(); // запуск только одного сервиса
    boolean isAsynchronous(); // асинхронный запуск при обращение к контроллеру IB
    boolean isVirtual(); // запустк в эмуляции режима реального времени
    Date getDate();
    void setDate(Date date); 
    boolean isDate(); // расчет только по отдельно выбранной дате
    boolean isRefresh(); // обновляемый ли это сервис по времени
    boolean isUpdatable(); // обновляемый ли это сервис через другй сервис
    void updateSelf(Bar bar); // обновление самого сервиса на основе полученного бара
    void updateTrigger(Bar bar); // распоространение обновление на список зависимых сервисов 
    int refreshPeriod(); // период обновления
    void setHost(Host host); // обновление хоста, где запустился сервис
    void setState(String status); // обновление статуса
    void setState(String status, String message); // обновление статуса с сообщением
    String getState(); // получение текущего (последнего) состояния сервиса
    void puts(Object o);
}

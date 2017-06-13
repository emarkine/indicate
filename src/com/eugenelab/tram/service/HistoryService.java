/*
 * Copyright (c) 2004 - 2015, EugeneLab. All Rights Reserved
 */
package com.eugenelab.tram.service;

import com.eugenelab.tram.database.Bar;
import com.eugenelab.tram.database.ServiceData;
import com.eugenelab.tram.database.Tick;
import com.eugenelab.tram.interfaces.Barable;
import com.eugenelab.tram.util.HistoryReader;
import com.eugenelab.tram.util.MarketData;
import com.eugenelab.tram.util.Period;
import com.eugenelab.tram.util.Utils;
import com.ib.controller.ApiController;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import javax.persistence.EntityManager;
import org.apache.commons.lang3.time.DateUtils;

/**
 * https://www.interactivebrokers.com/en/software/api/apiguide/tables/historical_data_limitations.htm
 *
 * @author eugene
 */
public class HistoryService extends Service implements Barable {

    protected final HistoryReader history;

    public HistoryService(ServiceData data, EntityManager manager, ApiController controller) {
        super(data, manager, controller);
        history = new HistoryReader(this, controller, fund, frame);
//        this.period = frame.getDuration() / frame.getId();
//        history = new HistoryReader(this, controller, fund, frame, calculator.beginTime, calculator.endTime);
        print = true;
    }

    /**
     * Поиск предшевствующего бара
     *
     * @param current_bar_time время текущего бара
     * @return найденый бар
     */
    private Bar findPrev(Date date) {
        Date prevDay = Utils.previousDay(date);
        Date lastClose = Utils.timeToDate(fund.getCloseTime(), prevDay);
        long lastLong = lastClose.getTime() - calculator.bar_size / 2;
        Date lastTime = new Date(lastLong);
        Bar bar = reader.bar(lastTime);
        return bar;
    }

    private void initUnitDuration() {
        long unit_sec = MarketData.duration(frame.getUnit());
        long duration = frame.getDuration() * unit_sec * 1000L;
        Date now = new Date();
        Date begin = new Date(now.getTime() - duration);
        calculator.time(begin, now, period);
    }

    @Override
    public void start() {
        if (isDate()) { // передана дата
            this.prev = findPrev(this.getDate());
        } else { // даты нет 
            setDate(new Date());// устанавливаем сегодняшнию
//           initUnitDuration(); // читаем максимально возможное их количество
//            if (frame.getName().equals("1d")) { // в случае если запрашиваем дневные бары
//                initUnitDuration(); // читаем максимально возможное их количество
//            } else {
//                this.setDate(new Date());// устанавливаем дату на сегодняшний день
//                this.prev = findPrev(this.getDate());
//            }
//            history.setPeriod(calculator.beginTime, calculator.endTime);
        }
        if (this.frame.getId() == 5) { // для 5s все расчитывается по другому
            history.setPeriod(new Period(calculator.beginTime, calculator.endTime));
        } else {
            history.setDate(calculator.endTime);
        }
        super.start();
        reader.bars();
        history.request();
    }

    /**
     * В запуске происходит пересчет считки только 5s периода
     */
    @Override
    public void run() {
    }

    @Override
    public void complete() {
        if (this.frame.getId() == 5) {
            puts("5s");
        }
        if (isSingle()) { // одиночный запуск 
            System.exit(0); // вываливаемся
        }
    }

    @Override
    public Bar make(com.ib.controller.Bar ib_bar) {
        long t = ib_bar.time() * 1000L + calculator.bar_size / 2;
        if (frame.getId() == 1 || frame.getId() == 5) {
            t -= 500; // уменьшаем серединку на пол-секунды только для 5s баров
        }
        Bar bar = (Bar) reader.bars.get(t); // пытаемся считать бар из коллекции
        if (bar == null) { // нет такого бара в коллекции
            bar = reader.bar(new Date(t)); // пытаемся считать бар из базы
        }
        if (bar == null) { // нет такого бара 
            bar = writer.create(prev, ib_bar); // создаем новый бар
            reader.bars.put(bar.getTime().getTime(), bar); // добавляем вновь созданный бар в коллекцию
        } else if (bar.isLast()) { // такой бар есть и он последний
            bar = writer.update(prev, bar, ib_bar); // просто обновляем его
        } else if (bar != null && force) {
            bar = writer.update(prev, bar, ib_bar); // принудительное обновление бара
        }
        prev = bar;
        puts(bar);
        return bar;
    }
    
    @Override
    public boolean isAsynchronous() {
        return true;
    }


}


/* SELLAR
 @Override
 public void start() {
 for (Frame f : frames) {
 this.frame = f;
 calculator.time();
 if (print) {
 System.out.println(frame);
 }
 String endDateTime = FORMAT_IB_REQUEST.format(calculator.endTime);
 BarSize barSize = MarketData.barSize(frame.getId());
 Types.DurationUnit unit = MarketData.duration(frame.getUnit());
 history(endDateTime, frame.getDuration(), unit, barSize);
 }
 }


 */

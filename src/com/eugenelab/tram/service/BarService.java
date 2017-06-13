/*
 * Copyright (c) 2004 - 2015, EugeneLab. All Rights Reserved
 */
package com.eugenelab.tram.service;

import com.eugenelab.tram.database.Bar;
import com.eugenelab.tram.database.Frame;
import com.eugenelab.tram.database.ServiceData;
import com.eugenelab.tram.database.Tick;
import com.eugenelab.tram.util.MarketData;
import com.eugenelab.tram.util.Period;
import com.eugenelab.tram.util.Reader;
import com.eugenelab.tram.util.Utils;
import com.ib.controller.ApiController;
import java.util.Date;
import javax.persistence.EntityManager;

/**
 * https://www.interactivebrokers.com/en/software/api/apiguide/tables/historical_data_limitations.htm
 *
 * @author eugene
 */
public class BarService extends HistoryService {

//    private boolean first_start = true;

//    protected final HistoryReader history;
    public BarService(ServiceData data, EntityManager manager, ApiController controller) {
        super(data, manager, controller);
        print = true;
    }

    @Override
    public void start() {
        super.start();
//        last = reader.lastBar;
    }

    /**
     * Пересоздание бара через обращение к истории последнего бара включая
     * текущий
     *
     */
    private void renew() {
        Date openTime = new Date(last.getOpenTime().getTime() - calculator.bar_size*2); // сдвигаемся еще на два бара назад
        Date closeTime = new Date(last.getCloseTime().getTime() + calculator.bar_size); // сдвигаемся на один бар вперед
//        Date openTime = new Date(last.getOpenTime().getTime()); // 
//        Date closeTime = new Date(last.getCloseTime().getTime() + calculator.bar_size * 2); // сдвигаемся на один бар вперед
        history.setPeriod(new Period(openTime, closeTime)); // должно выскочить 3 бара включая последний (last)
        history.request();
    }

    /**
     * Бары обновляются на основе исторических данных
     */
    @Override
    public void run() {
        if (last != null ) {
            renew();
            updateTrigger(last);
        }
    }


//    @Override
//    public Bar make(com.ib.controller.Bar ib_bar) {
//        prev = super.make(ib_bar); // мы должны вновь созданный бар связать с текущим тиковым баром
////        writer.updateOpenRate(prev, last);
//        return prev;
//    }

//    @Override
//    public Bar make(com.ib.controller.Bar ib_bar) {
//        Bar bar = super.make(ib_bar);
//        if (System.currentTimeMillis() < bar.getCloseTime().getTime()) {
//            last = bar; // это последний бар
//            writer.updateLastBar(last); // мы должны выставить ему флажок last
//        }
//        return bar;
//    }

    /**
     * Поиск, создание, сдвиг последнего бара на основе тика
     */
    private void checkLast(Tick tick) {
        if (last == null) { // последний бар не определен
            last = reader.bar(tick.getTime()); // пытаемся считать его из базы
            if (last == null) { // такго бара в базе нет
                last = writer.create(prev, tick);// создаем его
            }
        }
        if (tick.getTime().getTime() > last.getCloseTime().getTime()) { // выскочили за пределы текущего бара
            last = reader.bar(tick.getTime()); // пытаемся считать его из базы
            if (last == null) { // такго бара в базе нет
                last = writer.create(prev, tick);// создаем новый бар 
                puts(last);
            }
        }
    }

    /**
     * Поиск, создание, сдвиг последнего бара на основе бара
     */
    private void checkLast(Bar bar) {
        if (last == null) { // последний бар не определен
            last = reader.bar(bar.getTime()); // пытаемся считать его из базы
            if (last == null) { // такго бара в базе нет
                last = writer.create(prev, bar);// создаем его
            }
        }
        if (bar.getCloseTime().getTime() > last.getCloseTime().getTime()) { // выскочили за пределы текущего бара
            last = reader.bar(bar.getTime()); // пытаемся считать его из базы
            if (last == null) { // такго бара в базе нет
                last = writer.create(prev, bar);// создаем новый бар 
                puts(last);
            }
        }
    }

    /**
     * Обновление списка сервисов на основе тика
     *
     * @param tick последний тик
     */
//    @Override
//    public void update(Tick tick) {
//        checkLast(tick);
//        writer.update(prev, last, tick);
//    }

    @Override
    public void updateSelf(Bar bar) {
        checkLast(bar);
        puts(writer.update(prev, last, bar));
    }

//    public void updateLast(Bar last) {
//       this.last = last;
//       puts("updateLast: "+last);
//    }

}

/* SELLAR
 //@Override
 public void run_() {
 if (first_start) {
 first_start = false;
 } else {
 Date time = new Date();
 Bar bar1m = reader.bar(time, Reader.frame1m);
 if (bar1m != null) {
 //            puts("1m: "+bar1m);
 if (bar1m.getOpenTime().getTime() >= last.getCloseTime().getTime()) { // выскочили за пределы текущего бара
 history.setPeriod(last.getOpenTime(), last.getCloseTime());
 history.request();
 Bar bar = reader.bar(time);
 if (bar != null) {// такой бар уже есть в базе
 writer.updateLastBar(bar, bar1m.getClose().doubleValue());
 last = bar;
 puts("updated: " + last);
 } else {
 last = writer.createLastBar(last, bar1m.getClose());// создаем новый бар 
 puts("creaded: " + last);
 }
 } else {
 writer.updateLastBar(last, bar1m.getClose().doubleValue());
 puts("updated: " + last);
 }
 }
 }
 }
 */

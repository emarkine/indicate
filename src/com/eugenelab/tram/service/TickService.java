/*
 * Copyright (c) 2004 - 2015, EugeneLab. All Rights Reserved
 */
package com.eugenelab.tram.service;

import com.eugenelab.tram.database.Bar;
import com.eugenelab.tram.database.Point;
import com.eugenelab.tram.database.ServiceData;
import com.eugenelab.tram.database.Setting;
import com.eugenelab.tram.database.Tick;
import com.eugenelab.tram.util.Constant;
import com.eugenelab.tram.util.MarketData;
import com.eugenelab.tram.util.ServiceFinder;
import com.eugenelab.tram.util.Utils;
import com.ib.client.TickType;
import com.ib.controller.ApiController;
import com.ib.controller.ApiController.IRealTimeBarHandler;
import com.ib.controller.ApiController.ITopMktDataHandler;
import com.ib.client.Contract;
import com.ib.controller.NewTickType;
import com.ib.client.Types;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import javax.persistence.EntityManager;

/**
 *
 * @author eugene
 */
public class TickService extends Service implements IRealTimeBarHandler, ITopMktDataHandler {

//    public static final Map<Date, Rateable> ticks = new TreeMap<>();
//    public static final Map<Date, Rateable> bars5s = new TreeMap<>();
//    private static final Map<Frame, Bar> lastBars = new TreeMap<>();
//    private static final Frame[] FRAMES = new Frame[] {Reader.frame1m};
    private double bid;
    private double ask;
    private double lastPrice;
    private double lastTickPrice;
    private int lastVolume;
//    private double previous_close;
//    private double opening_price;
    private int bidSize;
    private int askSize;
    private int volume;

    private final Contract contract;
    private boolean hasOpen = false;
    private boolean hasClose = false;

//    private boolean first_start = true;
//    private Bar last1m;
//    private long time;
//    private long lastTime;
//    private boolean first;
    public TickService(ServiceData data, EntityManager manager, ApiController controller) {
        super(data, manager, controller);
        print = true;
        contract = MarketData.initContract(this.fund);
//        initVirtualTime();
    }

//    private void initLastBars() {
//        Date t = new Date();
//        for( Frame f: reader.frames()) {
//            Bar bar = reader.bar(t, f);
//            lastBars.put(f, bar);
//        }
//    }
    @Override
    public void start() {
        super.start();
        controller.reqRealTimeBars(contract, Types.WhatToShow.MIDPOINT, false, this);
        controller.reqTopMktData(contract, "", false, this);
        prev = reader.prevBar(new Date());
        if (prev != null) {
            calculator.time(prev.getOpenTime(), prev.getCloseTime(), period);
            reader.bars();
        }
    }

    /**
     * Обновление запроса к базе даныых
     */
    private void renew() {
        controller.cancelRealtimeBars(this);
        controller.reqRealTimeBars(contract, Types.WhatToShow.MIDPOINT, false, this);
        controller.cancelTopMktData(this);
        controller.reqTopMktData(contract, "", false, this);
    }

//    /**
//     *
//     * @param bar
//     */
//    private void checkBarTime(Bar bar) {
//        Date time5s = Utils.prevFiveSecond(new Date());
//        puts("Check 5s: " + Constant.FORMAT.format(time5s) + ", bar: " + bar);
//        if (bar.getCloseTime().getTime() != time5s.getTime()) {
//            sendError("Wrong 5s bar: " + bar);
//            renew();
//        }
//    }
    /**
     * В запуске мы считываем пересчитываем исторические данные чтобы избежать
     * отставания курсов
     */
    @Override
    public void run() {
        renew();
//        Bar bar = reader.bar(); // читаем последний 5s бар
//        if (prev != null) {
//            checkBarTime(prev);
//        }
    }

    @Override
    public void realtimeBar(com.ib.controller.Bar ib_bar) {
        makeBar(ib_bar);
    }

    public void tickPrice(NewTickType tickType, double price, int canAutoExecute) {
        switch (tickType) {
            case BID:
                bid = price;
                if (!fund.getCategory().equals("stock") && !fund.getCategory().equals("index")) {
                    saveTickMidpoint();
                }
                break;
            case ASK:
                ask = price;
                if (!fund.getCategory().equals("stock") && !fund.getCategory().equals("index")) {
                    saveTickMidpoint();
                }
                break;
            case LAST:
                lastPrice = BigDecimal.valueOf(price).setScale(fund.getComma(), RoundingMode.HALF_UP).doubleValue();
                saveTick();
                break;
            case OPEN:
                if (!hasOpen) {
                    savePoint("line_opening_price", price);
                    hasOpen = true;
                }
                break;
            case CLOSE:
                if (!hasClose) {
                    savePoint("line_previous_close", price);
                    hasClose = true;
                }
        }
    }

    protected void savePoint(String set_name, double price) {
        Point prev = null;
        Setting set = reader.set(set_name);
        if ( set != null ) { // такой set быть обязан, но на всякий случай
            prev = reader.last(set); // считываем последнюю точку
            if ( prev != null ) { // если точка нашлась
                String prev_date = Constant.FORMAT_DATE.format(prev.getTime()); // берем дату ее создания
                String now_date = Constant.FORMAT_DATE.format(new Date()); // и текущую дату
                if (prev_date.equals(now_date)) { // если они совпадают
                    return; // то ничего делать не надо
                }
            } 
        }
        Point point = writer.createPoint(System.currentTimeMillis(), price, set, prev);
        point.setService(subservice);
        manager.persist(point);
        transaction();
        puts(point);
    }

    public void tickSize(NewTickType tickType, int size) {
//        System.out.println("time: " + Commander.FORMAT.format(new Date()));
        switch (tickType) {
            case BID_SIZE:
                bidSize = size;
                break;
            case ASK_SIZE:
                askSize = size;
                break;
            case VOLUME:
                volume = size;
                saveTick();
                break;
        }
    }

    // фильтр, который пропускает тики только с изменением не больше чем percent
    private boolean priceFilter(double lastPrice, double price, int percent) {
        double diff = Math.abs(price - lastPrice);
        double result = (diff / price) * 100.0;
        return result < percent;
    }

    private void saveTickMidpoint() {
        double price = bid + (ask - bid) / 2.0;
        lastPrice = BigDecimal.valueOf(price).setScale(fund.getComma(), RoundingMode.HALF_UP).doubleValue();
        if (volume == 0) {
            volume = lastVolume;
        }
        saveTick();
    }

    private void saveTick() {
        if (checkTime()) {
            if (lastPrice != lastTickPrice) {
                if (lastPrice <= 0.0 || bid < 0 || ask < 0) { // глюки
                    System.err.println("gluk!(<=0)[" + fund.getFullName() + "] price: " + lastPrice + ", lastPrice:" + lastTickPrice + ", bid: " + bid + ", ask" + ask);
                    return;
                }
                if (lastTickPrice == 0.0) {// пропускаем первый тик, т.к. почему-то он идет с глюком по цене
                    System.err.println("(first=0)[" + fund.getFullName() + "] price: " + lastPrice);
                    lastTickPrice = lastPrice;
//                lastTime = time;
                    return;
                }
                if (lastTickPrice > 0 && !priceFilter(lastTickPrice, lastPrice, 30)) { // критерий 30%
                    System.err.println("gluk!(30%)[" + fund.getFullName() + "] price: " + lastPrice + ", lastPrice:" + lastTickPrice);
                    lastTickPrice = lastPrice;// если это  действительно истннный gap, то второй тик уже пройдет
                    return;
                }
                Tick tick = new Tick();
                tick.setFund(fund);
                tick.setTime(new Date());
                tick.setMs(System.currentTimeMillis());
                tick.setRate(BigDecimal.valueOf(lastPrice));
                tick.setBid(BigDecimal.valueOf(bid));
                tick.setAsk(BigDecimal.valueOf(ask));
                tick.setBidSize(bidSize);
                tick.setAskSize(askSize);
                if (fund.getCategory().equals("valuta")) { // у валюты не фиксируются объемы
                    tick.setVolume(0);
                    tick.setLastSize(0);
                } else {
                    tick.setVolume(volume);
                    tick.setLastSize(volume - lastVolume);
                    lastVolume = volume;
                }
                manager.getTransaction().begin();
                manager.persist(tick);
                manager.getTransaction().commit();
//                ticks.put(tick.getTime(), tick);
                lastTickPrice = lastPrice;
//            lastTime = time;
                puts(tick);
//                update(tick);
            }
        }
    }

    public void tickString(NewTickType tickType, String value) {
        if (tickType == NewTickType.LAST_TIMESTAMP) {
//            long t = Long.parseLong(value) * 1000;
//            puts( "LAST_TIMESTAMP: " + new Date(t));
        }
    }

    @Override
    public void tickSnapshotEnd() {
        puts("tickSnapshotEnd()");
    }

    @Override
    public void marketDataType(Types.MktDataType marketDataType) {
        boolean frozen = marketDataType == Types.MktDataType.Frozen;
        puts("marketDataType(" + frozen + ")");
    }

    protected void makeBar(com.ib.controller.Bar ib_bar) {
        Date t = new Date(ib_bar.time() * 1000L + calculator.bar_size / 2);
//        if (!bars5s.containsKey(t)) {
        Bar bar = reader.bar(t);
        if (bar == null) {
            bar = writer.create(prev, ib_bar);
        } else if (bar.isLast()) {
            bar = writer.update(prev, bar, ib_bar);
        }
//            bars5s.put(bar.getTime(), bar);
        prev = bar;
        puts(bar);
        updateTrigger(bar);
//           for (Service service : Commander.updatables(this.fund)) {
//                Commander.barServices(this.fund)
//            for (Updatable service : Commander.updatables(this.fund)) {
//                service.update(bar);
//            }
//            updateLastBars(bar);
//            currentBar = bar;

    }

    @Override
    public boolean isAsynchronous() {
        return true;
    }

    @Override
    public void tickPrice(TickType tickType, double price, int canAutoExecute) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void tickSize(TickType tickType, int size) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void tickString(TickType tickType, String value) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }


//     /**
//     * Обновление последнего бара
//     *
//     * @param bar последний 5s bar
//     */
//    protected void update(Bar bar) {
//        
//    }
    /**
     * Обновление последнего бара
     *
     * @param last новый последний бар
     */
//    @Override
//    public void updateLast(Bar last) {
//    }
//    @Override
//    public Bar make(com.ib.controller.Bar ib_bar) {
//        prev = super.make(ib_bar); // мы должны вновь созданный бар связать с текущим тиковым баром
//        writer.updateOpenRate(prev, last);
//        return prev;
//    }
}

/* SELLAR
 */

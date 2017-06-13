/*
 * Copyright (c) 2004 - 2015, EugeneLab. All Rights Reserved
 */
package com.eugenelab.tram.util;

import com.eugenelab.tram.database.Frame;
import com.eugenelab.tram.database.Fund;
import com.eugenelab.tram.interfaces.Barable;
import static com.eugenelab.tram.util.Constant.FORMAT_IB_REQUEST;
import static com.eugenelab.tram.util.MarketData.initContract;
import com.ib.controller.ApiController;
import com.ib.controller.ApiController.IHistoricalDataHandler;
import com.ib.controller.NewContract;
import com.ib.controller.Types;
import java.util.Date;

/**
 *
 * @author eugene
 */
public class HistoryReader implements IHistoricalDataHandler {

    private final Barable service;
    private final Frame frame;
    private final ApiController controller;
    private final NewContract contract;
    private final Types.BarSize barSize;
    private Types.DurationUnit unit;
//    private final Calculator calculator;
    private Period period;
    private boolean isIncrement;
    private String endDateTime;
    private int duration;
    public static final int HISTORICAL_SECONDS_LIMITATION = 10000;

    public HistoryReader(Barable service, ApiController controller, Fund fund, Frame frame) {
        this.service = service;
        this.controller = controller;
        this.frame = frame;
        this.contract = initContract(fund);
        this.barSize = MarketData.barSize(frame.getId());
        this.unit = MarketData.unit(frame.getUnit());
//        this.endDateTime = FORMAT_IB_REQUEST.format(new Date());
//        this.duration = frame.getDuration();
    }

    /**
     * Считываем бары для одного дня
     *
     * @param endTime - конец дня
     */
    public void setDate(Date endTime) {
        this.endDateTime = FORMAT_IB_REQUEST.format(endTime);
        if (this.unit == Types.DurationUnit.SECOND) {
            duration = HISTORICAL_SECONDS_LIMITATION;
        } else {
            this.unit = Types.DurationUnit.DAY;
//            this.duration = 2; // вынужден выставлять 2 дня потому что библиотека глючит с одинм днем
            this.duration = 1; // проверяю работоспособность с одним днем
        }
    }

    /**
     * Считываем бары для произвольного периода
     *
     * @param period
     */
    public void setPeriod(Period period) {
        this.period =  new Period(period);
        this.unit = Types.DurationUnit.SECOND;
        if (this.period.getSeconds() > HISTORICAL_SECONDS_LIMITATION) { // требуется несколько итераций
            isIncrement = true;
            this.duration = HISTORICAL_SECONDS_LIMITATION; // максимальное число баров
            this.period.begin = new Date(period.begin.getTime() + HISTORICAL_SECONDS_LIMITATION * 1000L); // сдвигаем начало периода для следующей итерации
            this.endDateTime = FORMAT_IB_REQUEST.format(this.period.begin);
        } else { // считаем всего один раз 
            isIncrement = false;
            this.endDateTime = FORMAT_IB_REQUEST.format(this.period.end);
            this.duration = (int) this.period.getSeconds();
        }
//        int duration_sec = MarketData.duration(this.unit);
//        Date shiftedDateTime = new Date(endTime.getTime()+Constant.TWO_HOURS);
//        this.endDateTime = FORMAT_IB_REQUEST.format(shiftedDateTime);
//        this.duration = period_sec / duration_sec;
    }

    @Override
    public void historicalData(com.ib.controller.Bar ib_bar, boolean hasGaps) {
        service.make(ib_bar);
    }

    @Override
    public void historicalDataEnd() {
        if ( isIncrement ) {
            setPeriod(this.period);
            request();
        } else {
//        controller.cancelHistoricalData(this);
            service.complete();
        }
    }

//    public void requestNow(Frame frame, int duration) {
//        int seconds = duration * frame.getId();
//        String dateTimeNow = FORMAT_IB_REQUEST.format(new Date());
//        controller.reqHistoricalData(
//                contract, // contract - This class contains attributes used to describe the contract
//                dateTimeNow, // endDateTime - yyyymmdd hh:mm:ss tmz
//                seconds, // This is the time span the request will cover, and is specified using the format: <integer> <unit>, 
//                Types.DurationUnit.SECOND, // i.e., 1 D, where valid units are: S (seconds), D (days), W (weeks), M (months), Y (years)
//                //                calculator.days, // This is the time span the request will cover, and is specified using the format: <integer> <unit>, 
//                //                Types.DurationUnit.DAY, // i.e., 1 D, where valid units are: S (seconds), D (days), W (weeks), M (months), Y (years)
//                Types.BarSize._1_min, // 1 sec .. 1 day
//                Types.WhatToShow.MIDPOINT, // TRADES, MIDPOINT, BID, ASK, BID_ASK, HISTORICAL_VOLATILITY, OPTION_IMPLIED_VOLATILITY
//                false, // useRTH Determines whether to return all data available during the requested time span, 
//                // or only data that falls within regular trading hours. 
//                // false - all data is returned even where the market in question was outside of its regular trading hours.
//                // true - only data within the regular trading hours is returned
//                this);
//    }
    public void request() {
        // коррекция для исторического запроса для 5s
        if (unit == Types.DurationUnit.SECOND && duration > HISTORICAL_SECONDS_LIMITATION) {
            duration = HISTORICAL_SECONDS_LIMITATION;
        }
        controller.reqHistoricalData(
                contract, // contract - This class contains attributes used to describe the contract
                endDateTime, // endDateTime - yyyymmdd hh:mm:ss tmz
                duration, // This is the time span the request will cover, and is specified using the format: <integer> <unit>, 
                unit, // i.e., 1 D, where valid units are: S (seconds), D (days), W (weeks), M (months), Y (years)
                //                calculator.days, // This is the time span the request will cover, and is specified using the format: <integer> <unit>, 
                //                Types.DurationUnit.DAY, // i.e., 1 D, where valid units are: S (seconds), D (days), W (weeks), M (months), Y (years)
                barSize, // 1 sec .. 1 day
                Types.WhatToShow.MIDPOINT, // TRADES, MIDPOINT, BID, ASK, BID_ASK, HISTORICAL_VOLATILITY, OPTION_IMPLIED_VOLATILITY
                false, // useRTH Determines whether to return all data available during the requested time span, 
                // or only data that falls within regular trading hours. 
                // false - all data is returned even where the market in question was outside of its regular trading hours.
                // true - only data within the regular trading hours is returned
                this);
    }

    // TODO  Здесь нужно делать запрос на считываение бара не из истории, а life данные иначе идет переполнение запросов
    // https://www.interactivebrokers.com/en/software/api/apiguide/tables/historical_data_limitations.htm
//    private void readLastBarFromIB() {
//        Types.BarSize barSize = BarTimerTask.barSize(this.frame.getId());
//        String timeStr = timeRoundedMinute(new Date());
//        controller.reqHistoricalData(
//                MarketTick.initContract(this.fund), // contract - This class contains attributes used to describe the contract
//                timeStr, // endDateTime - yyyymmdd hh:mm:ss tmz
//                this.frame.getId(), // This is the time span the request will cover, and is specified using the format: <integer> <unit>, 
//                Types.DurationUnit.SECOND, // i.e., 1 D, where valid units are: S (seconds), D (days), W (weeks), M (months), Y (years)
//                barSize, // 1 sec .. 1 day
//                Types.WhatToShow.MIDPOINT, // TRADES, MIDPOINT, BID, ASK, BID_ASK, HISTORICAL_VOLATILITY, OPTION_IMPLIED_VOLATILITY
//                false, // useRTH Determines whether to return all data available during the requested time span, 
//                // or only data that falls within regular trading hours. 
//                // false - all data is returned even where the market in question was outside of its regular trading hours.
//                // true - only data within the regular trading hours is returned
//                this);
//    }
}

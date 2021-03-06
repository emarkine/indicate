/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eugenelab.tram.util;

import com.eugenelab.tram.domain.Fund;
import com.ib.client.Contract;
import com.ib.client.Types;
import com.ib.client.Types.DurationUnit;
import com.ib.client.Types.BarSize;
import com.ib.client.Types.SecType;

/**
 *
 * @author eugene
 */
public class MarketData {

    public MarketData() {
    }

    public static DurationUnit unit(String name) {
        if ("second".equals(name)) {
            return DurationUnit.SECOND;
        } else if ("day".equals(name)) {
            return DurationUnit.DAY;
        } else if ("week".equals(name)) {
            return DurationUnit.WEEK;
        } else if ("month".equals(name)) {
            return DurationUnit.MONTH;
        } else if ("year".equals(name)) {
            return DurationUnit.YEAR;
        } else {
            return null;
        }
    }

    public static int duration(DurationUnit unit) {
        if (unit == DurationUnit.SECOND) {
            return 1;
        } else if (unit == DurationUnit.DAY) {
            return 86400;
        } else if (unit == DurationUnit.WEEK) {
            return 604800;
        } else if (unit == DurationUnit.MONTH) {
            return 2592000;
        } else if (unit == DurationUnit.YEAR) {
            return 31557600;
        } else {
            return 1;
        }
    }

    public static int duration(String name) {
        return duration(unit(name));
    }

    public static BarSize barSize(int ms) {
        int sec = ms / 1000;
        switch (sec) {
            case 1:
                return BarSize._1_secs;
            case 5:
                return BarSize._5_secs;
            case 10:
                return BarSize._10_secs;
            case 15:
                return BarSize._15_secs;
            case 30:
                return BarSize._30_secs;
            case 60:
                return BarSize._1_min;
            case 120:
                return BarSize._2_mins;
            case 180:
                return BarSize._3_mins;
            case 300:
                return BarSize._5_mins;
            case 600:
                return BarSize._10_mins;
            case 900:
                return BarSize._15_mins;
            case 1200:
                return BarSize._20_mins;
            case 1800:
                return BarSize._30_mins;
            case 3600:
                return BarSize._1_hour;
            case 14400:
                return BarSize._4_hours;
            case 86400:
                return BarSize._1_day;
            case 604800:
                return BarSize._1_week;
        }
        return null;
    }

    public static Contract initContract(Fund fund) {
        Contract contract = new Contract();
        switch (fund.getCategory()) {
            case "index":
                contract.symbol(fund.getUnderlying());
                contract.secType(Types.SecType.IND);
                contract.exchange(fund.getExchange());
                contract.currency(fund.getCurrency().getCode());
                break;
            case "future":
                contract.symbol(fund.getProduct());
                contract.secType(Types.SecType.FUT);
                contract.lastTradeDateOrContractMonth(fund.getContractMonthString());
                contract.exchange(fund.getExchange());
                contract.tradingClass(fund.getTradingClass());
                contract.currency(fund.getCurrency().getCode());
                break;
            case "option":
                break;
            case "sprinter":
                break;
            case "commodity":
                contract.symbol(fund.getProduct());
                contract.secType(Types.SecType.FUT);
                contract.lastTradeDateOrContractMonth(fund.getContractMonthString());
                contract.exchange(fund.getExchange());
                contract.currency(fund.getCurrency().getCode());
                contract.tradingClass(fund.getTradingClass());
                break;
            case "valuta":
                contract.symbol(fund.getUnderlying());
                contract.secType(Types.SecType.CASH);
                contract.exchange(fund.getExchange());
                contract.currency(fund.getCurrency().getCode());
                break;
            case "stock":
                break;
            default:
                break;
        }
        return contract;
    }

}

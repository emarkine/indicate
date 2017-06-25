/*
 * Copyright (c) 2004 - 2015, EugeneLab. All Rights Reserved
 */
package com.eugenelab.tram.service;

import com.eugenelab.tram.domain.Point;
import com.eugenelab.tram.domain.ServiceData;
import com.eugenelab.tram.util.MarketData;
import com.ib.controller.ApiController;
import com.ib.controller.ApiController.ITopMktDataHandler;
import com.ib.client.Contract;
import com.ib.client.TickType;
import com.ib.controller.NewTickType;
import com.ib.controller.Types;
import java.math.BigDecimal;
import java.math.RoundingMode;
import javax.persistence.EntityManager;

/**
 *
 * @author eugene
 */
public class LineServiceApiController extends Service implements ITopMktDataHandler {

    private final Contract contract;
    private boolean hasOpen = false;
    private boolean hasClose = false;
    private boolean canceled = false;

    public LineServiceApiController(ServiceData source, EntityManager manager, ApiController controller) {
        super(source, manager, controller);
        contract = MarketData.initContract(this.fund);
        print = true;
    }

    @Override
    public void start() {
        super.start();
        controller.reqTopMktData(contract, "", false, this);
    }

    @Override
    public void run() {
        time();
        if (hasOpen && hasClose) {
            if (!canceled) {
                controller.cancelTopMktData(this);
                canceled = true;
                puts("cancel top marked data");
            }
        }
        runtime();
    }

    public void tickPrice(NewTickType tickType, double price, int canAutoExecute) {
        switch (tickType) {
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
                break;
        }
    }

    public void tickSize(NewTickType tickType, int size) {
    }

    public void tickString(NewTickType tickType, String value) {
    }

    @Override
    public void tickSnapshotEnd() {
    }

    public void marketDataType(Types.MktDataType marketDataType) {
    }

    protected void savePoint(String set_name, double price) {
        Point point = writer.createPoint(System.currentTimeMillis(), price, set_name);
        manager.persist(point);
        transaction();
        puts(point);
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

    @Override
    public void marketDataType(com.ib.client.Types.MktDataType marketDataType) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}

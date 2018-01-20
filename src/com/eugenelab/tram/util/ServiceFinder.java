/*
 * Copyright (c) 2004 - 2015, EugeneLab. All Rights Reserved
 */
package com.eugenelab.tram.util;

import com.eugenelab.tram.domain.Indicator;
import com.eugenelab.tram.domain.Setting;
import com.eugenelab.tram.service.AccountService;
import com.eugenelab.tram.service.BarService;
import com.eugenelab.tram.service.BinaryService;
import com.eugenelab.tram.service.BollingerService;
import com.eugenelab.tram.service.CrystalService;
import com.eugenelab.tram.service.ExponentialMovingAverageService;
import com.eugenelab.tram.service.HistoryService;
import com.eugenelab.tram.service.LineService;
import com.eugenelab.tram.service.MovingAverageConvergenceDivergenceService;
import com.eugenelab.tram.service.ParabolicStopAndReverseService;
import com.eugenelab.tram.service.RandomService;
import com.eugenelab.tram.service.RelativeStrengthIndexService;
import com.eugenelab.tram.service.SimpleMovingAverageService;
import com.eugenelab.tram.service.SolutionService;
import com.eugenelab.tram.service.StateService;
import com.eugenelab.tram.service.TickService;
import com.eugenelab.tram.service.TrendService;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author eugene
 */
public class ServiceFinder {
        private final static Map<String, Class> SERVICES = new TreeMap<>();

    static {
        SERVICES.put("state", StateService.class);
        SERVICES.put("tick", TickService.class);
        SERVICES.put("bar", BarService.class);
        SERVICES.put("history", HistoryService.class);
        SERVICES.put("trend", TrendService.class);
        SERVICES.put("sol", SolutionService.class);
        SERVICES.put("line", LineService.class);
        SERVICES.put("ema", ExponentialMovingAverageService.class);
        SERVICES.put("sma", SimpleMovingAverageService.class);
        SERVICES.put("macd", MovingAverageConvergenceDivergenceService.class);
        SERVICES.put("rsi", RelativeStrengthIndexService.class);
        SERVICES.put("bol", BollingerService.class);
        SERVICES.put("sar", ParabolicStopAndReverseService.class);
        SERVICES.put("account", AccountService.class);
        SERVICES.put("random", RandomService.class);
        SERVICES.put("binary", BinaryService.class);
        SERVICES.put("crystal", CrystalService.class);
    }
    
    public static final Class find(Setting setting) {
        Indicator indicator = setting.getIndicator();
        if (indicator.getName().equals("crystal")) {
            return CrystalService.class;
        } else {
            return find(setting.getName());
        }
    }

    public static final Class find(String service_name) {
        if (service_name.startsWith("ema")) {
            service_name = "ema";
        } else if (service_name.startsWith("sma")) {
            service_name = "sma";
        } else if (service_name.startsWith("sol")) {
            service_name = "sol";
        } else if (service_name.startsWith("binary")) {
            service_name = "binary";
        } else if (service_name.startsWith("crystal")) {
            service_name = "crystal";
        }
        return SERVICES.get(service_name);
    }
    
}

/*
 * Copyright (c) 2004 - 2018, EugeneLab. All Rights Reserved
 */
package com.eugenelab.tram.service;

import com.eugenelab.tram.domain.Point;
import com.eugenelab.tram.domain.ServiceData;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import javax.persistence.EntityManager;

/**
 * Получение данных о состоянии сервисов
 *
 * @author eugene
 */
public class NodeService extends Service {

    protected Process process;
    protected BufferedReader bufferedReader;

    public NodeService(ServiceData data, EntityManager manager) {
        super(data, manager);
        this.print = true;
    }

    /**
     * Connection to Arduino
     */
    @Override
    public void start() {
        manager.getTransaction().begin();
        try {
//            process = Runtime.getRuntime().exec("screen /dev/ttyACM0");
            process = Runtime.getRuntime().exec("bin/screen.usb.sh");
            bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.start();
        manager.getTransaction().commit();
    }

    /**
     *
     */
    @Override
    public void run() {
        try {
            String line = null;
            do {
                line = bufferedReader.readLine();
                try {
                    int value = Integer.parseInt(line);
                    puts(value);
                    manager.getTransaction().begin();
                    Point point = writer.createPoint(value);
                    point.setService(data);
                    manager.getTransaction().commit();
                    puts(point);
                } catch (NumberFormatException e) {
               }
            } while (line != null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.run();
    }

    /**
     *
     */
    @Override
    public void stop() {
        manager.getTransaction().begin();
        try {
            process.waitFor();
            puts("exit: " + process.exitValue());
            process.destroy();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.stop();
        manager.getTransaction().commit();
    }

}

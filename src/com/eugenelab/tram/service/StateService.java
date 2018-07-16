/*
 * Copyright (c) 2004 - 2018, EugeneLab. All Rights Reserved
 */
package com.eugenelab.tram.service;

import com.eugenelab.tram.domain.ServiceData;
import com.ib.controller.ApiController;
import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.persistence.EntityManager;

/**
 * Получение данных о состоянии сервисов
 *
 * @author eugene
 */
public class StateService extends Service {

    private List<ServiceData> services;
    private Map<Integer,Process> processes = new TreeMap<Integer,Process>();

    public static long getPID() {
    String processName =
      java.lang.management.ManagementFactory.getRuntimeMXBean().getName();
    return Long.parseLong(processName.split("@")[0]);
  }

    public StateService(ServiceData data, EntityManager manager, ApiController controller) {
        super(data, manager, controller);
        this.print = true;
        services = reader.services_by_host(data.getHost());
        services.remove(data);
        String msg = "My PID is " + StateService.getPID();
        javax.swing.JOptionPane.showConfirmDialog((java.awt.Component)
        null, msg, "SystemUtils", javax.swing.JOptionPane.DEFAULT_OPTION);
//        final RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
//        final long pid = runtime.getPid();
//        final Console console = System.console();
//        out.println("Process ID is '" + pid + "' Press <ENTER> to continue.");
//        console.readLine();
    }

    /**
     *
     */
    @Override
    public void start() {
        manager.getTransaction().begin();
        System.err.println("error");
//        for (ServiceData data : services) {
//            puts(data.getCommand());
//            try {
//                Process process = Runtime.getRuntime().exec("/home/indicate/bin/" + data.getCommand());
//                if (process.isAlive()) {
//                    processes.put(data.getId(),process);
////                    processes.pid();
//                    Field field = process.getClass().getDeclaredField("pid");
//                    field.setAccessible(true);
//                    int pid = field.getInt(processes);
//                    puts("pid: " + pid);
//                } else {
//                }
//            } catch (Exception e) {
//                e.printStackTrace(System.err);
//            }
//        }
        super.start();
        manager.getTransaction().commit();
    }

    /**
     *
     */
    @Override
    public void run() {
        manager.getTransaction().begin();
        super.run();
        puts(service_time);
        manager.getTransaction().commit();
    }

    /**
     *
     */
    @Override
    public void stop() {
        manager.getTransaction().begin();
        for (Process process : processes.values()) {
            if (process.isAlive()) {
                process.destroy();
            }
        }
        super.stop();
        manager.getTransaction().commit();
    }

}


/*
        super(data, manager, controller);
        this.print = true;
        List<ServiceData> listServiceData = reader.services_by_host(data.getHost());
        listServiceData.remove(data);
        for (ServiceData sd : listServiceData) {
            puts(sd.getCommand());
            try {
                Process process = Runtime.getRuntime().exec("/home/indicate/bin/" + sd.getCommand());
                processes.add(process);
                BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String s;
                while ((s = br.readLine()) != null) {
                    System.out.println("line: " + s);
                }
                process.waitFor();
                System.out.println("exit: " + process.exitValue());
                process.destroy();
            } catch (Exception e) {
                e.printStackTrace(System.err);
            }
        }
 */

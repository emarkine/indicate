/*
 * Copyright (c) 2004 - 2015, EugeneLab. All Rights Reserved
 */
package com.eugenelab.tram.service;

import com.eugenelab.tram.domain.Fund;
import com.eugenelab.tram.util.ServiceTimerTask;
import com.eugenelab.tram.interfaces.Serviceable;
import com.eugenelab.tram.domain.Host;
import com.eugenelab.tram.domain.Indicator;
import com.eugenelab.tram.util.Parser;
import com.eugenelab.tram.domain.ServiceData;
import com.eugenelab.tram.domain.Setting;
import static com.eugenelab.tram.util.Constant.FORMAT;
import com.eugenelab.tram.util.Reader;
import com.eugenelab.tram.util.ServiceFinder;
import com.eugenelab.tram.util.Writer;
import com.ib.controller.ApiConnection;
import com.ib.controller.ApiController;
import java.lang.reflect.Constructor;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TreeMap;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

/**
 *
 * @author eugene
 *
 * Пересчет индикаторов
 *
 */
public class Commander {

    public static Parser ARG;
    public final static double HUNDRED_MILLISECOND = 0.1;
    public final static int ONE_SECOND = 1;
    public final static int ONE_MINUTE = 60;
    public final static int TWO_MINUTES = 120;
    public final static int THREE_MINUTES = 180;
    public final static int FIVE_MINUTES = 300;
    public final static int TEN_MINUTES = 600;
    public final static int FIFTEEN_MINUTES = 900;
    private final EntityManager manager;
    private EntityManager sdb_manager;
    private ApiController controller;
    public final static List<Service> services = new ArrayList<>();
    private final Timer timer = new Timer();
    private static Host host;

    public static void main(String args[]) throws Exception {
        ARG = new Parser(args);
        Commander commander = new Commander();
//        EntityManager manager = Persistence.createEntityManagerFactory("TramPU").createEntityManager();
//        Commander commander = new Commander(initDatabase(args));
//        Commander commander = new Commander(manager);
//        EntityManager sdb_manager = Persistence.createEntityManagerFactory("SDB").createEntityManager();
//        Commander commander = new Commander(manager, sdb_manager);
//        Commander commander = new Commander(initDatabase(args), sdb_factory.createEntityManager());
        String host_name = InetAddress.getLocalHost().getHostName();
        if (host_name.indexOf('.') != -1) {
            host_name = host_name.substring(0, host_name.indexOf('.'));
        }
        System.out.println("host_name: " + host_name);
        host = Reader.host(commander.manager, host_name);
        System.out.println("Host: " + host);
        System.out.println("IB Client ID: " + ARG.getIBClientId());
        if (ARG.getItem("single") != null) { // передана команда на запуск единичного сервиса
            ServiceData data = Reader.single_service(commander.manager);
            Serviceable service = commander.initService(data,host);
            service.setSingle(true);
            commander.startService(service);
            if (!service.isAsynchronous()) { // сервис не должен асинхронно обновляться
                System.exit(0); // вываливаемся
            }
        } else if (ARG.getItem("name") != null) { // передана команда на запуск сервиса по имени
            ServiceData data = Reader.service(commander.manager, ARG.getItem("name"));
            Serviceable service = commander.initService(data,host);
            commander.startService(service);
        } else if (ARG.getItem("group") != null) { // передана команда на запуск группы сервисов 
            List<ServiceData> list = Reader.services_by_group(commander.manager, ARG.getItem("group"));
            if (list.isEmpty()) {
                System.err.println("No services found for group: " + ARG.getItem("group"));
                System.exit(0); // вываливаемся
            } else {
                commander.initServices(list, host);
                commander.start();
            }
        } else {
            List<ServiceData> list = Reader.services(commander.manager);
            commander.initServices(list, host);
            commander.start();
        }
//        if ( ARG.getDate() != null && ARG.getTime() == null) { // мы в режиме расчета по переданной дате
//            System.exit(0); // вываливаемся
//        }
//        long ms = new Date().getTime() - service.init_time.getTime();
//        System.out.println("init: " + ms + " ms");
    }

    public Commander() {
        this.manager = connectDatabase();
//        this.manager = Persistence.createEntityManagerFactory("TramPU").createEntityManager();
//        this.sdb_manager = Persistence.createEntityManagerFactory("SDB").createEntityManager();
    }

    public static ApiController initController(ApiConnection.ILogger logger, int clientId) {
        ApiController controller = new ApiController(new ConnectionHandler(), logger, logger);
        controller.connect(ARG.getIBHost(), ARG.getIBPort(), clientId, null);
        return controller;
    }

    public static EntityManager connectDatabase() {
        System.out.println("Database Connecting...");
        EntityManagerFactory factory = Persistence.createEntityManagerFactory("TramPU");
        return factory.createEntityManager();
    }

    public static EntityManager connectDatabaseParams() {
        System.out.println("Database Connecting...");
        Map<String, String> persistenceMap = new HashMap<String, String>();
        String url = "jdbc:mysql://" + ARG.getDataBaseHost() + ":"
                + ARG.getDataBasePort() + "/" + ARG.getDataBaseName()
                + "?zeroDateTimeBehavior=convertToNull";
        persistenceMap.put("javax.persistence.jdbc.driver", "com.mysql.jdbc.Driver");
        persistenceMap.put("javax.persistence.jdbc.url", url);
        persistenceMap.put("javax.persistence.jdbc.user", ARG.getDataBaseUser());
        persistenceMap.put("javax.persistence.jdbc.password", ARG.getDataBasePassword());
//        if (ARG.hasDataBasePassword()) {
//            url += "&password=" + ARG.getDataBasePassword();
//        }
        EntityManagerFactory factory
                = Persistence.createEntityManagerFactory("TramPU", persistenceMap);
        System.out.println("database url: " + url);
        return factory.createEntityManager();
    }

//    public Commander(ApiController controller, EntityManager manager, EntityManager sdb_manager) {
//        this.controller = controller;
//        this.manager = manager;
//        this.sdb_manager = sdb_manager;
//    }
//
//    public Commander(EntityManager manager, EntityManager sdb_manager) {
//        this.manager = manager;
//        this.sdb_manager = sdb_manager;
//    }
//    public Commander(EntityManager manager) {
//        this.manager = manager;
//    }
    /**
     * Инициализация сервиса - нахождение и вызов его конструктора, передача
     * праметров
     */
    private Serviceable initService(ServiceData data, Host host) throws Exception {
//        if (data.isActive()) {
//            System.out.println(data);
//        }
        Setting setting = data.getSetting();
        if (setting == null) {
            System.err.println("No settings for " + data);
        }
        String set_name = setting.getName();
        if (set_name == null) {
            System.err.println("No name for settings " + setting);
        }
        Class service_class = ServiceFinder.find(setting);
//        Class service_class = ServiceFinder.find(set_name);
        Service service;
        // надо выбрать правильный конструктор
        if (set_name.startsWith("tick")
                || set_name.startsWith("bar")
                || set_name.startsWith("history")
                || set_name.startsWith("account")) {
            if (controller == null) { // надо запустить контроллер
                this.controller = initController(new Logger(), ARG.getIBClientId());
            }
            Constructor<Service> constructor = service_class.getConstructor(ServiceData.class, EntityManager.class, ApiController.class);
            service = constructor.newInstance(data, manager, controller);
        } else {
            Constructor<Service> constructor = service_class.getConstructor(ServiceData.class, EntityManager.class);
            service = constructor.newInstance(data, manager);
        }
        if (ARG.isForse()) {
            service.force = true;
        }
        if (ARG.getDate() != null) {
            service.setDate(ARG.getDate());
//            if (ARG.getTime() != null) {
//                data.setStartTime(ARG.getTime());
//            }
        }
        services.add(service);
        service.setHost(host);
        service.setState("add");
        return service;
    }

    /**
     * Запуск сервиса
     *
     * @param service
     */
    public void startService(Serviceable service) {
        System.out.println(service);
        service.start();
//        if (service.isRefresh() && !service.isUpdatable() && !service.isSingle()) { 
        // Таймер запускаем только в случае если есть время обнавления 
        if (service.isRefresh() && !service.isSingle()) {
            ServiceTimerTask task = new ServiceTimerTask(service);
            timer.schedule(task, task.alignedTime(), task.frameMs());
            System.out.println("Servive[" + service.getId() + "] timer(start: " + FORMAT.format(task.alignedTime()) + ", refresh: " + service.refreshPeriod() + "s)");
        } else { // иначе просто выполняем один раз метод run
            service.run();
            service.setState("run");
        }
    }

    /**
     * Инициализация сервисов
     */
    private void initServices(List<ServiceData> list, Host host) throws Exception {
        for (ServiceData service : list) {
//            if (host.equals(service.getHost())) {
            initService(service, host);
//            }
        }
    }

    /**
     * Запуск задач и сервисов
     */
    public void start() {
//        CommandTimerTask commandTask = new CommandTimerTask(manager);
//        timer.schedule(commandTask, commandTask.alignedTime(), commandTask.frameMs());
        for (Serviceable service : services) {
            if (service.isActive()) {// это активный сервис, который надо запускать всегда
                startService(service);
            }
        }

//        this.manager.getTransaction().begin();
//        command.setAction("accepted");
//        this.manager.persist(command);
//        try {
//            Service service = command.getService();
//            service.setStatus("starting");
//            this.manager.persist(service);
//        } catch (Exception e) {
//            System.err.println(e.getMessage());
//            command.setAction("error");
//            command.setMessage(e.getMessage());
//            this.manager.persist(command);
//        } finally {
////            this.manager.getTransaction().commit();
//        }
    }

    /**
     * Остановка работающих сервисов
     */
    public void stop() {
        timer.cancel();

    }

    /**
     * Обработака активных сервисов
     */
//    public void make() {
//        for (Service service : services) {
//            service.make();
//        }
//
//    }
//    private class CommandTimerTask extends MarketTask {
//
////        private final Commander commander;
//        private final EntityManager manager;
//
//        public CommandTimerTask(EntityManager manager) {
//            super(1);
//            this.manager = manager;
////            this.commander = commander;
//        }
//
//        private void checkCommands() {
//            Query query = manager.createNamedQuery("Command.findAll");
//            List<Command> list = query.getResultList();
//            for (Command command : list) {
//                if (command.getStatus().equals("waiting")) {
//                    Service service = command.getService();
//                    if (command.getName().equals("start")) {
////                        service.init(manager, command.getFund(), command.getFrame());
////                        initService(service);
//                    }
//                    if (command.getName().equals("stop")) {
////                        service.stop();
//                    }
//                    manager.getTransaction().begin();
//                    command.setStatus("executed");
//                    manager.persist(command);
//                    manager.getTransaction().commit();
//                    System.out.println(command);
//                }
//
//            }
//        }
//
//        @Override
//        public void run() {
//            checkCommands();
//            System.out.println("CommanderTimerTask: " + FORMAT.format(new Date()));
//        }
//    }
    private static class ConnectionHandler implements ApiController.IConnectionHandler {

        @Override
        public void connected() {
            System.err.println("connected");
        }

        @Override
        public void disconnected() {
            System.err.println("disconnected");
        }

        @Override
        public void accountList(ArrayList<String> list) {
            System.out.println("list[" + list.size() + "]");
            System.out.println(list.get(0));
        }

        @Override
        public void error(Exception e) {
            System.err.println(e.getMessage());
        }

        @Override
        public void message(int id, int errorCode, String errorMsg) {
            System.err.println(errorMsg);
        }

        @Override
        public void show(String string) {
            System.out.println(string);
        }

    }

    public static class Logger implements ApiConnection.ILogger {

        public void log(String value) {
//            System.err.print(value);
        }
    }
}

/**
 * SELLAR
 *
 *
 *
 */

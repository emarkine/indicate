/*
 * Copyright (c) 2004 - 2015, EugeneLab. All Rights Reserved
 */
package com.eugenelab.tram.util;

import java.text.ParseException;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

/**
 * Разбор аргументов коммандной строки
 *
 * @author eugene
 */
public class Parser {
    private static final Map<String,String> DEFAULT = new TreeMap<>();
    //    public final static long TIME_OFFSET = 0;
    //    public final static long TIME_OFFSET = TimeZone.getDefault().getRawOffset();

    private final Map<String,String> list = new TreeMap<>();
    
    static {
//        DEFAULT.put("frame", "1m");
//        DEFAULT.put("fund", "100");
        DEFAULT.put("db_port", "3306");
        DEFAULT.put("db_host", "localhost");
        DEFAULT.put("db_name", "tram_adapt");
        DEFAULT.put("db_user", "root");
        DEFAULT.put("ib_host", "eugenelab.com");
        DEFAULT.put("ib_port", "4001");
        DEFAULT.put("ib_client_id", "1");
    }
    public Parser(String[] argv) {
        Map<String, String> env = System.getenv();
        for (String key : env.keySet()) {
            if ( key.equals("DB_HOST") ) {
                list.put("db_host", env.get(key));
            } else if ( key.equals("DB_PORT") ) {
                list.put("db_port", env.get(key));
            } else if ( key.equals("DB_NAME") ) {
                list.put("db_name", env.get(key));
            } else if ( key.equals("DB_USER") ) {
                list.put("db_user", env.get(key));
            } else if ( key.equals("DB_PASSWORD") ) {
                list.put("db_password", env.get(key));
            } else if ( key.equals("IB_CLIENT_ID") ) {
                list.put("ib_client_id", env.get(key));
            }
        }
        for (int i = 0; i < argv.length; i++) {
            String item = argv[i];
            if (item.startsWith("-")) { // ключ 
                String key = item.substring(1);
                if ( key.equals("force")) {
                    list.put(key, null);
                } else {
                    String value = argv[++i];
                    list.put(key, value);
                }
            }
        }
    }
    
    public String getItem(String key) {
        String value = DEFAULT.get(key);
        if (list.containsKey(key)) {
            value = list.get(key);
        } 
//        System.out.println(key + ": " + value);
        return value;
    }

    public Date getDate() throws ParseException {
        String date = getItem("date");
        if (date != null) {
            Date d = Constant.FORMAT_DATE.parse(date);
            return d;
        } else {
            return null;
        }
    }

    public Date getTime() throws ParseException {
        String time = getItem("time");
        if (time != null)
            return Constant.FORMAT_TIME.parse(time);
        else
            return null;
    }

    public String getName() {
        return getItem("name");
    }

    public String getDataBasePort() {
        return getItem("db_port");
    }
    
    public String getDataBaseHost() {
        return getItem("db_host");
    }
    
    public String getDataBaseName() {
        return getItem("db_name");
    }
    
    public String getDataBaseUser() {
        return getItem("db_user");
    }
    
    public boolean hasDataBasePassword() {
         String password = getItem("db_password");
         return password != null && !password.isEmpty();
    }
    
    public String getDataBasePassword() {
        return getItem("db_password");
    }
    
    public String getIBHost() {
        return getItem("ib_host");
    }

    public int getIBPort() {
        return Integer.parseInt(getItem("ib_port"));
    }

    public int getIBClientId() {
        return Integer.parseInt(getItem("ib_client_id"));
    }
    
    public boolean isForse() {
        return list.containsKey("force");
    }
}

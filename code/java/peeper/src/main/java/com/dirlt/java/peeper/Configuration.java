package com.dirlt.java.peeper;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: dirlt
 * Date: 12/8/12
 * Time: 1:32 AM
 * To change this template use File | Settings | File Templates.
 */
public class Configuration {
    private String ip = "0.0.0.0";
    private int port = 8001;
    private int proxyQueueSize = 4096;
    private String serviceName = "peeper";
    private boolean debug = true;
    private boolean stat = true;
    private Map<String, String> kv = new HashMap<String, String>();

    public boolean parse(String[] args) {
        for (String arg : args) {
            if (arg.startsWith("--ip=")) {
                ip = arg.substring("--ip=".length());
            } else if (arg.startsWith("--port=")) {
                port = Integer.valueOf(arg.substring("--port=".length())).intValue();
            } else if (arg.startsWith("--proxy-queue-size=")) {
                proxyQueueSize = Integer.valueOf(arg.substring("--proxy-queue-size=".length()));
            } else if (arg.startsWith("--service-name=")) {
                serviceName = arg.substring("--service-name=".length());
            } else if (arg.startsWith("--no-debug")) {
                debug = false;
            } else if (arg.startsWith("--no-stat")) {
                stat = false;
            } else if (arg.startsWith("--kv=")) {
                String s = arg.substring("--kv=".length());
                String[] ss = s.split(":");
                kv.put(ss[0], ss[1]);
            } else {
                return false;
            }
        }
        return true;
    }

    public static void usage() {
        System.out.println("peeper");
        System.out.println("\t--ip # default 0.0.0.0");
        System.out.println("\t--port # default 8001");
        System.out.println("\t--proxy-queue-size # default 4096");
        System.out.println("\t--service-name # set service name");
        System.out.println("\t--no-debug # turn off debug mode");
        System.out.println("\t--no-stat # turn off statistics");
        System.out.println("\t--kv=<key>:<value> # key value pair");
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(String.format("stat=%s, debug=%s\n", isStat(), isDebug()));
        sb.append(String.format("ip=%s, port=%d\n", getIp(), getPort()));
        sb.append(String.format("service-name=%s\n", getServiceName()));
        sb.append(String.format("proxy-queue-size=%d\n", getProxyQueueSize()));
        for (String key : kv.keySet()) {
            sb.append(String.format("kv = %s:%s\n", key, kv.get(key)));
        }
        return sb.toString();
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public int getProxyQueueSize() {
        return proxyQueueSize;
    }

    public String getServiceName() {
        return serviceName;
    }

    public boolean isDebug() {
        return debug;
    }

    public boolean isStat() {
        return stat;
    }

    public Map<String, String> getKv() {
        return kv;
    }
}

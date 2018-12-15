package com.minicreate.online_taxi.config;

import com.minicreate.online_taxi.transmission.strategy.ProtocalHi3520DV300Strategy;
import com.minicreate.online_taxi.transmission.strategy.ProtocolDCStrategy;
import com.minicreate.online_taxi.transmission.strategy.ProtocolBeiDouStrategy;
import com.minicreate.online_taxi.transmission.strategy.Strategy;

/**
 * 服务器配置类，里面包含有各个不同服务器的ip、端口
 * <p>
 * TODO 感觉同时有IP和name不太好，只用name作为唯一标识符可能会好一些
 */
public enum EndpointConfig {
    SERVER_DC("39.108.36.145", 6007, new ProtocolDCStrategy()),
    SERVER_8081("221.179.65.38", 9997, new ProtocolBeiDouStrategy()),
    SERVER_8082("", 0, new ProtocolBeiDouStrategy()),
    SERVER_905("", 0, new ProtocolBeiDouStrategy()),
    SERVER_TEST("39.108.194.249", 6608, new ProtocolBeiDouStrategy()),//测试服务器
    USB("usb-endpoint", new ProtocalHi3520DV300Strategy());

    EndpointConfig(String name, Strategy strategy) {
        this.name = name;
        this.strategy = strategy;
    }

    EndpointConfig(String ip, int port, Strategy strategy) {
        this.ip = ip;
        this.port = port;
        this.strategy = strategy;
    }

    private String ip = "";
    private int port = -1;
    private Strategy strategy;
    private String name;

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public Strategy getStrategy() {
        return strategy;
    }

    public String getName() {
        return name;
    }
}

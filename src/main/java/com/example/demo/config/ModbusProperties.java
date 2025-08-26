package com.example.demo.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix = "modbus")
public class ModbusProperties {
    private String ip;
    private int port;
    private int unitId = 1;
    private int rateMs = 2000;
    private int timeoutMs = 1000;
    private int retries = 1;
    private List<Integer> registers = new ArrayList<>();

    public String getIp() { return ip; }
    public void setIp(String ip) { this.ip = ip; }

    public int getPort() { return port; }
    public void setPort(int port) { this.port = port; }

    public int getUnitId() { return unitId; }
    public void setUnitId(int unitId) { this.unitId = unitId; }

    public int getRateMs() { return rateMs; }
    public void setRateMs(int rateMs) { this.rateMs = rateMs; }

    public int getTimeoutMs() { return timeoutMs; }
    public void setTimeoutMs(int timeoutMs) { this.timeoutMs = timeoutMs; }

    public int getRetries() { return retries; }
    public void setRetries(int retries) { this.retries = retries; }

    public List<Integer> getRegisters() { return registers; }
    public void setRegisters(List<Integer> registers) { this.registers = registers; }
}

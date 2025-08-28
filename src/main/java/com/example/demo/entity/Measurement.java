package com.example.demo.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "measurement")
public class Measurement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "device_name")
    private String deviceName;

    @Column(name = "register_address")
    private Integer registerAddress;

    @Column(name = "measured_value")
    private String value;

    @Column(name = "ts")
    private Instant ts;

    public Measurement() {}

    public Measurement(String deviceName, Integer registerAddress, String value, Instant ts) {
        this.deviceName = deviceName;
        this.registerAddress = registerAddress;
        this.value = value;
        this.ts = ts;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public Integer getRegisterAddress() {
        return registerAddress;
    }

    public void setRegisterAddress(Integer registerAddress) {
        this.registerAddress = registerAddress;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Instant getTs() {
        return ts;
    }

    public void setTs(Instant ts) {
        this.ts = ts;
    }

    @Override
    public String toString() {
        return "Measurement{" +
                "id=" + id +
                ", deviceName='" + deviceName + '\'' +
                ", registerAddress=" + registerAddress +
                ", value='" + value + '\'' +
                ", ts=" + ts +
                '}';
    }
}

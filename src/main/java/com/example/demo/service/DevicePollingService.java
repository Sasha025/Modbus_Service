package com.example.demo.service;

import com.example.demo.config.ModbusProperties;
import com.example.demo.entity.Measurement;
import com.example.demo.repository.MeasurementRepository;
import com.serotonin.modbus4j.code.DataType;
import com.serotonin.modbus4j.exception.ErrorResponseException;
import com.serotonin.modbus4j.exception.ModbusTransportException;
import com.serotonin.modbus4j.locator.BaseLocator;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Gauge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.time.Instant;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class DevicePollingService {
    private static final Logger log = LoggerFactory.getLogger(DevicePollingService.class);

    private final ModbusProperties props;
    private final com.example.demo.config.ModbusManager modbusManager;
    private final MeasurementRepository repo;
    private final MeterRegistry meterRegistry;

    private ScheduledExecutorService scheduler;
    // lastValue map для метрик (ключ: device:register)
    private final ConcurrentMap<String, AtomicReference<Double>> lastValues = new ConcurrentHashMap<>();

    public DevicePollingService(ModbusProperties props,
                                com.example.demo.config.ModbusManager modbusManager,
                                MeasurementRepository repo,
                                MeterRegistry meterRegistry) {
        this.props = props;
        this.modbusManager = modbusManager;
        this.repo = repo;
        this.meterRegistry = meterRegistry;
    }

    @PostConstruct
    public void start() {
        int size = Math.max(1, props.getDevices().size());
        scheduler = Executors.newScheduledThreadPool(size);

        for (ModbusProperties.DeviceConfig d : props.getDevices()) {
            final String deviceName = d.getName() != null ? d.getName() : d.getIp() + ":" + d.getPort();
            final int unitId = d.getUnitId();
            final long rate = Math.max(100, d.getRateMs()); // защита от нулевого rate
            // подготовим lastValue для каждого register и метрики
            for (Integer reg : d.getRegisters()) {
                String key = mkKey(deviceName, reg);
                lastValues.putIfAbsent(key, new AtomicReference<>(Double.NaN));
                AtomicReference<Double> ref = lastValues.get(key);
                Gauge.builder("modbus.last_value", ref, v -> {
                            Double val = v.get();
                            return val == null ? Double.NaN : val;
                        })
                        .description("Last read value for modbus register")
                        .tag("device", deviceName)
                        .tag("register", String.valueOf(reg))
                        .register(meterRegistry);
            }

            Runnable task = () -> {
                modbusManager.getMaster(deviceName).ifPresentOrElse(master -> {
                    for (Integer reg : d.getRegisters()) {
                        try {
                            Number value = master.getValue(
                                    BaseLocator.holdingRegister(unitId, reg, DataType.TWO_BYTE_INT_SIGNED)
                            );
                            String sVal = value == null ? null : value.toString();
                            // лог / консоль
                            log.info("Device={} Holding[{}] = {}", deviceName, reg, sVal);
                            System.out.printf("%s Holding[%d] = %s%n", deviceName, reg, sVal);

                            // сохранение в БД
                            Measurement m = new Measurement(deviceName, reg, sVal, Instant.now());
                            repo.save(m);

                            // метрики
                            meterRegistry.counter("modbus.reads.total", "device", deviceName, "register", String.valueOf(reg)).increment();
                            AtomicReference<Double> ref = lastValues.get(mkKey(deviceName, reg));
                            if (ref != null) {
                                try {
                                    ref.set(value == null ? Double.NaN : value.doubleValue());
                                } catch (Exception ex) {
                                    ref.set(Double.NaN);
                                }
                            }
                        } catch (ModbusTransportException | ErrorResponseException ex) {
                            log.error("Ошибка чтения {} reg {}: {}", deviceName, reg, ex.getMessage());
                            meterRegistry.counter("modbus.read.errors", "device", deviceName).increment();
                        } catch (Exception ex) {
                            log.error("Неожиданная ошибка при чтении {} reg {}: {}", deviceName, reg, ex.toString());
                            meterRegistry.counter("modbus.read.errors", "device", deviceName).increment();
                        }
                    }
                }, () -> {
                    log.warn("Modbus master для устройства {} не инициализирован.", deviceName);
                    meterRegistry.counter("modbus.master.missing", "device", deviceName).increment();
                });
            };

            scheduler.scheduleAtFixedRate(task, 0, rate, TimeUnit.MILLISECONDS);
        }
    }

    private String mkKey(String device, Integer register) {
        return device + ":" + register;
    }

    @PreDestroy
    public void stop() {
        if (scheduler != null) scheduler.shutdownNow();
    }
}

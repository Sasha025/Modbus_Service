package com.example.demo.config;

import com.serotonin.modbus4j.ModbusFactory;
import com.serotonin.modbus4j.ModbusMaster;
import com.serotonin.modbus4j.exception.ModbusInitException;
import com.serotonin.modbus4j.ip.IpParameters;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.util.*;

@Component
public class ModbusManager {

    private final ModbusProperties props;
    private final Map<String, ModbusMaster> masters = new HashMap<>();

    public ModbusManager(ModbusProperties props) {
        this.props = props;
    }

    @PostConstruct
    public void init() {
        ModbusFactory factory = new ModbusFactory();
        for (ModbusProperties.DeviceConfig d : props.getDevices()) {
            String name = d.getName() != null ? d.getName() : d.getIp() + ":" + d.getPort();
            IpParameters ip = new IpParameters();
            ip.setHost(d.getIp());
            ip.setPort(d.getPort());
            ModbusMaster master = factory.createTcpMaster(ip, true);
            master.setTimeout(d.getTimeoutMs());
            master.setRetries(d.getRetries());
            try {
                master.init();
                masters.put(name, master);
            } catch (ModbusInitException e) {
                System.err.println("Не удалось инициализировать Modbus master для " + name + ": " + e.getMessage());
            }
        }
    }

    public Optional<ModbusMaster> getMaster(String name) {
        return Optional.ofNullable(masters.get(name));
    }

    public Map<String, ModbusMaster> getAllMasters() {
        return Collections.unmodifiableMap(masters);
    }

    @PreDestroy
    public void destroy() {
        masters.values().forEach(m -> {
            try { m.destroy(); } catch (Exception ignored) {}
        });
    }
}

package com.example.demo.service;

import com.example.demo.config.ModbusProperties;
import com.serotonin.modbus4j.ModbusMaster;
import com.serotonin.modbus4j.code.DataType;
import com.serotonin.modbus4j.exception.ErrorResponseException;
import com.serotonin.modbus4j.exception.ModbusTransportException;
import com.serotonin.modbus4j.locator.BaseLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class ModbusPollingService {

    private static final Logger log = LoggerFactory.getLogger(ModbusPollingService.class);

    private final ModbusMaster master;
    private final ModbusProperties props;

    public ModbusPollingService(ModbusMaster master, ModbusProperties props) {
        this.master = master;
        this.props = props;
    }

    // Запускаем с задержкой rateMs из конфига
    @Scheduled(fixedDelayString = "${modbus.rateMs}")
    public void poll() {
        if (props.getRegisters() == null || props.getRegisters().isEmpty()) {
            log.warn("Список регистров пуст. Добавь modbus.registers в application.yml");
            return;
        }
        for (Integer reg : props.getRegisters()) {
            try {
                // Читаем holding register как 2-байтовое знаковое целое
                Number value = master.getValue(
                        BaseLocator.holdingRegister(props.getUnitId(), reg, DataType.TWO_BYTE_INT_SIGNED)
                );
                // Вывод в консоль (и лог)
                System.out.printf("Holding[%d] = %s%n", reg, value);
                log.info("Holding[{}] = {}", reg, value);
            } catch (ModbusTransportException | ErrorResponseException e) {
                log.error("Ошибка чтения регистра {}: {}", reg, e.getMessage());
            } catch (Exception e) {
                log.error("Неожиданная ошибка при чтении регистра {}: {}", reg, e.toString());
            }
        }
    }
}

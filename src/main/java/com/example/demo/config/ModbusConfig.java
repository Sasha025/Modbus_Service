//package com.example.demo.config;
//
//import com.serotonin.modbus4j.ModbusFactory;
//import com.serotonin.modbus4j.ModbusMaster;
//import com.serotonin.modbus4j.exception.ModbusInitException;
//import com.serotonin.modbus4j.ip.IpParameters;
//import org.springframework.boot.context.properties.EnableConfigurationProperties;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//@EnableConfigurationProperties(ModbusProperties.class)
//public class ModbusConfig {
//
//    @Bean(destroyMethod = "destroy")
//    public ModbusMaster modbusMaster(ModbusProperties props) throws ModbusInitException {
//        IpParameters params = new IpParameters();
//        params.setHost(props.getIp());
//        params.setPort(props.getPort());
//
//        ModbusFactory factory = new ModbusFactory();
//        // true = keepAlive (удерживать соединение)
//        ModbusMaster master = factory.createTcpMaster(params, true);
//        master.setTimeout(props.getTimeoutMs());
//        master.setRetries(props.getRetries());
//        master.init(); // важный момент!
//        return master;
//    }
//}

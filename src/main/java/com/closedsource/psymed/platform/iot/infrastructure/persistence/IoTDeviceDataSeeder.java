package com.closedsource.psymed.platform.iot.infrastructure.persistence;

import com.closedsource.psymed.platform.iot.domain.model.aggregates.IoTDevice;
import com.closedsource.psymed.platform.iot.infrastructure.persistence.jpa.repositories.IoTDeviceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class IoTDeviceDataSeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(IoTDeviceDataSeeder.class);

    private static final String DEMO_DEVICE_ID = "ESP32_001";
    // TODO(producción): reemplazar por SecureRandom al registrar hardware en fábrica (Opción A).
    private static final String DEMO_PAIRING_CODE = "123456";

    private final IoTDeviceRepository deviceRepository;

    public IoTDeviceDataSeeder(IoTDeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!deviceRepository.existsByDeviceId(DEMO_DEVICE_ID)) {
            deviceRepository.save(IoTDevice.createUnclaimed(DEMO_DEVICE_ID, DEMO_PAIRING_CODE, "default-edge"));
            log.info("Seeded unclaimed IoT device {} with pairing code (demo)", DEMO_DEVICE_ID);
        }
    }
}

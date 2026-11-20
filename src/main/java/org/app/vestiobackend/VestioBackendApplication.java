package org.app.vestiobackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EntityScan(basePackageClasses = {org.app.vestiobackend.entity.User.class, com.vestio.common.entity.Notification.class})
@EnableCaching
public class VestioBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(VestioBackendApplication.class, args);
    }

}

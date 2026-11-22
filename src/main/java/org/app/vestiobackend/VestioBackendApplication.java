package org.app.vestiobackend;

import com.vestio.common.entity.Notification;
import org.app.vestiobackend.entity.User;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EntityScan(basePackageClasses = {User.class, Notification.class})
@EnableCaching
public class VestioBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(VestioBackendApplication.class, args);
    }

}

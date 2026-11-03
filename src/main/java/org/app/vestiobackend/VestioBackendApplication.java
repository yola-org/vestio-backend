package org.app.vestiobackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class VestioBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(VestioBackendApplication.class, args);
    }

}

package org.example.ganggrbkbackend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.sql.init.SqlInitializationAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication(exclude = {
        SqlInitializationAutoConfiguration.class
})
@MapperScan("org.example.ganggrbkbackend.mapper")
@EnableCaching
public class GangGrbkBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(GangGrbkBackendApplication.class, args);
    }

}
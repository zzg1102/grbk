package org.example.ganggrbkbackend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.sql.init.SqlInitializationAutoConfiguration;

@SpringBootApplication(exclude = {
        SqlInitializationAutoConfiguration.class
})
@MapperScan("org.example.ganggrbkbackend.mapper")
public class GangGrbkBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(GangGrbkBackendApplication.class, args);
    }

}
package org.example.ganggrbkbackend.config;

import org.apache.catalina.connector.Connector;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * HTTPS配置类
 * 用于配置HTTP到HTTPS的重定向和双端口支持
 */
@Configuration
@Profile("https")
public class HttpsConfig {

    /**
     * 配置Tomcat支持HTTP和HTTPS双端口
     * HTTP端口用于重定向到HTTPS
     */
    @Bean
    public ServletWebServerFactory servletContainer() {
        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory();
        
        // 添加HTTP连接器，用于重定向到HTTPS
        tomcat.addAdditionalTomcatConnectors(createHttpConnector());
        
        return tomcat;
    }

    /**
     * 创建HTTP连接器，将HTTP请求重定向到HTTPS
     */
    private Connector createHttpConnector() {
        Connector connector = new Connector(TomcatServletWebServerFactory.DEFAULT_PROTOCOL);
        connector.setScheme("http");
        connector.setPort(8080);
        connector.setSecure(false);
        connector.setRedirectPort(8443);
        return connector;
    }
}

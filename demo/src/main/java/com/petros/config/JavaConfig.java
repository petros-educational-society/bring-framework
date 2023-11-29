package com.petros.config;

import com.petros.bringframework.context.annotation.Bean;
import com.petros.bringframework.context.annotation.ComponentScan;
import com.petros.bringframework.context.annotation.Configuration;
import com.petros.bringframework.context.annotation.Primary;
import com.petros.services.HelloWorld2;
import com.petros.services.configtest.NotificationService;
import com.petros.services.configtest.NotificationServiceImpl;
import com.petros.services.configtest.UserService;

/**
 * @author "Maksym Oliinyk"
 */
@ComponentScan(basePackages = "com.petros")
@Configuration
public class JavaConfig {

    @Bean
    public HelloWorld2 getHelloWorld2() {
        return new HelloWorld2("first");
    }

    @Bean(name = "notificationServiceA")
    @Primary
    public NotificationService notificationServiceA() {
        return new NotificationServiceImpl().setEmail("configtest@com");
    }

    @Primary
    @Bean(name = "userServiceA")
    public UserService userService() {
        return new UserService(notificationServiceA());
    }

}
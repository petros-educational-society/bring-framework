package com.petros.config;

import com.petros.bringframework.context.annotation.Bean;
import com.petros.bringframework.context.annotation.ComponentScan;
import com.petros.bringframework.context.annotation.Configuration;
import com.petros.services.HelloWorld2;

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

    /*@Bean
    public HelloWorld2 getHelloWorld22() {
        return new HelloWorld2("second");
    }*/

}
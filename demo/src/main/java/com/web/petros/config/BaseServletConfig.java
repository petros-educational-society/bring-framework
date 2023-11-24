package com.web.petros.config;

import com.petros.bringframework.context.annotation.ComponentScan;
import com.petros.bringframework.context.annotation.Configuration;

/**
 * @author "Maksym Oliinyk"
 */
@ComponentScan(basePackages = {"com.web.petros", "com.petros"})
@Configuration
public class BaseServletConfig {
}

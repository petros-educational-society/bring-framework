package com.web.petros.config;

import com.petros.bringframework.context.annotation.ComponentScan;
import com.petros.bringframework.context.annotation.Configuration;
import com.web.petros.service.PictureService;

/**
 * @author "Maksym Oliinyk"
 * @author "Viktor Basanets"
 */
@ComponentScan(basePackages = {"com.web.petros", "com.petros"})
@Configuration
public class DefaultAppConfig {

    //@Bean
    public PictureService pictureService() {
//        return new PictureService();
        return null;
    }
}

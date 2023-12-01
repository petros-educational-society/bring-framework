package com.web.petros.config;

import com.petros.bringframework.context.annotation.Bean;
import com.petros.bringframework.context.annotation.Configuration;
import com.petros.bringframework.context.annotation.Primary;
import com.petros.bringframework.web.servlet.support.mapper.DataMapper;

/**
 * @author Serhii Dorodko
 */
@Configuration
public class ObjectMapperConfig {

    @Bean
    @Primary
    public DataMapper getCustomJsonMapper(){
        return new CustomJsonMapper();
    }
}

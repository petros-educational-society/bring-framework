package com.petros.services;

import com.petros.bringframework.context.annotation.Component;
import com.petros.bringframework.context.annotation.Primary;
import com.petros.bringframework.context.annotation.Scope;
import lombok.EqualsAndHashCode;
import lombok.extern.log4j.Log4j2;

import java.util.UUID;

/**
 * @author "Maksym Oliinyk"
 */
@Log4j2
@Scope("prototype")
@Primary
@Component
@EqualsAndHashCode
public class PrototypeTest {

    private final String id;

    public PrototypeTest() {
        this.id = UUID.randomUUID().toString();
        log.info("PrototypeTest constructor {}", id);
    }

}

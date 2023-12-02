package com.petros.services;

import com.petros.bringframework.beans.factory.annotation.DestroyPlease;
import com.petros.bringframework.beans.factory.annotation.InitPlease;
import com.petros.bringframework.beans.factory.annotation.Value;
import com.petros.bringframework.context.annotation.Component;
import com.petros.bringframework.context.annotation.Scope;
import lombok.extern.log4j.Log4j2;

/**
 * @author "Maksym Oliinyk"
 */
@Log4j2
@Scope("singleton")
@Component("worldHello")
public class HelloWorld {

    public HelloWorld() {
        log.info("HelloWorld constructor");
    }

    @InitPlease
    public void init() {
        log.info("To begin, let me say:");
    }

    @Value(value = "word")
    private String word;

    public void print() {
        log.info("Hello " + word);
    }

    @DestroyPlease
    public void after() {
        log.info("Good bye! Keep safe!");
    }

}

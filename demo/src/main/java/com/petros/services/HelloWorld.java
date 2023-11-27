package com.petros.services;

import com.petros.bringframework.beans.factory.annotation.DestroyPlease;
import com.petros.bringframework.beans.factory.annotation.InitPlease;
import com.petros.bringframework.beans.factory.annotation.Value;
import com.petros.bringframework.context.annotation.Component;

/**
 * @author "Maksym Oliinyk"
 */
@Component("worldHello")
public class HelloWorld {

    public HelloWorld() {
        System.out.println("HelloWorld constructor");
    }

    @InitPlease
    public void init() {
        System.out.println("To begin, let me say:");
    }

    @Value(value = "word")
    private String word;

    public void print() {
        System.out.println("Hello " + word);
    }

    @DestroyPlease
    public void after() {
        System.out.println("Good bye! Keep safe!");
    }

}

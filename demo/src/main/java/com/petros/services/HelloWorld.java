package com.petros.services;

import com.petros.bringframework.context.annotation.Component;

/**
 * @author "Maksym Oliinyk"
 */
@Component("worldHello")
public class HelloWorld {

    public void print() {
        System.out.println("Hello World!");
    }

}

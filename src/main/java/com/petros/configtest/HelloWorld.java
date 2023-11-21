package com.petros.configtest;

import com.petros.bringframework.context.annotation.Component;

/**
 * @author "Maksym Oliinyk"
 */
@Component
public class HelloWorld {

    public void print() {
        System.out.println("Hello World!");
    }

}

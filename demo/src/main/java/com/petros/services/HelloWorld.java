package com.petros.services;

import com.petros.bringframework.beans.factory.annotation.Value;
import com.petros.bringframework.context.annotation.Component;

/**
 * @author "Maksym Oliinyk"
 */
@Component("worldHello")
public class HelloWorld {

    @Value(value = "word")
    private String word;

    public void print() {
        System.out.println("Hello " + word);
    }

}

package com.petros.services;

import com.petros.bringframework.beans.factory.annotation.InjectPlease;
import com.petros.bringframework.context.annotation.Component;

@Component
public class SayHello {

    @InjectPlease
    private HelloWorld helloWorld;

    public void print() {
        helloWorld.print();
    }
}
